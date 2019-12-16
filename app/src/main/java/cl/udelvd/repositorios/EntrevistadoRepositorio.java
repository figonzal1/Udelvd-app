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

import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.servicios.VolleySingleton;
import cl.udelvd.utilidades.SingleLiveEvent;

public class EntrevistadoRepositorio {

    private static EntrevistadoRepositorio instancia;
    private final Application application;

    private List<Entrevistado> entrevistadoList;

    private SingleLiveEvent<String> errorMsg;

    private EntrevistadoRepositorio(Application application) {
        this.application = application;
    }

    public static EntrevistadoRepositorio getInstance(Application application) {

        if (instancia == null) {
            instancia = new EntrevistadoRepositorio(application);
        }
        return instancia;
    }

    public SingleLiveEvent<String> getErrorMsg() {
        if (errorMsg == null) {
            errorMsg = new SingleLiveEvent<>();
        }
        return errorMsg;
    }

    /**
     * Funcion encargada de buscar usuarios y enviar listado a ViewModel
     *
     * @return MutableLiveData con listado de usuarios
     */
    public MutableLiveData<List<Entrevistado>> getUsuarios() {
        MutableLiveData<List<Entrevistado>> entrevistadosMutableLiveData = new MutableLiveData<>();
        sendGetUsuarios(entrevistadosMutableLiveData);
        return entrevistadosMutableLiveData;
    }

    /**
     * Funcion que realizar solicitud GET para obtener listado de usuarios
     *
     * @param entrevistadosMutableLiveData Lista mutable vacia rellenada con lista de entrevistados
     */
    private void sendGetUsuarios(final MutableLiveData<List<Entrevistado>> entrevistadosMutableLiveData) {

        entrevistadoList = new ArrayList<>();

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray("data");

                    for (int i = 0; i < jsonData.length(); i++) {

                        JSONObject jsonUsuario = jsonData.getJSONObject(i);
                        JSONObject jsonUsuarioAttributes = jsonUsuario.getJSONObject("attributes");

                        Entrevistado entrevistado = new Entrevistado();
                        entrevistado.setId(jsonUsuario.getInt("id"));
                        entrevistado.setNombre(jsonUsuarioAttributes.getString("nombre"));
                        entrevistado.setApellido(jsonUsuarioAttributes.getString("apellido"));
                        entrevistado.setSexo(jsonUsuarioAttributes.getString("sexo"));

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.US);
                        entrevistado.setFechaNacimiento(simpleDateFormat.parse(jsonUsuarioAttributes.getString("fecha_nacimiento")));

                        entrevistado.setIdCiudad(jsonUsuarioAttributes.getInt("id_ciudad"));

                        //Jubilado Legal
                        if (jsonUsuarioAttributes.getInt("jubilado_legal") == 1) {
                            entrevistado.setJubiladoLegal(true);
                        } else {
                            entrevistado.setJubiladoLegal(false);
                        }

                        //Caidas
                        if (jsonUsuarioAttributes.getInt("caidas") == 1) {
                            entrevistado.setCaidas(true);
                            entrevistado.setnCaidas(jsonUsuarioAttributes.getInt("n_caidas"));
                        } else {
                            entrevistado.setCaidas(false);
                        }

                        entrevistado.setnConvivientes3Meses(jsonUsuarioAttributes.getInt("n_convivientes_3_meses"));

                        //Foraneas
                        entrevistado.setIdInvestigador(jsonUsuarioAttributes.getInt("id_investigador"));
                        entrevistado.setIdEstadoCivil(jsonUsuarioAttributes.getInt("id_estado_civil"));

                        //Foraneas opcionales
                        if (jsonUsuarioAttributes.has("id_nivel_educacional") && !jsonUsuarioAttributes.isNull("id_nivel_educacional")) {
                            entrevistado.setIdNivelEducacional(jsonUsuarioAttributes.getInt("id_nivel_educacional"));
                        }
                        if (jsonUsuarioAttributes.has("id_conviviente") && !jsonUsuarioAttributes.isNull("id_conviviente")) {
                            entrevistado.setIdConviviente(jsonUsuarioAttributes.getInt("id_conviviente"));
                        }
                        if (jsonUsuarioAttributes.has("id_profesion") && !jsonUsuarioAttributes.isNull("id_profesion")) {
                            entrevistado.setIdProfesion(jsonUsuarioAttributes.getInt("id_profesion"));
                        }

                        entrevistadoList.add(entrevistado);
                    }

                    entrevistadosMutableLiveData.postValue(entrevistadoList);


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
            public Map<String, String> getHeaders() {
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
