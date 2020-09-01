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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cl.udelvd.R;
import cl.udelvd.models.Interview;
import cl.udelvd.models.InterviewType;
import cl.udelvd.models.Interviewee;
import cl.udelvd.services.VolleySingleton;
import cl.udelvd.utils.SingleLiveEvent;
import cl.udelvd.utils.Utils;

public class InterviewRepository {


    private static final String TAG_GET_INTERVIEWS = "ListadoEntrevistas";
    private final Application application;
    private static final String TAG_GET_INTERVIEW = "ObtenerEntrevista";
    private static final String TAG_NEW_INTERVIEW = "NuevaEntrevista";
    private static final String TAG_UPDATE_INTERVIEW = "ActualizarEntrevista";
    private static final String TAG_DELETE_INTERVIEW = "EliminarEntrevista";
    private static InterviewRepository instance;
    //LIST
    private final List<Interview> interviewList = new ArrayList<>();
    private final SingleLiveEvent<List<Interview>> interviewsMutableLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgErrorList = new SingleLiveEvent<>();

    //REGISTRY
    private final SingleLiveEvent<String> responseMsgRegistry = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgErrorRegistry = new SingleLiveEvent<>();

    //UPDATE
    private final SingleLiveEvent<String> responseMsgErrorInterview = new SingleLiveEvent<>();
    private final SingleLiveEvent<Interview> interviewMutableLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgUpdate = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgErrorUpdate = new SingleLiveEvent<>();

    //DELETE
    private final SingleLiveEvent<String> responseMsgDelete = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgErrorDelete = new SingleLiveEvent<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private InterviewRepository(Application application) {
        this.application = application;
    }

    public static InterviewRepository getInstance(Application application) {

        if (instance == null) {
            instance = new InterviewRepository(application);
        }
        return instance;
    }

    public SingleLiveEvent<List<Interview>> getPersonalInterviews(Interviewee interviewee) {

        sendGetPersonalInterviews(interviewee);

        return interviewsMutableLiveData;
    }

    private void sendGetPersonalInterviews(Interviewee interviewee) {

        interviewList.clear();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject;
                    jsonObject = new JSONObject(response);
                    JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                    for (int i = 0; i < jsonData.length(); i++) {

                        JSONObject jsonInterview = jsonData.getJSONObject(i);
                        JSONObject jsonAttributes = jsonInterview.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                        Interview interview = new Interview();
                        interview.setId(jsonInterview.getInt(application.getString(R.string.KEY_ENTREVISTA_ID)));
                        interview.setIdInterviewee(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTA_ID_ENTREVISTADO)));

