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
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cl.udelvd.R;
import cl.udelvd.modelo.Profesion;
import cl.udelvd.servicios.VolleySingleton;
import cl.udelvd.utilidades.SSLConection;
import cl.udelvd.utilidades.SingleLiveEvent;

public class ProfesionRepositorio {

    private static ProfesionRepositorio instancia;
    private final Application application;

    //LISTADO
    private final List<Profesion> profesionsList = new ArrayList<>();
    private final MutableLiveData<List<Profesion>> profesionMutableLiveData = new MutableLiveData<>();    //Instanciar aqui permite refresh
    private final SingleLiveEvent<String> responseMsgErrorListado = new SingleLiveEvent<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private static final String TAG_PROFESION = "ListadoProfesion";

    private ProfesionRepositorio(Application application) {
        this.application = application;
    }

    public static ProfesionRepositorio getInstancia(Application application) {
        if (instancia == null) {
            instancia = new ProfesionRepositorio(application);
        }
        return instancia;
    }

    /*
    LISTADO DE PROFESIONES
     */
    public MutableLiveData<List<Profesion>> obtenerNivelesEducacionales() {
        enviarGetProfesion();
        return profesionMutableLiveData;
    }

    private void enviarGetProfesion() {

        profesionsList.clear();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                    for (int i = 0; i < jsonData.length(); i++) {
                        JSONObject jsonNivelEduc = jsonData.getJSONObject(i);

                        int id_nivel = jsonNivelEduc.getInt(application.getString(R.string.KEY_PROFESION_ID));

                        JSONObject jsonAttributes = jsonNivelEduc.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));
                        String nombre_nivel = jsonAttributes.getString(application.getString(R.string.KEY_PROFESION_NOMBRE));

                        Profesion profesion = new Profesion();
                        profesion.setId(id_nivel);
                        profesion.setNombre(nombre_nivel);

                        profesionsList.add(profesion);
                    }

                    profesionMutableLiveData.postValue(profesionsList);

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
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_PROFESION), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgErrorListado.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_PROFESION), application.getString(R.string.NETWORK_ERROR));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_PROFESION), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_PROFESION), String.format("%s %s", application.getString(R.string.SERVER_ERROR), errorObject));
                        responseMsgErrorListado.postValue(application.getString(R.string.SERVER_ERROR_MSG_VM));
                    }
                }
            }
        };

        String url = String.format(application.getString(R.string.URL_GET_PROFESIONES), application.getString(R.string.HEROKU_DOMAIN));

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

        VolleySingleton.getInstance(application).addToRequestQueue(request,
                new HurlStack(null, SSLConection.getSocketFactory(application.getApplicationContext())));
        //VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_PROFESION);
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
