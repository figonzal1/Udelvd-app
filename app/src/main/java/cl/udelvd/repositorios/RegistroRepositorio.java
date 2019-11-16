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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import cl.udelvd.model.Investigador;
import cl.udelvd.services.VolleySingleton;

public class RegistroRepositorio {
    private static RegistroRepositorio instancia;
    private Application application;

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

    /**
     * @param investigador
     */
    public void insertInvestigador(Investigador investigador) {
        postRequest(investigador);
    }

    private void postRequest(Investigador investigador) {

        //Definicion de listener de respuesta
        final Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RESPONSE", String.valueOf(response));
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

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nombre", investigador.getNombre());
            jsonObject.put("apellido", investigador.getPassword());
            jsonObject.put("email", investigador.getEmail());
            jsonObject.put("password", investigador.getPassword());
            jsonObject.put("id_rol", 0);
            jsonObject.put("activado", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        //Hacer peticion post
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                responseListener, errorListener) {

        };

        String TAG = "RegistroInvestigador";
        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG);
    }


}
