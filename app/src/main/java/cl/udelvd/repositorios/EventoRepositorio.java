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

import cl.udelvd.R;
import cl.udelvd.modelo.Accion;
import cl.udelvd.modelo.Emoticon;
import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.Evento;
import cl.udelvd.servicios.VolleySingleton;
import cl.udelvd.utilidades.SingleLiveEvent;
import cl.udelvd.utilidades.Utils;

public class EventoRepositorio {

    private static final String TAG_GET_EVENTOS_ENTREVISTA = "ListaEventosEntrevista";
    private static final String TAG_CREAR_EVENTO = "CrearEvento";

    private static EventoRepositorio instancia;
    private Application application;

    private List<Evento> eventoList = new ArrayList<>();

    private MutableLiveData<List<Evento>> eventosMutableLiveData = new MutableLiveData<>();

    private SingleLiveEvent<String> responseErrorMsg = new SingleLiveEvent<>();
    private SingleLiveEvent<String> responseMsgRegistro = new SingleLiveEvent<>();


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

    public SingleLiveEvent<String> getResponseMsgRegistro() {
        return responseMsgRegistro;
    }

    /**
     * Funcion encargada de obtener los eventos de una entrevista dada
     *
     * @param entrevista Datos de entrevista para realizar la busqueda
     * @return mutableLiveData Mutable live data con los datos de la entrevista
     */
    public MutableLiveData<List<Evento>> obtenerEventosEntrevista(Entrevista entrevista) {
        enviarGetEventosEntrevista(entrevista);
        return eventosMutableLiveData;
    }

    /**
     * Funcion encargada de enviar la peticion GET hacia el servidor
     *
     * @param entrevista Datos de entrevista para realizar la busqueda
     */
    private void enviarGetEventosEntrevista(Entrevista entrevista) {
        eventoList.clear();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                    for (int i = 0; i < jsonData.length(); i++) {
                        JSONObject jsonEvento = jsonData.getJSONObject(i);

                        JSONObject jsonAttributes = jsonEvento.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                        Evento evento = new Evento();
                        evento.setId(jsonEvento.getInt(application.getString(R.string.KEY_EVENTO_ID)));

                        Entrevista e = new Entrevista();
                        e.setId(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTA_ID_LARGO)));
                        evento.setEntrevista(e);

                        evento.setJustificacion(jsonAttributes.getString(application.getString(R.string.KEY_EVENTO_JUSTIFICACION)));

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(application.getString(R.string.FORMATO_HORA), Locale.US);
                        evento.setHora_evento(simpleDateFormat.parse(jsonAttributes.getString(application.getString(R.string.KEY_EVENTO_HORA_EVENTO))));

                        JSONObject jsonRelationships = jsonEvento.getJSONObject(application.getString(R.string.JSON_RELATIONSHIPS));

                        JSONObject jsonAccionData = jsonRelationships.getJSONObject(application.getString(R.string.KEY_ACCION_OBJECT)).getJSONObject(application.getString(R.string.JSON_DATA));
                        Accion accion = new Accion();
                        accion.setId(jsonAccionData.getInt(application.getString(R.string.KEY_ACCION_ID)));
                        accion.setNombre(jsonAccionData.getString(application.getString(R.string.KEY_ACCION_NOMBRE)));
                        evento.setAccion(accion);

                        JSONObject jsonEmoticonData = jsonRelationships
                                .getJSONObject(application.getString(R.string.KEY_EMOTICON_OBJECT))
                                .getJSONObject(application.getString(R.string.JSON_DATA));
                        Emoticon emoticon = new Emoticon();
                        emoticon.setId(jsonEmoticonData.getInt(application.getString(R.string.KEY_EMOTICON_ID)));
                        emoticon.setDescripcion(jsonEmoticonData.getString(application.getString(R.string.KEY_EMOTICON_DESCRIPCION)));
                        emoticon.setUrl(jsonEmoticonData.getString(application.getString(R.string.KEY_EMOTICON_URL)));
                        evento.setEmoticon(emoticon);

                        eventoList.add(evento);
                    }

                    eventosMutableLiveData.postValue(eventoList);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_EVENTOS), application.getString(R.string.TIMEOUT_ERROR));
                    responseErrorMsg.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                } else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_EVENTOS), application.getString(R.string.NETWORK_ERROR));
                    responseErrorMsg.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_EVENTOS), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                        responseErrorMsg.postValue(application.getString(R.string.AUTHENTICATION_ERROR_MSG_VM));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_EVENTOS), String.format("%s %s", application.getString(R.string.SERVER_ERROR), errorObject));
                        responseErrorMsg.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                    }
                }
            }
        };

        String url = String.format(application.getString(R.string.URL_GET_EVENTO), entrevista.getId());

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

        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_GET_EVENTOS_ENTREVISTA);

    }

    /**
     * Funcion encargada de registrar evento en el sistema
     *
     * @param evento Objeto evento con la informacion
     */
    public void registrarEvento(Evento evento) {
        sendPostEvento(evento);
    }

    /**
     * Funcion encargada de enviar mediante POST un evento
     *
     * @param evento Objeto evento con la informacion asociada
     */
    private void sendPostEvento(final Evento evento) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE_CREATE", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));

                    JSONObject jsonAttribute = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    Evento eventoInternet = new Evento();

                    eventoInternet.setJustificacion(jsonAttribute.getString(application.getString(R.string.KEY_EVENTO_JUSTIFICACION)));

                    Date horaEvento = Utils.stringToDate(application, true, jsonAttribute.getString(application.getString(R.string.KEY_EVENTO_HORA_EVENTO)));
                    eventoInternet.setHora_evento(horaEvento);

                    Log.d("MEMORIA", evento.toString());
                    Log.d("INTERNET", evento.toString());

                    String create_time = jsonAttribute.getString(application.getString(R.string.KEY_CREATE_TIME));

                    if (evento.equals(eventoInternet) && !create_time.isEmpty()) {
                        responseMsgRegistro.postValue(application.getString(R.string.MSG_REGISTRO_EVENTO));
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
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_CREAR_EVENTO), application.getString(R.string.TIMEOUT_ERROR));
                    //TODO: AGREGAR ERROR
                }

                //Error de conexion a internet
                else if (error instanceof NetworkError) {
                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_CREAR_EVENTO), application.getString(R.string.NETWORK_ERROR));
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
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_CREAR_EVENTO), String.format("%s %s", application.getString(R.string.AUTHENTICATION_ERROR), errorObject));
                    }

                    //Error de servidor
                    else if (error instanceof ServerError) {
                        Log.d(application.getString(R.string.TAG_VOLLEY_ERR_CREAR_EVENTO), String.format("%s %s", application.getString(R.string.SERVER_ERROR), errorObject));
                    }
                }
            }
        };

        String url = String.format(application.getString(R.string.URL_POST_EVENTO), evento.getEntrevista().getId());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener, errorListener) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.KEY_EVENTO_JUSTIFICACION), evento.getJustificacion());
                params.put(application.getString(R.string.KEY_EVENTO_ID_ACCION), String.valueOf(evento.getAccion().getId()));
                params.put(application.getString(R.string.KEY_EVENTO_ID_EMOTICON), String.valueOf(evento.getEmoticon().getId()));

                params.put(application.getString(R.string.KEY_EVENTO_HORA_EVENTO), Utils.dateToString(application, true, evento.getHora_evento()));

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
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_CREAR_EVENTO);
    }
}
