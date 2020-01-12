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

import cl.udelvd.modelo.Accion;
import cl.udelvd.servicios.VolleySingleton;

public class AccionRepositorio {

    private static final String TAG_GET_ACCIONES = "ListaAcciones";
    private static AccionRepositorio instancia;
    private Application application;

    private List<Accion> accionList;

    private AccionRepositorio(Application application) {
        this.application = application;
    }

    public static AccionRepositorio getInstancia(Application application) {
        if (instancia == null) {
            instancia = new AccionRepositorio(application);
        }
        return instancia;
    }

    /**
     * Funcion encargada de obtener el listado de acciones desde el servidor
     *
     * @return MutableList con la lista de acciones
     */
    public MutableLiveData<List<Accion>> obtenerAcciones() {
        MutableLiveData<List<Accion>> mutableLiveData = new MutableLiveData<>();
        sendGetAcciones(mutableLiveData);
        return mutableLiveData;
    }

    /**
     * Funcion encargada de enviar peticion GET al servidor
     *
     * @param mutableLiveData MutableLiveData con listado de acciones
     */
    private void sendGetAcciones(final MutableLiveData<List<Accion>> mutableLiveData) {

        accionList = new ArrayList<>();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray("data");

                    for (int i = 0; i < jsonData.length(); i++) {

                        JSONObject jsonAccion = jsonData.getJSONObject(i);
                        JSONObject jsonAttributes = jsonAccion.getJSONObject("attributes");

                        Accion accion = new Accion();
                        accion.setId(jsonAccion.getInt("id"));
                        accion.setNombre(jsonAttributes.getString("nombre"));

                        accionList.add(accion);
                    }

                    mutableLiveData.postValue(accionList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Log.d("VOLLEY_ERR_ACCION", "TIMEOUT_ERROR");
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d("VOLLEY_ERR_ACCION", "NETWORK_ERROR");
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
                        Log.d("VOLLEY_ERR_ACCION", "AUTHENTICATION_ERROR: " + errorObject);
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d("VOLLEY_ERR_ACCION", "SERVER_ERROR: " + errorObject);
                    }
                }
            }
        };

        String url = "http://192.168.0.14/acciones";
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

        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_GET_ACCIONES);
    }

    public Accion buscarAccionPorNombre(String nombre) {

        for (int i = 0; i < accionList.size(); i++) {
            if (nombre.equals(accionList.get(i).getNombre())) {
                return accionList.get(i);
            }
        }
        return null;
    }
}