                        Date interviewDate = Utils.stringToDate(application, false, jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTA_FECHA_ENTREVISTA)));
                        interview.setInterviewDate(interviewDate);

                        //Relationship
                        JSONObject jsonRelationship = jsonInterview.getJSONObject(application.getString(R.string.JSON_RELATIONSHIPS))
                                .getJSONObject(application.getString(R.string.KEY_TIPO_ENTREVISTA_OBJECT))
                                .getJSONObject(application.getString(R.string.JSON_DATA));

                        InterviewType interviewType = new InterviewType();
                        interviewType.setId(jsonRelationship.getInt(application.getString(R.string.KEY_TIPO_ENTREVISTA_ID)));
                        interviewType.setName(jsonRelationship.getString(application.getString(R.string.KEY_TIPO_ENTREVISTA_NOMBRE)));
                        interview.setInterviewType(interviewType);

                        interviewList.add(interview);
                    }

                    interviewsMutableLiveData.postValue(interviewList);
                    isLoading.postValue(false);

                } catch (JSONException e) {

                    Log.d("JSON_ERROR", "PERSONAL INTERVIEWS JSON ERROR PARSE");
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isLoading.postValue(false);

                Utils.deadAPIHandler(
                        error,
                        application,
                        responseMsgErrorList,
                        application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA)
                );

            }
        };


        String url = String.format(Locale.US, application.getString(R.string.URL_GET_ENTREVISTAS), application.getString(R.string.HEROKU_DOMAIN), interviewee.getId(), Utils.getLanguage(application));

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
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_GET_INTERVIEWS);
    }

    public void registryInterview(Interview interview) {
        sendPostInterview(interview);
    }

    private void sendPostInterview(final Interview interview) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));

                    JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    Interview interviewInternet = new Interview();
                    interviewInternet.setId(jsonData.getInt(application.getString(R.string.KEY_ENTREVISTA_ID)));

                    interviewInternet.setIdInterviewee(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTA_ID_ENTREVISTADO)));

                    Date interviewDate = Utils.stringToDate(application, false, jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTA_FECHA_ENTREVISTA)));
                    interviewInternet.setInterviewDate(interviewDate);

                    InterviewType interviewType = new InterviewType();
                    interviewType.setId(jsonAttributes.getInt(application.getString(R.string.KEY_TIPO_CONVIVENCIA_ID_LARGO)));
                    interviewInternet.setInterviewType(interviewType);

                    String create_time = jsonAttributes.getString(application.getString(R.string.KEY_CREATE_TIME));

                    Log.d("MEMORIA", interview.toString());
                    Log.d("INTERNET", interviewInternet.toString());

                    if (interview.equals(interviewInternet) && !create_time.isEmpty()) {
                        responseMsgRegistry.postValue(application.getString(R.string.MSG_REGISTRO_ENTREVISTA));
                    }
                    isLoading.postValue(false);

                } catch (JSONException e) {

                    Log.d("JSON_ERROR", "POST INTERVIEW JSON ERROR PARSE");
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isLoading.postValue(false);

                Utils.deadAPIHandler(
                        error,
                        application,
                        responseMsgErrorRegistry,
                        application.getString(R.string.TAG_VOLLEY_ERR_NUEVA_ENTREVISTA)
                );

            }
        };

        String url = String.format(application.getString(R.string.URL_POST_ENTREVISTAS), application.getString(R.string.HEROKU_DOMAIN), interview.getIdInterviewee());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener, errorListener) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.KEY_TIPO_CONVIVENCIA_ID_LARGO), String.valueOf(interview.getInterviewType().getId()));

                String fechaEntrevista = Utils.dateToString(application, false, interview.getInterviewDate());
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
        //VolleySingleton.getInstance(application).addToRequestQueue(stringRequest,
        //        new HurlStack(null, SSLConection.getSocketFactory(application.getApplicationContext())));
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_NEW_INTERVIEW);

    }

    public SingleLiveEvent<Interview> getPersonalInterview(Interview interview) {

        sendGetPersonalInterview(interview);

        return interviewMutableLiveData;
    }

    private void sendGetPersonalInterview(Interview interview) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d("RESPONSE_GET_ENTREVISTA", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));

                    JSONObject jsonAttribute = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    Interview interviewInternet = new Interview();
                    interviewInternet.setId(jsonData.getInt(application.getString(R.string.KEY_ENTREVISTA_ID)));
                    interviewInternet.setIdInterviewee(jsonAttribute.getInt(application.getString(R.string.KEY_ENTREVISTA_ID_ENTREVISTADO)));

                    InterviewType interviewType = new InterviewType();
                    interviewType.setId(jsonAttribute.getInt(application.getString(R.string.KEY_TIPO_ENTREVISTA_ID_LARGO)));

                    interviewInternet.setInterviewType(interviewType);

                    Date interviewDate = Utils.stringToDate(application, false, jsonAttribute.getString(application.getString(R.string.KEY_ENTREVISTA_FECHA_ENTREVISTA)));
                    interviewInternet.setInterviewDate(interviewDate);

                    interviewMutableLiveData.postValue(interviewInternet);

                    isLoading.postValue(false);

                } catch (JSONException e) {

                    Log.d("JSON_ERROR", "PERSONAL INTERVIEW JSON ERROR PARSE");
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isLoading.postValue(false);

                Utils.deadAPIHandler(
                        error,
                        application,
                        responseMsgErrorInterview,
                        application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTA)
                );
            }
        };


        String url = String.format(application.getString(R.string.URL_GET_ENTREVISTA_ESPECIFICA), application.getString(R.string.HEROKU_DOMAIN), interview.getIdInterviewee(), interview.getId());

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
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_GET_INTERVIEW);

    }

    public void updateInterview(Interview interview) {
        sendPutInterview(interview);
    }

    private void sendPutInterview(final Interview interview) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE_EDIT", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));

                    JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    Interview interviewInternet = new Interview();
                    interviewInternet.setId(jsonData.getInt(application.getString(R.string.KEY_ENTREVISTA_ID)));
                    interviewInternet.setIdInterviewee(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTA_ID_ENTREVISTADO)));

                    Date interviewDate = Utils.stringToDate(application, false, jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTA_FECHA_ENTREVISTA)));
                    interviewInternet.setInterviewDate(interviewDate);

                    String updateTime = jsonAttributes.getString(application.getString(R.string.KEY_UPDATE_TIME));

                    if (interview.equals(interviewInternet) && !updateTime.isEmpty()) {
                        responseMsgUpdate.postValue(application.getString(R.string.MSG_UPDATE_ENTREVISTA));
                    }
                    isLoading.postValue(false);

                } catch (JSONException e) {

                    Log.d("JSON_ERROR", "PUT INTERVIEW JSON ERROR PARSE");
                    e.printStackTrace();
                }

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isLoading.postValue(false);

                Utils.deadAPIHandler(
                        error,
                        application,
                        responseMsgErrorUpdate,
                        application.getString(R.string.TAG_VOLLEY_ERR_EDITAR_ENTREVISTA)
                );
            }
        };

        String url = String.format(application.getString(R.string.URL_PUT_ENTREVISTA_ESPECIFICA), application.getString(R.string.HEROKU_DOMAIN), interview.getIdInterviewee(), interview.getId());

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.KEY_TIPO_CONVIVENCIA_ID_LARGO), String.valueOf(interview.getInterviewType().getId()));

                String fechaEntrevista = Utils.dateToString(application, false, interview.getInterviewDate());
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
        //VolleySingleton.getInstance(application).addToRequestQueue(stringRequest,
        //        new HurlStack(null, SSLConection.getSocketFactory(application.getApplicationContext())));
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_UPDATE_INTERVIEW);
    }

    public void deleteInterview(Interview interview) {
        sendDeleteInterview(interview);
    }

    private void sendDeleteInterview(Interview interview) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                    if (jsonData.length() == 0) {
                        responseMsgDelete.postValue(application.getString(R.string.MSG_DELETE_ENTREVISTA));
                    }

                    isLoading.postValue(false);

                } catch (JSONException e) {

                    Log.d("JSON_ERROR", "DELETE INTERVIEW JSON ERROR PARSE");
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isLoading.postValue(false);

                Utils.deadAPIHandler(
                        error,
                        application,
                        responseMsgErrorDelete,
                        application.getString(R.string.TAG_VOLLEY_ERR_ELIMINAR_ENTREVISTA)
                );
            }
        };

        String url = String.format(Locale.US, application.getString(R.string.URL_DELETE_ENTREVISTA_ESPECIFICA), application.getString(R.string.HEROKU_DOMAIN), interview.getIdInterviewee(), interview.getId());

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
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_DELETE_INTERVIEW);
    }

    /*
    GETTERS
     */
    public SingleLiveEvent<String> getResponseMsgErrorList() {
        return responseMsgErrorList;
    }

    public SingleLiveEvent<String> getResponseMsgRegistry() {
        return responseMsgRegistry;
    }

    public SingleLiveEvent<String> getResponseMsgErrorRegistry() {
        return responseMsgErrorRegistry;
    }

    public SingleLiveEvent<String> getResponseMsgErrorInterview() {
        return responseMsgErrorInterview;
    }

    public SingleLiveEvent<String> getResponseMsgUpdate() {
        return responseMsgUpdate;
    }

    public SingleLiveEvent<String> getResponseMsgErrorUpdate() {
        return responseMsgErrorUpdate;
    }

    public SingleLiveEvent<String> getResponseMsgDelete() {
        return responseMsgDelete;
    }

    public SingleLiveEvent<String> getResponseMsgErrorDelete() {
        return responseMsgErrorDelete;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
