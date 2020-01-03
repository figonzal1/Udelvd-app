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

import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.modelo.TipoEntrevista;
import cl.udelvd.servicios.VolleySingleton;
import cl.udelvd.utilidades.SingleLiveEvent;

public class EntrevistaRepositorio {


    private static EntrevistaRepositorio instancia;
    private final Application application;

    private static final String TAG_GET_ENTREVISTAS = "ListadoEntrevistas";
    private static final String TAG_NEW_ENTREVISTA = "NuevaEntrevista";

    private List<Entrevista> entrevistaList;
    private MutableLiveData<List<Entrevista>> entrevistaMutableLiveData = new MutableLiveData<>();

    private SingleLiveEvent<String> responseMsgRegistro = new SingleLiveEvent<>();
    private SingleLiveEvent<String> responseMsgError = new SingleLiveEvent<>();

    private EntrevistaRepositorio(Application application) {
        this.application = application;
    }

    public static EntrevistaRepositorio getInstancia(Application application) {
        if (instancia == null) {
            instancia = new EntrevistaRepositorio(application);
        }
        return instancia;
    }

    public SingleLiveEvent<String> getResponseMsgRegistro() {
        return responseMsgRegistro;
    }

    public SingleLiveEvent<String> getResponseMsgError() {
        return responseMsgError;
    }

    /**
     * Funcion que permite obtener el listado de entrevistas de una persona
     *
     * @param entrevistado Objeto entrevistaco que contiene el Id para buscar entrevistas
     * @return Listado mutable con entrevistas de usuario
     */
    public MutableLiveData<List<Entrevista>> obtenerEntrevistasPersonales(Entrevistado entrevistado) {
        sendGetEntrevistasPersonales(entrevistado, entrevistaMutableLiveData);
        return entrevistaMutableLiveData;
    }

    /**
     * Funcion que envía peticion GET para obtener listado de entrevistas
     * @param entrevistado Objeto entrevistado
     * @param mutableEntrevistasPersonales Lista mutable de entrevistas
     */
    private void sendGetEntrevistasPersonales(Entrevistado entrevistado, final MutableLiveData<List<Entrevista>> mutableEntrevistasPersonales) {

        entrevistaList = new ArrayList<>();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE", response);
                try {
                    JSONObject jsonObject;
                    jsonObject = new JSONObject(response);
                    JSONArray jsonData = jsonObject.getJSONArray("data");

                    for (int i = 0; i < jsonData.length(); i++) {

                        JSONObject jsonEntrevista = jsonData.getJSONObject(i);
                        JSONObject jsonAttributes = jsonEntrevista.getJSONObject("attributes");

                        Entrevista entrevista = new Entrevista();
                        entrevista.setId(jsonEntrevista.getInt("id"));
                        entrevista.setId_entrevistado(jsonAttributes.getInt("id_entrevistado"));

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        entrevista.setFecha_entrevista(simpleDateFormat.parse(jsonAttributes.getString("fecha_entrevista")));

                        //Relationship
                        JSONObject jsonRelationship = jsonEntrevista.getJSONObject("relationships").getJSONObject("tipoEntrevista").getJSONObject("data");
                        TipoEntrevista tipoEntrevista = new TipoEntrevista();
                        tipoEntrevista.setId(jsonRelationship.getInt("id"));
                        tipoEntrevista.setNombre(jsonRelationship.getString("nombre"));
                        entrevista.setTipoEntrevista(tipoEntrevista);

                        entrevistaList.add(entrevista);
                    }

                    mutableEntrevistasPersonales.postValue(entrevistaList);

                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }


            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Log.d("VOLLEY_ERR_ENTREVISTA", "TIMEOUT_ERROR");
                    responseMsgError.postValue("Servidor no responde, intente más tarde");
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d("VOLLEY_ERR_ENTREVISTA", "NETWORK_ERROR");
                    responseMsgError.postValue("No tienes conexión a Internet");
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
                        Log.d("VOLLEY_ERR_ENTREVISTA", "AUTHENTICATION_ERROR: " + errorObject);
                        responseMsgError.postValue("Acceso no autorizado");
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d("VOLLEY_ERR_ENTREVISTA", "SERVER_ERROR: " + errorObject);
                        responseMsgError.postValue("Servidor no responde, intente más tarde");
                    }
                }
            }
        };


        String url = "http://192.168.0.14/entrevistados/" + entrevistado.getId() + "/entrevistas";

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

        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_GET_ENTREVISTAS);
    }

    /**
     * Funcion encargada de registrar une entrevista en el sistema
     * @param entrevista Objeto entrevista con datos asociados
     */
    public void registrarEntrevista(Entrevista entrevista) {
        enviarPostEntrevista(entrevista);
    }

    /**
     * Función encargada de enviar peticion POST para registrar entrevista en el sistema
     * @param entrevista Objeto entrevista con datos asoiados
     */
    private void enviarPostEntrevista(final Entrevista entrevista) {


        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonData = jsonObject.getJSONObject("data");

                    JSONObject jsonAttributes = jsonData.getJSONObject("attributes");

                    Entrevista entrevistaInternet = new Entrevista();
                    entrevistaInternet.setId(jsonData.getInt("id"));

                    entrevistaInternet.setId_entrevistado(jsonAttributes.getInt("id_entrevistado"));

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    entrevistaInternet.setFecha_entrevista(simpleDateFormat.parse(jsonAttributes.getString("fecha_entrevista")));

                    TipoEntrevista tipoEntrevista = new TipoEntrevista();
                    tipoEntrevista.setId(jsonAttributes.getInt("id_tipo_entrevista"));
                    entrevistaInternet.setTipoEntrevista(tipoEntrevista);

                    String create_time = jsonAttributes.getString("create_time");

                    Log.d("MEMORIA", entrevista.toString());
                    Log.d("INTERNET", entrevistaInternet.toString());

                    if (entrevista.equals(entrevistaInternet) && !create_time.isEmpty()) {
                        responseMsgRegistro.postValue("¡Entrevista registrada!");
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
                    Log.d("VOLLEY_ERR_ENTREVISTA", "TIMEOUT_ERROR");
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d("VOLLEY_ERR_ENTREVISTA", "NETWORK_ERROR");
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
                        Log.d("VOLLEY_ERR_ENTREVISTA", "AUTHENTICATION_ERROR: " + errorObject);
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d("VOLLEY_ERR_ENTREVISTA", "SERVER_ERROR: " + errorObject);
                    }
                }
            }
        };

        String url = "http://192.168.0.14/entrevistados/" + entrevista.getId_entrevistado() + "/entrevistas";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener, errorListener) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("id_tipo_entrevista", String.valueOf(entrevista.getTipoEntrevista().getId()));

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                params.put("fecha_entrevista", simpleDateFormat.format(entrevista.getFecha_entrevista()));

                return params;
            }

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
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_NEW_ENTREVISTA);

    }
}
