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

import cl.udelvd.modelo.Investigador;
import cl.udelvd.servicios.VolleySingleton;
import cl.udelvd.utilidades.SingleLiveEvent;

public class InvestigadorRepositorio {
    private static InvestigadorRepositorio instancia;
    private final Application application;

    private final SingleLiveEvent<Map<String, Object>> responseMsgLogin = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgRegistro = new SingleLiveEvent<>();
    private final SingleLiveEvent<Map<String, Object>> responseMsgActualizacion = new SingleLiveEvent<>();

    private final SingleLiveEvent<String> errorMsg = new SingleLiveEvent<>();

    private static final String TAG_INVESTIGADOR_REGISTRO = "RegistroInvestigador";
    private static final String TAG_INVESTIGADOR_LOGIN = "LoginInvestigador";
    private static final String TAG_INVESTIGADOR_ACTUALIZACION = "ActualizacionInvestigador";

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

    public SingleLiveEvent<Map<String, Object>> getResponseMsgActualizacion() {
        return responseMsgActualizacion;
    }

    /**
     * Funcion encargada de insertar investigador en BD
     *
     * @param investigador Objecto investigador a registrar
     */
    public void registrarInvestigador(Investigador investigador) {
        enviarPostRegistro(investigador);
    }

    /**
     * Funcion encargada de enviar peticion POST para registro investigador
     *
     * @param investigador Objeto investigador que hara registro en sistema
     */
    private void enviarPostRegistro(final Investigador investigador) {

        //Definicion de listener de respuesta
        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d("RESPONSE",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject jsonData = jsonObject.getJSONObject("data");
                    JSONObject jsonAttributes = jsonData.getJSONObject("attributes");

                    //Obtener json desde respuesta
                    Investigador invResponse = new Investigador();
                    invResponse.setNombre(jsonAttributes.getString("nombre"));
                    invResponse.setApellido(jsonAttributes.getString("apellido"));
                    invResponse.setEmail(jsonAttributes.getString("email"));

                    //Obtener relacion Rol de investigador
                    JSONObject jsonObjectRolData = jsonData
                            .getJSONObject("relationships")
                            .getJSONObject("rol")
                            .getJSONObject("data");

                    invResponse.setNombreRol(jsonObjectRolData.getString("nombre"));

                    if (jsonAttributes.getInt("activado") == 0) {
                        invResponse.setActivado(false);
                    } else if (jsonAttributes.getInt("activado") == 1) {
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
                    Log.d("VOLLEY_ERR_REGIS_INVEST", "TIMEOUT_ERROR");
                    errorMsg.postValue("Servidor no responde, intente más tarde");
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d("VOLLEY_ERR_REGIS_INVEST", "NETWORK_ERROR");
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
                        Log.d("VOLLEY_ERR_REGIS_INVEST", "AUTHENTICATION_ERROR: " + errorObject);
                        errorMsg.postValue("Acceso no autorizado");
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d("VOLLEY_ERR_REGIS_INVEST", "SERVER_ERROR: " + errorObject);

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
                params.put("nombre_rol", investigador.getNombreRol());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_INVESTIGADOR_REGISTRO);
    }

    /**
     * Funcion encargada de realizar el login del investigador
     *
     * @param investigador Objeto investigador que hará login en sistema
     */
    public void loginInvestigador(Investigador investigador) {
        enviarPostLogin(investigador);
    }

    /**
     * Funcion encargada de enviar peticion POST para login
     *
     * @param investigadorForm Datos del investigador enviados via POST
     */
    private void enviarPostLogin(final Investigador investigadorForm) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE", response);

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject jsonData = jsonObject.getJSONObject("data");
                    JSONObject jsonLogin = jsonObject.getJSONObject("login");

                    String status = jsonLogin.getString("status");

                    if (status.equals("Correct")) {

                        String token = jsonLogin.getString("token");
                        Log.d("TOKEN_LOGIN", token);

                        //Guardar token
                        SharedPreferences sharedPreferences = application.getSharedPreferences(
                                "udelvd", Context.MODE_PRIVATE);

                        //Guardar token en shared pref
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("TOKEN_LOGIN", token);
                        editor.apply();

                        //Obtener datos investigador post login
                        Investigador invResponse = new Investigador();
                        invResponse.setId(jsonData.getInt("id"));

                        JSONObject jsonAttributes = jsonData.getJSONObject(
                                "attributes");
                        invResponse.setEmail(jsonAttributes.getString("email"));
                        invResponse.setNombre(jsonAttributes.getString("nombre"));
                        invResponse.setApellido(jsonAttributes.getString("apellido"));
                        invResponse.setCreateTime(jsonAttributes.getString("create_time"));

                        if (jsonAttributes.getInt("activado") == 0) {
                            invResponse.setActivado(false);
                        } else if (jsonAttributes.getInt("activado") == 1) {
                            invResponse.setActivado(true);
                        }

                        invResponse.setIdRol(jsonAttributes.getInt("id_rol"));

                        //Buscar en JSON nombre del rol
                        JSONObject jsonObjectRolData = jsonData.getJSONObject(
                                "relationships").getJSONObject("rol").getJSONObject("data");
                        invResponse.setNombreRol(jsonObjectRolData.getString("nombre"));

                        //Enviar investigador y mensaje para toast
                        Map<String, Object> result = new HashMap<>();
                        result.put("investigador", invResponse);
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
                params.put("email", investigadorForm.getEmail());
                params.put("password", investigadorForm.getPassword());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_INVESTIGADOR_LOGIN);

    }

