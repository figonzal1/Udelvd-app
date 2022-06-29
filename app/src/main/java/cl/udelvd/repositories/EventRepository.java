package cl.udelvd.repositories;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.Response;
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
import cl.udelvd.models.Action;
import cl.udelvd.models.Emoticon;
import cl.udelvd.models.Event;
import cl.udelvd.models.Interview;
import cl.udelvd.services.VolleySingleton;
import cl.udelvd.utils.SingleLiveEvent;
import cl.udelvd.utils.Utils;

public class EventRepository {

    private static final String TAG_GET_INTERVIEW_EVENTS = "ListaEventosEntrevista";
    private static final String TAG_NEW_EVENTS = "CrearEvento";
    private static final String TAG_GET_EVENT = "ObtenerEvento";
    private static final String TAG_UPDATE_EVENT = "ActualizarEvento";
    private static final String TAG_DELETE_EVENT = "EliminarEvento";

    private static EventRepository instance;
    private final Application application;

    //LIST
    private final List<Event> eventList = new ArrayList<>();
    private final SingleLiveEvent<List<Event>> eventsMutableLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseErrorMsgList = new SingleLiveEvent<>();

    //REGISTRY
    private final SingleLiveEvent<String> responseMsgRegistry = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseErrorMsgRegistry = new SingleLiveEvent<>();

    //GET EVENT & UPDATE
    private final SingleLiveEvent<Event> eventMutableLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseErrorMsgEvent = new SingleLiveEvent<>(); //Load specific event
    private final SingleLiveEvent<String> responseErrorMsgUpdate = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgUpdate = new SingleLiveEvent<>();

    //DELETE EVENT
    private final SingleLiveEvent<String> responseErrorMsgDelete = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgDelete = new SingleLiveEvent<>();

    //PROGRESS DIALOG
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private EventRepository(Application application) {
        this.application = application;
    }

    public static EventRepository getInstance(Application application) {

        if (instance == null) {
            instance = new EventRepository(application);
        }

        return instance;
    }

    public SingleLiveEvent<List<Event>> getInterviewEvents(Interview interview) {

        sendGetInterviewEvents(interview);

        return eventsMutableLiveData;
    }

