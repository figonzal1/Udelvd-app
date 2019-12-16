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

import cl.udelvd.modelo.EstadoCivil;
import cl.udelvd.servicios.VolleySingleton;

public class EstadoCivilRepositorio {

    private static EstadoCivilRepositorio instancia;
    private Application application;

    private List<EstadoCivil> estadoCivilList;

    /**
     * Constructor de clase
     *
     * @param application
     */
    private EstadoCivilRepositorio(Application application) {
        this.application = application;
    }

    /**
     * Clase singleton para creacion de repositorio
     *
     * @param application Contexto de uso
     * @return Instancia de clase
     */
    public static EstadoCivilRepositorio getInstance(Application application) {
        if (instancia == null) {
            instancia = new EstadoCivilRepositorio(application);
        }
        return instancia;
    }

    /**
     * Funcion encargada de consultar la lista de estados civiles
     *
     * @return MutableLivedata usado en viewModel
     */
    public MutableLiveData<List<EstadoCivil>> obtenerEstadosCiviles() {

        MutableLiveData<List<EstadoCivil>> mutableEstadosCiviles = new MutableLiveData<>();
        enviarGetEstadosCiviles(mutableEstadosCiviles);
        return mutableEstadosCiviles;
    }

    /**
     * Funcion encargada de enviar la solicitud GET al servidor para obtener listado de estados civiles
     *
     * @param mutableEstadosCiviles
     */
    private void enviarGetEstadosCiviles(final MutableLiveData<List<EstadoCivil>> mutableEstadosCiviles) {

        estadoCivilList = new ArrayList<>();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray("data");

                    for (int i = 0; i < jsonData.length(); i++) {
                        JSONObject jsonEstadoCivil = jsonData.getJSONObject(i);

                        int id_estado_vivil = jsonEstadoCivil.getInt("id");

                        JSONObject jsonAttributes = jsonEstadoCivil.getJSONObject("attributes");
                        String nombre_estado_civil = jsonAttributes.getString("nombre");

                        EstadoCivil estadoCivil = new EstadoCivil();
                        estadoCivil.setId(id_estado_vivil);
                        estadoCivil.setNombre(nombre_estado_civil);

                        estadoCivilList.add(estadoCivil);
                    }

                    mutableEstadosCiviles.postValue(estadoCivilList);

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


        String url = "http://192.168.0.14/estadosCiviles";

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

    /**
     * Funcion para buscar estado civil
     *
     * @param nombre Nombre del estado civil a buscar
     * @return Objeto estado civil
     */
    public EstadoCivil buscarEstadoCivil(String nombre) {

        for (int i = 0; i < estadoCivilList.size(); i++) {
            EstadoCivil estadoCivil = estadoCivilList.get(i);

            if (estadoCivil.getNombre().equals(nombre)) {
                return estadoCivil;
            }
        }
        return null;
    }

}
