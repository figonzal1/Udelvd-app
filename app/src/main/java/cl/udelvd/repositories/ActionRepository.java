package cl.udelvd.repositories;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.Response;
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
import cl.udelvd.models.Action;
import cl.udelvd.services.VolleySingleton;
import cl.udelvd.utils.SingleLiveEvent;
import cl.udelvd.utils.Utils;

public class ActionRepository {

    private static final String TAG_GET_ACTIONS = "ListaAcciones";
    private static final String TAG_NUEVA_ACTION = "NuevaAccion";
    private static final String TAG_ELIMINAR_ACTION = "EliminarAccion";
    private static final String TAG_UPDATE_ACTION = "ActualizarAccion";

    private static ActionRepository instance;
    private final Application application;

    //LIST
    private final List<Action> actionList = new ArrayList<>();
    private final MutableLiveData<List<Action>> actionMutableLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<String> responseMsgErrorList = new SingleLiveEvent<>();

    //REGISTRY
    private SingleLiveEvent<String> responseMsgErrorRegistry = new SingleLiveEvent<>();
    private SingleLiveEvent<String> responseMsgRegistry = new SingleLiveEvent<>();

    //UPDATE
    private SingleLiveEvent<String> responseMsgUpdate = new SingleLiveEvent<>();
    private SingleLiveEvent<String> responseMsgErrorUpdate = new SingleLiveEvent<>();

    //DELETE
    private SingleLiveEvent<String> responseMsgDelete = new SingleLiveEvent<>();
    private SingleLiveEvent<String> responseMsgErrorDelete = new SingleLiveEvent<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private ActionRepository(Application application) {
        this.application = application;
    }

    public static ActionRepository getInstance(Application application) {

        if (instance == null) {
            instance = new ActionRepository(application);
        }

        return instance;
    }

    public MutableLiveData<List<Action>> getActionsByLanguage(String language) {

        sendGetActionsByLanguage(language);

        return actionMutableLiveData;
    }

