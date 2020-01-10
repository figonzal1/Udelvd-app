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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cl.udelvd.modelo.Accion;
import cl.udelvd.modelo.Emoticon;
import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.Evento;
import cl.udelvd.servicios.VolleySingleton;
import cl.udelvd.utilidades.SingleLiveEvent;

public class EventoRepositorio {

    private static final String TAG_GET_EVENTOS_ENTREVISTA = "ListaEventosEntrevista";
    private static EventoRepositorio instancia;
    private Application application;

    private List<Evento> eventoList;

    private SingleLiveEvent<String> responseErrorMsg = new SingleLiveEvent<>();

    private EventoRepositorio(Application application) {
        this.application = application;
    }

    public static EventoRepositorio getInstancia(Application application) {
        if (instancia == null) {
            instancia = new EventoRepositorio(application);
        }
        return instancia;
    }

    public SingleLiveEvent<String> getResponseErrorMsg() {
        return responseErrorMsg;
    }

    /**
     * Funcion encargada de obtener los eventos de una entrevista dada
     *
     * @param entrevista Datos de entrevista para realizar la busqueda
     * @return mutableLiveData Mutable live data con los datos de la entrevista
     */
    public MutableLiveData<List<Evento>> obtenerEventosEntrevista(Entrevista entrevista) {
        MutableLiveData<List<Evento>> mutableLiveData = new MutableLiveData<>();
        enviarGetEventosEntrevista(entrevista, mutableLiveData);

        return mutableLiveData;
    }

    /**
     * Funcion encargada de enviar la peticion GET hacia el servidor
     *
     * @param entrevista      Datos de entrevista para realizar la busqueda
     * @param mutableLiveData Mutable Live Data usado para enviar datos a viewModel
     */
    private void enviarGetEventosEntrevista(Entrevista entrevista, final MutableLiveData<List<Evento>> mutableLiveData) {
        eventoList = new ArrayList<>();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray("data");

                    for (int i = 0; i < jsonData.length(); i++) {
                        JSONObject jsonEvento = jsonData.getJSONObject(i);

                        JSONObject jsonAttributes = jsonEvento.getJSONObject("attributes");

                        Evento evento = new Evento();
                        evento.setId(jsonEvento.getInt("id"));

                        Entrevista e = new Entrevista();
                        e.setId(jsonAttributes.getInt("id_entrevista"));
                        evento.setEntrevista(e);

                        evento.setJustificacion(jsonAttributes.getString("justificacion"));
                        evento.setHora_evento(jsonAttributes.getString("hora_evento"));

                        JSONObject jsonRelationships = jsonEvento.getJSONObject("relationships");

                        JSONObject jsonAccionData = jsonRelationships.getJSONObject("accion").getJSONObject("data");
                        Accion accion = new Accion();
                        accion.setId(jsonAccionData.getInt("id"));
                        accion.setNombre(jsonAccionData.getString("nombre"));
                        evento.setAccion(accion);

                        JSONObject jsonEmoticonData = jsonRelationships.getJSONObject("emoticon").getJSONObject("data");
                        Emoticon emoticon = new Emoticon();
                        emoticon.setId(jsonEmoticonData.getInt("id"));
                        emoticon.setDescripcion(jsonEmoticonData.getString("descripcion"));
                        emoticon.setUrl(jsonEmoticonData.getString("url"));
                        evento.setEmoticon(emoticon);

                        eventoList.add(evento);
                    }

                    mutableLiveData.postValue(eventoList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Log.d("VOLLEY_EVENTOS", "TIMEOUT_ERROR");
                    responseErrorMsg.postValue("Servidor no responde, intente más tarde");
                } else if (error instanceof NetworkError) {
                    Log.d("VOLLEY_EVENTOS", "NETWORK_ERROR");
                    responseErrorMsg.postValue("No tienes conexión a Internet");
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
                        Log.d("VOLLEY_EVENTOS", "AUTHENTICATION_ERROR: " + errorObject);
                        responseErrorMsg.postValue("Acceso no autorizado");
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d("VOLLEY_EVENTOS", "SERVER_ERROR: " + errorObject);
                        responseErrorMsg.postValue("Servidor no responde, intente más tarde");
                    }
                }
            }
        };

        String url = "http://192.168.0.14/entrevistas/" + entrevista.getId() + "/eventos";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, responseListener, errorListener) {
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

        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_GET_EVENTOS_ENTREVISTA);

    }
}
