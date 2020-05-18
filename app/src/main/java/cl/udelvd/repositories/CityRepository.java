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
import cl.udelvd.models.City;
import cl.udelvd.services.VolleySingleton;
import cl.udelvd.utils.SingleLiveEvent;

public class CityRepository {

    private static final String TAG_GET_CITIES = "ListaCiudades";
    private final Application application;
    private static CityRepository instance;
    private final List<City> cityList = new ArrayList<>();
    private final MutableLiveData<List<City>> citiesMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final SingleLiveEvent<String> responseMsgErrorList = new SingleLiveEvent<>();

    private CityRepository(Application application) {
        this.application = application;
    }

    public static CityRepository getInstance(Application application) {
        if (instance == null) {
            instance = new CityRepository(application);
        }
        return instance;
    }

    public MutableLiveData<List<City>> getCities() {
        sedGetCities();
        return citiesMutableLiveData;
    }

    private void sedGetCities() {

        cityList.clear();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                    for (int i = 0; i < jsonData.length(); i++) {
                        JSONObject jsonCity = jsonData.getJSONObject(i);

                        int idCity = jsonCity.getInt(application.getString(R.string.KEY_CIUDAD_ID));
                        JSONObject jsonAttributes = jsonCity.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));
                        String cityName = jsonAttributes.getString(application.getString(R.string.KEY_CIUDAD_NOMBRE));

                        City city = new City();
                        city.setId(idCity);
                        city.setName(cityName);

                        cityList.add(city);
                    }

                    citiesMutableLiveData.postValue(cityList);

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
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_CIUDAD), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgErrorList.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_CIUDAD), application.getString(R.string.NETWORK_ERROR));
                    responseMsgErrorList.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
                }

                else if (error.networkResponse != null && error.networkResponse.data != null) {

                    String json = new String(error.networkResponse.data);

                    JSONObject errorObject = null;

                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        errorObject = jsonObject.getJSONObject(application.getString(R.string.JSON_ERROR));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (error instanceof AuthFailureError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_CIUDAD), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                    }

                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_CIUDAD), String.format("%s %s", application.getString(R.string.SERVER_ERROR), errorObject));
                        responseMsgErrorList.postValue(application.getString(R.string.SERVER_ERROR_MSG_VM));
                    }
                }
            }
        };


        String url = String.format(application.getString(R.string.URL_GET_CIUDADES), application.getString(R.string.HEROKU_DOMAIN));

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
        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_GET_CITIES);
    }

    /*
    GETTERS
     */
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public SingleLiveEvent<String> getResponseMsgErrorList() {
        return responseMsgErrorList;
    }
}
