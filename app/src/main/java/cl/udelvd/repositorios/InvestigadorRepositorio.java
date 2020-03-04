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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cl.udelvd.R;
import cl.udelvd.modelo.Investigador;
import cl.udelvd.servicios.VolleySingleton;
import cl.udelvd.utilidades.SingleLiveEvent;
import cl.udelvd.utilidades.Utils;

public class InvestigadorRepositorio {

    private static InvestigadorRepositorio instancia;
    private final Application application;

    //Listados
    private List<Investigador> investigadorList = new ArrayList<>();
    private final SingleLiveEvent<List<Investigador>> investigadoresPrimeraPaginaLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<List<Investigador>> investigadoresSgtPaginaLiveData = new SingleLiveEvent<>();
    private List<Investigador> investigadoresSgtPagina = new ArrayList<>();
    //TAGS
    private static final String TAG_INVESTIGADOR_LISTADO = "InvestigadorListado";

    //LOGIN
    private final SingleLiveEvent<Map<String, Object>> responseMsgLogin = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgErrorLogin = new SingleLiveEvent<>();

    //REGISTRO
    private final SingleLiveEvent<Map<String, String>> responseMsgRegistro = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgErrorRegistro = new SingleLiveEvent<>();

    //Actualizacion
    private final SingleLiveEvent<String> responseMsgErrorActualizacion = new SingleLiveEvent<>();
    private final SingleLiveEvent<Map<String, Object>> responseMsgActualizacion = new SingleLiveEvent<>();

    //Recuperacion Cuenta
    private final SingleLiveEvent<String> responseMsgErrorRecuperacion = new SingleLiveEvent<>();
    private final SingleLiveEvent<Map<String, String>> responseMsgRecuperacion = new SingleLiveEvent<>();

    //PROGRESS DIALOG
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private static final String TAG_INVESTIGADOR_RESET = "ResetearPassword";

    //Resetear password
    private final SingleLiveEvent<String> responseMsgReset = new SingleLiveEvent<>();
    private static final String TAG_INVESTIGADOR_RECUPERACION = "RecuperarInvestigador";
    private static final Object TAG_INVESTIGADOR_ACTIVACION = "ActivacionInvestigador";
    private final SingleLiveEvent<String> responseMsgErrorReset = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgErrorListado = new SingleLiveEvent<>();
    private static final String TAG_INVESTIGADOR_REGISTRO = "RegistroInvestigador";
    private static final String TAG_INVESTIGADOR_LOGIN = "LoginInvestigador";
    private static final String TAG_INVESTIGADOR_ACTUALIZACION = "ActualizacionInvestigador";
    //ACTIVACION
    private final SingleLiveEvent<String> responseMsgErrorActivacion = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgActivacion = new SingleLiveEvent<>();

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

    public SingleLiveEvent<List<Investigador>> obtenerInvestigadores(int pagina, Investigador investigador) {
        sendGetListado(pagina, investigador);
        if (pagina == 1) {
            return investigadoresPrimeraPaginaLiveData;
        } else {
            return investigadoresSgtPaginaLiveData;
        }
    }

    private void sendGetListado(final int pagina, final Investigador investigador) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d("RESPONSE", response);

