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
import cl.udelvd.modelo.NivelEducacional;
import cl.udelvd.servicios.VolleySingleton;
import cl.udelvd.utilidades.SingleLiveEvent;

public class NivelEducacionalRepositorio {

    private static NivelEducacionalRepositorio instancia;
    private Application application;

    private List<NivelEducacional> nivelEducacionalList;

    private SingleLiveEvent<String> responseMsgError = new SingleLiveEvent<>();

    private static final String TAG_NIVEL_EDUCACIONAL = "ListadoNivelEducacional";

    private NivelEducacionalRepositorio(Application application) {
        this.application = application;
    }

    public static NivelEducacionalRepositorio getInstancia(Application application) {

        if (instancia == null) {
            instancia = new NivelEducacionalRepositorio(application);
        }
        return instancia;
    }

    public SingleLiveEvent<String> getResponseMsgError() {
        return responseMsgError;
    }

    /**
     * Funcion encargada de consultar la lista de niveles educacionales
     *
     * @return MutableLivedata usado en viewModel
     */
    public MutableLiveData<List<NivelEducacional>> obtenerNivelesEducacionales() {
        MutableLiveData<List<NivelEducacional>> nivelEducMutableLiveData = new MutableLiveData<>();
        enviarGetNivelesEduc(nivelEducMutableLiveData);
        return nivelEducMutableLiveData;
    }

    /**
     * Funcion encargada de enviar la solicitud GET al servidor para obtener listado de niveles educacionales
     *
     * @param nivelEducMutableLiveData Lista vacia que será rellenada con lista de niveles educacionales
     */
    private void enviarGetNivelesEduc(final MutableLiveData<List<NivelEducacional>> nivelEducMutableLiveData) {

        nivelEducacionalList = new ArrayList<>();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                    for (int i = 0; i < jsonData.length(); i++) {
                        JSONObject jsonNivelEduc = jsonData.getJSONObject(i);

                        int id_nivel = jsonNivelEduc.getInt(application.getString(R.string.KEY_NIVEL_EDUC_ID));

                        JSONObject jsonAttributes = jsonNivelEduc.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));
                        String nombre_nivel = jsonAttributes.getString(application.getString(R.string.KEY_NIVEL_EDUC_NOMBRE));

                        NivelEducacional nivelEducacional = new NivelEducacional();
                        nivelEducacional.setId(id_nivel);
                        nivelEducacional.setNombre(nombre_nivel);

                        nivelEducacionalList.add(nivelEducacional);
                    }

                    nivelEducMutableLiveData.postValue(nivelEducacionalList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_NIVEL_EDUC), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgError.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_NIVEL_EDUC), application.getString(R.string.NETWORK_ERROR));
                    responseMsgError.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_NIVEL_EDUC), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_NIVEL_EDUC), String.format("%s %s", application.getString(R.string.SERVER_ERROR), errorObject));
                        responseMsgError.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                    }
                }
            }
        };

        String url = application.getString(R.string.URL_GET_NIVELES_EDUCACIONALES);

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

        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_NIVEL_EDUCACIONAL);
    }

    public NivelEducacional buscarNivelEducacionalPorNombre(String nombre) {

        for (int i = 0; i < nivelEducacionalList.size(); i++) {
            if (nivelEducacionalList.get(i).getNombre().equals(nombre)) {
                return nivelEducacionalList.get(i);
            }
        }
        return null;
    }

    public NivelEducacional buscarNivelEducacionalPorId(int id) {

        for (int i = 0; i < nivelEducacionalList.size(); i++) {
            if (nivelEducacionalList.get(i).getId() == id) {
                return nivelEducacionalList.get(i);
            }
        }
        return null;
    }
}
