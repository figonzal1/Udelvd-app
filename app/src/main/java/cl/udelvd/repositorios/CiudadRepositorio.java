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

import cl.udelvd.modelo.Ciudad;
import cl.udelvd.servicios.VolleySingleton;

public class CiudadRepositorio {

    private static CiudadRepositorio instancia;
    private final Application application;

    private List<Ciudad> ciudadList;

    private CiudadRepositorio(Application application) {
        this.application = application;
    }

    public static CiudadRepositorio getInstancia(Application application) {
        if (instancia == null) {
            instancia = new CiudadRepositorio(application);
        }
        return instancia;
    }

    /**
     * Funcion encargada de consultar la lista de ciudades actuales
     *
     * @return MutableLiveData usado en viewModel
     */
    public MutableLiveData<List<Ciudad>> obtenerCiudades() {
        MutableLiveData<List<Ciudad>> ciudadesMutableLiveData = new MutableLiveData<>();
        enviarGetCiudades(ciudadesMutableLiveData);
        return ciudadesMutableLiveData;
    }

    /**
     * Funcion encargada de enviar la solicitud GET al servidor, para obtener listado de ciudades disponibles.
     *
     * @param ciudadesMutableLiveData Lista mutable vacia rellenada con lista de ciudades
     */
    private void enviarGetCiudades(final MutableLiveData<List<Ciudad>> ciudadesMutableLiveData) {

        ciudadList = new ArrayList<>();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray("data");

                    for (int i = 0; i < jsonData.length(); i++) {
                        JSONObject jsonCiudad = jsonData.getJSONObject(i);

                        int id_ciudad = jsonCiudad.getInt("id");
                        JSONObject jsonAttributes = jsonCiudad.getJSONObject("attributes");
                        String nombre_ciudad = jsonAttributes.getString("nombre");

                        Ciudad ciudad = new Ciudad();
                        ciudad.setId(id_ciudad);
                        ciudad.setNombre(nombre_ciudad);

                        ciudadList.add(ciudad);

                    }

                    ciudadesMutableLiveData.postValue(ciudadList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Log.d("VOLLEY_ERROR_LOGIN", "TIMEOUT_ERROR");
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d("VOLLEY_ERROR_LOGIN", "NETWORK_ERROR");
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
                        Log.d("VOLLEY_ERROR_LOGIN", "AUTHENTICATION_ERROR: " + errorObject);
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d("VOLLEY_ERROR_LOGIN", "SERVER_ERROR: " + errorObject);
                    }
                }
            }
        };


        String url = "http://192.168.0.14/ciudades";

        StringRequest request = new StringRequest(Request.Method.GET, url, responseListener, errorListener) {

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

        String TAG = "ciudades";
        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG);

    }
}
