package cl.udelvd.repositorios;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
import cl.udelvd.utils.SingleLiveEvent;

public class InvestigadorRepositorio {
    private static InvestigadorRepositorio instancia;
    private Application application;

    private SingleLiveEvent<Map<String, Object>> responseMsgLogin = new SingleLiveEvent<>();
    private SingleLiveEvent<String> responseMsgRegistro = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorMsg = new SingleLiveEvent<>();

    private InvestigadorRepositorio(Application application) {
        this.application = application;
    }

    /**
     * Funcion singleton que instancia el repositorio
     *
     * @return Instancia
     */
    public static InvestigadorRepositorio getInstance(Application application) {

        if (instancia == null) {
            instancia = new InvestigadorRepositorio(application);
        }
        return instancia;
    }

    public SingleLiveEvent<String> getErrorMsg() {
        return errorMsg;
    }

    public SingleLiveEvent<Map<String, Object>> getResponseMsgLogin() {
        return responseMsgLogin;
    }

    public SingleLiveEvent<String> getResponseMsgRegistro() {
        return responseMsgRegistro;
    }

    /**
     * Funcion encargada de insertar investigador en BD
     *
     * @param investigador
     */
    public void registrarInvestigador(Investigador investigador) {
        enviarPostRegistro(investigador);
    }

    /**
     * Funcion encargada de realizar el login del investigador
     *
     * @param investigador
     */
    public void loginInvestigador(Investigador investigador, Context context) {
        enviarPostLogin(investigador, context);
    }

    /**
     * Funcion encargada de enviar peticion POST para login
     *
     * @param investigador Datos del investigador enviados via POST
     * @param context      Contexto de la app para utilizar recursos
     */
    private void enviarPostLogin(final Investigador investigador, final Context context) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE", response);

                JSONObject jsonObjectLogin, jsonObjectData;
                try {
                    jsonObjectLogin = new JSONObject(response).getJSONObject("login");
                    jsonObjectData = new JSONObject(response).getJSONObject("data");


                    String status = jsonObjectLogin.getString("status");

                    if (status.equals("Correct")) {

                        String token = jsonObjectLogin.getString("token");
                        Log.d("TOKEN_LOGIN", token);

                        //Guardar token
                        SharedPreferences sharedPreferences = context.getSharedPreferences(
                                "udelvd", Context.MODE_PRIVATE);

                        //Guardar token en shared pref
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("TOKEN_LOGIN", token);
                        editor.apply();

                        //Obtener datos investigador post login
                        Investigador investigador = new Investigador();

                        investigador.setId(jsonObjectData.getInt("id"));

                        JSONObject jsonObjectAttributes = jsonObjectData.getJSONObject(
                                "attributes");
                        investigador.setEmail(jsonObjectAttributes.getString("email"));
                        investigador.setNombre(jsonObjectAttributes.getString("nombre"));
                        investigador.setApellido(jsonObjectAttributes.getString("apellido"));

                        if (jsonObjectAttributes.getInt("activado") == 0) {
                            investigador.setActivado(false);
                        } else if (jsonObjectAttributes.getInt("activado") == 1) {
                            investigador.setActivado(true);
                        }

                        investigador.setIdRol(jsonObjectAttributes.getInt("id_rol"));
                        investigador.setNombreRol(jsonObjectAttributes.getString("nombre_rol"));

                        //Enviar investigador y mensaje para toast
                        Map<String, Object> result = new HashMap<>();
                        result.put("investigador", investigador);
                        result.put("mensaje_login", "¡Bienvenido!");

                        responseMsgLogin.postValue(result);
                    }
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
                    errorMsg.postValue("Servidor no responde, intente más tarde");
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d("VOLLEY_ERROR_LOGIN", "NETWORK_ERROR");
                    errorMsg.postValue("No tienes conexión a Internet");
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

                        try {
                            assert errorObject != null;

                            if (errorObject.get("detail").equals("Please check your credentials")) {
                                errorMsg.postValue("Login fallido, revisa tus datos");
                            } else {
                                errorMsg.postValue("Acceso no autorizado");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d("VOLLEY_ERROR_LOGIN", "SERVER_ERROR: " + errorObject);

                        try {
                            assert errorObject != null;

                            //Si el error es de email no existente
                            if (errorObject.get("detail").equals("Email does not exist")) {
                                errorMsg.postValue("Email no registrado");
                            } else {
                                errorMsg.postValue("Servidor no responde, intente más tarde");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }
            }
        };

        //String url = "https://udelvd-dev.herokuapp.com/investigadores/login";
        String url = "http://192.168.0.14/investigadores/login";

        StringRequest request = new StringRequest(Request.Method.POST, url, responseListener,
                errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", investigador.getEmail());
                params.put("password", investigador.getPassword());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        String TAG = "LoginInvestigador";
        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG);

    }

    /**
     * Funcion encargada de enviar peticion POST para registro investigador
     *
     * @param investigador
     */
    private void enviarPostRegistro(final Investigador investigador) {

        //Definicion de listener de respuesta
        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject =
                            new JSONObject(response).getJSONObject("data").getJSONObject(
                                    "attributes");

                    //Comparar respuesta server con objeto enviado
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
                        responseMsgRegistro.postValue("¡Estas registrado!");
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

                if (error instanceof TimeoutError) {
                    Log.d("VOLLEY_ERROR", "TIMEOUT_ERROR");
                    errorMsg.postValue("Servidor no responde, intente más tarde");
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d("VOLLEY_ERROR", "NETWORK_ERROR");
                    errorMsg.postValue("No tienes conexión a Internet");
                }

                //Errores cuandoel servidor si response
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

                        try {

                            assert errorObject != null;
                            //Si el error es de mail repetido, postear error msg
                            if (errorObject.get("detail").equals("Email already exists")) {
                                errorMsg.postValue("Email ya registrado");
                            } else {
                                errorMsg.postValue("Servidor no responde, intente más tarde");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        };


        //String url = "https://udelvd-dev.herokuapp.com/investigadores";
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
