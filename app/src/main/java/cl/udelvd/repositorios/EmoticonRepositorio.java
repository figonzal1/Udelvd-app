package cl.udelvd.repositorios;

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

import cl.udelvd.modelo.Emoticon;
import cl.udelvd.servicios.VolleySingleton;

public class EmoticonRepositorio {

    private static final String TAG_GET_EMOTICONES = "ListaEmoticones";
    private static EmoticonRepositorio instancia;
    private Application application;

    private List<Emoticon> emoticonList;

    private EmoticonRepositorio(Application application) {
        this.application = application;
    }

    public static EmoticonRepositorio getInstancia(Application application) {
        if (instancia == null) {
            instancia = new EmoticonRepositorio(application);
        }
        return instancia;
    }

    /**
     * Funcion encargada de obtener el listado de emoticones del sistema
     *
     * @return listaMutable con listado de emoticones
     */
    public MutableLiveData<List<Emoticon>> obtenerEmoticones() {
        MutableLiveData<List<Emoticon>> mutableLiveData = new MutableLiveData<>();
        sendGetEmoticones(mutableLiveData);
        return mutableLiveData;
    }

    /**
     * Funcion encargada de enviar peticion GET para obtener emoticones del sistema
     *
     * @param mutableLiveData listado con datos de emoticones
     */
    private void sendGetEmoticones(final MutableLiveData<List<Emoticon>> mutableLiveData) {

        emoticonList = new ArrayList<>();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d("RESPONSE",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonEmoticon = jsonArray.getJSONObject(i);

                        JSONObject jsonAttribute = jsonEmoticon.getJSONObject("attributes");

                        Emoticon emoticon = new Emoticon();
                        emoticon.setId(jsonEmoticon.getInt("id"));
                        emoticon.setUrl(jsonAttribute.getString("url"));
                        emoticon.setDescripcion(jsonAttribute.getString("descripcion"));

                        emoticonList.add(emoticon);
                    }

                    mutableLiveData.postValue(emoticonList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Log.d("VOLLEY_ERR_EMOTICON", "TIMEOUT_ERROR");
                    //TODO: Agregar error response
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d("VOLLEY_ERR_EMOTICON", "NETWORK_ERROR");
                }

                //Errores cuando el servidor si responde
                else if (error.networkResponse != null && error.networkResponse.data != null) {

                    String json = new String(error.networkResponse.data);

                    JSONObject errorObject = null;

                    //Obtener json error
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        errorObject = jsonObject.getJSONObject("error");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Error de autorizacion
                    if (error instanceof AuthFailureError) {
                        Log.d("VOLLEY_ERR_EMOTICON", "AUTHENTICATION_ERROR: " + errorObject);
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d("VOLLEY_ERR_EMOTICON", "SERVER_ERROR: " + errorObject);
                    }
                }
            }
        };

        String url = "http://192.168.0.14/emoticones";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {

                SharedPreferences sharedPreferences = application.getSharedPreferences("udelvd",
                        Context.MODE_PRIVATE);

                String token = sharedPreferences.getString("TOKEN_LOGIN", "");

                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };

        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_GET_EMOTICONES);

    }
}
