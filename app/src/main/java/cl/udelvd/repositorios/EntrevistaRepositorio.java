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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cl.udelvd.R;
import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.modelo.TipoEntrevista;
import cl.udelvd.servicios.VolleySingleton;
import cl.udelvd.utilidades.SingleLiveEvent;
import cl.udelvd.utilidades.Utils;

public class EntrevistaRepositorio {


    private static EntrevistaRepositorio instancia;
    private final Application application;

    private static final String TAG_GET_ENTREVISTAS = "ListadoEntrevistas";
    private static final String TAG_GET_ENTREVISTA = "ObtenerEntrevista";
    private static final String TAG_NEW_ENTREVISTA = "NuevaEntrevista";
    private static final String TAG_UPDATE_ENTREVISTA = "ActualizarEntrevista";
    private static final String TAG_DELETE_ENTREVISTA = "EliminarEntrevista";

    //LISTADO
    private final List<Entrevista> entrevistaList = new ArrayList<>();
    private final MutableLiveData<List<Entrevista>> entrevistasMutableLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<String> responseMsgErrorListado = new SingleLiveEvent<>();

    //Registro
    private final SingleLiveEvent<String> responseMsgRegistro = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgErrorRegistro = new SingleLiveEvent<>();

    //Actualizacion
    private final SingleLiveEvent<String> responseMsgErrorEntrevista = new SingleLiveEvent<>();
    private final MutableLiveData<Entrevista> entrevistaMutableLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<String> responseMsgActualizacion = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgErrorActualizacion = new SingleLiveEvent<>();

    //Eliminar
    private final SingleLiveEvent<String> responseMsgEliminar = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgErrorEliminar = new SingleLiveEvent<>();

    //PROGRESS DIALOG
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private EntrevistaRepositorio(Application application) {
        this.application = application;
    }

    public static EntrevistaRepositorio getInstancia(Application application) {
        if (instancia == null) {
            instancia = new EntrevistaRepositorio(application);
        }
        return instancia;
    }

    /*
    LISTADO DE ENREVISTA
     */
    public SingleLiveEvent<String> getResponseMsgErrorListado() {
        return responseMsgErrorListado;
    }

    /**
     * Funcion que permite obtener el listado de entrevistas de una persona
     *
     * @param entrevistado Objeto entrevistaco que contiene el Id para buscar entrevistas
     * @return Listado mutable con entrevistas de usuario
     */
    public MutableLiveData<List<Entrevista>> obtenerEntrevistasPersonales(Entrevistado entrevistado) {
        sendGetEntrevistasPersonales(entrevistado);
        return entrevistasMutableLiveData;
    }