    /**
     * Funcion encargada de actualizar los datos del investigador
     *
     * @param investigador Objeto investigador
     */
    public void actualizarInvestigador(Investigador investigador) {
        enviarPutActualizacion(investigador);
    }

    /**
     * Funcion encargada de enviar actualización al servidor mediante metodo PUT
     *
     * @param investigadorForm Objeto investigador a ser enviado
     */
    private void enviarPutActualizacion(final Investigador investigadorForm) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d("RESPONSE", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject jsonData = jsonObject.getJSONObject("data");


                    //Investigador actualizado
                    Investigador invResponse = new Investigador();
                    invResponse.setId(jsonData.getInt("id"));

                    JSONObject jsonAttributes = jsonData.getJSONObject("attributes");

                    invResponse.setEmail(jsonAttributes.getString("email"));
                    invResponse.setNombre(jsonAttributes.getString("nombre"));
                    invResponse.setApellido(jsonAttributes.getString("apellido"));
                    invResponse.setIdRol(jsonAttributes.getInt("id_rol"));

                    if (jsonAttributes.getInt("activado") == 0) {
                        invResponse.setActivado(false);
                    } else if (jsonAttributes.getInt("activado") == 1) {
                        invResponse.setActivado(true);
                    }

                    //Buscar en JSON nombre del rol
                    JSONObject jsonObjectRolData = jsonData.getJSONObject(
                            "relationships").getJSONObject("rol").getJSONObject("data");
                    invResponse.setNombreRol(jsonObjectRolData.getString("nombre"));

                    String update_time = jsonAttributes.getString("update_time");

                    Log.d("MEMORIA", investigadorForm.toString());
                    Log.d("INTERNET", invResponse.toString());

                    if (investigadorForm.equals(invResponse) && !update_time.isEmpty()) {

                        //Enviar investigador y mensaje para toast
                        Map<String, Object> result = new HashMap<>();
                        result.put("investigador", invResponse);
                        result.put("mensaje_update", "¡Datos actualizados!");

                        Log.d("UPDATE_STATUS", update_time);
                        responseMsgActualizacion.postValue(result);
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
                    Log.d("VOLLEY_ERR_INV_UPDATE", "TIMEOUT_ERROR");
                    errorMsg.postValue("Servidor no responde, intente más tarde");
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d("VOLLEY_ERR_INV_UPDATE", "NETWORK_ERROR");
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
                        Log.d("VOLLEY_ERR_INV_UPDATE", "AUTHENTICATION_ERROR: " + errorObject);
                        errorMsg.postValue("Acceso no autorizado");
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d("VOLLEY_ERR_INV_UPDATE", "SERVER_ERROR: " + errorObject);
                    }
                }
            }
        };


        String url = "http://192.168.0.14/investigadores/" + investigadorForm.getId();

        StringRequest request = new StringRequest(Request.Method.PUT, url, responseListener, errorListener) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put("nombre", investigadorForm.getNombre());
                params.put("apellido", investigadorForm.getApellido());
                params.put("email", investigadorForm.getEmail());
                params.put("password", investigadorForm.getPassword());
                params.put("id_rol", String.valueOf(investigadorForm.getIdRol()));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {

                SharedPreferences sharedPreferences = application.getSharedPreferences("udelvd", Context.MODE_PRIVATE);

                String token = sharedPreferences.getString("TOKEN_LOGIN", "");

                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };

        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_INVESTIGADOR_ACTUALIZACION);
    }
}
