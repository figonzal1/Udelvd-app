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
import cl.udelvd.modelo.TipoEntrevista;
import cl.udelvd.servicios.VolleySingleton;

public class TipoEntrevistaRepositorio {

    private static TipoEntrevistaRepositorio instancia;
    private Application application;

    private List<TipoEntrevista> tipoEntrevistaList;

    private static final String TAG_TIPO_ENTREVISTA = "TiposEntrevistas";

    private TipoEntrevistaRepositorio(Application application) {
        this.application = application;
    }

    public static TipoEntrevistaRepositorio getInstancia(Application application) {
        if (instancia == null) {
            instancia = new TipoEntrevistaRepositorio(application);
        }
        return instancia;
    }

    /**
     * Funcion encargada de obtener el listado de tipos de entrevista
     *
     * @return MutableLiveData de tipos de entrevista
     */
    public MutableLiveData<List<TipoEntrevista>> obtenerTiposEntrevista() {
        MutableLiveData<List<TipoEntrevista>> mutableLiveData = new MutableLiveData<>();
        sendGetTiposEntrevista(mutableLiveData);
        return mutableLiveData;
    }

    /**
     * Funcion encargada de enviar peticion GET al servidor
     *
     * @param mutableLiveData Listado mutable de tipos de entrevistas
     */
    private void sendGetTiposEntrevista(final MutableLiveData<List<TipoEntrevista>> mutableLiveData) {

        tipoEntrevistaList = new ArrayList<>();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d("RESPONSE_TIPO_ENTRE", response);

                try {
                    JSONObject jsonObject;
                    jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonTipoEntrevista = jsonArray.getJSONObject(i);

                        JSONObject jsonAttributes = jsonTipoEntrevista.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                        TipoEntrevista tipoEntrevista = new TipoEntrevista();
                        tipoEntrevista.setId(jsonTipoEntrevista.getInt(application.getString(R.string.KEY_TIPO_ENTREVISTA_ID)));
                        tipoEntrevista.setNombre(jsonAttributes.getString(application.getString(R.string.KEY_TIPO_ENTREVISTA_NOMBRE)));

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
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_TIPO_ENTR), application.getString(R.string.TIMEOUT_ERROR));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_TIPO_ENTR), application.getString(R.string.NETWORK_ERROR));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_TIPO_ENTR), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_TIPO_ENTR), String.format("%s %s", application.getString(R.string.SERVER_ERROR), errorObject));
                    }
                }
            }
        };

        String url = application.getString(R.string.URL_GET_TIPOS_ENTREVISTAS);

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
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_TIPO_ENTREVISTA);
    }

    /**
     * Funcion encargada de buscar una TipoEntrevista dado parametro
     *
     * @param nombre Nombre del tipo entrevista
     * @return Objeto tipoEntrevista
     */
    public TipoEntrevista buscarTipoEntrevistaPorNombre(String nombre) {

        for (int i = 0; i < tipoEntrevistaList.size(); i++) {
            if (tipoEntrevistaList.get(i).getNombre().equals(nombre)) {
                return tipoEntrevistaList.get(i);
            }
        }
        return null;
    }

    /**
     * Funcion encargada de buscar una TipoEntrevista dado parametro
     *
     * @param id Id del tipo entrevista
     * @return Objeto tipoEntrevista
     */
    public TipoEntrevista buscarTipoEntrevistaPorId(int id) {

        for (int i = 0; i < tipoEntrevistaList.size(); i++) {
            if (tipoEntrevistaList.get(i).getId() == id) {
                return tipoEntrevistaList.get(i);
            }
        }
        return null;
    }
}
