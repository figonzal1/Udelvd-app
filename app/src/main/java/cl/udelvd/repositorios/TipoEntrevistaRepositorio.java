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

import cl.udelvd.modelo.TipoEntrevista;
import cl.udelvd.servicios.VolleySingleton;

public class TipoEntrevistaRepositorio {

    private static TipoEntrevistaRepositorio instancia;
    private Application application;

    private List<TipoEntrevista> tipoEntrevistaList;

    private TipoEntrevistaRepositorio(Application application) {
        this.application = application;
    }

    public static TipoEntrevistaRepositorio getInstancia(Application application) {
        if (instancia == null) {
            instancia = new TipoEntrevistaRepositorio(application);
        }
        return instancia;
    }

    public MutableLiveData<List<TipoEntrevista>> obtenerTiposEntrevista() {
        MutableLiveData<List<TipoEntrevista>> mutableLiveData = new MutableLiveData<>();
        sendGetTiposEntrevista(mutableLiveData);
        return mutableLiveData;
    }

    private void sendGetTiposEntrevista(final MutableLiveData<List<TipoEntrevista>> mutableLiveData) {

        tipoEntrevistaList = new ArrayList<>();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("RESPONSE_TIPO_ENTRE", response);

                try {
                    JSONObject jsonObject;
                    jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonTipoEntrevista = jsonArray.getJSONObject(i);

                        JSONObject jsonAttributes = jsonTipoEntrevista.getJSONObject("attributes");

                        TipoEntrevista tipoEntrevista = new TipoEntrevista();
                        tipoEntrevista.setId(jsonTipoEntrevista.getInt("id"));
                        tipoEntrevista.setNombre(jsonAttributes.getString("nombre"));

                        tipoEntrevistaList.add(tipoEntrevista);
                    }

                    mutableLiveData.postValue(tipoEntrevistaList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Log.d("VOLLEY_ERR_TIPO_ENTR", "TIMEOUT_ERROR");
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d("VOLLEY_ERR_TIPO_ENTR", "NETWORK_ERROR");
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
                        Log.d("VOLLEY_ERR_TIPO_ENTR", "AUTHENTICATION_ERROR: " + errorObject);
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d("VOLLEY_ERR_TIPO_ENTR", "SERVER_ERROR: " + errorObject);
                    }
                }
            }
        };

        String url = "http://192.168.0.14/tiposEntrevistas";

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

        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, "TiposEntrevistas");
    }

    public TipoEntrevista buscarTipoEntrevistaPorNombre(String nombre) {

        for (int i = 0; i < tipoEntrevistaList.size(); i++) {
            if (tipoEntrevistaList.get(i).getNombre().equals(nombre)) {
                return tipoEntrevistaList.get(i);
            }
        }

        return null;
    }
}
