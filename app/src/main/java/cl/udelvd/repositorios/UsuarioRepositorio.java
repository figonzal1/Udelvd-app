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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cl.udelvd.model.Usuario;
import cl.udelvd.services.VolleySingleton;
import cl.udelvd.utils.SingleLiveEvent;

public class UsuarioRepositorio {

    private static UsuarioRepositorio instancia;
    private Application application;

    private List<Usuario> usuarioList;
    private MutableLiveData<List<Usuario>> mutableUsuariosList = new MutableLiveData<>();

    private SingleLiveEvent<String> errorMsg = new SingleLiveEvent<>();

    private UsuarioRepositorio(Application application) {
        this.application = application;
    }

    public static UsuarioRepositorio getInstance(Application application) {

        if (instancia == null) {
            instancia = new UsuarioRepositorio(application);
        }
        return instancia;
    }

    public SingleLiveEvent<String> getErrorMsg() {
        return errorMsg;
    }

    /**
     * Funcion encargada de buscar usuarios y enviar listado a ViewModel
     *
     * @return MutableLiveData con listado de ussuarios
     */
    public MutableLiveData<List<Usuario>> getUsuarios() {
        sendGetUsuarios();
        return mutableUsuariosList;
    }

    /**
     * Funcion que realizar solicitud GET para obtener listado de usuarios
     */
    private void sendGetUsuarios() {

        usuarioList = new ArrayList<>();

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray("data");

                    for (int i = 0; i < jsonData.length(); i++) {

                        JSONObject jsonUsuario = jsonData.getJSONObject(i);
                        JSONObject jsonUsuarioAttributes = jsonUsuario.getJSONObject("attributes");

                        Usuario usuario = new Usuario();
                        usuario.setId(jsonUsuario.getInt("id"));
                        usuario.setNombre(jsonUsuarioAttributes.getString("nombre"));
                        usuario.setApellido(jsonUsuarioAttributes.getString("apellido"));
                        usuario.setSexo(jsonUsuarioAttributes.getString("sexo"));

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.US);
                        usuario.setFechaNacimiento(simpleDateFormat.parse(jsonUsuarioAttributes.getString("fecha_nacimiento")));

                        usuario.setCiudad(jsonUsuarioAttributes.getString("ciudad"));

                        //Jubilado Legal
                        if (jsonUsuarioAttributes.getInt("jubilado_legal") == 1) {
                            usuario.setJubiladoLegal(true);
                        } else {
                            usuario.setJubiladoLegal(false);
                        }

                        //Caidas
                        if (jsonUsuarioAttributes.getInt("caidas") == 1) {
                            usuario.setCaidas(true);
                            usuario.setnCaidas(jsonUsuarioAttributes.getInt("n_caidas"));
                        } else {
                            usuario.setCaidas(false);
                        }

                        usuario.setnConvivientes3Meses(jsonUsuarioAttributes.getInt("n_convivientes_3_meses"));

                        //Foraneas
                        usuario.setIdInvestigador(jsonUsuarioAttributes.getInt("id_investigador"));
                        usuario.setIdEstadoCivil(jsonUsuarioAttributes.getInt("id_estado_civil"));

                        //Foraneas opcionales
                        if (jsonUsuarioAttributes.has("id_nivel_educacional") && !jsonUsuarioAttributes.isNull("id_nivel_educacional")) {
                            usuario.setIdNivelEducacional(jsonUsuarioAttributes.getInt("id_nivel_educacional"));
                        }
                        if (jsonUsuarioAttributes.has("id_conviviente") && !jsonUsuarioAttributes.isNull("id_conviviente")) {
                            usuario.setIdConviviente(jsonUsuarioAttributes.getInt("id_conviviente"));
                        }
                        if (jsonUsuarioAttributes.has("id_profesion") && !jsonUsuarioAttributes.isNull("id_profesion")) {
                            usuario.setIdProfesion(jsonUsuarioAttributes.getInt("id_profesion"));
                        }

                        usuarioList.add(usuario);
                    }

                    mutableUsuariosList.postValue(usuarioList);


                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        };

        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Log.d("VOLLEY_ERROR", "TIMEOUT_ERROR");
                    errorMsg.postValue("Servidor no responde, intente más tarde");
                } else if (error instanceof NetworkError) {
                    Log.d("VOLLEY_ERROR", "NETWORK_ERROR");
                    errorMsg.postValue("No tienes conexión a Internet");
                }

                //Errores cuando el servidor si response
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
                        Log.d("VOLLEY_ERROR", "AUTHENTICATION_ERROR: " + errorObject);
                        errorMsg.postValue("Acceso no autorizado");
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d("VOLLEY_ERROR", "SERVER_ERROR: " + errorObject);
                    }
                }
            }
        };

        //String url = "https://udelvd-dev.herokuapp.com/entrevistados";
        String url = "http://192.168.0.14/entrevistados";

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
