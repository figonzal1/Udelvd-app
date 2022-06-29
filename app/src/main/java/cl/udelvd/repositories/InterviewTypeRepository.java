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
import cl.udelvd.models.InterviewType;
import cl.udelvd.services.VolleySingleton;
import cl.udelvd.utils.SingleLiveEvent;
import cl.udelvd.utils.Utils;

public class InterviewTypeRepository {

    private static final String TAG_INTERVIEW_TYPE = "TiposEntrevistas";
    private final Application application;
    private static InterviewTypeRepository instance;
    //LISTADO
    private final List<InterviewType> interviewTypeList = new ArrayList<>();
    private final MutableLiveData<List<InterviewType>> interviewTypeMutable = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final SingleLiveEvent<String> responseMsgErrorList = new SingleLiveEvent<>();

    private InterviewTypeRepository(Application application) {
        this.application = application;
    }

    public static InterviewTypeRepository getInstance(Application application) {

        if (instance == null) {
            instance = new InterviewTypeRepository(application);
        }

        return instance;
    }

    public MutableLiveData<List<InterviewType>> getInterviewType() {

        sendGetInterviewType();

        return interviewTypeMutable;
    }

    private void sendGetInterviewType() {

        interviewTypeList.clear();

        Response.Listener<String> responseListener = response -> {

            //Log.d("RESPONSE_TIPO_ENTRE", response);

            try {
                JSONObject jsonObject;
                jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonTipoEntrevista = jsonArray.getJSONObject(i);

                    JSONObject jsonAttributes = jsonTipoEntrevista.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    InterviewType interviewType = new InterviewType();
                    interviewType.setId(jsonTipoEntrevista.getInt(application.getString(R.string.KEY_TIPO_ENTREVISTA_ID)));
                    interviewType.setName(jsonAttributes.getString(application.getString(R.string.KEY_TIPO_ENTREVISTA_NOMBRE)));

                    interviewTypeList.add(interviewType);
                }

                interviewTypeMutable.postValue(interviewTypeList);

                isLoading.postValue(false);
            } catch (JSONException e) {

                Log.d("JSON_ERROR", "INTERVIEW TYPE JSON ERROR PARSE");
                e.printStackTrace();
            }
        };

        Response.ErrorListener errorListener = error -> {

            isLoading.postValue(false);

            Utils.deadAPIHandler(
                    error,
                    application,
                    responseMsgErrorList,
                    application.getString(R.string.TAG_VOLLEY_ERR_TIPO_ENTR)
            );

        };

        String url = String.format(application.getString(R.string.URL_GET_TIPOS_ENTREVISTAS), application.getString(R.string.HEROKU_DOMAIN), Utils.getLanguage(application));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, responseListener, errorListener) {

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

        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_INTERVIEW_TYPE);
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
