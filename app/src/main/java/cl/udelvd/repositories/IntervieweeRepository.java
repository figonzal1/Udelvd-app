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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cl.udelvd.R;
import cl.udelvd.models.City;
import cl.udelvd.models.CivilState;
import cl.udelvd.models.CohabitType;
import cl.udelvd.models.EducationalLevel;
import cl.udelvd.models.Interviewee;
import cl.udelvd.models.Profession;
import cl.udelvd.models.Researcher;
import cl.udelvd.services.VolleySingleton;
import cl.udelvd.utils.SingleLiveEvent;
import cl.udelvd.utils.Utils;

public class IntervieweeRepository {

    private static final String TAG_INTERVIEWEE_LIST = "ListaEntrevistados";
    private static final String TAG_INTERVIEWEE_REGISTRY = "RegistroEntrevistado";
    private static final String TAG_INTERVIEWEE = "ObtenerEntrevistado";
    private static final String TAG_INTERVIEWEE_UPDATE = "ActualizarEntrevistado";
    private static final String TAG_INTERVIEWEE_DELETE = "EliminarEntrevistado";

    private static IntervieweeRepository instance;
    private final Application application;

    /*
    LIST
     */
    private final List<Interviewee> intervieweeList = new ArrayList<>();
    private final List<Interviewee> intervieweeSgtPage = new ArrayList<>();
    private final SingleLiveEvent<List<Interviewee>> intervieweesFirstPageLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<List<Interviewee>> intervieweesSgtPageLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgErrorList = new SingleLiveEvent<>();
    private final MutableLiveData<Integer> mutableNInterviewees = new MutableLiveData<>();

    /*
    REGISTRY
     */
    private final SingleLiveEvent<String> responseMsgRegistry = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgErrorRegistry = new SingleLiveEvent<>();

    /*
    UPDATE
     */
    private final SingleLiveEvent<Interviewee> intervieweeMutableLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgErrorInterviewee = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgUpdate = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgErrorUpdate = new SingleLiveEvent<>();

    /*
    DELETE
     */
    private final SingleLiveEvent<String> responseMsgDelete = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> responseMsgErrorDelete = new SingleLiveEvent<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private IntervieweeRepository(Application application) {
        this.application = application;
    }

    public static IntervieweeRepository getInstance(Application application) {

        if (instance == null) {
            instance = new IntervieweeRepository(application);
        }

        return instance;
    }

    public SingleLiveEvent<List<Interviewee>> getInterviewees(int page, Researcher researcher, boolean totalList) {

        sendGetInterviewees(page, researcher, totalList);

        if (page == 1) {
            return intervieweesFirstPageLiveData;
        } else {
            return intervieweesSgtPageLiveData;
        }
    }

    public SingleLiveEvent<List<Interviewee>> getNextPage() {
        return intervieweesSgtPageLiveData;
    }