    /**
     * Funcion que envía peticion GET para obtener listado de entrevistas
     *
     * @param entrevistado Objeto entrevistado
     */
    private void sendGetEntrevistasPersonales(Entrevistado entrevistado) {

        entrevistaList.clear();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE", response);
                try {
                    JSONObject jsonObject;
                    jsonObject = new JSONObject(response);
                    JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                    for (int i = 0; i < jsonData.length(); i++) {

                        JSONObject jsonEntrevista = jsonData.getJSONObject(i);
                        JSONObject jsonAttributes = jsonEntrevista.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                        Entrevista entrevista = new Entrevista();
                        entrevista.setId(jsonEntrevista.getInt(application.getString(R.string.KEY_ENTREVISTA_ID)));
                        entrevista.setId_entrevistado(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTA_ID_ENTREVISTADO)));

                        Date fechaNac = Utils.stringToDate(application, false, jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTA_FECHA_ENTREVISTA)));
                        entrevista.setFecha_entrevista(fechaNac);

                        //Relationship
                        JSONObject jsonRelationship = jsonEntrevista.getJSONObject(application.getString(R.string.JSON_RELATIONSHIPS))
                                .getJSONObject(application.getString(R.string.KEY_TIPO_ENTREVISTA_OBJECT))
                                .getJSONObject(application.getString(R.string.JSON_DATA));

                        TipoEntrevista tipoEntrevista = new TipoEntrevista();
                        tipoEntrevista.setId(jsonRelationship.getInt(application.getString(R.string.KEY_TIPO_ENTREVISTA_ID)));
                        tipoEntrevista.setNombre(jsonRelationship.getString(application.getString(R.string.KEY_TIPO_ENTREVISTA_NOMBRE)));
                        entrevista.setTipoEntrevista(tipoEntrevista);

                        entrevistaList.add(entrevista);
                    }

                    entrevistasMutableLiveData.postValue(entrevistaList);
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
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgErrorListado.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), application.getString(R.string.NETWORK_ERROR));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                        responseMsgErrorListado.postValue(application.getString(R.string.AUTHENTICATION_ERROR_MSG_VM));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), application.getString(R.string.SERVER_ERROR) + errorObject);
                        responseMsgErrorListado.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                    }
                }
            }
        };


        String url = String.format(Locale.US, application.getString(R.string.URL_GET_ENTREVISTAS), application.getString(R.string.HEROKU_DOMAIN), entrevistado.getId());

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
        isLoading.postValue(true);
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_GET_ENTREVISTAS);
    }

    /*
    REGISTRO DE ENTREVISTA
     */
    public SingleLiveEvent<String> getResponseMsgRegistro() {
        return responseMsgRegistro;
    }

    public SingleLiveEvent<String> getResponseMsgErrorRegistro() {
        return responseMsgErrorRegistro;
    }

    /**
     * Funcion encargada de registrar une entrevista en el sistema
     *
     * @param entrevista Objeto entrevista con datos asociados
     */
    public void registrarEntrevista(Entrevista entrevista) {
        enviarPostEntrevista(entrevista);
    }

    /**
     * Función encargada de enviar peticion POST para registrar entrevista en el sistema
     *
     * @param entrevista Objeto entrevista con datos asoiados
     */
    private void enviarPostEntrevista(final Entrevista entrevista) {


        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));

                    JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    Entrevista entrevistaInternet = new Entrevista();
                    entrevistaInternet.setId(jsonData.getInt(application.getString(R.string.KEY_ENTREVISTA_ID)));

                    entrevistaInternet.setId_entrevistado(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTA_ID_ENTREVISTADO)));

                    Date fechaEntrevista = Utils.stringToDate(application, false, jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTA_FECHA_ENTREVISTA)));
                    entrevistaInternet.setFecha_entrevista(fechaEntrevista);

                    TipoEntrevista tipoEntrevista = new TipoEntrevista();
                    tipoEntrevista.setId(jsonAttributes.getInt(application.getString(R.string.KEY_TIPO_CONVIVENCIA_ID_LARGO)));
                    entrevistaInternet.setTipoEntrevista(tipoEntrevista);

                    String create_time = jsonAttributes.getString(application.getString(R.string.KEY_CREATE_TIME));

                    Log.d("MEMORIA", entrevista.toString());
                    Log.d("INTERNET", entrevistaInternet.toString());

                    if (entrevista.equals(entrevistaInternet) && !create_time.isEmpty()) {
                        responseMsgRegistro.postValue(application.getString(R.string.MSG_REGISTRO_ENTREVISTA));

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
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgErrorRegistro.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), application.getString(R.string.NETWORK_ERROR));
                    responseMsgErrorRegistro.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), application.getString(R.string.AUTHENTICATION_ERROR) + errorObject);
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), application.getString(R.string.SERVER_ERROR) + errorObject);
                        responseMsgErrorRegistro.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                    }
                }
            }
        };

        String url = String.format(application.getString(R.string.URL_POST_ENTREVISTAS), application.getString(R.string.HEROKU_DOMAIN), entrevista.getId_entrevistado());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener, errorListener) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.KEY_TIPO_CONVIVENCIA_ID_LARGO), String.valueOf(entrevista.getTipoEntrevista().getId()));

                String fechaEntrevista = Utils.dateToString(application, false, entrevista.getFecha_entrevista());
                params.put(application.getString(R.string.KEY_ENTREVISTA_FECHA_ENTREVISTA), fechaEntrevista);

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
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_NEW_ENTREVISTA);

    }

    /*
    ACTUALIZACION DE ENTREVISTA
     */
    public SingleLiveEvent<String> getResponseMsgErrorEntrevista() {
        return responseMsgErrorEntrevista;
    }

    /**
     * Funcion encargada de obtener una entrevista especifica de un entrevistado
     *
     * @param entrevista Datos de la entrevista
     * @return Objeto mutable de la entrevista solicitada
     */
    public MutableLiveData<Entrevista> obtenerEntrevistaPersonal(Entrevista entrevista) {
        enviarGetEntrevistaPersonal(entrevista);
        return entrevistaMutableLiveData;
    }

    /**
     * Funcion encargada de enviar la peticion GET al servidor para obtener la entrevista solicitada
     *
     * @param entrevista Objeto entrevista con datos de consulta
     */
    private void enviarGetEntrevistaPersonal(Entrevista entrevista) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d("RESPONSE_GET_ENTREVISTA", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));

                    JSONObject jsonAttribute = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    Entrevista entrevistaInternet = new Entrevista();
                    entrevistaInternet.setId(jsonData.getInt(application.getString(R.string.KEY_ENTREVISTA_ID)));
                    entrevistaInternet.setId_entrevistado(jsonAttribute.getInt(application.getString(R.string.KEY_ENTREVISTA_ID_ENTREVISTADO)));

                    TipoEntrevista tipoEntrevista = new TipoEntrevista();
                    tipoEntrevista.setId(jsonAttribute.getInt(application.getString(R.string.KEY_TIPO_ENTREVISTA_ID_LARGO)));

                    entrevistaInternet.setTipoEntrevista(tipoEntrevista);

                    Date fechaEntrevista = Utils.stringToDate(application, false, jsonAttribute.getString(application.getString(R.string.KEY_ENTREVISTA_FECHA_ENTREVISTA)));
                    entrevistaInternet.setFecha_entrevista(fechaEntrevista);

                    entrevistaMutableLiveData.postValue(entrevistaInternet);

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
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgErrorEntrevista.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), application.getString(R.string.NETWORK_ERROR));
                    responseMsgErrorEntrevista.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), String.format("%s %s", application.getString(R.string.SERVER_ERROR), errorObject));
                        responseMsgErrorEntrevista.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                    }
                }
            }
        };


        String url = String.format(application.getString(R.string.URL_GET_ENTREVISTA_ESPECIFICA), application.getString(R.string.HEROKU_DOMAIN), entrevista.getId_entrevistado(), entrevista.getId());

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

        isLoading.postValue(true);
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_GET_ENTREVISTA);
    }

    public SingleLiveEvent<String> getResponseMsgActualizacion() {
        return responseMsgActualizacion;
    }

    public SingleLiveEvent<String> getResponseMsgErrorActualizacion() {
        return responseMsgErrorActualizacion;
    }

    /**
     * Funcion encargada de editar una entrevista
     *
     * @param entrevista Objeto entrevista con los datos a enviar para actualizar
     */
    public void actualizarEntrevista(Entrevista entrevista) {
        enviarPutEntrevista(entrevista);
    }

    /**
     * Funcion encargada de enviar peticion PUT para actualizar datos de entrevista
     *
     * @param entrevista Objeto entrevista con los datoa a enviar para actualizar
     */
    private void enviarPutEntrevista(final Entrevista entrevista) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE_EDIT", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));

                    JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    Entrevista entrevistaInternet = new Entrevista();
                    entrevistaInternet.setId(jsonData.getInt(application.getString(R.string.KEY_ENTREVISTA_ID)));
                    entrevistaInternet.setId_entrevistado(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTA_ID_ENTREVISTADO)));

                    Date fechaEntrevista = Utils.stringToDate(application, false, jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTA_FECHA_ENTREVISTA)));
                    entrevistaInternet.setFecha_entrevista(fechaEntrevista);

                    String update_time = jsonAttributes.getString(application.getString(R.string.KEY_UPDATE_TIME));

                    if (entrevista.equals(entrevistaInternet) && !update_time.isEmpty()) {
                        responseMsgActualizacion.postValue(application.getString(R.string.MSG_UPDATE_ENTREVISTA));
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
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgErrorActualizacion.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), application.getString(R.string.NETWORK_ERROR));
                    responseMsgErrorActualizacion.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), String.format("%s %s", application.getString(R.string.SERVER_ERROR), errorObject));
                        responseMsgErrorActualizacion.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                    }
                }
            }
        };

        String url = String.format(application.getString(R.string.URL_PUT_ENTREVISTA_ESPECIFICA), application.getString(R.string.HEROKU_DOMAIN), entrevista.getId_entrevistado(), entrevista.getId());

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.KEY_TIPO_CONVIVENCIA_ID_LARGO), String.valueOf(entrevista.getTipoEntrevista().getId()));

                String fechaEntrevista = Utils.dateToString(application, false, entrevista.getFecha_entrevista());
                params.put(application.getString(R.string.KEY_ENTREVISTA_FECHA_ENTREVISTA), fechaEntrevista);

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
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_UPDATE_ENTREVISTA);
    }

    /*
    ELIMINAR ENTREVISTA
     */

    public SingleLiveEvent<String> getResponseMsgEliminar() {
        return responseMsgEliminar;
    }

    public SingleLiveEvent<String> getResponseMsgErrorEliminar() {
        return responseMsgErrorEliminar;
    }

    public void eliminarEntrevista(Entrevista entrevista) {
        sendDeleteEntrevista(entrevista);
    }

    private void sendDeleteEntrevista(Entrevista entrevista) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                    if (jsonData.length() == 0) {
                        responseMsgEliminar.postValue(application.getString(R.string.MSG_DELETE_ENTREVISTA));
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
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgErrorEliminar.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), application.getString(R.string.NETWORK_ERROR));
                    responseMsgErrorEliminar.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA), String.format("%s %s", application.getString(R.string.SERVER_ERROR), errorObject));
                        responseMsgErrorEliminar.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                    }
                }
            }
        };

        String url = String.format(Locale.US, application.getString(R.string.URL_DELETE_ENTREVISTA_ESPECIFICA), application.getString(R.string.HEROKU_DOMAIN), entrevista.getId_entrevistado(), entrevista.getId());

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, responseListener, errorListener) {
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
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_DELETE_ENTREVISTA);
    }

    //PROGRESS DIALOG
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

}
