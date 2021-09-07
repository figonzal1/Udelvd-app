package cl.udelvd.repositories;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cl.udelvd.R;
import cl.udelvd.models.CivilState;
import cl.udelvd.services.VolleySingleton;
import cl.udelvd.utils.SingleLiveEvent;
import cl.udelvd.utils.Utils;

public class CivilStateRepository {

    private static final String TAG_GET_CIVIL_STATE = "ListaEstadosCiviles";
    private final Application application;
    private static CivilStateRepository instance;
    private final List<CivilState> civilStateList = new ArrayList<>();
    private final MutableLiveData<List<CivilState>> civilStateMutable = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final SingleLiveEvent<String> responseMsgErrorList = new SingleLiveEvent<>();

    private CivilStateRepository(Application application) {
        this.application = application;
    }

    public static CivilStateRepository getInstance(Application application) {

        if (instance == null) {
            instance = new CivilStateRepository(application);
        }

        return instance;
    }

    public MutableLiveData<List<CivilState>> getCivilStates() {

        sendGetCivilStates();

        return civilStateMutable;
    }

    private void sendGetCivilStates() {

        civilStateList.clear();

        Response.Listener<String> responseListener = response -> {

            //Log.d("RESPONSE", response);

            try {
                JSONObject jsonObject = new JSONObject(response);

                JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                for (int i = 0; i < jsonData.length(); i++) {
                    JSONObject jsonEstadoCivil = jsonData.getJSONObject(i);

                    int idCivilState = jsonEstadoCivil.getInt(application.getString(R.string.KEY_ESTADO_CIVIL_ID));

                    JSONObject jsonAttributes = jsonEstadoCivil.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));
                    String civilStateName = jsonAttributes.getString(application.getString(R.string.KEY_ESTADO_CIVIL_NOMRE));

                    CivilState civilState = new CivilState();
                    civilState.setId(idCivilState);
                    civilState.setName(civilStateName);

                    civilStateList.add(civilState);
                }

                civilStateMutable.postValue(civilStateList);

                isLoading.postValue(false);

            } catch (JSONException e) {

                Log.d("JSON_ERROR", "CIVIL STATES JSON PARSE ERROR");
                e.printStackTrace();
            }
        };

        Response.ErrorListener errorListener = error -> {

            isLoading.postValue(false);

            Utils.deadAPIHandler(
                    error,
                    application,
                    responseMsgErrorList,
                    application.getString(R.string.TAG_VOLLEY_ERR_EST_CIVIL)
            );
        };


        String url = String.format(application.getString(R.string.URL_GET_ESTADOS_CIVILES), application.getString(R.string.HEROKU_DOMAIN), Utils.getLanguage(application));

        StringRequest request = new StringRequest(Request.Method.GET, url, responseListener, errorListener) {

            @Override
            public Map<String, String> getHeaders() {

                SharedPreferences sharedPreferences = application.getSharedPreferences(application.getString(R.string.SHARED_PREF_MASTER_KEY),
                        Context.MODE_PRIVATE);

                String token = sharedPreferences.getString(application.getString(R.string.SHARED_PREF_TOKEN_LOGIN), "");

                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.JSON_CONTENT_TYPE), application.getString(R.string.JSON_CONTENT_TYPE_MSG));
                params.put(application.getString(R.string.JSON_AUTH), String.format("%s %s", application.getString(R.string.JSON_AUTH_MSG), token));

                return params;
            }
        };

        isLoading.postValue(true);
        //VolleySingleton.getInstance(application).addToRequestQueue(request,
        //        new HurlStack(null, SSLConection.getSocketFactory(application.getApplicationContext())));
        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_GET_CIVIL_STATE);
    }

    /*
    GETTERS
     */
    public SingleLiveEvent<String> getResponseMsgErrorList() {
        return responseMsgErrorList;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
