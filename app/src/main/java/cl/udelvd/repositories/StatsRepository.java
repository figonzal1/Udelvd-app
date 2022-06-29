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
import cl.udelvd.models.Stat;
import cl.udelvd.services.VolleySingleton;
import cl.udelvd.utils.SingleLiveEvent;
import cl.udelvd.utils.Utils;

public class StatsRepository {

    private static final String TAG_GET_STATS = "ListaEstadisticas";
    private static StatsRepository instance;
    private final Application application;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final List<Stat> statList = new ArrayList<>();
    private final MutableLiveData<List<Stat>> statsMutableLivedata = new MutableLiveData<>();
    private final SingleLiveEvent<String> responseMsgErrorList = new SingleLiveEvent<>();


    private StatsRepository(Application application) {
        this.application = application;
    }

    public static StatsRepository getInstance(Application application) {

        if (instance == null) {
            instance = new StatsRepository(application);
        }
        return instance;
    }

    public MutableLiveData<List<Stat>> getStats() {

        sendGetStats();

        return statsMutableLivedata;
    }

    public SingleLiveEvent<String> getResponseMsgErrorList() {
        return responseMsgErrorList;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    private void sendGetStats() {

        statList.clear();

        Response.Listener<String> responseListener = response -> {

            //Log.d("RESPONSE", response);
            try {
                JSONObject jsonObject = new JSONObject(response);

                JSONArray jsonArray = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonStat = jsonArray.getJSONObject(i);
                    JSONObject jsonAttributes = jsonStat.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    Stat stat = new Stat();
                    stat.setId(jsonStat.getInt(application.getString(R.string.KEY_STAT_ID)));

                    String lang = Utils.getLanguage(application);

                    if (lang.equals(application.getString(R.string.ESPANOL))) {

                        stat.setName(jsonAttributes.getString(application.getString(R.string.KEY_STAT_NAME_ES)));

                    } else if (lang.equals(application.getString(R.string.INGLES))) {

                        stat.setName(jsonAttributes.getString(application.getString(R.string.KEY_STAT_NAME_EN)));
                    }

                    stat.setUrl(jsonAttributes.getString(application.getString(R.string.KEY_STAT_URL)));
                    stat.setPinPass(jsonAttributes.getString(application.getString(R.string.KEY_STAT_PIN_PASS)));

                    statList.add(stat);
                }

                statsMutableLivedata.postValue(statList);

                isLoading.postValue(false);

            } catch (JSONException e) {

                Log.d("JSON_ERROR", "STATS JSON ERROR PARSE");
                e.printStackTrace();
            }
        };

        Response.ErrorListener errorListener = error -> {

            isLoading.postValue(false);

            Utils.deadAPIHandler(
                    error,
                    application,
                    responseMsgErrorList,
                    application.getString(R.string.TAG_VOLLEY_ERR_ESTADISTICAS)
            );

        };

        String url = String.format(application.getString(R.string.URL_GET_ESTADISTICAS), application.getString(R.string.HEROKU_DOMAIN));

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
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_GET_STATS);
    }
}
