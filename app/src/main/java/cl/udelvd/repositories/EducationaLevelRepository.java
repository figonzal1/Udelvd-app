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
import cl.udelvd.models.EducationalLevel;
import cl.udelvd.services.VolleySingleton;
import cl.udelvd.utils.SingleLiveEvent;
import cl.udelvd.utils.Utils;

public class EducationaLevelRepository {

    private static final String TAG_EDUCATIONAL_LEVEL = "ListadoNivelEducacional";
    private final Application application;
    private static EducationaLevelRepository instance;
    //GET
    private final List<EducationalLevel> educationalLevelList = new ArrayList<>();
    private final MutableLiveData<List<EducationalLevel>> educationaLevelMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final SingleLiveEvent<String> responseMsgErrorList = new SingleLiveEvent<>();

    private EducationaLevelRepository(Application application) {
        this.application = application;
    }

    public static EducationaLevelRepository getInstance(Application application) {

        if (instance == null) {
            instance = new EducationaLevelRepository(application);
        }

        return instance;
    }

    public MutableLiveData<List<EducationalLevel>> getEducationalLevel() {

        sendGetEducationalLevel();

        return educationaLevelMutableLiveData;
    }

    private void sendGetEducationalLevel() {

        educationalLevelList.clear();

        Response.Listener<String> responseListener = response -> {
            //Log.d("Response", response);

            try {
                JSONObject jsonObject = new JSONObject(response);

                JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                for (int i = 0; i < jsonData.length(); i++) {
                    JSONObject jsonEducationalLevel = jsonData.getJSONObject(i);

                    int id_nivel = jsonEducationalLevel.getInt(application.getString(R.string.KEY_NIVEL_EDUC_ID));

                    JSONObject jsonAttributes = jsonEducationalLevel.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));
                    String nombreNivel = jsonAttributes.getString(application.getString(R.string.KEY_NIVEL_EDUC_NOMBRE));

                    EducationalLevel educationalLevel = new EducationalLevel();
                    educationalLevel.setId(id_nivel);
                    educationalLevel.setName(nombreNivel);

                    educationalLevelList.add(educationalLevel);
                }

                educationaLevelMutableLiveData.postValue(educationalLevelList);

                isLoading.postValue(false);

            } catch (JSONException e) {

                Log.d("JSON_ERROR", "EDUCATIONAL LIST JSON ERROR PARSE");
                e.printStackTrace();
            }

        };

        Response.ErrorListener errorListener = error -> {

            isLoading.postValue(false);

            Utils.deadAPIHandler(
                    error,
                    application,
                    responseMsgErrorList,
                    application.getString(R.string.TAG_VOLLEY_ERR_NIVEL_EDUC)
            );
        };

        String url = String.format(application.getString(R.string.URL_GET_NIVELES_EDUCACIONALES), application.getString(R.string.HEROKU_DOMAIN), Utils.getLanguage(application));

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
        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_EDUCATIONAL_LEVEL);
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
