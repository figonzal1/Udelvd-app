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
import cl.udelvd.models.Emoticon;
import cl.udelvd.services.VolleySingleton;
import cl.udelvd.utils.SingleLiveEvent;
import cl.udelvd.utils.Utils;

public class EmoticonRepository {

    private static final String TAG_GET_EMOTICONS = "ListaEmoticones";

    private static EmoticonRepository instance;
    private final Application application;

    //LIST
    private final List<Emoticon> emoticonList = new ArrayList<>();
    private final MutableLiveData<List<Emoticon>> emoticonMutableLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<String> responseMsgErrorList = new SingleLiveEvent<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private EmoticonRepository(Application application) {
        this.application = application;
    }

    public static EmoticonRepository getInstance(Application application) {

        if (instance == null) {
            instance = new EmoticonRepository(application);
        }

        return instance;
    }

    public MutableLiveData<List<Emoticon>> getEmoticons() {

        sendGetEmoticons();

        return emoticonMutableLiveData;
    }

    private void sendGetEmoticons() {

        emoticonList.clear();

        Response.Listener<String> responseListener = response -> {

            //Log.d("RESPONSE",response);
            try {
                JSONObject jsonObject = new JSONObject(response);

                JSONArray jsonArray = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonEmoticon = jsonArray.getJSONObject(i);

                    JSONObject jsonAttribute = jsonEmoticon.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    Emoticon emoticon = new Emoticon();
                    emoticon.setId(jsonEmoticon.getInt(application.getString(R.string.KEY_EMOTICON_ID)));
                    emoticon.setUrl(jsonAttribute.getString(application.getString(R.string.KEY_EMOTICON_URL)));
                    emoticon.setDescription(jsonAttribute.getString(application.getString(R.string.KEY_EMOTICON_DESCRIPCION)));

                    emoticonList.add(emoticon);
                }

                emoticonMutableLiveData.postValue(emoticonList);

                isLoading.postValue(false);

            } catch (JSONException e) {

                Log.d("JSON_ERROR", "EMOTICON LIST JSON ERROR PARSE");
                e.printStackTrace();
            }
        };

        Response.ErrorListener errorListener = error -> {

            isLoading.postValue(false);

            Utils.deadAPIHandler(
                    error,
                    application,
                    responseMsgErrorList,
                    application.getString(R.string.TAG_VOLLEY_ERR_EMOTICON)
            );
        };

        String url = String.format(application.getString(R.string.URL_GET_EMOTICONES), application.getString(R.string.HEROKU_DOMAIN), Utils.getLanguage(application));

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
        //VolleySingleton.getInstance(application).addToRequestQueue(stringRequest,
        //        new HurlStack(null, SSLConection.getSocketFactory(application.getApplicationContext())));
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_GET_EMOTICONS);
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
