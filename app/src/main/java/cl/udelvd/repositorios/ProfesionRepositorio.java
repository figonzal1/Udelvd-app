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
import cl.udelvd.modelo.Profesion;
import cl.udelvd.servicios.VolleySingleton;
import cl.udelvd.utilidades.SingleLiveEvent;

public class ProfesionRepositorio {

    private static ProfesionRepositorio instancia;
    private Application application;

    private List<Profesion> profesionsList = new ArrayList<>();

    private MutableLiveData<List<Profesion>> profesionMutableLiveData = new MutableLiveData<>();    //Instanciar aqui permite refresh

    private SingleLiveEvent<String> responseMsgError = new SingleLiveEvent<>();

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

    public SingleLiveEvent<String> getResponseMsgError() {
        return responseMsgError;
    }

    /**
     * Funcion encargada de consultar la lista de niveles educacionales
     *
     * @return MutableLivedata usado en viewModel
     */
    public MutableLiveData<List<Profesion>> obtenerNivelesEducacionales() {
        enviarGetProfesion();
        return profesionMutableLiveData;
    }

    /**
     * Funcion encargada de enviar la solicitud GET al servidor para obtener listado de niveles educacionales
     */
    private void enviarGetProfesion() {

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_PROFESION), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgError.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_PROFESION), application.getString(R.string.NETWORK_ERROR));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_PROFESION), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_PROFESION), String.format("%s %s", application.getString(R.string.SERVER_ERROR), errorObject));
                        responseMsgError.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                    }
                }
            }
        };

        String url = application.getString(R.string.URL_GET_PROFESIONES);

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


        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_PROFESION);
    }

    /**
     * Funcion para buscar profesion según parametro
     *
     * @param nombre Nombre de la prefesion a buscar
     * @return Profesion
     */
    public Profesion buscarProfesionPorNombre(String nombre) {

        for (int i = 0; i < profesionsList.size(); i++) {

            if (profesionsList.get(i).getNombre().equals(nombre)) {
                return profesionsList.get(i);
            }
        }
        return null;
    }

    /**
     * Funcion para buscar profesion según parametro
     *
     * @param id Id de la profesion a buscar
     * @return Profesion
     */
    public Profesion buscarProfesionPorId(int id) {

        for (int i = 0; i < profesionsList.size(); i++) {

            if (profesionsList.get(i).getId() == id) {
                return profesionsList.get(i);
            }
        }
        return null;
    }
}
