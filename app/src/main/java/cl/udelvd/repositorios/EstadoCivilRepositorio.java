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
import cl.udelvd.modelo.EstadoCivil;
import cl.udelvd.servicios.VolleySingleton;
import cl.udelvd.utilidades.SingleLiveEvent;
import cl.udelvd.utilidades.Utils;

public class EstadoCivilRepositorio {

    private static EstadoCivilRepositorio instancia;
    private final Application application;

    private final List<EstadoCivil> estadoCivilList = new ArrayList<>();
    private final MutableLiveData<List<EstadoCivil>> estadosCivilesMutable = new MutableLiveData<>();
    private final SingleLiveEvent<String> responseMsgErrorListado = new SingleLiveEvent<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private static final String TAG_GET_ESTADOS_CIVILES = "ListaEstadosCiviles";

    private EstadoCivilRepositorio(Application application) {
        this.application = application;
    }

    public static EstadoCivilRepositorio getInstance(Application application) {
        if (instancia == null) {
            instancia = new EstadoCivilRepositorio(application);
        }
        return instancia;
    }

    /*
    LISTADO DE ESTADOS CIVILES
     */
    public MutableLiveData<List<EstadoCivil>> obtenerEstadosCiviles() {
        enviarGetEstadosCiviles();
        return estadosCivilesMutable;
    }

    private void enviarGetEstadosCiviles() {

        estadoCivilList.clear();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                    for (int i = 0; i < jsonData.length(); i++) {
                        JSONObject jsonEstadoCivil = jsonData.getJSONObject(i);

                        int id_estado_civil = jsonEstadoCivil.getInt(application.getString(R.string.KEY_ESTADO_CIVIL_ID));

                        JSONObject jsonAttributes = jsonEstadoCivil.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));
                        String nombre_estado_civil = jsonAttributes.getString(application.getString(R.string.KEY_ESTADO_CIVIL_NOMRE));

                        EstadoCivil estadoCivil = new EstadoCivil();
                        estadoCivil.setId(id_estado_civil);
                        estadoCivil.setNombre(nombre_estado_civil);

                        estadoCivilList.add(estadoCivil);
                    }

                    estadosCivilesMutable.postValue(estadoCivilList);

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
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_EST_CIVIL), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgErrorListado.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_EST_CIVIL), application.getString(R.string.NETWORK_ERROR));
                    responseMsgErrorListado.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_EST_CIVIL), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_EST_CIVIL), String.format("%s %s", application.getString(R.string.SERVER_ERROR), errorObject));
                        responseMsgErrorListado.postValue(application.getString(R.string.SERVER_ERROR_MSG_VM));
                    }
                }
            }
        };


        String url = String.format(application.getString(R.string.URL_GET_ESTADOS_CIVILES), application.getString(R.string.HEROKU_DOMAIN), Utils.obtenerIdioma(application));

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
        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_GET_ESTADOS_CIVILES);
    }

    /*
    GETTERS
     */
    public SingleLiveEvent<String> getResponseMsgErrorListado() {
        return responseMsgErrorListado;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