                //Reiniciar listado cuando se pregunta por primera pagina
                if (pagina == 1) {
                    investigadorList.clear();
                } else {
                    investigadoresSgtPagina.clear();
                }


                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonArray = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonInve = jsonArray.getJSONObject(i);
                        JSONObject jsonAttributes = jsonInve.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                        Investigador inv = new Investigador();
                        inv.setId(jsonInve.getInt(application.getString(R.string.KEY_INVES_ID)));
                        inv.setNombre(jsonAttributes.getString(application.getString(R.string.KEY_INVES_NOMBRE)));
                        inv.setApellido(jsonAttributes.getString(application.getString(R.string.KEY_INVES_APELLIDO)));
                        inv.setEmail(jsonAttributes.getString(application.getString(R.string.KEY_INVES_EMAIL)));
                        inv.setIdRol(jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ID_ROL)));
                        inv.setCreateTime(jsonAttributes.getString(application.getString(R.string.KEY_CREATE_TIME)));

                        if (jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ACTIVADO)) == 0) {
                            inv.setActivado(false);
                        } else {
                            inv.setActivado(true);
                        }

                        JSONObject jsonRelationshipsData = jsonInve.getJSONObject(application.getString(R.string.JSON_RELATIONSHIPS))
                                .getJSONObject(application.getString(R.string.KEY_ROL_OBJECT))
                                .getJSONObject(application.getString(R.string.JSON_DATA));
                        inv.setIdRol(jsonRelationshipsData.getInt(application.getString(R.string.KEY_ROL_ID)));
                        inv.setNombreRol(jsonRelationshipsData.getString(application.getString(R.string.KEY_ROL_NOMBRE)));

                        if (pagina == 1) {
                            investigadorList.add(inv);
                        } else {
                            investigadoresSgtPagina.add(inv);
                        }

                    }


                    if (pagina == 1) {
                        //Log.d("CARGANDO_PAGINA", "1");
                        investigadoresPrimeraPaginaLiveData.postValue(investigadorList);
                        isLoading.postValue(false);
                    } else {
                        //Log.d("CARGANDO_PAGINA", String.valueOf(page));
                        investigadoresSgtPaginaLiveData.postValue(investigadoresSgtPagina); //Enviar a ViewModel listado paginado
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
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_LISTA), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgErrorListado.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_LISTA), application.getString(R.string.NETWORK_ERROR));
                    responseMsgErrorListado.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_LISTA), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_LISTA), String.format("%s %s", application.getString(R.string.SERVER_ERROR), errorObject));
                        responseMsgErrorListado.postValue(application.getString(R.string.SERVER_ERROR_MSG_VM));
                    }
                }
            }
        };

        String url = String.format(application.getString(R.string.URL_GET_INVESTIGADORES), application.getString(R.string.HEROKU_DOMAIN), pagina, investigador.getId());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {

                SharedPreferences sharedPreferences = application.getSharedPreferences(application.getString(R.string.SHARED_PREF_MASTER_KEY),
                        Context.MODE_PRIVATE);

                String token = sharedPreferences.getString(application.getString(R.string.SHARED_PREF_TOKEN_LOGIN), "");

                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.JSON_CONTENT_TYPE), application.getString(R.string.JSON_CONTENT_TYPE_MSG));
                params.put(application.getString(R.string.JSON_AUTH), String.format("%s %s", application.getString(R.string.JSON_AUTH_MSG), token));
                return params;
            }
        };

        //Si es la primera pagina, activar progress dialog
        if (pagina == 1) {
            isLoading.postValue(true);
        }
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_INVESTIGADOR_LISTADO);
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

                        Map<String, String> map = new HashMap<>();

                        map.put(application.getString(R.string.INTENT_KEY_MSG_REGISTRO), application.getString(R.string.MSG_INVEST_REGISTRADO));
                        map.put(application.getString(R.string.INTENT_KEY_INVES_ACTIVADO), String.valueOf(jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ACTIVADO))));

                        responseMsgRegistro.postValue(map);

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
                                responseMsgErrorRegistro.postValue(application.getString(R.string.SERVER_ERROR_MSG_VM));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        };


        String url = String.format(application.getString(R.string.URL_POST_INVESTIGADORES), application.getString(R.string.HEROKU_DOMAIN));

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
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
                Log.d("RESPONSE", response);

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));
                    JSONObject jsonLogin = jsonObject.getJSONObject(application.getString(R.string.JSON_LOGIN));

                    String status = jsonLogin.getString(application.getString(R.string.JSON_LOGIN_STATUS));

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

                    /*
                    CHECKEO DE ACTIVACION
                     */
                    if (status.equals(application.getString(R.string.JSON_LOGIN_STATUS_RESPONSE)) && invResponse.isActivado()) {

                        String token = jsonLogin.getString(application.getString(R.string.JSON_TOKEN));
                        Log.d(application.getString(R.string.TAG_TOKEN_LOGIN), token);

                        //Guardar token
                        SharedPreferences sharedPreferences = application.getSharedPreferences(
                                application.getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);

                        //Guardar token en shared pref
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(application.getString(R.string.SHARED_PREF_TOKEN_LOGIN), token);
                        editor.apply();


                        //Enviar investigador y mensaje para toast
                        Map<String, Object> result = new HashMap<>();
                        result.put(application.getString(R.string.KEY_INVES_OBJECT), invResponse);
                        result.put(application.getString(R.string.LOGIN_MSG_VM), application.getString(R.string.LOGIN_MSG_VM_WELCOME));

                        responseMsgLogin.postValue(result);

                        isLoading.postValue(false);
                    } else {
                        responseMsgErrorLogin.postValue(application.getString(R.string.SNACKBAR_CUENTA_WAIT));

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
                                responseMsgErrorLogin.postValue(application.getString(R.string.SERVER_ERROR_MSG_VM));
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
                        responseMsgErrorActualizacion.postValue(application.getString(R.string.SERVER_ERROR_MSG_VM));
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

    /**
     * Funcion encargada de recuperar la cuenta de in investigadort
     *
     * @param investigador Objeto de investigador
     */
    public void recuperarCuenta(Investigador investigador) {
        sendPostRecuperar(investigador);
    }

    /**
     * Peticion POST para recuperacion de cuenta
     *
     * @param investigador Objeto investigador con datos necesarios
     */
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

        String url = String.format(application.getString(R.string.URL_GET_RECUPERAR_INVESTIGADOR), application.getString(R.string.HEROKU_DOMAIN), investigador.getEmail(), Utils.obtenerIdioma(application));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.JSON_CONTENT_TYPE), application.getString(R.string.JSON_CONTENT_TYPE_MSG));
                return params;
            }
        };

        isLoading.postValue(true);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_INVESTIGADOR_RECUPERACION);


    }

    /**
     * Funcion encargada de resetear contraseña
     *
     * @param email    Email del solicitante
     * @param password Nueva pass
     */
    public void resetearPassword(String email, String password) {
        sendPostResetPass(email, password);
    }

    /**
     * Peticion post para resetar la contraseña
     *
     * @param email    Email del solicitante
     * @param password Nueva pass
     */
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

    public void activarCuenta(Investigador investigador) {
        sendPatchActivar(investigador);
    }

    private void sendPatchActivar(final Investigador investigador) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));
                    JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    Investigador invResponse = new Investigador();
                    invResponse.setEmail(jsonAttributes.getString(application.getString(R.string.KEY_INVES_EMAIL)));

                    if (jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ACTIVADO)) == 0) {
                        invResponse.setActivado(false);
                    } else {
                        invResponse.setActivado(true);
                    }

                    if (investigador.getEmail().equals(invResponse.getEmail()) &&
                            (investigador.isActivado() == invResponse.isActivado())) {

                        if (investigador.isActivado()) {
                            responseMsgActivacion.postValue(application.getString(R.string.MSG_INVEST_CUENTA_ACTIVADA));
                        } else {
                            responseMsgActivacion.postValue(application.getString(R.string.MSG_INVEST_CUENTA_DESACTIVADA));
                        }
                    }
                    isLoading.postValue(false);
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
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_ACTIVAR), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgErrorActivacion.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_ACTIVAR), application.getString(R.string.NETWORK_ERROR));
                    responseMsgErrorActivacion.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
                }

                //Errores cuando el servidor si response
                else if (error.networkResponse != null && error.networkResponse.data != null) {

                    String json = new String(error.networkResponse.data);
                    //Log.d("JSON", json);

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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_ACTIVAR), application.getString(R.string.AUTHENTICATION_ERROR) + errorObject);
                        responseMsgErrorActivacion.postValue(application.getString(R.string.AUTHENTICATION_ERROR_MSG_VM));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_ACTIVAR), application.getString(R.string.SERVER_ERROR) + errorObject);
                        responseMsgErrorActivacion.postValue(application.getString(R.string.SERVER_ERROR_MSG_VM));
                    }
                }
            }
        };

        String url = String.format(Locale.getDefault(), application.getString(R.string.URL_PATCH_INVESTIGADORES_ACTIVAR), application.getString(R.string.HEROKU_DOMAIN), investigador.getId());

        StringRequest stringRequest = new StringRequest(Request.Method.PATCH, url, responseListener, errorListener) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (investigador.isActivado()) {
                    params.put(application.getString(R.string.KEY_INVES_ACTIVADO), String.valueOf(1));
                } else {
                    params.put(application.getString(R.string.KEY_INVES_ACTIVADO), String.valueOf(0));
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {

                SharedPreferences sharedPreferences = application.getSharedPreferences(application.getString(R.string.SHARED_PREF_MASTER_KEY),
                        Context.MODE_PRIVATE);

                String token = sharedPreferences.getString(application.getString(R.string.SHARED_PREF_TOKEN_LOGIN), "");

                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.JSON_CONTENT_TYPE), application.getString(R.string.JSON_CONTENT_TYPE_MSG));
                params.put(application.getString(R.string.JSON_AUTH), String.format("%s %s", application.getString(R.string.JSON_AUTH_MSG), token));
                return params;
            }
        };

        isLoading.postValue(true);
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_INVESTIGADOR_ACTIVACION);

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

    public SingleLiveEvent<Map<String, String>> getResponseMsgRegistro() {
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

    public SingleLiveEvent<String> getResponseMsgErrorListado() {
        return responseMsgErrorListado;
    }

    public SingleLiveEvent<String> getResponseMsgActivacion() {
        return responseMsgActivacion;
    }

    public SingleLiveEvent<String> getResponseMsgErrorActivacion() {
        return responseMsgErrorActivacion;
    }

    public SingleLiveEvent<List<Investigador>> obtenerSiguientePagina() {
        return investigadoresSgtPaginaLiveData;
    }
}
