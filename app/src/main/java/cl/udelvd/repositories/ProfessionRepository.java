package cl.udelvd.repositories;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cl.udelvd.R;
import cl.udelvd.models.Profession;
import cl.udelvd.services.VolleySingleton;
import cl.udelvd.utils.SingleLiveEvent;

public class ProfessionRepository {

    private static final String TAG_PROFESSION = "ListadoProfesion";
    private final Application application;
    private static ProfessionRepository instance;
    //LIST
    private final List<Profession> professionList = new ArrayList<>();
    private final MutableLiveData<List<Profession>> professionMutableLiveData = new MutableLiveData<>();    //INSTANCIATE HERE ALLOW REFRESH

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final SingleLiveEvent<String> responseMsgErrorList = new SingleLiveEvent<>();

    private ProfessionRepository(Application application) {
        this.application = application;
    }

    public static ProfessionRepository getInstance(Application application) {
        if (instance == null) {
            instance = new ProfessionRepository(application);
        }
        return instance;
    }

    public MutableLiveData<List<Profession>> getProfessions() {
        sendGetProfession();
        return professionMutableLiveData;
    }

    private void sendGetProfession() {

        professionList.clear();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                    for (int i = 0; i < jsonData.length(); i++) {
                        JSONObject jsonEducLevel = jsonData.getJSONObject(i);

                        int idNivel = jsonEducLevel.getInt(application.getString(R.string.KEY_PROFESION_ID));

                        JSONObject jsonAttributes = jsonEducLevel.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));
                        String nombreNivel = jsonAttributes.getString(application.getString(R.string.KEY_PROFESION_NOMBRE));

                        Profession profession = new Profession();
                        profession.setId(idNivel);
                        profession.setName(nombreNivel);

                        professionList.add(profession);
                    }

                    professionMutableLiveData.postValue(professionList);

                    isLoading.postValue(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isLoading.postValue(false);

                if (error instanceof TimeoutError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_PROFESION), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgErrorList.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                } else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_PROFESION), application.getString(R.string.NETWORK_ERROR));
                    responseMsgErrorList.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
                } else if (error.networkResponse != null && error.networkResponse.data != null) {

                    String json = new String(error.networkResponse.data);

                    JSONObject errorObject = null;

                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        errorObject = jsonObject.getJSONObject(application.getString(R.string.JSON_ERROR));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (error instanceof AuthFailureError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_PROFESION), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                    } else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_PROFESION), String.format("%s %s", application.getString(R.string.SERVER_ERROR), errorObject));
                        responseMsgErrorList.postValue(application.getString(R.string.SERVER_ERROR_MSG_VM));
                    }
                }
            }
        };

        String url = String.format(application.getString(R.string.URL_GET_PROFESIONES), application.getString(R.string.HEROKU_DOMAIN));

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
        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_PROFESSION);
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
