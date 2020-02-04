package cl.udelvd.repositorios;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

    //LOGIN
    private final SingleLiveEvent<Map<String, Object>> responseMsgLogin = new SingleLiveEvent<>();
    private SingleLiveEvent<String> responseMsgErrorLogin = new SingleLiveEvent<>();

    //REGISTRO
    private final SingleLiveEvent<String> responseMsgRegistro = new SingleLiveEvent<>();
    private SingleLiveEvent<String> responseMsgErrorRegistro = new SingleLiveEvent<>();

    //Actualizacion
    private SingleLiveEvent<String> responseMsgErrorActualizacion = new SingleLiveEvent<>();
    private final SingleLiveEvent<Map<String, Object>> responseMsgActualizacion = new SingleLiveEvent<>();

    private static final String TAG_INVESTIGADOR_RECUPERACION = "RecuperarInvestigador";
    private static final String TAG_INVESTIGADOR_RESET = "ResetearPassword";
    //Recuperacion Cuenta
    private SingleLiveEvent<String> responseMsgErrorRecuperacion = new SingleLiveEvent<>();
    private SingleLiveEvent<Map<String, String>> responseMsgRecuperacion = new SingleLiveEvent<>();

    //PROGRESS DIALOG
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private static final String TAG_INVESTIGADOR_REGISTRO = "RegistroInvestigador";
    private static final String TAG_INVESTIGADOR_LOGIN = "LoginInvestigador";
    private static final String TAG_INVESTIGADOR_ACTUALIZACION = "ActualizacionInvestigador";
    //Resetear password
    private SingleLiveEvent<String> responseMsgReset = new SingleLiveEvent<>();
    private SingleLiveEvent<String> responseMsgErrorReset = new SingleLiveEvent<>();


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
                        isLoading.postValue(false);
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

                isLoading.postValue(false);

                if (error instanceof TimeoutError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_INVES_REGISTRO), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgErrorRegistro.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_INVES_REGISTRO), application.getString(R.string.NETWORK_ERROR));
                    responseMsgErrorRegistro.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
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
                        responseMsgErrorRegistro.postValue(application.getString(R.string.AUTHENTICATION_ERROR_MSG_VM));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_INVES_REGISTRO), application.getString(R.string.SERVER_ERROR) + errorObject);

                        try {

                            assert errorObject != null;
                            //Si el error es de mail repetido, postear error msg
                            if (errorObject.get(application.getString(R.string.JSON_ERROR_DETAIL)).equals(application.getString(R.string.SERVER_ERROR_REGISTRO_MSG))) {
                                responseMsgErrorRegistro.postValue(application.getString(R.string.SERVER_ERROR_REGISTRO_MSG_VM));
                            } else {
                                responseMsgErrorRegistro.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        };


        String url = String.format(application.getString(R.string.URL_GET_INVESTIGADORES), application.getString(R.string.HEROKU_DOMAIN));

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

        isLoading.postValue(true);
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

                        isLoading.postValue(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isLoading.postValue(false);
                if (error instanceof TimeoutError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERROR_LOGIN), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgErrorLogin.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERROR_LOGIN), application.getString(R.string.NETWORK_ERROR));
                    responseMsgErrorLogin.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
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
                                responseMsgErrorLogin.postValue(application.getString(R.string.SERVER_ERROR_AUTH_MSG_VM));
                            } else {
                                responseMsgErrorLogin.postValue(application.getString(R.string.AUTHENTICATION_ERROR_MSG_VM));
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
                                responseMsgErrorLogin.postValue(application.getString(R.string.SERVER_ERROR_REGISTRO_MSG_VM_2));
                            } else {
                                responseMsgErrorLogin.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }
            }
        };

        String url = String.format(application.getString(R.string.URL_LOGIN_INVESTIGADORES), application.getString(R.string.HEROKU_DOMAIN));

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

        isLoading.postValue(true);
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
                    JSONObject jsonObjectRolData = jsonData.getJSONObject(application.getString(R.string.JSON_RELATIONSHIPS))
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

                        isLoading.postValue(false);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isLoading.postValue(false);

                if (error instanceof TimeoutError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_UPDATE), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgErrorActualizacion.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_UPDATE), application.getString(R.string.NETWORK_ERROR));
                    responseMsgErrorActualizacion.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
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
                        responseMsgErrorActualizacion.postValue(application.getString(R.string.AUTHENTICATION_ERROR_MSG_VM));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_UPDATE), application.getString(R.string.SERVER_ERROR) + errorObject);
                        responseMsgErrorActualizacion.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                    }
                }
            }
        };


        String url = String.format(application.getString(R.string.URL_PUT_INVESTIGADORES), application.getString(R.string.HEROKU_DOMAIN), investigadorForm.getId());

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

        isLoading.postValue(true);
        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_INVESTIGADOR_ACTUALIZACION);
    }

    public void recuperarCuenta(Investigador investigador) {
        sendPostRecuperar(investigador);
    }

    private void sendPostRecuperar(Investigador investigador) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));

                    String email = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES)).getString(application.getString(R.string.KEY_INVES_EMAIL));

                    JSONObject jsonRecovery = jsonObject.getJSONObject(application.getString(R.string.JSON_RECOVERY));

                    String status = jsonRecovery.getString(application.getString(R.string.JSON_RECOVERY_STATUS));
                    String dybamicLink = jsonRecovery.getString(application.getString(R.string.JSON_RECOVERY_DL));

                    if (status.equals(application.getString(R.string.RECOVERY_MSG_VM))) {

                        isLoading.postValue(false);

                        Map<String, String> map = new HashMap<>();

                        map.put(application.getString(R.string.KEY_INVES_EMAIL), email);
                        map.put(application.getString(R.string.MSG_RECOVERY), application.getString(R.string.RECOVERY_MSG_VM_RESPONSE));
                        responseMsgRecuperacion.postValue(map);

                        Log.d(application.getString(R.string.TAG_DYNAMIC_LINK_JSON), dybamicLink);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isLoading.postValue(false);

                if (error instanceof TimeoutError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_RECUPERAR), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgErrorRecuperacion.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_RECUPERAR), application.getString(R.string.NETWORK_ERROR));
                    responseMsgErrorRecuperacion.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_RECUPERAR), application.getString(R.string.AUTHENTICATION_ERROR) + errorObject);
                        responseMsgErrorRecuperacion.postValue(application.getString(R.string.AUTHENTICATION_ERROR_MSG_VM));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_RECUPERAR), application.getString(R.string.SERVER_ERROR) + errorObject);

                        try {
                            assert errorObject != null;
                            if (errorObject.get(application.getString(R.string.JSON_ERROR_DETAIL)).equals(application.getString(R.string.SERVER_ERROR_REGISTRO_MSG_2))) {
                                responseMsgErrorRecuperacion.postValue(application.getString(R.string.SERVER_ERROR_REGISTRO_MSG_VM_2));
                            } else {
                                responseMsgErrorRecuperacion.postValue(application.getString(R.string.SERVER_ERROR_MSG_VM));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        };

        String url = String.format(application.getString(R.string.URL_GET_RECUPERAR_INVESTIGADOR), application.getString(R.string.HEROKU_DOMAIN), investigador.getEmail());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.JSON_CONTENT_TYPE), application.getString(R.string.JSON_CONTENT_TYPE_MSG));
                return params;
            }
        };

        isLoading.postValue(true);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_INVESTIGADOR_RECUPERACION);


    }

    public void resetearPassword(String email, String password) {
        sendPostResetPass(email, password);
    }

    private void sendPostResetPass(final String email, final String password) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject jsonReset = jsonObject.getJSONObject(application.getString(R.string.JSON_RESET));

                    String status = jsonReset.getString(application.getString(R.string.JSON_RESET_STATUS));

                    if (status.equals(application.getString(R.string.MSG_PASSWORD_RESETEADA_VM))) {

                        isLoading.postValue(false);

                        responseMsgReset.postValue(application.getString(R.string.MSG_PASSWORD_RESETEADA_VM_RESULT));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isLoading.postValue(false);

                if (error instanceof TimeoutError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_RESET), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgErrorReset.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_RESET), application.getString(R.string.NETWORK_ERROR));
                    responseMsgErrorReset.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_RESET), application.getString(R.string.AUTHENTICATION_ERROR) + errorObject);
                        responseMsgErrorReset.postValue(application.getString(R.string.AUTHENTICATION_ERROR_MSG_VM));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_RESET), application.getString(R.string.SERVER_ERROR) + errorObject);
                        responseMsgErrorReset.postValue(application.getString(R.string.SERVER_ERROR_MSG_VM));
                    }
                }
            }
        };

        String url = String.format(application.getString(R.string.URL_PUT_RESETEAR_PASSWORD), application.getString(R.string.HEROKU_DOMAIN));

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, responseListener, errorListener) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.KEY_INVES_PASSWORD), password);
                params.put(application.getString(R.string.KEY_INVES_EMAIL), email);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {

                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.JSON_CONTENT_TYPE), application.getString(R.string.JSON_CONTENT_TYPE_MSG));
                return params;
            }
        };

        isLoading.postValue(true);
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_INVESTIGADOR_RESET);
    }


    /*
    GETTERS
     */
    public SingleLiveEvent<Map<String, Object>> getResponseMsgLogin() {
        return responseMsgLogin;
    }

    public SingleLiveEvent<String> getResponseMsgErrorLogin() {
        return responseMsgErrorLogin;
    }

    public SingleLiveEvent<String> getResponseMsgRegistro() {
        return responseMsgRegistro;
    }

    public SingleLiveEvent<String> getResponseMsgErrorRegistro() {
        return responseMsgErrorRegistro;
    }

    public SingleLiveEvent<Map<String, Object>> getResponseMsgActualizacion() {
        return responseMsgActualizacion;
    }

    public SingleLiveEvent<String> getResponseMsgErrorActualizacion() {
        return responseMsgErrorActualizacion;
    }

    public SingleLiveEvent<Map<String, String>> getResponseMsgRecuperacion() {
        return responseMsgRecuperacion;
    }

    public SingleLiveEvent<String> getResponseMsgErrorRecuperacion() {
        return responseMsgErrorRecuperacion;
    }

    public SingleLiveEvent<String> getResponseMsgReset() {
        return responseMsgReset;
    }

    public SingleLiveEvent<String> getResponseMsgErrorReset() {
        return responseMsgErrorReset;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
