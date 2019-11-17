package cl.udelvd.repositorios;

import android.app.Application;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cl.udelvd.model.Investigador;
import cl.udelvd.services.VolleySingleton;

public class RegistroRepositorio {
    private static RegistroRepositorio instancia;
    private Application application;

    private MutableLiveData<String> responseMsg = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg;

    private RegistroRepositorio(Application application) {
        this.application = application;
    }

    /**
     * Funcion singleton que instancia el repositorio
     *
     * @return Instancia
     */
    public static RegistroRepositorio getInstance(Application application) {

        if (instancia == null) {
            instancia = new RegistroRepositorio(application);
        }
        return instancia;
    }

    public MutableLiveData<String> getErrorMsg() {
        return errorMsg;
    }

    public MutableLiveData<String> getResponseMsg() {
        return responseMsg;
    }

    /**
     * @param investigador
     */
    public void insertInvestigador(Investigador investigador) {
        postRequest(investigador);
    }

    private void postRequest(final Investigador investigador) {

        //Definicion de listener de respuesta
        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject =
                            new JSONObject(response).getJSONObject("data").getJSONObject(
                                    "attributes");

                    Investigador invResponse = new Investigador();
                    invResponse.setNombre(jsonObject.getString("nombre"));
                    invResponse.setApellido(jsonObject.getString("apellido"));
                    invResponse.setEmail(jsonObject.getString("email"));
                    invResponse.setIdRol(jsonObject.getInt("id_rol"));

                    if (jsonObject.getInt("activado") == 0) {
                        invResponse.setActivado(false);
                    } else if (jsonObject.getInt("activdo") == 1) {
                        invResponse.setActivado(true);
                    }

                    Log.d("MEMORIA", investigador.toString());
                    Log.d("INTERNET", invResponse.toString());

                    if (investigador.equals(invResponse)) {
                        responseMsg.postValue("¡Estas registrado!");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        //Definicion de listener de error
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse != null && error.networkResponse.data != null) {

                    errorMsg = new MutableLiveData<>();
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
                        Log.d("VOLLEY_ERROR", "AUTHENTICATION_ERROR: " + errorObject);
                        errorMsg.postValue("Acceso no autorizado");
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d("VOLLEY_ERROR", "SERVER_ERROR: " + errorObject);
                        errorMsg.postValue("Servidor no responde, intente más tarde");
                    }

                    //Error de timeout
                    else if (error instanceof TimeoutError) {
                        Log.d("VOLLEY_ERROR", "TIMEOUT_ERROR: " + errorObject);
                        errorMsg.postValue("Servidor no responde, intente más tarde");
                    }

                    //Error de conexion a internet
                    else if (error instanceof NetworkError) {
                        Log.d("VOLLEY_ERROR", "NETWORK_ERROR: " + errorObject);
                        errorMsg.postValue("No tienes conexión a Internet");
                    }
                }
            }
        };


        String url = "http://192.168.0.14/investigadores";


        //Hacer peticion post
        StringRequest request = new StringRequest(Request.Method.POST, url,
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("nombre", investigador.getNombre());
                params.put("apellido", investigador.getApellido());
                params.put("email", investigador.getEmail());
                params.put("password", investigador.getPassword());
                params.put("id_rol", String.valueOf(investigador.getIdRol()));

                Log.d("PARAMS_INVESTIGADOR", params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        String TAG = "RegistroInvestigador";
        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG);
    }


}
