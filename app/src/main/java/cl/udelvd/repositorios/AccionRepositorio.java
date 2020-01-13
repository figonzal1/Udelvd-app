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
                //Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                    for (int i = 0; i < jsonData.length(); i++) {

                        JSONObject jsonAccion = jsonData.getJSONObject(i);
                        JSONObject jsonAttributes = jsonAccion.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                        Accion accion = new Accion();
                        accion.setId(jsonAccion.getInt(application.getString(R.string.KEY_ACCION_ID)));
                        accion.setNombre(jsonAttributes.getString(application.getString(R.string.KEY_ACCION_NOMBRE)));

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
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ACCION), application.getString(R.string.TIMEOUT_ERROR));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ACCION), application.getString(R.string.NETWORK_ERROR));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ACCION), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ACCION), String.format("%s %s", application.getString(R.string.SERVER_ERROR), errorObject));
                    }
                }
            }
        };

        String url = application.getString(R.string.URL_GET_ACCIONES);
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
