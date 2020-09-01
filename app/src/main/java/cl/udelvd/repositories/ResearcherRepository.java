package cl.udelvd.repositories;

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
import cl.udelvd.models.Researcher;
import cl.udelvd.services.VolleySingleton;
import cl.udelvd.utils.SingleLiveEvent;
import cl.udelvd.utils.Utils;

public class ResearcherRepository {

    private static final String TAG_RESEARCHER_LIST = "InvestigadorListado";
    private static final String TAG_RESEARCHER_RESET = "ResetearPassword";
    private static final String TAG_RESEARCHER_RECOVERY = "RecuperarInvestigador";
    private static final Object TAG_RESEARCHER_ACTIVATION = "ActivacionInvestigador";
    private static final String TAG_RESEARCHER_REGISTRY = "RegistroInvestigador";
    private static final String TAG_RESEARCHER_LOGIN = "LoginInvestigador";
    private static final String TAG_RESEARCHER_UPDATE = "ActualizacionInvestigador";

    private static ResearcherRepository instance;
    private final Application application;
    private final SingleLiveEvent<List<Researcher>> researcherFirstPageLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<List<Researcher>> researcherSgtPageLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgErrorList = new SingleLiveEvent<>();

    //REGISTRY
    private final SingleLiveEvent<Map<String, String>> responseMsgRegistry = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgErrorRegistry = new SingleLiveEvent<>();
    //UPDATE
    private final SingleLiveEvent<String> responseMsgErrorUpdate = new SingleLiveEvent<>();

    //LOGIN
    private final SingleLiveEvent<Map<String, Object>> responseMsgLogin = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgErrorLogin = new SingleLiveEvent<>();
    private final SingleLiveEvent<Map<String, Object>> responseMsgUpdate = new SingleLiveEvent<>();
    //RECOVER ACCOUNT
    private final SingleLiveEvent<String> responseMsgErrorRecovery = new SingleLiveEvent<>();
    private final SingleLiveEvent<Map<String, String>> responseMsgRecovery = new SingleLiveEvent<>();
    //PASSWORD RESET
    private final SingleLiveEvent<String> responseMsgReset = new SingleLiveEvent<>();
    //ACTIVATION
    private final SingleLiveEvent<String> responseMsgErrorActivation = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgActivation = new SingleLiveEvent<>();
    private final MutableLiveData<Boolean> isActivated = new MutableLiveData<>();
    private final SingleLiveEvent<String> responseMsgErrorReset = new SingleLiveEvent<>();
    //LIST
    private List<Researcher> researcherList = new ArrayList<>();
    private List<Researcher> researcherSgtPage = new ArrayList<>();

    //PROGRESS DIALOG
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Integer> mutableNResearcher = new MutableLiveData<>();

    private ResearcherRepository(Application application) {
        this.application = application;
    }

    public static ResearcherRepository getInstance(Application application) {

        if (instance == null) {
            instance = new ResearcherRepository(application);
        }
        return instance;
    }

    public SingleLiveEvent<List<Researcher>> getResearchers(int page, Researcher researcher) {

        sendGetResearchers(page, researcher);

        if (page == 1) {
            return researcherFirstPageLiveData;
        } else {
            return researcherSgtPageLiveData;
        }
    }

    private void sendGetResearchers(final int page, final Researcher researcher) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d("RESPONSE", response);

                //Reiniciar listado cuando se pregunta por primera pagina
                if (page == 1) {
                    researcherList.clear();
                } else {
                    researcherSgtPage.clear();
                }


