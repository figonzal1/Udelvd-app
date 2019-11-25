package cl.udelvd.repositorios;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cl.udelvd.model.Usuario;
import cl.udelvd.services.VolleySingleton;

public class UsuarioRepositorio {

    private static UsuarioRepositorio instancia;
    private Application application;
    private MutableLiveData<List<Usuario>> mutableUsuariosList = new MutableLiveData<>();
    private List<Usuario> usuarioList;

    private UsuarioRepositorio(Application application) {
        this.application = application;
    }

    public static UsuarioRepositorio getInstance(Application application) {

        if (instancia == null) {
            instancia = new UsuarioRepositorio(application);
        }
        return instancia;
    }

    //TODO: Terminar obtencion de listado de usuarios
    public MutableLiveData<List<Usuario>> getUsuarios() {
        sendGetUsuarios();
        return null;
    }

    private void sendGetUsuarios() {

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE", response);
            }
        };

        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //TODO: Finalizar error listener para obtener lista usuarios
                if (error.networkResponse != null && error.networkResponse.data != null) {

                    String json = new String(error.networkResponse.data);

                    Log.d("Json", json);

                    try {
                        JSONObject jsonObject = new JSONObject(json);

                        JSONObject jsonError = jsonObject.getJSONObject("error");

                        String detail_error = jsonError.getString("detail");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        String url = "https://udelvd-dev.herokuapp.com/usuarios";

        //Hacer request
        StringRequest request = new StringRequest(Request.Method.GET, url, responseListener,
                errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences sharedPreferences = application.getSharedPreferences("udelvd",
                        Context.MODE_PRIVATE);

                String token = sharedPreferences.getString("TOKEN_LOGIN", "");

                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };

        String TAG = "ListaUsuario";
        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG);
    }

}