    private void sendGetActionsByLanguage(String language) {

        actionList.clear();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                    for (int i = 0; i < jsonData.length(); i++) {

                        JSONObject jsonAction = jsonData.getJSONObject(i);
                        JSONObject jsonAttributes = jsonAction.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                        Action action = new Action();
                        action.setId(jsonAction.getInt(application.getString(R.string.KEY_ACCION_ID)));
                        action.setName(jsonAttributes.getString(application.getString(R.string.KEY_ACCION_NOMBRE)));

                        actionList.add(action);
                    }

                    actionMutableLiveData.postValue(actionList);

                    isLoading.postValue(false);

                } catch (JSONException e) {

                    Log.d("JSON_ERROR", "GET ACTIONS BY LANGUAGE JSON ERROR PARSE");
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isLoading.postValue(false);

                Utils.deadAPIHandler(error,
                        application,
                        responseMsgErrorList,
                        application.getString(R.string.TAG_VOLLEY_ERR_ACCION));
            }
        };

        String url = String.format(application.getString(R.string.URL_GET_ACCIONES_IDIOMA), application.getString(R.string.HEROKU_DOMAIN), language);

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
        //VolleySingleton.getInstance(application).addToRequestQueue(stringRequest,
        //        new HurlStack(null, SSLConection.getSocketFactory(application.getApplicationContext())));
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_GET_ACTIONS);
    }

    public MutableLiveData<List<Action>> getActions() {

        sendGetActions();

        return actionMutableLiveData;
    }

    private void sendGetActions() {

        actionList.clear();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                    for (int i = 0; i < jsonData.length(); i++) {

                        JSONObject jsonAccion = jsonData.getJSONObject(i);
                        JSONObject jsonAttributes = jsonAccion.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                        Action action = new Action();
                        action.setId(jsonAccion.getInt(application.getString(R.string.KEY_ACCION_ID)));
                        action.setNameEs(jsonAttributes.getString(application.getString(R.string.KEY_ACCION_NOMBRE_ES)));
                        action.setNameEng(jsonAttributes.getString(application.getString(R.string.KEY_ACCION_NOMBRE_EN)));

                        actionList.add(action);
                    }

                    actionMutableLiveData.postValue(actionList);

                    isLoading.postValue(false);

                } catch (JSONException e) {

                    Log.d("JSON_ERROR", "GET ACTIONS ALL JSON ERROR PARSE");
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isLoading.postValue(false);

                Utils.deadAPIHandler(error,
                        application,
                        responseMsgErrorList,
                        application.getString(R.string.TAG_VOLLEY_ERR_ACCION));
            }
        };

        String url = String.format(application.getString(R.string.URL_GET_ACCIONES), application.getString(R.string.HEROKU_DOMAIN));

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
        //VolleySingleton.getInstance(application).addToRequestQueue(stringRequest,
        //        new HurlStack(null, SSLConection.getSocketFactory(application.getApplicationContext())));
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_GET_ACTIONS);
    }

    public void registryAction(Action actionIntent) {
        sendPostAction(actionIntent);
    }

    private void sendPostAction(final Action actionIntent) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));
                    JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    Action actionInternet = new Action();
                    actionInternet.setNameEng(jsonAttributes.getString(application.getString(R.string.KEY_ACCION_NOMBRE_EN)));
                    actionInternet.setNameEs(jsonAttributes.getString(application.getString(R.string.KEY_ACCION_NOMBRE_ES)));

                    String create_time = jsonAttributes.getString(application.getString(R.string.KEY_CREATE_TIME));

                    Log.d("MEMORIA", actionIntent.getNameEs() + " - " + actionIntent.getNameEng());
                    Log.d("INTERNET", actionInternet.getNameEs() + " - " + actionInternet.getNameEng());

                    if (actionIntent.equals(actionInternet) && !create_time.isEmpty()) {
                        responseMsgRegistry.postValue(application.getString(R.string.MSG_REGISTRO_ACCION));
                    }

                    isLoading.postValue(false);

                } catch (JSONException e) {

                    Log.d("JSON_ERROR", "POST ACTIONS JSON ERROR");
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isLoading.postValue(false);

                Utils.deadAPIHandler(error,
                        application,
                        responseMsgErrorRegistry,
                        application.getString(R.string.TAG_VOLLEY_ERR_NUEVA_ACCION));

            }
        };

        String url = String.format(application.getString(R.string.URL_POST_ACCIONES), application.getString(R.string.HEROKU_DOMAIN));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.KEY_ACCION_NOMBRE_EN), actionIntent.getNameEng());
                params.put(application.getString(R.string.KEY_ACCION_NOMBRE_ES), actionIntent.getNameEs());

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
        //VolleySingleton.getInstance(application).addToRequestQueue(stringRequest,
        //        new HurlStack(null, SSLConection.getSocketFactory(application.getApplicationContext())));
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_NUEVA_ACTION);
    }

    public void updateAction(Action action) {
        sendPutAction(action);
    }

    private void sendPutAction(final Action action) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE_EDIT", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));

                    JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    Action actionInternet = new Action();
                    actionInternet.setId(jsonData.getInt(application.getString(R.string.KEY_ACCION_ID)));
                    actionInternet.setNameEs(jsonAttributes.getString(application.getString(R.string.KEY_ACCION_NOMBRE_ES)));
                    actionInternet.setNameEng(jsonAttributes.getString(application.getString(R.string.KEY_ACCION_NOMBRE_EN)));

                    String update_time = jsonAttributes.getString(application.getString(R.string.KEY_UPDATE_TIME));

                    if (action.equals(actionInternet) && !update_time.isEmpty()) {
                        responseMsgUpdate.postValue(application.getString(R.string.MSG_UPDATE_ACCION));
                    }

                    isLoading.postValue(false);

                } catch (JSONException e) {

                    Log.d("JSON_ERROR", "PUT ACTION JSON ERROR PARSE");
                    e.printStackTrace();
                }

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isLoading.postValue(false);

                Utils.deadAPIHandler(error,
                        application,
                        responseMsgErrorUpdate,
                        application.getString(R.string.TAG_VOLLEY_ERR_EDITAR_ACCION));
            }
        };

        String url = String.format(application.getString(R.string.URL_PUT_ACCIONES), application.getString(R.string.HEROKU_DOMAIN), action.getId());

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.KEY_ACCION_NOMBRE_EN), action.getNameEng());
                params.put(application.getString(R.string.KEY_ACCION_NOMBRE_ES), action.getNameEs());

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
        //VolleySingleton.getInstance(application).addToRequestQueue(stringRequest,
        //        new HurlStack(null, SSLConection.getSocketFactory(application.getApplicationContext())));
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_UPDATE_ACTION);
    }

    public void deleteAction(Action object) {
        sendDeleteAction(object);
    }

    private void sendDeleteAction(Action action) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                    if (jsonData.length() == 0) {
                        responseMsgDelete.postValue(application.getString(R.string.MSG_DELETE_ACCION));
                    }

                    isLoading.postValue(false);

                } catch (JSONException e) {

                    Log.d("JSON_ERROR", "DELETE ACTION JSON ERROR PARSE");
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isLoading.postValue(false);

                Utils.deadAPIHandler(error,
                        application,
                        responseMsgErrorDelete,
                        application.getString(R.string.TAG_VOLLEY_ERR_ELIMINAR_ACCION));
            }
        };

        String url = String.format(Locale.US, application.getString(R.string.URL_DELETE_ACCIONES), application.getString(R.string.HEROKU_DOMAIN), action.getId());

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
        //VolleySingleton.getInstance(application).addToRequestQueue(stringRequest,
        //        new HurlStack(null, SSLConection.getSocketFactory(application.getApplicationContext())));
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_ELIMINAR_ACTION);
    }

    /*
    GETTERS
     */
    public SingleLiveEvent<String> getResponseMsgRegistry() {
        return responseMsgRegistry;
    }

    public SingleLiveEvent<String> getResponseMsgErrorRegistry() {
        return responseMsgErrorRegistry;
    }

    public SingleLiveEvent<String> getResponseMsgErrorList() {
        return responseMsgErrorList;
    }

    public SingleLiveEvent<String> getResponseMsgErrorDelete() {
        return responseMsgErrorDelete;
    }

    public SingleLiveEvent<String> getResponseMsgDelete() {
        return responseMsgDelete;
    }

    public SingleLiveEvent<String> getResponseMsgUpdate() {
        return responseMsgUpdate;
    }

    public SingleLiveEvent<String> getResponseMsgErrorUpdate() {
        return responseMsgErrorUpdate;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
