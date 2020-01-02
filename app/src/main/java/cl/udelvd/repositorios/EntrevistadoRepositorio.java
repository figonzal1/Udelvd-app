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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cl.udelvd.modelo.Ciudad;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.modelo.EstadoCivil;
import cl.udelvd.modelo.NivelEducacional;
import cl.udelvd.modelo.Profesion;
import cl.udelvd.modelo.TipoConvivencia;
import cl.udelvd.servicios.VolleySingleton;
import cl.udelvd.utilidades.SingleLiveEvent;

public class EntrevistadoRepositorio {

    private static EntrevistadoRepositorio instancia;
    private final Application application;

    private List<Entrevistado> entrevistadoList;

    //Mensajeria
    private final SingleLiveEvent<String> responseMsgRegistro = new SingleLiveEvent<>();
    private SingleLiveEvent<String> responseMsgActualizacion = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorMsg = new SingleLiveEvent<>();

    private MutableLiveData<List<Entrevistado>> entrevistadosMutableLiveData = new MutableLiveData<>();

    private static final String TAG_ENTREVISTADOS_LISTA = "ListaEntrevistados";
    private static final String TAG_ENTREVISTADO_REGISTRO = "RegistroEntrevistado";
    private static final Object TAG_ENTREVISTADO = "ObtenerEntrevistado";

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
        entrevistadoList = new ArrayList<>();
        sendGetEntrevistados(entrevistadosMutableLiveData);
        return entrevistadosMutableLiveData;
    }

    /**
     * Funcion que realizar solicitud GET para obtener listado de usuarios
     *
     * @param entrevistadosMutableLiveData Lista mutable vacia rellenada con lista de entrevistados
     */
    private void sendGetEntrevistados(final MutableLiveData<List<Entrevistado>> entrevistadosMutableLiveData) {


        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


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

                        Ciudad ciudad = new Ciudad();
                        ciudad.setId(jsonUsuarioAttributes.getInt("id_ciudad"));
                        entrevistado.setCiudad(ciudad);

                        //Jubilado Legal
                        if (jsonUsuarioAttributes.getInt("jubilado_legal") == 1) {
                            entrevistado.setJubiladoLegal(true);
                        } else {
                            entrevistado.setJubiladoLegal(false);
                        }

                        //Caidas
                        if (jsonUsuarioAttributes.getInt("caidas") == 1) {
                            entrevistado.setCaidas(true);
                            entrevistado.setNCaidas(jsonUsuarioAttributes.getInt("n_caidas"));
                        } else {
                            entrevistado.setCaidas(false);
                        }

                        entrevistado.setnConvivientes3Meses(jsonUsuarioAttributes.getInt("n_convivientes_3_meses"));

                        //Foraneas
                        entrevistado.setIdInvestigador(jsonUsuarioAttributes.getInt("id_investigador"));
                        EstadoCivil estadoCivil = new EstadoCivil();
                        estadoCivil.setId(jsonUsuarioAttributes.getInt("id_estado_civil"));
                        entrevistado.setEstadoCivil(estadoCivil);

                        //Foraneas opcionales
                        if (jsonUsuarioAttributes.has("id_nivel_educacional") && !jsonUsuarioAttributes.isNull("id_nivel_educacional")) {
                            NivelEducacional nivelEducacional = new NivelEducacional();
                            nivelEducacional.setId(jsonUsuarioAttributes.getInt("id_nivel_educacional"));
                            entrevistado.setNivelEducacional(nivelEducacional);
                        }
                        if (jsonUsuarioAttributes.has("id_tipo_convivencia") && !jsonUsuarioAttributes.isNull("id_tipo_convivencia")) {
                            TipoConvivencia tipoConvivencia = new TipoConvivencia();
                            tipoConvivencia.setId(jsonUsuarioAttributes.getInt("id_tipo_convivencia"));
                            entrevistado.setTipoConvivencia(tipoConvivencia);
                        }
                        if (jsonUsuarioAttributes.has("id_profesion") && !jsonUsuarioAttributes.isNull("id_profesion")) {
                            Profesion profesion = new Profesion();
                            profesion.setId(jsonUsuarioAttributes.getInt("id_profesion"));
                            entrevistado.setProfesion(profesion);
                        }

                        JSONObject jsonRelationships = jsonUsuario.getJSONObject("relationships");
                        entrevistado.setN_entrevistas(
                                jsonRelationships.getJSONObject("entrevistas").getJSONObject("data").getInt("n_entrevistas")
                        );

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
                    Log.d("VOLLEY_ER_ENTREVISTADOS", "TIMEOUT_ERROR");
                    errorMsg.postValue("Servidor no responde, intente más tarde");
                } else if (error instanceof NetworkError) {
                    Log.d("VOLLEY_ER_ENTREVISTADOS", "NETWORK_ERROR");
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
                        Log.d("VOLLEY_ER_ENTREVISTADOS", "AUTHENTICATION_ERROR: " + errorObject);
                        errorMsg.postValue("Acceso no autorizado");
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d("VOLLEY_ER_ENTREVISTADOS", "SERVER_ERROR: " + errorObject);
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

                    JSONObject jsonData = jsonObject.getJSONObject("data");

                    JSONObject jsonAttributes = jsonData.getJSONObject("attributes");

                    //Obtener json desde respuesta
                    Entrevistado entResponse = new Entrevistado();
                    entResponse.setNombre(jsonAttributes.getString("nombre"));
                    entResponse.setApellido(jsonAttributes.getString("apellido"));

                    entResponse.setSexo(jsonAttributes.getString("sexo"));

                    if (jsonAttributes.getInt("jubilado_legal") == 0) {
                        entResponse.setJubiladoLegal(false);
                    } else {
                        entResponse.setJubiladoLegal(true);
                    }

                    if (jsonAttributes.getInt("caidas") == 0) {
                        entResponse.setCaidas(false);
                    } else {
                        entResponse.setCaidas(true);
                    }
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date fechaNac = simpleDateFormat.parse(jsonAttributes.getString("fecha_nacimiento"));
                    entResponse.setFechaNacimiento(fechaNac);

                    entResponse.setnConvivientes3Meses(jsonAttributes.getInt("n_convivientes_3_meses"));
                    entResponse.setIdInvestigador(jsonAttributes.getInt("id_investigador"));

                    String create_time = jsonAttributes.getString("create_time");

                    Log.d("MEMORIA", entrevistado.toString());
                    Log.d("INTERNET", entResponse.toString());

                    if (entrevistado.equals(entResponse) && !create_time.isEmpty()) {
                        responseMsgRegistro.postValue("¡Entrevistado registrado!");
                    }

                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Log.d("VOLLEY_ERR_ENTREVISTADO", "TIMEOUT_ERROR");
                    errorMsg.postValue("Servidor no responde, intente más tarde");
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d("VOLLEY_ERR_ENTREVISTADO", "NETWORK_ERROR");
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
                        Log.d("VOLLEY_ERR_ENTREVISTADO", "AUTHENTICATION_ERROR: " + errorObject);
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d("VOLLEY_ERR_ENTREVISTADO", "SERVER_ERROR: " + errorObject);
                    }
                }
            }
        };

        String url = "http://192.168.0.14/entrevistados";

        StringRequest request = new StringRequest(Request.Method.POST, url, responseListener, errorListener) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", entrevistado.getNombre());
                params.put("apellido", entrevistado.getApellido());
                params.put("sexo", entrevistado.getSexo());

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String fechaNac = simpleDateFormat.format(entrevistado.getFechaNacimiento());
                params.put("fecha_nacimiento", fechaNac);
                params.put("nombre_ciudad", entrevistado.getCiudad().getNombre());

                if (entrevistado.isJubiladoLegal()) {
                    params.put("jubilado_legal", String.valueOf(1));
                } else {
                    params.put("jubilado_legal", String.valueOf(0));
                }

                if (entrevistado.isCaidas()) {
                    params.put("caidas", String.valueOf(1));

                    params.put("n_caidas", String.valueOf(entrevistado.getNCaidas()));
                } else {
                    params.put("caidas", String.valueOf(0));
                }

                params.put("n_convivientes_3_meses", String.valueOf(entrevistado.getNConvivientes3Meses()));
                params.put("id_investigador", String.valueOf(entrevistado.getIdInvestigador()));
                params.put("id_estado_civil", String.valueOf(entrevistado.getEstadoCivil().getId()));

                /*
                    OPCIONALES
                 */
                if (entrevistado.getNivelEducacional() != null) {
                    params.put("id_nivel_educacional", String.valueOf(entrevistado.getNivelEducacional().getId()));
                }

                if (entrevistado.getTipoConvivencia() != null) {
                    params.put("id_tipo_convivencia", String.valueOf(entrevistado.getTipoConvivencia().getId()));
                }

                if (entrevistado.getProfesion() != null) {
                    params.put("nombre_profesion", entrevistado.getProfesion().getNombre());
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                SharedPreferences sharedPreferences = application.getSharedPreferences("udelvd",
                        Context.MODE_PRIVATE);

                String token = sharedPreferences.getString("TOKEN_LOGIN", "");

                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + token);
                params.put("Content-Type", "application/x-www-form-urlencoded");

                return params;
            }
        };

        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_ENTREVISTADO_REGISTRO);
    }

    /**
     * Funcion encargada de obtener la información de un entrevistado en específico
     *
     * @param entrevistado Objeto entrevistado
     * @return
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

                    JSONObject jsonData = jsonObject.getJSONObject("data");
                    JSONObject jsonAttributes = jsonData.getJSONObject("attributes");

                    entrevistado.setId(jsonData.getInt("id"));

                    entrevistado.setNombre(jsonAttributes.getString("nombre"));
                    entrevistado.setApellido(jsonAttributes.getString("apellido"));
                    entrevistado.setSexo(jsonAttributes.getString("sexo"));

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date fechaNac = simpleDateFormat.parse(jsonAttributes.getString("fecha_nacimiento"));
                    entrevistado.setFechaNacimiento(fechaNac);

                    if (jsonAttributes.getInt("jubilado_legal") == 0) {
                        entrevistado.setJubiladoLegal(false);
                    } else {
                        entrevistado.setJubiladoLegal(true);
                    }

                    if (jsonAttributes.getInt("caidas") == 0) {
                        entrevistado.setCaidas(false);
                    } else {
                        entrevistado.setCaidas(true);

                        entrevistado.setNCaidas(jsonAttributes.getInt("n_caidas"));
                    }

                    entrevistado.setnConvivientes3Meses(jsonAttributes.getInt("n_convivientes_3_meses"));
                    entrevistado.setIdInvestigador(jsonAttributes.getInt("id_investigador"));

                    Ciudad ciudad = new Ciudad();
                    ciudad.setId(jsonAttributes.getInt("id_ciudad"));
                    entrevistado.setCiudad(ciudad);

                    EstadoCivil estadoCivil = new EstadoCivil();
                    estadoCivil.setId(jsonAttributes.getInt("id_estado_civil"));
                    entrevistado.setEstadoCivil(estadoCivil);

                    //Opcionales (manejar nulls como strings)
                    String id_nivel_educacional = jsonAttributes.getString("id_nivel_educacional");

                    if (!id_nivel_educacional.equals("null")) {
                        NivelEducacional nivelEducacional = new NivelEducacional();
                        nivelEducacional.setId(Integer.parseInt(id_nivel_educacional));

                        entrevistado.setNivelEducacional(nivelEducacional);
                    }

                    String id_tipo_convivencia = jsonAttributes.getString("id_tipo_convivencia");
                    if (!id_tipo_convivencia.equals("null")) {
                        TipoConvivencia tipoConvivencia = new TipoConvivencia();
                        tipoConvivencia.setId(Integer.parseInt(id_tipo_convivencia));

                        entrevistado.setTipoConvivencia(tipoConvivencia);
                    }

                    String id_profesion = jsonAttributes.getString("id_profesion");
                    if (!id_profesion.equals("null")) {
                        Profesion profesion = new Profesion();
                        profesion.setId(Integer.parseInt(id_profesion));

                        entrevistado.setProfesion(profesion);
                    }

                    JSONObject jsonRelationships = jsonData.getJSONObject("relationships");
                    entrevistado.setN_entrevistas(
                            jsonRelationships.getJSONObject("entrevistas").getJSONObject("data").getInt("n_entrevistas")
                    );

                    entrevistadoMutableLiveData.postValue(entrevistado);

                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Log.d("VOLLEY_ER_ENTREVISTADO", "TIMEOUT_ERROR");
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d("VOLLEY_ER_ENTREVISTADO", "NETWORK_ERROR");
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
                        Log.d("VOLLEY_ER_ENTREVISTADO", "AUTHENTICATION_ERROR: " + errorObject);
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d("VOLLEY_ER_ENTREVISTADO", "SERVER_ERROR: " + errorObject);
                    }
                }
            }
        };

        String url = "http://192.168.0.14/entrevistados/" + entrevistado.getId();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, responseListener, errorListener) {

            @Override
            public Map<String, String> getHeaders() {

                SharedPreferences sharedPreferences = application.getSharedPreferences("udelvd",
                        Context.MODE_PRIVATE);

                String token = sharedPreferences.getString("TOKEN_LOGIN", "");

                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", "Bearer " + token);
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
                    JSONObject jsonData = jsonObject.getJSONObject("data");

                    JSONObject jsonAttributes = jsonData.getJSONObject("attributes");

                    Entrevistado entrevistadoInternet = new Entrevistado();
                    entrevistadoInternet.setId(jsonData.getInt("id"));

                    entrevistadoInternet.setNombre(jsonAttributes.getString("nombre"));
                    entrevistadoInternet.setApellido(jsonAttributes.getString("apellido"));
                    entrevistadoInternet.setSexo(jsonAttributes.getString("sexo"));

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date fechaNac = simpleDateFormat.parse(jsonAttributes.getString("fecha_nacimiento"));
                    entrevistadoInternet.setFechaNacimiento(fechaNac);

                    if (jsonAttributes.getInt("jubilado_legal") == 0) {
                        entrevistadoInternet.setJubiladoLegal(false);
                    } else {
                        entrevistadoInternet.setJubiladoLegal(true);
                    }

                    if (jsonAttributes.getInt("caidas") == 0) {
                        entrevistadoInternet.setCaidas(false);
                    } else {
                        entrevistadoInternet.setCaidas(true);

                        entrevistadoInternet.setNCaidas(jsonAttributes.getInt("n_caidas"));
                    }

                    entrevistadoInternet.setnConvivientes3Meses(jsonAttributes.getInt("n_convivientes_3_meses"));
                    entrevistadoInternet.setIdInvestigador(jsonAttributes.getInt("id_investigador"));

                    Log.d("MEMORIA", entrevistado.toString());
                    Log.d("INTERNET", entrevistadoInternet.toString());

                    String update_time = jsonAttributes.getString("update_time");

                    if (entrevistado.equals(entrevistadoInternet) && !update_time.isEmpty()) {
                        responseMsgActualizacion.postValue("¡Entrevistado actualizado!");
                    }

                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Log.d("VOLLEY_ERR_ENTREVISTADO", "TIMEOUT_ERROR");
                    errorMsg.postValue("Servidor no responde, intente más tarde");
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d("VOLLEY_ERR_ENTREVISTADO", "NETWORK_ERROR");
                    errorMsg.postValue("No tienes conexión a Internet");
                }

                //Errores cuando el servidor si responde
                else if (error.networkResponse != null && error.networkResponse.data != null) {

                    String json = new String(error.networkResponse.data);
                    Log.d("ERROR", json);
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
                        Log.d("VOLLEY_ERR_ENTREVISTADO", "AUTHENTICATION_ERROR: " + errorObject);
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d("VOLLEY_ERR_ENTREVISTADO", "SERVER_ERROR: " + errorObject);
                    }
                }
            }
        };

        String url = "http://192.168.0.14/entrevistados/" + entrevistado.getId();

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, responseListener, errorListener) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("nombre", entrevistado.getNombre());
                params.put("apellido", entrevistado.getApellido());
                params.put("sexo", entrevistado.getSexo());

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String fechaNac = simpleDateFormat.format(entrevistado.getFechaNacimiento());
                params.put("fecha_nacimiento", fechaNac);
                params.put("nombre_ciudad", entrevistado.getCiudad().getNombre());

                if (entrevistado.isJubiladoLegal()) {
                    params.put("jubilado_legal", String.valueOf(1));
                } else {
                    params.put("jubilado_legal", String.valueOf(0));
                }

                if (entrevistado.isCaidas()) {
                    params.put("caidas", String.valueOf(1));

                    params.put("n_caidas", String.valueOf(entrevistado.getNCaidas()));
                } else {
                    params.put("caidas", String.valueOf(0));
                }

                params.put("n_convivientes_3_meses", String.valueOf(entrevistado.getNConvivientes3Meses()));
                params.put("id_investigador", String.valueOf(entrevistado.getIdInvestigador()));
                params.put("id_estado_civil", String.valueOf(entrevistado.getEstadoCivil().getId()));

                /*
                    OPCIONALES
                 */
                if (entrevistado.getNivelEducacional() != null) {
                    params.put("id_nivel_educacional", String.valueOf(entrevistado.getNivelEducacional().getId()));
                }

                if (entrevistado.getTipoConvivencia() != null) {
                    params.put("id_tipo_convivencia", String.valueOf(entrevistado.getTipoConvivencia().getId()));
                }

                if (entrevistado.getProfesion() != null) {
                    params.put("nombre_profesion", entrevistado.getProfesion().getNombre());
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                SharedPreferences sharedPreferences = application.getSharedPreferences("udelvd",
                        Context.MODE_PRIVATE);

                String token = sharedPreferences.getString("TOKEN_LOGIN", "");

                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + token);
                params.put("Content-Type", "application/x-www-form-urlencoded");

                return params;
            }
        };

        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, "ActualizarEntrevistado");
    }
}
