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

import cl.udelvd.R;
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

                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));
                    JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    //Obtener json desde respuesta
                    Investigador invResponse = new Investigador();
                    invResponse.setNombre(jsonAttributes.getString(application.getString(R.string.KEY_INVES_NOMBRE)));
                    invResponse.setApellido(jsonAttributes.getString(application.getString(R.string.KEY_INVES_APELLIDO)));
                    invResponse.setEmail(jsonAttributes.getString(application.getString(R.string.KEY_INVES_EMAIL)));

                    //Obtener relacion Rol de investigador
                    JSONObject jsonObjectRolData = jsonData
                            .getJSONObject(application.getString(R.string.JSON_RELATIONSHIPS))
                            .getJSONObject(application.getString(R.string.KEY_ROL_OBJECT))
                            .getJSONObject(application.getString(R.string.JSON_DATA));

                    invResponse.setNombreRol(jsonObjectRolData.getString(application.getString(R.string.KEY_ROL_NOMBRE)));

                    if (jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ACTIVADO)) == 0) {
                        invResponse.setActivado(false);
                    } else if (jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ACTIVADO)) == 1) {
                        invResponse.setActivado(true);
                    }

                    Log.d("MEMORIA", investigador.toString());
                    Log.d("INTERNET", invResponse.toString());

                    if (investigador.equals(invResponse)) {
                        responseMsgRegistro.postValue(application.getString(R.string.MSG_INVEST_REGISTRADO));
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
                    Log.d(application.getString(R.string.TAG_VOLLEY_INVES_REGISTRO), application.getString(R.string.TIMEOUT_ERROR));
                    errorMsg.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_INVES_REGISTRO), application.getString(R.string.NETWORK_ERROR));
                    errorMsg.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
                }

                //Errores cuandoel servidor si response
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_INVES_REGISTRO), application.getString(R.string.AUTHENTICATION_ERROR) + errorObject);
                        errorMsg.postValue(application.getString(R.string.AUTHENTICATION_ERROR_MSG_VM));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_INVES_REGISTRO), application.getString(R.string.SERVER_ERROR) + errorObject);

                        try {

                            assert errorObject != null;
                            //Si el error es de mail repetido, postear error msg
                            if (errorObject.get(application.getString(R.string.JSON_ERROR_DETAIL)).equals(application.getString(R.string.SERVER_ERROR_REGISTRO_MSG))) {
                                errorMsg.postValue(application.getString(R.string.SERVER_ERROR_REGISTRO_MSG_VM));
                            } else {
                                errorMsg.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        };


        String url = application.getString(R.string.URL_GET_INVESTIGADORES);

        //Hacer peticion post
        StringRequest request = new StringRequest(Request.Method.POST, url,
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put(application.getString(R.string.KEY_INVES_NOMBRE), investigador.getNombre());
                params.put(application.getString(R.string.KEY_INVES_APELLIDO), investigador.getApellido());
                params.put(application.getString(R.string.KEY_INVES_EMAIL), investigador.getEmail());
                params.put(application.getString(R.string.KEY_INVES_PASSWORD), investigador.getPassword());
                params.put(application.getString(R.string.KEY_INVES_NOMBRE_ROL), investigador.getNombreRol());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.JSON_CONTENT_TYPE), application.getString(R.string.JSON_CONTENT_TYPE_MSG));
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

                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));
                    JSONObject jsonLogin = jsonObject.getJSONObject(application.getString(R.string.JSON_LOGIN));

                    String status = jsonLogin.getString(application.getString(R.string.JSON_LOGIN_STATUS));

                    if (status.equals(application.getString(R.string.JSON_LOGIN_STATUS_RESPONSE))) {

                        String token = jsonLogin.getString(application.getString(R.string.JSON_TOKEN));
                        Log.d(application.getString(R.string.TAG_TOKEN_LOGIN), token);

                        //Guardar token
                        SharedPreferences sharedPreferences = application.getSharedPreferences(
                                application.getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);

                        //Guardar token en shared pref
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(application.getString(R.string.SHARED_PREF_TOKEN_LOGIN), token);
                        editor.apply();

                        //Obtener datos investigador post login
                        Investigador invResponse = new Investigador();
                        invResponse.setId(jsonData.getInt(application.getString(R.string.KEY_INVES_ID)));

                        JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));
                        invResponse.setEmail(jsonAttributes.getString(application.getString(R.string.KEY_INVES_EMAIL)));
                        invResponse.setNombre(jsonAttributes.getString(application.getString(R.string.KEY_INVES_NOMBRE)));
                        invResponse.setApellido(jsonAttributes.getString(application.getString(R.string.KEY_INVES_APELLIDO)));
                        invResponse.setCreateTime(jsonAttributes.getString(application.getString(R.string.KEY_CREATE_TIME)));

                        if (jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ACTIVADO)) == 0) {
                            invResponse.setActivado(false);
                        } else if (jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ACTIVADO)) == 1) {
                            invResponse.setActivado(true);
                        }

                        invResponse.setIdRol(jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ID_ROL)));

                        //Buscar en JSON nombre del rol
                        JSONObject jsonObjectRolData = jsonData.getJSONObject(application.getString(R.string.JSON_RELATIONSHIPS))
                                .getJSONObject(application.getString(R.string.KEY_ROL_OBJECT))
                                .getJSONObject(application.getString(R.string.JSON_DATA));
                        invResponse.setNombreRol(jsonObjectRolData.getString(application.getString(R.string.KEY_ROL_NOMBRE)));

                        //Enviar investigador y mensaje para toast
                        Map<String, Object> result = new HashMap<>();
                        result.put(application.getString(R.string.KEY_INVES_OBJECT), invResponse);
                        result.put(application.getString(R.string.LOGIN_MSG_VM), application.getString(R.string.LOGIN_MSG_VM_WELCOME));

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
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERROR_LOGIN), application.getString(R.string.TIMEOUT_ERROR));
                    errorMsg.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERROR_LOGIN), application.getString(R.string.NETWORK_ERROR));
                    errorMsg.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERROR_LOGIN), application.getString(R.string.AUTHENTICATION_ERROR) + errorObject);

                        try {
                            assert errorObject != null;

                            if (errorObject.get(application.getString(R.string.JSON_ERROR_DETAIL)).equals(application.getString(R.string.SERVER_ERROR_AUTH_MSG))) {
                                errorMsg.postValue(application.getString(R.string.SERVER_ERROR_AUTH_MSG_VM));
                            } else {
                                errorMsg.postValue(application.getString(R.string.AUTHENTICATION_ERROR_MSG_VM));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERROR_LOGIN), application.getString(R.string.SERVER_ERROR) + errorObject);

                        try {
                            assert errorObject != null;

                            //Si el error es de email no existente
                            if (errorObject.get(application.getString(R.string.JSON_ERROR_DETAIL)).equals(application.getString(R.string.SERVER_ERROR_REGISTRO_MSG_2))) {
                                errorMsg.postValue(application.getString(R.string.SERVER_ERROR_REGISTRO_MSG_VM_2));
                            } else {
                                errorMsg.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }
            }
        };

        String url = application.getString(R.string.URL_LOGIN_INVESTIGADORES);

        StringRequest request = new StringRequest(Request.Method.POST, url, responseListener,
                errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.KEY_INVES_EMAIL), investigadorForm.getEmail());
                params.put(application.getString(R.string.KEY_INVES_PASSWORD), investigadorForm.getPassword());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.JSON_CONTENT_TYPE), application.getString(R.string.JSON_CONTENT_TYPE_MSG));
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

                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));


                    //Investigador actualizado
                    Investigador invResponse = new Investigador();
                    invResponse.setId(jsonData.getInt(application.getString(R.string.KEY_INVES_ID)));

                    JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    invResponse.setNombre(jsonAttributes.getString(application.getString(R.string.KEY_INVES_NOMBRE)));
                    invResponse.setApellido(jsonAttributes.getString(application.getString(R.string.KEY_INVES_APELLIDO)));
                    invResponse.setEmail(jsonAttributes.getString(application.getString(R.string.KEY_INVES_EMAIL)));
                    invResponse.setIdRol(jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ID_ROL)));

                    if (jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ACTIVADO)) == 0) {
                        invResponse.setActivado(false);
                    } else if (jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ACTIVADO)) == 1) {
                        invResponse.setActivado(true);
                    }

                    //Buscar en JSON nombre del rol
                    JSONObject jsonObjectRolData = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES))
                            .getJSONObject(application.getString(R.string.KEY_ROL_OBJECT))
                            .getJSONObject(application.getString(R.string.JSON_DATA));
                    invResponse.setNombreRol(jsonObjectRolData.getString(application.getString(R.string.KEY_ROL_NOMBRE)));

                    String update_time = jsonAttributes.getString(application.getString(R.string.KEY_UPDATE_TIME));

                    Log.d("MEMORIA", investigadorForm.toString());
                    Log.d("INTERNET", invResponse.toString());

                    if (investigadorForm.equals(invResponse) && !update_time.isEmpty()) {

                        //Enviar investigador y mensaje para toast
                        Map<String, Object> result = new HashMap<>();
                        result.put(application.getString(R.string.KEY_INVES_OBJECT), invResponse);
                        result.put(application.getString(R.string.UPDATE_MSG_VM), application.getString(R.string.UPDATE_MSG_VM_SAVE));

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
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_UPDATE), application.getString(R.string.TIMEOUT_ERROR));
                    errorMsg.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_UPDATE), application.getString(R.string.NETWORK_ERROR));
                    errorMsg.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
                }

                //Errores cuando el servidor si response
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_UPDATE), application.getString(R.string.AUTHENTICATION_ERROR) + errorObject);
                        errorMsg.postValue(application.getString(R.string.AUTHENTICATION_ERROR_MSG_VM));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_UPDATE), application.getString(R.string.SERVER_ERROR) + errorObject);
                        errorMsg.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                    }
                }
            }
        };


        String url = application.getString(R.string.URL_PUT_INVESTIGADORES) + investigadorForm.getId();

        StringRequest request = new StringRequest(Request.Method.PUT, url, responseListener, errorListener) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put(application.getString(R.string.KEY_INVES_NOMBRE), investigadorForm.getNombre());
                params.put(application.getString(R.string.KEY_INVES_APELLIDO), investigadorForm.getApellido());
                params.put(application.getString(R.string.KEY_INVES_EMAIL), investigadorForm.getEmail());
                params.put(application.getString(R.string.KEY_INVES_PASSWORD), investigadorForm.getPassword());
                params.put(application.getString(R.string.KEY_INVES_ID_ROL), String.valueOf(investigadorForm.getIdRol()));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {

                SharedPreferences sharedPreferences = application.getSharedPreferences(application.getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);

                String token = sharedPreferences.getString(application.getString(R.string.SHARED_PREF_TOKEN_LOGIN), "");

                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.JSON_CONTENT_TYPE), application.getString(R.string.JSON_CONTENT_TYPE_MSG));
                params.put(application.getString(R.string.JSON_AUTH), String.format("%s %s", application.getString(R.string.JSON_AUTH_MSG), token));
                return params;
            }
        };

        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_INVESTIGADOR_ACTUALIZACION);
    }
}
