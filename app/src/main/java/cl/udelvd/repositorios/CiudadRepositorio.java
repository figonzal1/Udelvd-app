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

import cl.udelvd.R;
import cl.udelvd.modelo.Ciudad;
import cl.udelvd.servicios.VolleySingleton;

public class CiudadRepositorio {

    private static CiudadRepositorio instancia;
    private final Application application;

    private List<Ciudad> ciudadList;

    private static final String TAG_GET_CIUDADES = "ListaCiudades";

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
                //Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                    for (int i = 0; i < jsonData.length(); i++) {
                        JSONObject jsonCiudad = jsonData.getJSONObject(i);

                        int id_ciudad = jsonCiudad.getInt(application.getString(R.string.KEY_CIUDAD_ID));
                        JSONObject jsonAttributes = jsonCiudad.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));
                        String nombre_ciudad = jsonAttributes.getString(application.getString(R.string.KEY_CIUDAD_NOMBRE));

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
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_CIUDAD), application.getString(R.string.TIMEOUT_ERROR));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_CIUDAD), application.getString(R.string.NETWORK_ERROR));
                }

                //Errores cuando el servidor si responde
                else if (error.networkResponse != null && error.networkResponse.data != null) {

                    String json = new String(error.networkResponse.data);

                    JSONObject errorObject = null;

                    //Obtener json error
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        errorObject = jsonObject.getJSONObject(application.getString(R.string.JSON_ERROR));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Error de autorizacion
                    if (error instanceof AuthFailureError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_CIUDAD), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_CIUDAD), String.format("%s %s", application.getString(R.string.SERVER_ERROR), errorObject));
                    }
                }
            }
        };


        String url = application.getString(R.string.URL_GET_CIUDADES);

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

        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_GET_CIUDADES);

    }

    /**
     * Obtener la ciudad según parametro
     *
     * @param id Id de la ciudad a buscar
     * @return Ciudad
     */
    public Ciudad buscarCiudadPorId(int id) {

        for (int i = 0; i < ciudadList.size(); i++) {
            if (ciudadList.get(i).getId() == id) {
                return ciudadList.get(i);
            }
        }
        return null;
    }
}