    private void sendGetInterviewEvents(Interview interview) {
        eventList.clear();

        Response.Listener<String> responseListener = response -> {
            //Log.d("RESPONSE", response);
            try {
                JSONObject jsonObject = new JSONObject(response);

                JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                for (int i = 0; i < jsonData.length(); i++) {
                    JSONObject jsonEvent = jsonData.getJSONObject(i);

                    JSONObject jsonAttributes = jsonEvent.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    Event event = new Event();
                    event.setId(jsonEvent.getInt(application.getString(R.string.KEY_EVENTO_ID)));

                    Interview e = new Interview();
                    e.setId(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTA_ID_LARGO)));
                    event.setInterview(e);

                    event.setJustification(jsonAttributes.getString(application.getString(R.string.KEY_EVENTO_JUSTIFICACION)));

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(application.getString(R.string.FORMATO_HORA), Locale.US);
                    event.setEventHour(simpleDateFormat.parse(jsonAttributes.getString(application.getString(R.string.KEY_EVENTO_HORA_EVENTO))));

                    JSONObject jsonRelationships = jsonEvent.getJSONObject(application.getString(R.string.JSON_RELATIONSHIPS));

                    JSONObject jsonActionData = jsonRelationships.getJSONObject(application.getString(R.string.KEY_ACCION_OBJECT)).getJSONObject(application.getString(R.string.JSON_DATA));
                    Action action = new Action();
                    action.setId(jsonActionData.getInt(application.getString(R.string.KEY_ACCION_ID)));
                    action.setName(jsonActionData.getString(application.getString(R.string.KEY_ACCION_NOMBRE)));
                    event.setAction(action);

                    JSONObject jsonEmoticonData = jsonRelationships
                            .getJSONObject(application.getString(R.string.KEY_EMOTICON_OBJECT))
                            .getJSONObject(application.getString(R.string.JSON_DATA));
                    Emoticon emoticon = new Emoticon();
                    emoticon.setId(jsonEmoticonData.getInt(application.getString(R.string.KEY_EMOTICON_ID)));
                    emoticon.setDescription(jsonEmoticonData.getString(application.getString(R.string.KEY_EMOTICON_DESCRIPCION)));
                    emoticon.setUrl(jsonEmoticonData.getString(application.getString(R.string.KEY_EMOTICON_URL)));
                    event.setEmoticon(emoticon);

                    eventList.add(event);
                }

                eventsMutableLiveData.postValue(eventList);

                isLoading.postValue(false);

            } catch (JSONException | ParseException e) {

                Log.d("JSON_ERROR", "GET INTERVIEW EVENTS JSON ERROR PARSE");
                e.printStackTrace();
            }
        };

        Response.ErrorListener errorListener = error -> {

            isLoading.postValue(false);

            Utils.deadAPIHandler(
                    error,
                    application,
                    responseErrorMsgList,
                    application.getString(R.string.TAG_VOLLEY_ERR_EVENTOS)
            );
        };

        String url = String.format(application.getString(R.string.URL_GET_EVENTO), application.getString(R.string.HEROKU_DOMAIN), interview.getId(), Utils.getLanguage(application));

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
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_GET_INTERVIEW_EVENTS);

    }

    public void registryEvent(Event event) {
        sendPostEvent(event);
    }

    private void sendPostEvent(final Event event) {

        Response.Listener<String> responseListener = response -> {

            //Log.d("RESPONSE_CREATE", response);
            try {
                JSONObject jsonObject = new JSONObject(response);

                JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));

                JSONObject jsonAttribute = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                Event eventInternet = new Event();

                eventInternet.setJustification(jsonAttribute.getString(application.getString(R.string.KEY_EVENTO_JUSTIFICACION)));

                Date eventHour = null;
                try {
                    eventHour = Utils.stringToDate(application, true, jsonAttribute.getString(application.getString(R.string.KEY_EVENTO_HORA_EVENTO)));
                } catch (ParseException e) {
                    Log.d("STRING_TO_DATE", "Parse exception error");
                    e.printStackTrace();
                }
                eventInternet.setEventHour(eventHour);

                Log.d("MEMORIA", event.toString());
                Log.d("INTERNET", event.toString());

                String createTime = jsonAttribute.getString(application.getString(R.string.KEY_CREATE_TIME));

                if (event.equals(eventInternet) && !createTime.isEmpty()) {
                    responseMsgRegistry.postValue(application.getString(R.string.MSG_REGISTRO_EVENTO));
                }

                isLoading.postValue(false);

            } catch (JSONException e) {

                Log.d("JSON_ERROR", "POST EVENT JSON ERROR PARSE");
                e.printStackTrace();
            }
        };

        Response.ErrorListener errorListener = error -> {

            isLoading.postValue(false);

            Utils.deadAPIHandler(
                    error,
                    application,
                    responseErrorMsgRegistry,
                    application.getString(R.string.TAG_VOLLEY_ERR_CREAR_EVENTO)
            );
        };

        String url = String.format(application.getString(R.string.URL_POST_EVENTO), application.getString(R.string.HEROKU_DOMAIN), event.getInterview().getId());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener, errorListener) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.KEY_EVENTO_JUSTIFICACION), event.getJustification());
                params.put(application.getString(R.string.KEY_EVENTO_ID_ACCION), String.valueOf(event.getAction().getId()));
                params.put(application.getString(R.string.KEY_EVENTO_ID_EMOTICON), String.valueOf(event.getEmoticon().getId()));
                params.put(application.getString(R.string.KEY_EVENTO_HORA_EVENTO), Utils.dateToString(application, true, event.getEventHour()));

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
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_NEW_EVENTS);
    }

    public SingleLiveEvent<Event> getEvent(Event eventIntent) {

        sendGetEvent(eventIntent);

        return eventMutableLiveData;
    }

    private void sendGetEvent(Event eventIntent) {

        Response.Listener<String> responseListener = response -> {

            try {

                JSONObject jsonObject = new JSONObject(response);

                JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));

                JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                Event event = new Event();
                event.setId(jsonData.getInt(application.getString(R.string.KEY_EVENTO_ID)));

                event.setJustification(jsonAttributes.getString(application.getString(R.string.KEY_EVENTO_JUSTIFICACION)));

                try {
                    event.setEventHour(Utils.stringToDate(application, true, jsonAttributes.getString(application.getString(R.string.KEY_EVENTO_HORA_EVENTO))));
                } catch (ParseException e) {

                    Log.d("STRING_TO_DATE", "Parse exception error");
                    e.printStackTrace();
                }

                Action action = new Action();
                action.setId(jsonAttributes.getInt(application.getString(R.string.KEY_EVENTO_ID_ACCION)));
                event.setAction(action);

                Emoticon emoticon = new Emoticon();
                emoticon.setId(jsonAttributes.getInt(application.getString(R.string.KEY_EVENTO_ID_EMOTICON)));
                event.setEmoticon(emoticon);

                Interview interview = new Interview();
                interview.setId(jsonAttributes.getInt(application.getString(R.string.KEY_EVENTO_ID_ENTREVISTA)));
                event.setInterview(interview);

                eventMutableLiveData.postValue(event);

                isLoading.postValue(false);

            } catch (JSONException e) {

                Log.d("JSON_ERROR", "EVENT LIST JSON ERROR PARSE");
                e.printStackTrace();
            }
        };

        Response.ErrorListener errorListener = error -> {

            isLoading.postValue(false);

            Utils.deadAPIHandler(
                    error,
                    application,
                    responseErrorMsgEvent,
                    application.getString(R.string.TAG_VOLLEY_ERR_EVENTO)
            );
        };

        String url = String.format(Locale.US, application.getString(R.string.URL_GET_EVENTO_ESPECIFICO), application.getString(R.string.HEROKU_DOMAIN), eventIntent.getInterview().getId(), eventIntent.getId());

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
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_GET_EVENT);
    }

    public void updateEvent(Event event) {
        sendPutEvent(event);
    }

    private void sendPutEvent(final Event event) {

        Response.Listener<String> responseListener = response -> {

            try {
                JSONObject jsonObject = new JSONObject(response);

                JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));

                JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                Event eventInternet = new Event();
                eventInternet.setId(jsonData.getInt(application.getString(R.string.KEY_EVENTO_ID)));

                try {
                    eventInternet.setEventHour(Utils.stringToDate(application, true, jsonAttributes.getString(application.getString(R.string.KEY_EVENTO_HORA_EVENTO))));
                } catch (ParseException e) {
                    Log.d("STRING_TO_DATE", "Parse exception error");
                    e.printStackTrace();
                }
                eventInternet.setJustification(jsonAttributes.getString(application.getString(R.string.KEY_EVENTO_JUSTIFICACION)));

                String updateTime = jsonAttributes.getString(application.getString(R.string.KEY_UPDATE_TIME));

                if (event.equals(eventInternet) && !updateTime.isEmpty()) {

                    responseMsgUpdate.postValue(application.getString(R.string.MSG_UPDATE_EVENTO));
                }

                isLoading.postValue(false);

            } catch (JSONException e) {

                Log.d("JSON_ERROR", "PUT EVENT JSON ERROR PARSE");
                e.printStackTrace();
            }
        };

        Response.ErrorListener errorListener = error -> {

            isLoading.postValue(false);

            Utils.deadAPIHandler(
                    error,
                    application,
                    responseErrorMsgUpdate,
                    application.getString(R.string.TAG_VOLLEY_ERR_EDITAR_EVENTO)
            );
        };

        String url = String.format(application.getString(R.string.URL_PUT_EVENTO), application.getString(R.string.HEROKU_DOMAIN), event.getInterview().getId(), event.getId());

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, responseListener, errorListener) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.KEY_EVENTO_JUSTIFICACION), event.getJustification());
                params.put(application.getString(R.string.KEY_EVENTO_ID_ACCION), String.valueOf(event.getAction().getId()));
                params.put(application.getString(R.string.KEY_EVENTO_ID_EMOTICON), String.valueOf(event.getEmoticon().getId()));

                params.put(application.getString(R.string.KEY_EVENTO_HORA_EVENTO), Utils.dateToString(application, true, event.getEventHour()));

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

        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_UPDATE_EVENT);
    }

    public void deleteEvent(Event event) {
        sendDeleteEvent(event);
    }

    private void sendDeleteEvent(Event event) {

        Response.Listener<String> resposeListener = response -> {
            //Log.d("RESPONSe", response);

            try {
                JSONObject jsonObject = new JSONObject(response);

                JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                if (jsonData.length() == 0) {
                    responseMsgDelete.postValue(application.getString(R.string.MSG_DELETE_EVENTO));
                }
                isLoading.postValue(false);
            } catch (JSONException e) {

                Log.d("JSON_ERROR", "DELETE EVENT JSON ERROR PARSE");
                e.printStackTrace();
            }
        };

        Response.ErrorListener errorListener = error -> {

            isLoading.postValue(false);

            Utils.deadAPIHandler(
                    error,
                    application,
                    responseErrorMsgDelete,
                    application.getString(R.string.TAG_VOLLEY_ERR_ELIMINAR_ENTREVISTA)
            );
        };

        String url = String.format(Locale.US, application.getString(R.string.URL_DELETE_EVENTO), application.getString(R.string.HEROKU_DOMAIN), event.getInterview().getId(), event.getId());

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, resposeListener, errorListener) {
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

        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_DELETE_EVENT);
    }

    /*
    GETTERS
     */
    public SingleLiveEvent<String> getResponseMsgRegistry() {
        return responseMsgRegistry;
    }

    public SingleLiveEvent<String> getResponseErrorMsgRegistry() {
        return responseErrorMsgRegistry;
    }

    public SingleLiveEvent<String> getResponseErrorMsgList() {
        return responseErrorMsgList;
    }

    public SingleLiveEvent<String> getResponseMsgUpdate() {
        return responseMsgUpdate;
    }

    public SingleLiveEvent<String> getResponseErrorMsgUpdate() {
        return responseErrorMsgUpdate;
    }

    public SingleLiveEvent<String> getResponseErrorMsgEvent() {
        return responseErrorMsgEvent;
    }

    public SingleLiveEvent<String> getResponseMsgDelete() {
        return responseMsgDelete;
    }

    public SingleLiveEvent<String> getResponseErrorMsgDelete() {
        return responseErrorMsgDelete;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
