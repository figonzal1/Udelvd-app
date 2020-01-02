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

import cl.udelvd.modelo.NivelEducacional;
import cl.udelvd.servicios.VolleySingleton;

public class NivelEducacionalRepositorio {

    private static NivelEducacionalRepositorio instancia;
    private Application application;

    private List<NivelEducacional> nivelEducacionalList;

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

                    JSONArray jsonData = jsonObject.getJSONArray("data");

                    for (int i = 0; i < jsonData.length(); i++) {
                        JSONObject jsonNivelEduc = jsonData.getJSONObject(i);

                        int id_nivel = jsonNivelEduc.getInt("id");

                        JSONObject jsonAttributes = jsonNivelEduc.getJSONObject("attributes");
                        String nombre_nivel = jsonAttributes.getString("nombre");

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
                    Log.d("VOLLEY_ERR_NIVEL_EDUC", "TIMEOUT_ERROR");
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d("VOLLEY_ERR_NIVEL_EDUC", "NETWORK_ERROR");
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
                        Log.d("VOLLEY_ERR_NIVEL_EDUC", "AUTHENTICATION_ERROR: " + errorObject);
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d("VOLLEY_ERR_NIVEL_EDUC", "SERVER_ERROR: " + errorObject);
                    }
                }
            }
        };

        String url = "http://192.168.0.14/nivelesEducacionales";

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
