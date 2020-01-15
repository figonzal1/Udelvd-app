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
import java.util.Map;

import cl.udelvd.R;
import cl.udelvd.modelo.Ciudad;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.modelo.EstadoCivil;
import cl.udelvd.modelo.NivelEducacional;
import cl.udelvd.modelo.Profesion;
import cl.udelvd.modelo.TipoConvivencia;
import cl.udelvd.servicios.VolleySingleton;
import cl.udelvd.utilidades.SingleLiveEvent;
import cl.udelvd.utilidades.Utils;

public class EntrevistadoRepositorio {


    private static EntrevistadoRepositorio instancia;
    private final Application application;

    private List<Entrevistado> entrevistadoList;
    private MutableLiveData<List<Entrevistado>> entrevistadoMutableLiveData = new MutableLiveData<>();

    private final SingleLiveEvent<String> responseMsgRegistro = new SingleLiveEvent<>();
    private SingleLiveEvent<String> responseMsgActualizacion = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorMsg = new SingleLiveEvent<>();

    private static final String TAG_ENTREVISTADOS_LISTA = "ListaEntrevistados";
    private static final String TAG_ENTREVISTADO_REGISTRO = "RegistroEntrevistado";
    private static final String TAG_ENTREVISTADO = "ObtenerEntrevistado";
    private static final String TAG_ENTREVISTADO_ACTUALIZADO = "ActualizarEntrevistado";

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
        return errorMsg;
    }

    public SingleLiveEvent<String> getResponseMsgRegistro() {
        return responseMsgRegistro;
    }

    public SingleLiveEvent<String> getResponseMsgActualizacion() {
        return responseMsgActualizacion;
    }

    /**
     * Funcion encargada de buscar usuarios y enviar listado a ViewModel
     *
     * @return MutableLiveData con listado de usuarios
     */
    public MutableLiveData<List<Entrevistado>> obtenerEntrevistados() {
        sendGetEntrevistados(entrevistadoMutableLiveData);
        return entrevistadoMutableLiveData;
    }

    /**
     * Funcion que realizar solicitud GET para obtener listado de usuarios
     *
     * @param entrevistadosMutableLiveData Lista mutable vacia rellenada con lista de entrevistados
     */
    private void sendGetEntrevistados(final MutableLiveData<List<Entrevistado>> entrevistadosMutableLiveData) {

        entrevistadoList = new ArrayList<>();

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                    for (int i = 0; i < jsonData.length(); i++) {

                        JSONObject jsonUsuario = jsonData.getJSONObject(i);
                        JSONObject jsonUsuarioAttributes = jsonUsuario.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                        Entrevistado entrevistado = new Entrevistado();
                        entrevistado.setId(jsonUsuario.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID)));
                        entrevistado.setNombre(jsonUsuarioAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE)));
                        entrevistado.setApellido(jsonUsuarioAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_APELLIDO)));
                        entrevistado.setSexo(jsonUsuarioAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_SEXO)));

                        Date fechaNac = Utils.stringToDate(application, false, jsonUsuarioAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_FECHA_NAC)));
                        entrevistado.setFechaNacimiento(fechaNac);

                        Ciudad ciudad = new Ciudad();
                        ciudad.setId(jsonUsuarioAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_CIUDAD)));
                        entrevistado.setCiudad(ciudad);

                        //Jubilado Legal
                        if (jsonUsuarioAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_JUBILADO_LEGAL)) == 1) {
                            entrevistado.setJubiladoLegal(true);
                        } else {
                            entrevistado.setJubiladoLegal(false);
                        }

                        //Caidas
                        if (jsonUsuarioAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_CAIDAS)) == 1) {
                            entrevistado.setCaidas(true);
                            entrevistado.setNCaidas(jsonUsuarioAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_N_CAIDAS)));
                        } else {
                            entrevistado.setCaidas(false);
                        }

                        entrevistado.setnConvivientes3Meses(jsonUsuarioAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_N_CONVI_3_MESES)));

                        //Foraneas
                        entrevistado.setIdInvestigador(jsonUsuarioAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_INVESTIGADOR)));

                        EstadoCivil estadoCivil = new EstadoCivil();
                        estadoCivil.setId(jsonUsuarioAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_ESTADO_CIVIL)));
                        entrevistado.setEstadoCivil(estadoCivil);

                        //Foraneas opcionales
                        if (jsonUsuarioAttributes.has(application.getString(R.string.KEY_ENTREVISTADO_ID_NIVEL_EDUCACIONAL)) && !jsonUsuarioAttributes.isNull(application.getString(R.string.KEY_ENTREVISTADO_ID_NIVEL_EDUCACIONAL))) {
                            NivelEducacional nivelEducacional = new NivelEducacional();
                            nivelEducacional.setId(jsonUsuarioAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_NIVEL_EDUCACIONAL)));
                            entrevistado.setNivelEducacional(nivelEducacional);
                        }
                        if (jsonUsuarioAttributes.has(application.getString(R.string.KEY_ENTREVISTADO_ID_TIPO_CONVIVENCIA)) && !jsonUsuarioAttributes.isNull(application.getString(R.string.KEY_ENTREVISTADO_ID_TIPO_CONVIVENCIA))) {
                            TipoConvivencia tipoConvivencia = new TipoConvivencia();
                            tipoConvivencia.setId(jsonUsuarioAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_TIPO_CONVIVENCIA)));
                            entrevistado.setTipoConvivencia(tipoConvivencia);
                        }
                        if (jsonUsuarioAttributes.has(application.getString(R.string.KEY_ENTREVISTADO_ID_PROFESION)) && !jsonUsuarioAttributes.isNull(application.getString(R.string.KEY_ENTREVISTADO_ID_PROFESION))) {
                            Profesion profesion = new Profesion();
                            profesion.setId(jsonUsuarioAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_PROFESION)));
                            entrevistado.setProfesion(profesion);
                        }

                        JSONObject jsonRelationships = jsonUsuario.getJSONObject(application.getString(R.string.JSON_RELATIONSHIPS));
                        entrevistado.setN_entrevistas(
                                jsonRelationships.getJSONObject(application.getString(R.string.KEY_ENTREVISTA_OBJECT)).getJSONObject(application.getString(R.string.JSON_DATA)).getInt(application.getString(R.string.KEY_ENTREVISTADO_N_ENTREVISTAS))
                        );

                        entrevistadoList.add(entrevistado);
                    }

                    entrevistadosMutableLiveData.postValue(entrevistadoList);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTADO), application.getString(R.string.TIMEOUT_ERROR));
                    errorMsg.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                } else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTADO), application.getString(R.string.NETWORK_ERROR));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTADO), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                        errorMsg.postValue(application.getString(R.string.AUTHENTICATION_ERROR_MSG_VM));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTADO), String.format("%s %s", application.getString(R.string.SERVER_ERROR), errorObject));
                        errorMsg.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                    }
                }
            }
        };

        String url = application.getString(R.string.URL_GET_ENTREVISTADOS);

        //Hacer request
        StringRequest request = new StringRequest(Request.Method.GET, url, responseListener,
                errorListener) {

            @Override
            public Map<String, String> getHeaders() {
                SharedPreferences sharedPreferences = application.getSharedPreferences(application.getString(R.string.SHARED_PREF_MASTER_KEY),
                        Context.MODE_PRIVATE);

                String token = sharedPreferences.getString(application.getString(R.string.SHARED_PREF_TOKEN_LOGIN), "");

                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.JSON_AUTH), String.format("%s %s", application.getString(R.string.JSON_AUTH_MSG), token));
                return params;
            }
        };
        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_ENTREVISTADOS_LISTA);
    }

    /**
     * Funcion encargada de registrar entrevistados en el sistema
     *
     * @param entrevistado Objeto entrevistado para el registro
     */
    public void registrarEntrevistado(Entrevistado entrevistado) {
        enviarPostEntrevistado(entrevistado);
    }

    /**
     * Funcion encargada de realizar la solicitud POST para registrar un entrevistado
     *
     * @param entrevistado Objeto entrevistado a ser registrado
     */
    private void enviarPostEntrevistado(final Entrevistado entrevistado) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));

                    JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    //Obtener json desde respuesta
                    Entrevistado entResponse = new Entrevistado();
                    entResponse.setNombre(jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE)));
                    entResponse.setApellido(jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_APELLIDO)));

                    entResponse.setSexo(jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_SEXO)));

                    if (jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_JUBILADO_LEGAL)) == 0) {
                        entResponse.setJubiladoLegal(false);
                    } else {
                        entResponse.setJubiladoLegal(true);
                    }

                    if (jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_CAIDAS)) == 0) {
                        entResponse.setCaidas(false);
                    } else {
                        entResponse.setCaidas(true);
                    }

                    Date fechaNac = Utils.stringToDate(application, false, jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_FECHA_NAC)));
                    entResponse.setFechaNacimiento(fechaNac);

                    entResponse.setnConvivientes3Meses(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_N_CONVI_3_MESES)));
                    entResponse.setIdInvestigador(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_INVESTIGADOR)));

                    String create_time = jsonAttributes.getString(application.getString(R.string.KEY_CREATE_TIME));

                    Log.d("MEMORIA", entrevistado.toString());
                    Log.d("INTERNET", entResponse.toString());

                    if (entrevistado.equals(entResponse) && !create_time.isEmpty()) {
                        responseMsgRegistro.postValue(application.getString(R.string.MSG_REGISTRO_ENTREVISTADO));
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
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTADO), application.getString(R.string.TIMEOUT_ERROR));
                    errorMsg.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTADO), application.getString(R.string.NETWORK_ERROR));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTADO), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTADO), String.format("%s %s", application.getString(R.string.SERVER_ERROR), errorObject));
                        errorMsg.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                    }
                }
            }
        };

        String url = application.getString(R.string.URL_POST_ENTREVISTADOS);

        StringRequest request = new StringRequest(Request.Method.POST, url, responseListener, errorListener) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE), entrevistado.getNombre());
                params.put(application.getString(R.string.KEY_ENTREVISTADO_APELLIDO), entrevistado.getApellido());
                params.put(application.getString(R.string.KEY_ENTREVISTADO_SEXO), entrevistado.getSexo());

                String fechaNac = Utils.dateToString(application, false, entrevistado.getFechaNacimiento());
                params.put(application.getString(R.string.KEY_ENTREVISTADO_FECHA_NAC), fechaNac);
                params.put(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE_CIUDAD), entrevistado.getCiudad().getNombre());

                if (entrevistado.isJubiladoLegal()) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_JUBILADO_LEGAL), String.valueOf(1));
                } else {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_JUBILADO_LEGAL), String.valueOf(0));
                }

                if (entrevistado.isCaidas()) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_CAIDAS), String.valueOf(1));

                    params.put(application.getString(R.string.KEY_ENTREVISTADO_N_CAIDAS), String.valueOf(entrevistado.getNCaidas()));
                } else {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_CAIDAS), String.valueOf(0));
                }

                params.put(application.getString(R.string.KEY_ENTREVISTADO_N_CONVI_3_MESES), String.valueOf(entrevistado.getNConvivientes3Meses()));
                params.put(application.getString(R.string.KEY_ENTREVISTADO_ID_INVESTIGADOR), String.valueOf(entrevistado.getIdInvestigador()));
                params.put(application.getString(R.string.KEY_ENTREVISTADO_ID_ESTADO_CIVIL), String.valueOf(entrevistado.getEstadoCivil().getId()));

                /*
                    OPCIONALES
                 */
                if (entrevistado.getNivelEducacional() != null) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_ID_NIVEL_EDUCACIONAL), String.valueOf(entrevistado.getNivelEducacional().getId()));
                }

                if (entrevistado.getTipoConvivencia() != null) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_ID_TIPO_CONVIVENCIA), String.valueOf(entrevistado.getTipoConvivencia().getId()));
                }

                if (entrevistado.getProfesion() != null) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE_PROFESION), entrevistado.getProfesion().getNombre());
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                SharedPreferences sharedPreferences = application.getSharedPreferences(application.getString(R.string.SHARED_PREF_MASTER_KEY),
                        Context.MODE_PRIVATE);

                String token = sharedPreferences.getString(application.getString(R.string.SHARED_PREF_TOKEN_LOGIN), "");

                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.JSON_AUTH), String.format("%s %s", application.getString(R.string.JSON_AUTH_MSG), token));
                params.put(application.getString(R.string.JSON_CONTENT_TYPE), application.getString(R.string.JSON_CONTENT_TYPE_MSG));

                return params;
            }
        };

        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_ENTREVISTADO_REGISTRO);
    }

    /**
     * Funcion encargada de obtener la información de un entrevistado en específico
     *
     * @param entrevistado Objeto entrevistado
     * @return MutableLiveData de entrevistado
     */
    public MutableLiveData<Entrevistado> obtenerEntrevistado(Entrevistado entrevistado) {
        MutableLiveData<Entrevistado> entrevistadoMutableLiveData = new MutableLiveData<>();
        enviarGetEntrevistado(entrevistado, entrevistadoMutableLiveData);
        return entrevistadoMutableLiveData;
    }

    /**
     * Funcion encargada de enviar solicitud GET para obtener datos de entrevistado específico
     *
     * @param entrevistado                Objeto entrevistado
     * @param entrevistadoMutableLiveData Información de un entrevistado
     */
    private void enviarGetEntrevistado(final Entrevistado entrevistado, final MutableLiveData<Entrevistado> entrevistadoMutableLiveData) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));
                    JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    entrevistado.setId(jsonData.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID)));

                    entrevistado.setNombre(jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE)));
                    entrevistado.setApellido(jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_APELLIDO)));
                    entrevistado.setSexo(jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_SEXO)));

                    Date fechaNac = Utils.stringToDate(application, false, jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_FECHA_NAC)));
                    entrevistado.setFechaNacimiento(fechaNac);

                    if (jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_JUBILADO_LEGAL)) == 0) {
                        entrevistado.setJubiladoLegal(false);
                    } else {
                        entrevistado.setJubiladoLegal(true);
                    }

                    if (jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_CAIDAS)) == 0) {
                        entrevistado.setCaidas(false);
                    } else {
                        entrevistado.setCaidas(true);

                        entrevistado.setNCaidas(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_N_CAIDAS)));
                    }

                    entrevistado.setnConvivientes3Meses(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_N_CONVI_3_MESES)));
                    entrevistado.setIdInvestigador(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_INVESTIGADOR)));

                    Ciudad ciudad = new Ciudad();
                    ciudad.setId(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_CIUDAD)));
                    entrevistado.setCiudad(ciudad);

                    EstadoCivil estadoCivil = new EstadoCivil();
                    estadoCivil.setId(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_ESTADO_CIVIL)));
                    entrevistado.setEstadoCivil(estadoCivil);

                    //Opcionales (manejar nulls como strings)
                    String id_nivel_educacional = jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_ID_NIVEL_EDUCACIONAL));

                    if (!id_nivel_educacional.equals(application.getString(R.string.NULL))) {
                        NivelEducacional nivelEducacional = new NivelEducacional();
                        nivelEducacional.setId(Integer.parseInt(id_nivel_educacional));

                        entrevistado.setNivelEducacional(nivelEducacional);
                    }

                    String id_tipo_convivencia = jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_ID_TIPO_CONVIVENCIA));
                    if (!id_tipo_convivencia.equals(application.getString(R.string.NULL))) {
                        TipoConvivencia tipoConvivencia = new TipoConvivencia();
                        tipoConvivencia.setId(Integer.parseInt(id_tipo_convivencia));

                        entrevistado.setTipoConvivencia(tipoConvivencia);
                    }

                    String id_profesion = jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_ID_PROFESION));
                    if (!id_profesion.equals(application.getString(R.string.NULL))) {
                        Profesion profesion = new Profesion();
                        profesion.setId(Integer.parseInt(id_profesion));

                        entrevistado.setProfesion(profesion);
                    }

                    entrevistadoMutableLiveData.postValue(entrevistado);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTADO), application.getString(R.string.TIMEOUT_ERROR));
                    errorMsg.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTADO), application.getString(R.string.NETWORK_ERROR));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTADO), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTADO), String.format("%s %s", application.getString(R.string.SERVER_ERROR), errorObject));
                        errorMsg.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                    }
                }
            }
        };

        String url = application.getString(R.string.URL_GET_ENTREVISTADO) + entrevistado.getId();

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
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_ENTREVISTADO);
    }

    /**
     * Funcion encargada de actualizar los datos de un entrevistado
     *
     * @param entrevistado Objeto entrevistado con datos a ser actualizados
     */
    public void actualizarEntrevistado(Entrevistado entrevistado) {
        enviarPutEntrevistado(entrevistado);
    }

    /**
     * Funcion encargada de enviar la peticion PUT al servidor
     *
     * @param entrevistado Objeto entrevistado con los datos actualizados
     */
    private void enviarPutEntrevistado(final Entrevistado entrevistado) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));

                    JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    Entrevistado entrevistadoInternet = new Entrevistado();
                    entrevistadoInternet.setId(jsonData.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID)));

                    entrevistadoInternet.setNombre(jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE)));
                    entrevistadoInternet.setApellido(jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_APELLIDO)));
                    entrevistadoInternet.setSexo(jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_SEXO)));


                    Date fechaNac = Utils.stringToDate(application, false, jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_FECHA_NAC)));
                    entrevistadoInternet.setFechaNacimiento(fechaNac);

                    if (jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_JUBILADO_LEGAL)) == 0) {
                        entrevistadoInternet.setJubiladoLegal(false);
                    } else {
                        entrevistadoInternet.setJubiladoLegal(true);
                    }

                    if (jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_CAIDAS)) == 0) {
                        entrevistadoInternet.setCaidas(false);
                    } else {
                        entrevistadoInternet.setCaidas(true);

                        entrevistadoInternet.setNCaidas(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_N_CAIDAS)));
                    }

                    entrevistadoInternet.setnConvivientes3Meses(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_N_CONVI_3_MESES)));
                    entrevistadoInternet.setIdInvestigador(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_INVESTIGADOR)));

                    Log.d("MEMORIA", entrevistado.toString());
                    Log.d("INTERNET", entrevistadoInternet.toString());

                    String update_time = jsonAttributes.getString(application.getString(R.string.KEY_UPDATE_TIME));

                    if (entrevistado.equals(entrevistadoInternet) && !update_time.isEmpty()) {
                        responseMsgActualizacion.postValue(application.getString(R.string.MSG_UPDATE_ENTREVISTADO));
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
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTADO), application.getString(R.string.TIMEOUT_ERROR));
                    errorMsg.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTADO), application.getString(R.string.NETWORK_ERROR));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTADO), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTADO), String.format("%s %s", application.getString(R.string.SERVER_ERROR), errorObject));
                    }
                }
            }
        };

        String url = application.getString(R.string.URL_PUT_ENTREVISTADOS) + entrevistado.getId();

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, responseListener, errorListener) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE), entrevistado.getNombre());
                params.put(application.getString(R.string.KEY_ENTREVISTADO_APELLIDO), entrevistado.getApellido());
                params.put(application.getString(R.string.KEY_ENTREVISTADO_SEXO), entrevistado.getSexo());

                String fechaNac = Utils.dateToString(application, false, entrevistado.getFechaNacimiento());
                params.put(application.getString(R.string.KEY_ENTREVISTADO_FECHA_NAC), fechaNac);
                params.put(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE_CIUDAD), entrevistado.getCiudad().getNombre());

                if (entrevistado.isJubiladoLegal()) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_JUBILADO_LEGAL), String.valueOf(1));
                } else {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_JUBILADO_LEGAL), String.valueOf(0));
                }

                if (entrevistado.isCaidas()) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_CAIDAS), String.valueOf(1));

                    params.put(application.getString(R.string.KEY_ENTREVISTADO_N_CAIDAS), String.valueOf(entrevistado.getNCaidas()));
                } else {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_CAIDAS), String.valueOf(0));
                }

                params.put(application.getString(R.string.KEY_ENTREVISTADO_N_CONVI_3_MESES), String.valueOf(entrevistado.getNConvivientes3Meses()));
                params.put(application.getString(R.string.KEY_ENTREVISTADO_ID_INVESTIGADOR), String.valueOf(entrevistado.getIdInvestigador()));
                params.put(application.getString(R.string.KEY_ENTREVISTADO_ID_ESTADO_CIVIL), String.valueOf(entrevistado.getEstadoCivil().getId()));

                /*
                    OPCIONALES
                 */
                if (entrevistado.getNivelEducacional() != null) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_ID_NIVEL_EDUCACIONAL), String.valueOf(entrevistado.getNivelEducacional().getId()));
                }

                if (entrevistado.getTipoConvivencia() != null) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_ID_TIPO_CONVIVENCIA), String.valueOf(entrevistado.getTipoConvivencia().getId()));
                }

                if (entrevistado.getProfesion() != null) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE_PROFESION), entrevistado.getProfesion().getNombre());
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                SharedPreferences sharedPreferences = application.getSharedPreferences(application.getString(R.string.SHARED_PREF_MASTER_KEY),
                        Context.MODE_PRIVATE);

                String token = sharedPreferences.getString(application.getString(R.string.SHARED_PREF_TOKEN_LOGIN), "");

                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.JSON_AUTH), String.format("%s %s", application.getString(R.string.JSON_AUTH_MSG), token));
                params.put(application.getString(R.string.JSON_CONTENT_TYPE), application.getString(R.string.JSON_CONTENT_TYPE_MSG));

                return params;
            }
        };

        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_ENTREVISTADO_ACTUALIZADO);
    }
}