    private void sendGetInterviewees(final int page, Researcher researcher, final boolean totalList) {

        final Response.Listener<String> responseListener = response -> {

            //Log.d("RESPONSE", response);
            //Reiniciar listado cuando se pregunta por primera pagina
            if (page == 1) {
                intervieweeList.clear();
            } else {
                intervieweeSgtPage.clear();
            }

            try {
                JSONObject jsonObject = new JSONObject(response);

                /*
                N INTERVIEWEES
                 */
                JSONObject jsonNInterviewee = jsonObject.getJSONObject(application.getString(R.string.KEY_ENTREVISTADOS)).getJSONObject(application.getString(R.string.JSON_DATA));

                int n_entrevistados = jsonNInterviewee.getInt(application.getString(R.string.KEY_ENTREVISTADO_N_ENTREVISTADOS));
                mutableNInterviewees.postValue(n_entrevistados);

                /*
                INTERVIEWEES LIST
                 */
                JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                for (int i = 0; i < jsonData.length(); i++) {

                    JSONObject jsonInterviewee = jsonData.getJSONObject(i);
                    JSONObject jsonIntervieweeAttributes = jsonInterviewee.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                    Interviewee interviewee = new Interviewee();
                    interviewee.setId(jsonInterviewee.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID)));
                    interviewee.setName(jsonIntervieweeAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE)));
                    interviewee.setLastName(jsonIntervieweeAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_APELLIDO)));
                    interviewee.setGender(jsonIntervieweeAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_SEXO)));

                    Date birthDate = null;
                    try {
                        birthDate = Utils.stringToDate(application, false, jsonIntervieweeAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_FECHA_NAC)));
                    } catch (ParseException e) {

                        Log.d("STRING_TO_DATE", "Parse exception error");
                        e.printStackTrace();
                    }
                    interviewee.setBirthDate(birthDate);

                    City city = new City();
                    city.setId(jsonIntervieweeAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_CIUDAD)));
                    interviewee.setCity(city);

                    //LEGAL retired
                    interviewee.setLegalRetired(jsonIntervieweeAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_JUBILADO_LEGAL)) == 1);

                    //FALSS
                    if (jsonIntervieweeAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_CAIDAS)) == 1) {
                        interviewee.setFalls(true);
                        interviewee.setNCaidas(jsonIntervieweeAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_N_CAIDAS)));
                    } else {
                        interviewee.setFalls(false);
                    }

                    interviewee.setNCohabiting3Months(jsonIntervieweeAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_N_CONVI_3_MESES)));

                    //Foreign
                    interviewee.setIdResearcher(jsonIntervieweeAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_INVESTIGADOR)));

                    CivilState civilState = new CivilState();
                    civilState.setId(jsonIntervieweeAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_ESTADO_CIVIL)));
                    interviewee.setCivilState(civilState);

                    //Foreign optionals
                    if (jsonIntervieweeAttributes.has(application.getString(R.string.KEY_ENTREVISTADO_ID_NIVEL_EDUCACIONAL)) && !jsonIntervieweeAttributes.isNull(application.getString(R.string.KEY_ENTREVISTADO_ID_NIVEL_EDUCACIONAL))) {
                        EducationalLevel educationalLevel = new EducationalLevel();
                        educationalLevel.setId(jsonIntervieweeAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_NIVEL_EDUCACIONAL)));
                        interviewee.setEducationalLevel(educationalLevel);
                    }
                    if (jsonIntervieweeAttributes.has(application.getString(R.string.KEY_ENTREVISTADO_ID_TIPO_CONVIVENCIA)) && !jsonIntervieweeAttributes.isNull(application.getString(R.string.KEY_ENTREVISTADO_ID_TIPO_CONVIVENCIA))) {
                        CohabitType cohabitType = new CohabitType();
                        cohabitType.setId(jsonIntervieweeAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_TIPO_CONVIVENCIA)));
                        interviewee.setCoexistenteType(cohabitType);
                    }
                    if (jsonIntervieweeAttributes.has(application.getString(R.string.KEY_ENTREVISTADO_ID_PROFESION)) && !jsonIntervieweeAttributes.isNull(application.getString(R.string.KEY_ENTREVISTADO_ID_PROFESION))) {
                        Profession profession = new Profession();
                        profession.setId(jsonIntervieweeAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_PROFESION)));
                        interviewee.setProfession(profession);
                    }

                    JSONObject jsonRelationships = jsonInterviewee.getJSONObject(application.getString(R.string.JSON_RELATIONSHIPS));

                    //N INTERVIEWS
                    JSONObject jsonNInterviews = jsonRelationships.getJSONObject(application.getString(R.string.KEY_ENTREVISTA_OBJECT));
                    interviewee.setNInterviews(
                            jsonNInterviews.getJSONObject(application.getString(R.string.JSON_DATA))
                                    .getInt(application.getString(R.string.KEY_ENTREVISTADO_N_ENTREVISTAS))
                    );

                    if (totalList) {
                        //RESEARCHER
                        JSONObject jsonResearcher = jsonRelationships.getJSONObject(application.getString(R.string.KEY_INVES));
                        interviewee.setResearcherName(
                                jsonResearcher.getJSONObject(application.getString(R.string.JSON_DATA))
                                        .getString(application.getString(R.string.KEY_INVES_NOMBRE))
                        );
                        interviewee.setLastNameResearcher(
                                jsonResearcher.getJSONObject(application.getString(R.string.JSON_DATA))
                                        .getString(application.getString(R.string.KEY_INVES_APELLIDO))
                        );
                    }

                    if (page == 1) {
                        intervieweeList.add(interviewee);
                    } else {
                        intervieweeSgtPage.add(interviewee);
                    }
                }


                if (page == 1) {
                    //Log.d("CARGANDO_PAGINA", "1");
                    intervieweesFirstPageLiveData.postValue(intervieweeList);
                } else {
                    //Log.d("CARGANDO_PAGINA", String.valueOf(page));
                    intervieweesSgtPageLiveData.postValue(intervieweeSgtPage); //Enviar a ViewModel listado paginado
                }

                isLoading.postValue(false);

            } catch (JSONException e) {

                Log.d("JSON_ERROR", "GET INTERVIEWEES JSON ERROR PARSE");
                e.printStackTrace();
            }
        };

        final Response.ErrorListener errorListener = error -> {

            isLoading.postValue(false);

            Utils.deadAPIHandler(
                    error,
                    application,
                    responseMsgErrorList,
                    application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTADOS)
            );
        };

        String url;
        if (totalList) {
            url = String.format(application.getString(R.string.URL_GET_ENTREVISTADOS_TOTALES), application.getString(R.string.HEROKU_DOMAIN), page);
        } else {
            url = String.format(application.getString(R.string.URL_GET_ENTREVISTADOS_INVESTIGADOR), application.getString(R.string.HEROKU_DOMAIN), page, researcher.getId());
        }

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

        //Si es la primera pagina, activar progress dialog
        if (page == 1) {
            isLoading.postValue(true);
        }

        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_INTERVIEWEE_LIST);
    }

    public void registryInterviewee(Interviewee interviewee) {
        sendPostInterviewee(interviewee);
    }

    private void sendPostInterviewee(final Interviewee interviewee) {

        Response.Listener<String> responseListener = response -> {


            try {
                JSONObject jsonObject = new JSONObject(response);

                JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));

                JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                //GET RESPONSE
                Interviewee entResponse = new Interviewee();
                entResponse.setName(jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE)));
                entResponse.setLastName(jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_APELLIDO)));

                entResponse.setGender(jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_SEXO)));

                entResponse.setLegalRetired(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_JUBILADO_LEGAL)) != 0);

                entResponse.setFalls(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_CAIDAS)) != 0);

                Date birthDate = null;
                try {
                    birthDate = Utils.stringToDate(application, false, jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_FECHA_NAC)));
                } catch (ParseException e) {

                    Log.d("STRING_TO_DATE", "Parse exception error");
                    e.printStackTrace();
                }
                entResponse.setBirthDate(birthDate);

                entResponse.setNCohabiting3Months(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_N_CONVI_3_MESES)));
                entResponse.setIdResearcher(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_INVESTIGADOR)));

                String create_time = jsonAttributes.getString(application.getString(R.string.KEY_CREATE_TIME));

                Log.d("MEMORIA", interviewee.toString());
                Log.d("INTERNET", entResponse.toString());

                if (interviewee.equals(entResponse) && !create_time.isEmpty()) {
                    responseMsgRegistry.postValue(application.getString(R.string.MSG_REGISTRO_ENTREVISTADO));
                }

                isLoading.postValue(false);

            } catch (JSONException e) {

                Log.d("JSON_ERROR", "POST INTERVIEWEE JSON ERROR PARSE");
                e.printStackTrace();
            }

        };

        Response.ErrorListener errorListener = error -> {

            isLoading.postValue(false);

            Utils.deadAPIHandler(
                    error,
                    application,
                    responseMsgErrorRegistry,
                    application.getString(R.string.TAG_VOLLEY_ERR_NUEVO_ENTREVISTADO)
            );

        };

        String url = String.format(application.getString(R.string.URL_POST_ENTREVISTADOS), application.getString(R.string.HEROKU_DOMAIN));

        StringRequest request = new StringRequest(Request.Method.POST, url, responseListener, errorListener) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE), interviewee.getName());
                params.put(application.getString(R.string.KEY_ENTREVISTADO_APELLIDO), interviewee.getLastName());
                params.put(application.getString(R.string.KEY_ENTREVISTADO_SEXO), interviewee.getGender());

                String fechaNac = Utils.dateToString(application, false, interviewee.getBirthDate());
                params.put(application.getString(R.string.KEY_ENTREVISTADO_FECHA_NAC), fechaNac);
                params.put(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE_CIUDAD), interviewee.getCity().getName());

                if (interviewee.isLegalRetired()) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_JUBILADO_LEGAL), String.valueOf(1));
                } else {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_JUBILADO_LEGAL), String.valueOf(0));
                }

                if (interviewee.isFalls()) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_CAIDAS), String.valueOf(1));

                    params.put(application.getString(R.string.KEY_ENTREVISTADO_N_CAIDAS), String.valueOf(interviewee.getNCaidas()));
                } else {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_CAIDAS), String.valueOf(0));
                }

                params.put(application.getString(R.string.KEY_ENTREVISTADO_N_CONVI_3_MESES), String.valueOf(interviewee.getNCohabiting3Months()));
                params.put(application.getString(R.string.KEY_ENTREVISTADO_ID_INVESTIGADOR), String.valueOf(interviewee.getIdResearcher()));
                params.put(application.getString(R.string.KEY_ENTREVISTADO_ID_ESTADO_CIVIL), String.valueOf(interviewee.getCivilState().getId()));

                /*
                    OPTIONALS
                 */
                if (interviewee.getEducationalLevel() != null) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_ID_NIVEL_EDUCACIONAL), String.valueOf(interviewee.getEducationalLevel().getId()));
                }

                if (interviewee.getCoexistenteType() != null) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_ID_TIPO_CONVIVENCIA), String.valueOf(interviewee.getCoexistenteType().getId()));
                }

                if (interviewee.getProfession() != null) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE_PROFESION), interviewee.getProfession().getName());
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

        isLoading.postValue(true);
        VolleySingleton.getInstance(application).addToRequestQueue(request, TAG_INTERVIEWEE_REGISTRY);
    }

    public SingleLiveEvent<Interviewee> getInterviewee(Interviewee interviewee) {

        sendGetInterviewee(interviewee);

        return intervieweeMutableLiveData;
    }

    private void sendGetInterviewee(final Interviewee interviewee) {

        Response.Listener<String> responseListener = response -> {
            //Log.d("RESPONSE", response);

            try {
                JSONObject jsonObject = new JSONObject(response);

                JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));
                JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                interviewee.setId(jsonData.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID)));

                interviewee.setName(jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE)));
                interviewee.setLastName(jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_APELLIDO)));
                interviewee.setGender(jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_SEXO)));

                Date birthDate = null;
                try {
                    birthDate = Utils.stringToDate(application, false, jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_FECHA_NAC)));
                } catch (ParseException e) {

                    Log.d("STRING_TO_DATE", "Parse exception error");
                    e.printStackTrace();
                }
                interviewee.setBirthDate(birthDate);

                interviewee.setLegalRetired(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_JUBILADO_LEGAL)) != 0);

                if (jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_CAIDAS)) == 0) {
                    interviewee.setFalls(false);
                } else {
                    interviewee.setFalls(true);

                    interviewee.setNCaidas(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_N_CAIDAS)));
                }

                interviewee.setNCohabiting3Months(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_N_CONVI_3_MESES)));
                interviewee.setIdResearcher(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_INVESTIGADOR)));

                City city = new City();
                city.setId(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_CIUDAD)));
                interviewee.setCity(city);

                CivilState civilState = new CivilState();
                civilState.setId(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_ESTADO_CIVIL)));
                interviewee.setCivilState(civilState);

                //OPTIONALS (manejar nulls como strings)
                String idEducationalLevel = jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_ID_NIVEL_EDUCACIONAL));

                if (!idEducationalLevel.equals(application.getString(R.string.NULL))) {

                    EducationalLevel educationalLevel = new EducationalLevel();
                    educationalLevel.setId(Integer.parseInt(idEducationalLevel));

                    interviewee.setEducationalLevel(educationalLevel);
                }

                String idCoexistanceType = jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_ID_TIPO_CONVIVENCIA));

                if (!idCoexistanceType.equals(application.getString(R.string.NULL))) {

                    CohabitType cohabitType = new CohabitType();
                    cohabitType.setId(Integer.parseInt(idCoexistanceType));

                    interviewee.setCoexistenteType(cohabitType);
                }

                String idProfession = jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_ID_PROFESION));

                if (!idProfession.equals(application.getString(R.string.NULL))) {

                    Profession profession = new Profession();
                    profession.setId(Integer.parseInt(idProfession));

                    interviewee.setProfession(profession);
                }

                intervieweeMutableLiveData.postValue(interviewee);

                isLoading.postValue(false);

            } catch (JSONException e) {

                Log.d("JSON_ERROR", "GET INTERVIEWEE JSON ERROR PARSE");
                e.printStackTrace();
            }

        };

        Response.ErrorListener errorListener = error -> {

            isLoading.postValue(false);

            Utils.deadAPIHandler(
                    error,
                    application,
                    responseMsgErrorInterviewee,
                    application.getString(R.string.TAG_VOLLEY_ERR_ENTREVISTADO)
            );
        };

        String url = String.format(application.getString(R.string.URL_GET_ENTREVISTADO), application.getString(R.string.HEROKU_DOMAIN), interviewee.getId());

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
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_INTERVIEWEE);
    }

    public void updateInterviewee(Interviewee interviewee) {
        sendPutInterviewee(interviewee);
    }

    private void sendPutInterviewee(final Interviewee interviewee) {

        Response.Listener<String> responseListener = response -> {

            //Log.d("RESPONSE", response);

            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject jsonData = jsonObject.getJSONObject(application.getString(R.string.JSON_DATA));

                JSONObject jsonAttributes = jsonData.getJSONObject(application.getString(R.string.JSON_ATTRIBUTES));

                Interviewee intervieweeInternet = new Interviewee();
                intervieweeInternet.setId(jsonData.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID)));

                intervieweeInternet.setName(jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE)));
                intervieweeInternet.setLastName(jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_APELLIDO)));
                intervieweeInternet.setGender(jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_SEXO)));


                Date birthDate = null;
                try {
                    birthDate = Utils.stringToDate(application, false, jsonAttributes.getString(application.getString(R.string.KEY_ENTREVISTADO_FECHA_NAC)));
                } catch (ParseException e) {
                    Log.d("STRING_TO_DATE", "Parse exception error");
                    e.printStackTrace();
                }
                intervieweeInternet.setBirthDate(birthDate);

                intervieweeInternet.setLegalRetired(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_JUBILADO_LEGAL)) != 0);

                if (jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_CAIDAS)) == 0) {
                    intervieweeInternet.setFalls(false);
                } else {
                    intervieweeInternet.setFalls(true);

                    intervieweeInternet.setNCaidas(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_N_CAIDAS)));
                }

                intervieweeInternet.setNCohabiting3Months(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_N_CONVI_3_MESES)));
                intervieweeInternet.setIdResearcher(jsonAttributes.getInt(application.getString(R.string.KEY_ENTREVISTADO_ID_INVESTIGADOR)));

                Log.d("MEMORIA", interviewee.toString());
                Log.d("INTERNET", intervieweeInternet.toString());

                String update_time = jsonAttributes.getString(application.getString(R.string.KEY_UPDATE_TIME));

                if (interviewee.equals(intervieweeInternet) && !update_time.isEmpty()) {
                    responseMsgUpdate.postValue(application.getString(R.string.MSG_UPDATE_ENTREVISTADO));
                }
                isLoading.postValue(false);

            } catch (JSONException e) {

                Log.d("JSON_ERROR", "PUT INTERVIEWEE JSON ERROR PARSE");
                e.printStackTrace();
            }

        };

        Response.ErrorListener errorListener = error -> {

            isLoading.postValue(false);

            Utils.deadAPIHandler(
                    error,
                    application,
                    responseMsgErrorUpdate,
                    application.getString(R.string.TAG_VOLLEY_ERR_EDITAR_ENTREVISTADO)
            );
        };

        String url = String.format(application.getString(R.string.URL_PUT_ENTREVISTADOS), application.getString(R.string.HEROKU_DOMAIN), interviewee.getId());

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, responseListener, errorListener) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE), interviewee.getName());
                params.put(application.getString(R.string.KEY_ENTREVISTADO_APELLIDO), interviewee.getLastName());
                params.put(application.getString(R.string.KEY_ENTREVISTADO_SEXO), interviewee.getGender());

                String fechaNac = Utils.dateToString(application, false, interviewee.getBirthDate());
                params.put(application.getString(R.string.KEY_ENTREVISTADO_FECHA_NAC), fechaNac);
                params.put(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE_CIUDAD), interviewee.getCity().getName());

                if (interviewee.isLegalRetired()) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_JUBILADO_LEGAL), String.valueOf(1));
                } else {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_JUBILADO_LEGAL), String.valueOf(0));
                }

                if (interviewee.isFalls()) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_CAIDAS), String.valueOf(1));

                    params.put(application.getString(R.string.KEY_ENTREVISTADO_N_CAIDAS), String.valueOf(interviewee.getNCaidas()));
                } else {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_CAIDAS), String.valueOf(0));
                }

                params.put(application.getString(R.string.KEY_ENTREVISTADO_N_CONVI_3_MESES), String.valueOf(interviewee.getNCohabiting3Months()));
                params.put(application.getString(R.string.KEY_ENTREVISTADO_ID_INVESTIGADOR), String.valueOf(interviewee.getIdResearcher()));
                params.put(application.getString(R.string.KEY_ENTREVISTADO_ID_ESTADO_CIVIL), String.valueOf(interviewee.getCivilState().getId()));

                /*
                    OPCIONALES
                 */
                if (interviewee.getEducationalLevel() != null) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_ID_NIVEL_EDUCACIONAL), String.valueOf(interviewee.getEducationalLevel().getId()));
                }

                if (interviewee.getCoexistenteType() != null) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_ID_TIPO_CONVIVENCIA), String.valueOf(interviewee.getCoexistenteType().getId()));
                }

                if (interviewee.getProfession() != null) {
                    params.put(application.getString(R.string.KEY_ENTREVISTADO_NOMBRE_PROFESION), interviewee.getProfession().getName());
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

        isLoading.postValue(true);
        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_INTERVIEWEE_UPDATE);
    }

    public void deleteInterviewee(Interviewee interviewee) {
        sendDeleteInterviewee(interviewee);
    }

    private void sendDeleteInterviewee(Interviewee interviewee) {

        Response.Listener<String> responseListener = response -> {
            //Log.d("RESPONSE", response);

            try {
                JSONObject jsonObject = new JSONObject(response);

                JSONArray jsonData = jsonObject.getJSONArray(application.getString(R.string.JSON_DATA));

                if (jsonData.length() == 0) {
                    responseMsgDelete.postValue(application.getString(R.string.MSG_DELETE_ENTREVISTADO));
                }
                isLoading.postValue(false);

            } catch (JSONException e) {

                Log.d("JSON_ERROR", "DELETE INTERVIEWEE JSON ERROR PARSE");
                e.printStackTrace();
            }
        };

        Response.ErrorListener errorListener = error -> {

            isLoading.postValue(false);

            Utils.deadAPIHandler(
                    error,
                    application,
                    responseMsgErrorDelete,
                    application.getString(R.string.TAG_VOLLEY_ERR_ELIMINAR_ENTREVISTADO)
            );
        };

        String url = String.format(Locale.US, application.getString(R.string.URL_DELETE_ENTREVISTADO), application.getString(R.string.HEROKU_DOMAIN), interviewee.getId());

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

        VolleySingleton.getInstance(application).addToRequestQueue(stringRequest, TAG_INTERVIEWEE_DELETE);
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

    public SingleLiveEvent<String> getResponseMsgErrorInterviewee() {
        return responseMsgErrorInterviewee;
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

    public MutableLiveData<Integer> getNInterviewees() {
        return mutableNInterviewees;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