                try {
                    JSONObject jsonObject = new JSONObject(response);

                    /*
                    N INVESTIGADORES
                     */
                    JSONObject jsonInterviewee = jsonObject.getJSONObject(application.getString(R.string.KEY_INVES)).getJSONObject(application.getString(R.string.JSON_DATA));

                    int nInterviewees = jsonInterviewee.getInt(application.getString(R.string.KEY_INVES_N_INVESTIGADORES));
                    mutableNResearcher.postValue(nInterviewees);

                    JSONArray jsonArray = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonInve = jsonArray.getJSONObject(i);
                        JSONObject jsonAttributes = jsonInve.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                        Researcher inv = new Researcher();
                        inv.setId(jsonInve.getInt(application.getString(R.string.KEY_INVES_ID)));
                        inv.setName(jsonAttributes.getString(application.getString(R.string.KEY_INVES_NOMBRE)));
                        inv.setLastName(jsonAttributes.getString(application.getString(R.string.KEY_INVES_APELLIDO)));
                        inv.setEmail(jsonAttributes.getString(application.getString(R.string.KEY_INVES_EMAIL)));
                        inv.setIdRole(jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ID_ROL)));
                        inv.setCreateTime(jsonAttributes.getString(application.getString(R.string.KEY_CREATE_TIME)));

                        if (jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ACTIVADO)) == 0) {
                            inv.setActivated(false);
                        } else {
                            inv.setActivated(true);
                        }

                        JSONObject jsonRelationshipsData = jsonInve.getJSONObject(application.getString(R.string.JSON_RELATIONSHIPS))
                                .getJSONObject(application.getString(R.string.KEY_ROL_OBJECT))
                                .getJSONObject(application.getString(R.string.JSON_DATA));
                        inv.setIdRole(jsonRelationshipsData.getInt(application.getString(R.string.KEY_ROL_ID)));
                        inv.setRolName(jsonRelationshipsData.getString(application.getString(R.string.KEY_ROL_NOMBRE)));

                        if (page == 1) {
                            researcherList.add(inv);
                        } else {
                            researcherSgtPage.add(inv);
                        }

                    }

                    if (page == 1) {
                        //Log.d("CARGANDO_PAGINA", "1");
                        researcherFirstPageLiveData.postValue(researcherList);
                    } else {
                        //Log.d("CARGANDO_PAGINA", String.valueOf(page));
                        researcherSgtPageLiveData.postValue(researcherSgtPage); //Enviar a ViewModel listado paginado
                    }

                    isLoading.postValue(false);

                } catch (JSONException e) {

                    Log.d("JSON_ERROR", "GET RESEARCHERS PARSE ERROR");
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
                        application.getString(R.string.TAG_VOLLEY_ERR_INV_LISTA)
                );
            }
        };

        String url = String.format(application.getString(R.string.URL_GET_INVESTIGADORES), application.getString(R.string.HEROKU_DOMAIN), page, researcher.getId());

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
        if (page == 1) {
            isLoading.postValue(true);
        }

        //VolleySingleton.getInstance(application).addToRequestQueue(stringRequest,
        //        new HurlStack(null, SSLConection.getSocketFactory(application.getApplicationContext())));
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_RESEARCHER_LIST);
    }

    public void registryResearcher(Researcher researcher) {
        sendPostRegistry(researcher);
    }

    private void sendPostRegistry(final Researcher researcher) {

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d("RESPONSE",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));
                    JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    //GET RESPONSE
                    Researcher invResponse = new Researcher();
                    invResponse.setName(jsonAttributes.getString(application.getString(R.string.KEY_INVES_NOMBRE)));
                    invResponse.setLastName(jsonAttributes.getString(application.getString(R.string.KEY_INVES_APELLIDO)));
                    invResponse.setEmail(jsonAttributes.getString(application.getString(R.string.KEY_INVES_EMAIL)));

                    //GET ROL RESEARCHER
                    JSONObject jsonObjectRolData = jsonData
                            .getJSONObject(application.getString(R.string.JSON_RELATIONSHIPS))
                            .getJSONObject(application.getString(R.string.KEY_ROL_OBJECT))
                            .getJSONObject(application.getString(R.string.JSON_DATA));

                    invResponse.setRolName(jsonObjectRolData.getString(application.getString(R.string.KEY_ROL_NOMBRE)));

                    if (jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ACTIVADO)) == 0) {

                        invResponse.setActivated(false);

                    } else if (jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ACTIVADO)) == 1) {

                        invResponse.setActivated(true);
                    }

                    Log.d("MEMORIA", researcher.toString());
                    Log.d("INTERNET", invResponse.toString());

                    if (researcher.equals(invResponse)) {

                        Map<String, String> map = new HashMap<>();

                        map.put(application.getString(R.string.INTENT_KEY_MSG_REGISTRO), application.getString(R.string.MSG_INVEST_REGISTRADO));
                        map.put(application.getString(R.string.INTENT_KEY_INVES_ACTIVADO), String.valueOf(jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ACTIVADO))));

                        responseMsgRegistry.postValue(map);
                    }

                    isLoading.postValue(false);

                } catch (JSONException e) {

                    Log.d("JSON_ERROR", "REGISTRY JSON ERROR PARSE");
                    e.printStackTrace();
                }
            }
        };

        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isLoading.postValue(false);

                //TIMEOUT ERROR
                if (error instanceof TimeoutError) {

                    Log.d(application.getString(R.string.TAG_VOLLEY_INVES_REGISTRO), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgErrorRegistry.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //NETWORK ERROR
                else if (error instanceof NetworkError) {

                    Log.d(application.getString(R.string.TAG_VOLLEY_INVES_REGISTRO), application.getString(R.string.NETWORK_ERROR));
                    responseMsgErrorRegistry.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
                }

                //SERVER ERROR
                else if (error.networkResponse != null && error.networkResponse.data != null) {

                    String json = new String(error.networkResponse.data);

                    JSONObject errorObject;

                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        errorObject = jsonObject.getJSONObject(application.getString(R.string.JSON_ERROR));

                        //AUTH ERROR
                        if (error instanceof AuthFailureError) {

                            Log.d(application.getString(R.string.TAG_VOLLEY_INVES_REGISTRO), application.getString(R.string.AUTHENTICATION_ERROR) + errorObject);
                            responseMsgErrorRegistry.postValue(application.getString(R.string.AUTHENTICATION_ERROR_MSG_VM));
                        }

                        //SERVER ERROR
                        else if (error instanceof ServerError) {

                            Log.d(application.getString(R.string.TAG_VOLLEY_INVES_REGISTRO), application.getString(R.string.SERVER_ERROR) + errorObject);

                            //MAIL REPEATED
                            if (errorObject.get(application.getString(R.string.JSON_ERROR_DETAIL)).equals(application.getString(R.string.SERVER_ERROR_REGISTRO_MSG))) {

                                responseMsgErrorRegistry.postValue(application.getString(R.string.SERVER_ERROR_REGISTRO_MSG_VM));
                            } else {
                                responseMsgErrorRegistry.postValue(application.getString(R.string.SERVER_ERROR_MSG_VM));
                            }
                        }

                    } catch (JSONException e) {

                        //SEND ERROR TO UI
                        responseMsgErrorRegistry.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                        e.printStackTrace();
                    }
                }
            }
        };


        String url = String.format(application.getString(R.string.URL_POST_INVESTIGADORES), application.getString(R.string.HEROKU_DOMAIN));

        StringRequest request = new StringRequest(Request.Method.POST, url,
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put(application.getString(R.string.KEY_INVES_NOMBRE), researcher.getName());
                params.put(application.getString(R.string.KEY_INVES_APELLIDO), researcher.getLastName());
                params.put(application.getString(R.string.KEY_INVES_EMAIL), researcher.getEmail());
                params.put(application.getString(R.string.KEY_INVES_PASSWORD), researcher.getPassword());
                params.put(application.getString(R.string.KEY_INVES_NOMBRE_ROL), researcher.getRolName());

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
                7000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //VolleySingleton.getInstance(application).addToRequestQueue(request,
        //        new HurlStack(null, SSLConection.getSocketFactory(application.getApplicationContext())));
        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_RESEARCHER_REGISTRY);
    }

    public void loginResearcher(Researcher researcher) {
        sendPostLogin(researcher);
    }

    private void sendPostLogin(final Researcher researcherForm) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("RESPONSE", response);

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));
                    JSONObject jsonLogin = jsonObject.getJSONObject(application.getString(R.string.JSON_LOGIN));

                    String status = jsonLogin.getString(application.getString(R.string.JSON_LOGIN_STATUS));

                    //GER RESPONSE
                    Researcher invResponse = new Researcher();
                    invResponse.setId(jsonData.getInt(application.getString(R.string.KEY_INVES_ID)));

                    JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));
                    invResponse.setEmail(jsonAttributes.getString(application.getString(R.string.KEY_INVES_EMAIL)));
                    invResponse.setName(jsonAttributes.getString(application.getString(R.string.KEY_INVES_NOMBRE)));
                    invResponse.setLastName(jsonAttributes.getString(application.getString(R.string.KEY_INVES_APELLIDO)));
                    invResponse.setCreateTime(jsonAttributes.getString(application.getString(R.string.KEY_CREATE_TIME)));

                    if (jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ACTIVADO)) == 0) {

                        invResponse.setActivated(false);

                    } else if (jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ACTIVADO)) == 1) {

                        invResponse.setActivated(true);
                    }

                    invResponse.setIdRole(jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ID_ROL)));

                    //Buscar en JSON nombre del rol
                    JSONObject jsonObjectRolData = jsonData.getJSONObject(application.getString(R.string.JSON_RELATIONSHIPS))
                            .getJSONObject(application.getString(R.string.KEY_ROL_OBJECT))
                            .getJSONObject(application.getString(R.string.JSON_DATA));
                    invResponse.setRolName(jsonObjectRolData.getString(application.getString(R.string.KEY_ROL_NOMBRE)));

                    /*
                    ACTIVATION CHECKING
                     */
                    if (status.equals(application.getString(R.string.JSON_LOGIN_STATUS_RESPONSE)) && invResponse.isActivated()) {

                        String token = jsonLogin.getString(application.getString(R.string.JSON_TOKEN));
                        Log.d(application.getString(R.string.TAG_TOKEN_LOGIN), token);

                        //SAVE TOKEN
                        SharedPreferences sharedPreferences = application.getSharedPreferences(
                                application.getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);

                        //SAVE TOKEN IN SHARED PREF
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(application.getString(R.string.SHARED_PREF_TOKEN_LOGIN), token);
                        editor.apply();


                        //Enviar investigador y mensaje para toast
                        Map<String, Object> result = new HashMap<>();
                        result.put(application.getString(R.string.KEY_INVES_OBJECT), invResponse);
                        result.put(application.getString(R.string.LOGIN_MSG_VM), application.getString(R.string.LOGIN_MSG_VM_WELCOME));

                        responseMsgLogin.postValue(result);

                    } else {
                        responseMsgErrorLogin.postValue(application.getString(R.string.SNACKBAR_CUENTA_WAIT));
                    }

                    isLoading.postValue(false);

                } catch (JSONException e) {

                    Log.d("JSON_ERROR", "LOGIN JSON ERROR PARSE");
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isLoading.postValue(false);

                //TIMEOUT ERROR
                if (error instanceof TimeoutError) {

                    Log.d(application.getString(R.string.TAG_VOLLEY_ERROR_LOGIN), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgErrorLogin.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //INTERNET ERROR
                else if (error instanceof NetworkError) {

                    Log.d(application.getString(R.string.TAG_VOLLEY_ERROR_LOGIN), application.getString(R.string.NETWORK_ERROR));
                    responseMsgErrorLogin.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
                }

                //SERVER ERROR
                else if (error.networkResponse != null && error.networkResponse.data != null) {

                    String json = new String(error.networkResponse.data);

                    JSONObject errorObject;

                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        errorObject = jsonObject.getJSONObject(application.getString(R.string.JSON_ERROR));

                        //AUTH ERROR
                        if (error instanceof AuthFailureError) {

                            Log.d(application.getString(R.string.TAG_VOLLEY_ERROR_LOGIN), application.getString(R.string.AUTHENTICATION_ERROR) + errorObject);

                            //PARSE AUTH ERROR API
                            if (errorObject.get(application.getString(R.string.JSON_ERROR_DETAIL)).equals(application.getString(R.string.SERVER_ERROR_AUTH_MSG))) {

                                responseMsgErrorLogin.postValue(application.getString(R.string.SERVER_ERROR_AUTH_MSG_VM));
                            } else {
                                responseMsgErrorLogin.postValue(application.getString(R.string.AUTHENTICATION_ERROR_MSG_VM));
                            }
                        }

                        //SERVER ERROR
                        else if (error instanceof ServerError) {

                            Log.d(application.getString(R.string.TAG_VOLLEY_ERROR_LOGIN), application.getString(R.string.SERVER_ERROR) + errorObject);

                            //MAIL NOT REGISTERED
                            if (errorObject.get(application.getString(R.string.JSON_ERROR_DETAIL)).equals(application.getString(R.string.SERVER_ERROR_REGISTRO_MSG_2))) {

                                responseMsgErrorLogin.postValue(application.getString(R.string.SERVER_ERROR_REGISTRO_MSG_VM_2));
                            } else {
                                responseMsgErrorLogin.postValue(application.getString(R.string.SERVER_ERROR_MSG_VM));
                            }
                        }

                    } catch (JSONException e) {

                        //Send error to UI
                        responseMsgErrorLogin.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                        e.printStackTrace();
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
                params.put(application.getString(R.string.KEY_INVES_EMAIL), researcherForm.getEmail());
                params.put(application.getString(R.string.KEY_INVES_PASSWORD), researcherForm.getPassword());

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
        //VolleySingleton.getInstance(application).addToRequestQueue(request,
        //        new HurlStack(null, SSLConection.getSocketFactory(application.getApplicationContext())));
        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_RESEARCHER_LOGIN);
    }

    public void updateResearcher(Researcher researcher) {
        sendPutUpdate(researcher);
    }

    private void sendPutUpdate(final Researcher researcherForm) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d("RESPONSE", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));

                    //Investigador actualizado
                    Researcher invResponse = new Researcher();
                    invResponse.setId(jsonData.getInt(application.getString(R.string.KEY_INVES_ID)));

                    JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    invResponse.setName(jsonAttributes.getString(application.getString(R.string.KEY_INVES_NOMBRE)));
                    invResponse.setLastName(jsonAttributes.getString(application.getString(R.string.KEY_INVES_APELLIDO)));
                    invResponse.setEmail(jsonAttributes.getString(application.getString(R.string.KEY_INVES_EMAIL)));
                    invResponse.setIdRole(jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ID_ROL)));

                    if (jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ACTIVADO)) == 0) {

                        invResponse.setActivated(false);

                    } else if (jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ACTIVADO)) == 1) {

                        invResponse.setActivated(true);
                    }

                    //Buscar en JSON nombre del rol
                    JSONObject jsonObjectRolData = jsonData.getJSONObject(application.getString(R.string.JSON_RELATIONSHIPS))
                            .getJSONObject(application.getString(R.string.KEY_ROL_OBJECT))
                            .getJSONObject(application.getString(R.string.JSON_DATA));
                    invResponse.setRolName(jsonObjectRolData.getString(application.getString(R.string.KEY_ROL_NOMBRE)));

                    String update_time = jsonAttributes.getString(application.getString(R.string.KEY_UPDATE_TIME));

                    Log.d("MEMORIA", researcherForm.toString());
                    Log.d("INTERNET", invResponse.toString());

                    if (researcherForm.equals(invResponse) && !update_time.isEmpty()) {

                        //Enviar investigador y mensaje para toast
                        Map<String, Object> result = new HashMap<>();
                        result.put(application.getString(R.string.KEY_INVES_OBJECT), invResponse);
                        result.put(application.getString(R.string.UPDATE_MSG_VM), application.getString(R.string.UPDATE_MSG_VM_SAVE));

                        responseMsgUpdate.postValue(result);
                    }

                    isLoading.postValue(false);

                } catch (JSONException e) {

                    Log.d("JSON_ERROR", "UPDATE JSON ERROR PARSE");
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
                        application.getString(R.string.TAG_VOLLEY_ERR_INV_UPDATE)
                );
            }
        };


        String url = String.format(application.getString(R.string.URL_PUT_INVESTIGADORES), application.getString(R.string.HEROKU_DOMAIN), researcherForm.getId());

        StringRequest request = new StringRequest(Request.Method.PUT, url, responseListener, errorListener) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put(application.getString(R.string.KEY_INVES_NOMBRE), researcherForm.getName());
                params.put(application.getString(R.string.KEY_INVES_APELLIDO), researcherForm.getLastName());
                params.put(application.getString(R.string.KEY_INVES_EMAIL), researcherForm.getEmail());
                params.put(application.getString(R.string.KEY_INVES_PASSWORD), researcherForm.getPassword());
                params.put(application.getString(R.string.KEY_INVES_ID_ROL), String.valueOf(researcherForm.getIdRole()));

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
        //VolleySingleton.getInstance(application).addToRequestQueue(request,
        //        new HurlStack(null, SSLConection.getSocketFactory(application.getApplicationContext())));
        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_RESEARCHER_UPDATE);
    }

    public void recoveryAccount(Researcher researcher) {
        sendPostRecovery(researcher);
    }

    private void sendPostRecovery(Researcher researcher) {

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

                        Map<String, String> map = new HashMap<>();

                        map.put(application.getString(R.string.KEY_INVES_EMAIL), email);
                        map.put(application.getString(R.string.MSG_RECOVERY), application.getString(R.string.RECOVERY_MSG_VM_RESPONSE));
                        responseMsgRecovery.postValue(map);

                        Log.d(application.getString(R.string.TAG_DYNAMIC_LINK_JSON), dybamicLink);
                    }

                    isLoading.postValue(false);

                } catch (JSONException e) {

                    Log.d("JSON_ERROR", "RECOVERY JSON ERROR PARSE");
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isLoading.postValue(false);

                //TIMEOUT ERROR
                if (error instanceof TimeoutError) {

                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_RECUPERAR), application.getString(R.string.TIMEOUT_ERROR));
                    responseMsgErrorRecovery.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                }

                //NETWORK ERROR
                else if (error instanceof NetworkError) {

                    Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_RECUPERAR), application.getString(R.string.NETWORK_ERROR));
                    responseMsgErrorRecovery.postValue(application.getString(R.string.NETWORK_ERROR_MSG_VM));
                }

                //SERVER ERROR
                else if (error.networkResponse != null && error.networkResponse.data != null) {

                    String json = new String(error.networkResponse.data);

                    JSONObject errorObject;

                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        errorObject = jsonObject.getJSONObject(application.getString(R.string.JSON_ERROR));

                        //AUTH ERROR
                        if (error instanceof AuthFailureError) {

                            Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_RECUPERAR), application.getString(R.string.AUTHENTICATION_ERROR) + errorObject);
                            responseMsgErrorRecovery.postValue(application.getString(R.string.AUTHENTICATION_ERROR_MSG_VM));
                        }

                        //SERVER ERROR
                        else if (error instanceof ServerError) {

                            Log.d(application.getString(R.string.TAG_VOLLEY_ERR_INV_RECUPERAR), application.getString(R.string.SERVER_ERROR) + errorObject);

                            if (errorObject.get(application.getString(R.string.JSON_ERROR_DETAIL)).equals(application.getString(R.string.SERVER_ERROR_REGISTRO_MSG_2))) {

                                responseMsgErrorRecovery.postValue(application.getString(R.string.SERVER_ERROR_REGISTRO_MSG_VM_2));
                            } else {
                                responseMsgErrorRecovery.postValue(application.getString(R.string.SERVER_ERROR_MSG_VM));
                            }
                        }
                    } catch (JSONException e) {

                        //Send error to UI
                        responseMsgErrorRecovery.postValue(application.getString(R.string.TIMEOUT_ERROR_MSG_VM));
                        e.printStackTrace();
                    }
                }
            }
        };

        String url = String.format(application.getString(R.string.URL_GET_RECUPERAR_INVESTIGADOR), application.getString(R.string.HEROKU_DOMAIN), researcher.getEmail(), Utils.getLanguage(application));

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
                7000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //VolleySingleton.getInstance(application).addToRequestQueue(stringRequest,
        //        new HurlStack(null, SSLConection.getSocketFactory(application.getApplicationContext())));
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_RESEARCHER_RECOVERY);
    }

    public void resetPassword(String email, String password) {
        sendPostResetPassword(email, password);
    }

    private void sendPostResetPassword(final String email, final String password) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject jsonReset = jsonObject.getJSONObject(application.getString(R.string.JSON_RESET));

                    String status = jsonReset.getString(application.getString(R.string.JSON_RESET_STATUS));

                    if (status.equals(application.getString(R.string.MSG_PASSWORD_RESETEADA_VM))) {

                        responseMsgReset.postValue(application.getString(R.string.MSG_PASSWORD_RESETEADA_VM_RESULT));
                    }

                    isLoading.postValue(false);

                } catch (JSONException e) {

                    Log.d("JSON_ERROR", "PASSWORD RESET JSON ERROR PARSE");
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
                        responseMsgErrorReset,
                        application.getString(R.string.TAG_VOLLEY_ERR_INV_RESET)
                );
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
        //VolleySingleton.getInstance(application).addToRequestQueue(stringRequest,
        //        new HurlStack(null, SSLConection.getSocketFactory(application.getApplicationContext())));
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_RESEARCHER_RESET);
    }

    public void activateAccount(Researcher researcher) {
        sendPatchActivate(researcher);
    }

    private void sendPatchActivate(final Researcher researcher) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d("RESPONSE", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));
                    JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    Researcher invResponse = new Researcher();
                    invResponse.setEmail(jsonAttributes.getString(application.getString(R.string.KEY_INVES_EMAIL)));

                    if (jsonAttributes.getInt(application.getString(R.string.KEY_INVES_ACTIVADO)) == 0) {

                        invResponse.setActivated(false);
                    } else {
                        invResponse.setActivated(true);
                    }

                    if (researcher.getEmail().equals(invResponse.getEmail()) &&
                            (researcher.isActivated() == invResponse.isActivated())) {

                        if (researcher.isActivated()) {

                            responseMsgActivation.postValue(application.getString(R.string.MSG_INVEST_CUENTA_ACTIVADA));
                        } else {
                            responseMsgActivation.postValue(application.getString(R.string.MSG_INVEST_CUENTA_DESACTIVADA));
                        }
                    }

                    isActivated.postValue(false);

                } catch (JSONException e) {

                    Log.d("JSON_ERROR", "ACTIVATION JSON ERROR PARSE");
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isActivated.postValue(false);

                Utils.deadAPIHandler(
                        error,
                        application,
                        responseMsgErrorActivation,
                        application.getString(R.string.TAG_VOLLEY_ERR_INV_ACTIVAR)
                );
            }
        };

        String url = String.format(Locale.getDefault(), application.getString(R.string.URL_PATCH_INVESTIGADORES_ACTIVAR), application.getString(R.string.HEROKU_DOMAIN), researcher.getId());

        StringRequest stringRequest = new StringRequest(Request.Method.PATCH, url, responseListener, errorListener) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                if (researcher.isActivated()) {

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

        isActivated.postValue(true);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                7000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //VolleySingleton.getInstance(application).addToRequestQueue(stringRequest,
        //        new HurlStack(null, SSLConection.getSocketFactory(application.getApplicationContext())));
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_RESEARCHER_ACTIVATION);
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

    public SingleLiveEvent<Map<String, String>> getResponseMsgRegistry() {
        return responseMsgRegistry;
    }

    public SingleLiveEvent<String> getResponseMsgErrorRegistry() {
        return responseMsgErrorRegistry;
    }

    public SingleLiveEvent<Map<String, Object>> getResponseMsgUpdate() {
        return responseMsgUpdate;
    }

    public SingleLiveEvent<String> getResponseMsgErrorUpdate() {
        return responseMsgErrorUpdate;
    }

    public SingleLiveEvent<Map<String, String>> getResponseMsgRecovery() {
        return responseMsgRecovery;
    }

    public SingleLiveEvent<String> getResponseMsgErrorRecovery() {
        return responseMsgErrorRecovery;
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

    public SingleLiveEvent<String> getResponseMsgErrorList() {
        return responseMsgErrorList;
    }

    public SingleLiveEvent<String> getResponseMsgActivation() {
        return responseMsgActivation;
    }

    public SingleLiveEvent<String> getResponseMsgErrorActivation() {
        return responseMsgErrorActivation;
    }

    public SingleLiveEvent<List<Researcher>> getNextPage() {
        return researcherSgtPageLiveData;
    }

    public MutableLiveData<Integer> getNResearcher() {
        return mutableNResearcher;
    }

    public MutableLiveData<Boolean> activatingResearcher() {
        return isActivated;
    }
}
