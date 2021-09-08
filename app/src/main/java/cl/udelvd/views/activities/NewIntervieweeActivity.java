package cl.udelvd.views.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.adapters.CityAdapter;
import cl.udelvd.adapters.CivilStateAdapter;
import cl.udelvd.adapters.CohabitTypeAdapter;
import cl.udelvd.adapters.EducationalLevelAdapter;
import cl.udelvd.adapters.ProfessionAdapter;
import cl.udelvd.models.City;
import cl.udelvd.models.CivilState;
import cl.udelvd.models.CohabitType;
import cl.udelvd.models.EducationalLevel;
import cl.udelvd.models.Interviewee;
import cl.udelvd.models.Profession;
import cl.udelvd.repositories.IntervieweeRepository;
import cl.udelvd.utils.SnackbarInterface;
import cl.udelvd.utils.Utils;
import cl.udelvd.viewmodels.NewIntervieweeViewModel;

public class NewIntervieweeActivity extends AppCompatActivity implements SnackbarInterface {

    private TextInputLayout ilName;
    private TextInputLayout ilLastName;
    private TextInputLayout ilGenre;
    private TextInputLayout ilBirthDate;
    private TextInputLayout ilCity;
    private TextInputLayout ilCivilState;
    private TextInputLayout ilNCoexistence;
    private SwitchMaterial switchLegalRetire;
    private TextView tvRetireValue;
    private SwitchMaterial switchFalls;
    private TextView tvFallsValue;
    private TextInputLayout ilNFalls;

    //Opcionales
    private TextInputLayout ilCoexistenceType;
    private TextInputLayout ilProfession;
    private TextInputLayout ilEducationalLevel;

    private TextInputEditText etName;
    private TextInputEditText etLastName;
    private AppCompatAutoCompleteTextView acGenre;
    private TextInputEditText etBirthDate;
    private TextInputEditText etNCoexistence;
    private AppCompatAutoCompleteTextView acCity;
    private AppCompatAutoCompleteTextView acCivilState;
    private TextInputEditText etNFalls;

    //OPCIONALES
    private AppCompatAutoCompleteTextView acEducationalLevel;
    private AppCompatAutoCompleteTextView acCoexistenceType;
    private AppCompatAutoCompleteTextView acProfession;

    private NewIntervieweeViewModel newIntervieweeViewModel;

    //LIST
    private List<City> cityList;
    private List<CivilState> civilStateList;
    private List<EducationalLevel> educationalLevelList;
    private List<CohabitType> coexistenceTypeList;
    private List<Profession> professionList;

    //Adaptadores
    private ArrayAdapter<City> cityAdapter;
    private ArrayAdapter<CivilState> civilStateAdapter;
    private ArrayAdapter<EducationalLevel> educacionalLevelAdapter;
    private ArrayAdapter<CohabitType> coexistenceTypeAdapter;
    private ArrayAdapter<Profession> professionAdapter;

    private ProgressBar progressBar;

    private boolean isSnackBarShow = false;

    private FirebaseCrashlytics crashlytics;

    public NewIntervieweeActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_interviewee);

        crashlytics = FirebaseCrashlytics.getInstance();

        Utils.configToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITULO_TOOLBAR_NUEVO_ENTREVISTADO));

        instantiateInterfaceResources();

        initViewModelObservers();

        setSpinnerGenre();

        setPickerBirthDate();

        setFalls();

        setRetire();

    }

    private void instantiateInterfaceResources() {

        ilName = findViewById(R.id.il_interview_name);
        ilLastName = findViewById(R.id.il_interview_last_name);
        ilGenre = findViewById(R.id.il_interviewee_genre);
        ilBirthDate = findViewById(R.id.il_birth_date);
        ilCity = findViewById(R.id.il_interview_city);
        ilCivilState = findViewById(R.id.il_interviewee_civil_state);
        ilNCoexistence = findViewById(R.id.il_n_coexistence_interviewee);
        ilNFalls = findViewById(R.id.il_n_falls_interviewee);

        ilCoexistenceType = findViewById(R.id.il_interviewee_coexistence_type);
        ilProfession = findViewById(R.id.il_interviewee_profession);
        ilEducationalLevel = findViewById(R.id.il_interviewee_educational_level);

        tvRetireValue = findViewById(R.id.tv_switch_retire_value);
        tvFallsValue = findViewById(R.id.tv_switch_falls);

        etName = findViewById(R.id.et_interview_name);
        etLastName = findViewById(R.id.et_interview_last_name);
        etBirthDate = findViewById(R.id.et_birth_date);
        etNFalls = findViewById(R.id.et_n_falls_interviewee);
        etNCoexistence = findViewById(R.id.et_n_coexistence_interviewee);

        acCity = findViewById(R.id.et_interview_city);
        acCivilState = findViewById(R.id.et_interviewee_civil_state);
        acGenre = findViewById(R.id.et_interviewee_genre);

        acEducationalLevel = findViewById(R.id.et_interviewee_educational_level);
        acCoexistenceType = findViewById(R.id.et_interviewee_coexistence_type);
        acProfession = findViewById(R.id.et_interviewee_profession);

        switchLegalRetire = findViewById(R.id.switch_retire_legal);
        switchFalls = findViewById(R.id.switch_interviewee_falls);

        progressBar = findViewById(R.id.progress_horizontal_registro_entrevistado);
        progressBar.setVisibility(View.VISIBLE);

        newIntervieweeViewModel = new ViewModelProvider(this).get(NewIntervieweeViewModel.class);

    }

    private void initViewModelObservers() {

        viewModelInterviewee();

        viewModelCivilState();

        viewModelCity();

        viewModelEducationalLevel();

        viewModelCoexistenceType();

        viewModelProfession();
    }

    private void viewModelInterviewee() {


        newIntervieweeViewModel.isLoadingInterviewee().observe(this, aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);
                activateInputs(false);

            } else {
                progressBar.setVisibility(View.GONE);
                activateInputs(true);
            }
        });


        newIntervieweeViewModel.showMsgRegistry().observe(this, s -> {

            progressBar.setVisibility(View.GONE);

            Log.d(getString(R.string.TAG_VIEW_MODEL_NUEVO_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_NUEVO_ENTREVISTADO) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));


            if (s.equals(getString(R.string.MSG_REGISTRO_ENTREVISTADO))) {

                Intent intent = getIntent();
                intent.putExtra(getString(R.string.INTENT_KEY_MSG_REGISTRO), s);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        newIntervieweeViewModel.showMsgErrorRegistry().observe(this, s -> {

            progressBar.setVisibility(View.GONE);

            if (!isSnackBarShow) {
                isSnackBarShow = true;
                showSnackbar(findViewById(R.id.form_new_interviewee), Snackbar.LENGTH_LONG, s, null);
            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_NUEVO_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_NUEVO_ENTREVISTADO) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
        });

    }

    private void viewModelCivilState() {


        newIntervieweeViewModel.isLoadingCivilStates().observe(this, aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);
                activateInputs(false);

            } else {
                progressBar.setVisibility(View.GONE);
                activateInputs(true);
            }
        });


        newIntervieweeViewModel.loadCivilStates().observe(this, civilStates -> {

            if (civilStates != null && civilStates.size() > 0) {

                civilStateList = civilStates;
                civilStateAdapter = new CivilStateAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, civilStateList);
                acCivilState.setAdapter(civilStateAdapter);

                civilStateAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_ESTADO_CIVIL), getString(R.string.VIEW_MODEL_LISTADO_CARGADO));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_ESTADO_CIVIL) + getString(R.string.VIEW_MODEL_LISTADO_CARGADO));
            }
        });


        newIntervieweeViewModel.showMsgErrorListCivilStates().observe(this, s -> {

            progressBar.setVisibility(View.GONE);

            if (!isSnackBarShow) {
                isSnackBarShow = true;
                showSnackbar(findViewById(R.id.form_new_interviewee), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_ESTADO_CIVIL), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_ESTADO_CIVIL) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
        });
    }

    private void viewModelCity() {


        newIntervieweeViewModel.isLoadingCities().observe(this, aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);
                activateInputs(false);

            } else {
                progressBar.setVisibility(View.GONE);
                activateInputs(true);
            }
        });


        newIntervieweeViewModel.loadCities().observe(this, cities -> {

            if (cities != null && cities.size() > 0) {

                cityList = cities;
                cityAdapter = new CityAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, cityList);
                acCity.setAdapter(cityAdapter);

                cityAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_CIUDAD), getString(R.string.VIEW_MODEL_LISTADO_CARGADO));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_CIUDAD) + getString(R.string.VIEW_MODEL_LISTADO_CARGADO));
            }

        });

        newIntervieweeViewModel.showMsgErrorListCity().observe(this, s -> {

            if (!isSnackBarShow) {

                isSnackBarShow = true;
                showSnackbar(findViewById(R.id.form_new_interviewee), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_CIUDAD), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_CIUDAD) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
        });
    }

    private void viewModelEducationalLevel() {


        newIntervieweeViewModel.isLoadingEducationalLevels().observe(this, aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);
                activateInputs(false);

            } else {
                progressBar.setVisibility(View.GONE);
                activateInputs(true);
            }
        });


        newIntervieweeViewModel.loadEducationalLevels().observe(this, educationalLevels -> {

            if (educationalLevels != null && educationalLevels.size() > 0) {

                educationalLevelList = educationalLevels;
                educacionalLevelAdapter = new EducationalLevelAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, educationalLevelList);
                acEducationalLevel.setAdapter(educacionalLevelAdapter);
                acEducationalLevel.setText(educationalLevelList.get(0).getName(), false);

                educacionalLevelAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_NIVEL_EDUCACION), getString(R.string.VIEW_MODEL_LISTADO_CARGADO));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_NIVEL_EDUCACION) + getString(R.string.VIEW_MODEL_LISTADO_CARGADO));
            }

        });

        newIntervieweeViewModel.showMsgErrorListEducationalLevels().observe(this, s -> {

            if (!isSnackBarShow) {

                isSnackBarShow = true;
                showSnackbar(findViewById(R.id.form_new_interviewee), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_NIVEL_EDUCACION), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_NIVEL_EDUCACION) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
        });
    }

    private void viewModelCoexistenceType() {


        newIntervieweeViewModel.isLoadingCoexistenceTypes().observe(this, aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);
                activateInputs(false);

            } else {
                progressBar.setVisibility(View.GONE);
                activateInputs(true);
            }
        });


        newIntervieweeViewModel.loadCoexistenceTypes().observe(this, list -> {

            if (list != null && list.size() > 0) {

                coexistenceTypeList = list;
                coexistenceTypeAdapter = new CohabitTypeAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, coexistenceTypeList);
                acCoexistenceType.setAdapter(coexistenceTypeAdapter);
                acCoexistenceType.setText(coexistenceTypeList.get(0).getName(), false);

                coexistenceTypeAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_CONVIVENCIA), getString(R.string.VIEW_MODEL_LISTADO_CARGADO));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_TIPO_CONVIVENCIA) + getString(R.string.VIEW_MODEL_LISTADO_CARGADO));
            }

        });


        newIntervieweeViewModel.showMsgErrorListCoexistenceTypes().observe(this, s -> {

            if (!isSnackBarShow) {

                isSnackBarShow = true;
                showSnackbar(findViewById(R.id.form_new_interviewee), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_CONVIVENCIA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_TIPO_CONVIVENCIA) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

        });
    }

    private void viewModelProfession() {

        newIntervieweeViewModel.isLoadingProfession().observe(this, aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);
                activateInputs(false);

            } else {
                progressBar.setVisibility(View.GONE);
                activateInputs(true);
            }
        });


        newIntervieweeViewModel.loadProfession().observe(this, professions -> {

            if (professions != null && professions.size() > 0) {

                professionList = professions;
                professionAdapter = new ProfessionAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, professionList);
                acProfession.setAdapter(professionAdapter);
                acProfession.setText(professionList.get(0).getName(), false);

                professionAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_PROFESIONES), getString(R.string.VIEW_MODEL_LISTADO_CARGADO));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_PROFESIONES) + getString(R.string.VIEW_MODEL_LISTADO_CARGADO));
            }
        });


        newIntervieweeViewModel.showMsgErrorListProfession().observe(this, s -> {

            if (!isSnackBarShow) {

                isSnackBarShow = true;
                showSnackbar(findViewById(R.id.form_new_interviewee), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));

            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_PROFESIONES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_PROFESIONES) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
        });
    }

    private void setSpinnerGenre() {

        String[] opcionesSexo = new String[]{getString(R.string.SEXO_MASCULINO), getString(R.string.SEXO_FEMENINO), getString(R.string.SEXO_OTRO)};
        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, opcionesSexo);
        acGenre.setAdapter(adapterSexo);
    }

    private void setPickerBirthDate() {

        etBirthDate.setOnClickListener(v -> Utils.iniciarDatePicker(etBirthDate, NewIntervieweeActivity.this, "interviewee"));


        ilBirthDate.setEndIconOnClickListener(v -> Utils.iniciarDatePicker(etBirthDate, NewIntervieweeActivity.this, "interviewee"));
    }

    private void setFalls() {

        switchFalls.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                ilNFalls.setVisibility(View.VISIBLE);
                etNFalls.setVisibility(View.VISIBLE);

                tvFallsValue.setText(R.string.SI);

            } else {
                ilNFalls.setVisibility(View.GONE);
                etNFalls.setVisibility(View.GONE);

                tvFallsValue.setText(R.string.NO);
            }
        });

    }

    private void setRetire() {

        switchLegalRetire.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                tvRetireValue.setText(getString(R.string.SI));
            } else {
                tvRetireValue.setText(getString(R.string.NO));
            }
        });
    }

    private boolean validateFields() {

        int errorCounter = 0;


        if (Objects.requireNonNull(etName.getText(), "Et name cannot be null").toString().isEmpty()) {

            ilName.setErrorEnabled(true);
            ilName.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;

        } else {
            ilName.setErrorEnabled(false);
        }


        if (Objects.requireNonNull(etLastName.getText(), "Et last name cannot be null").toString().isEmpty()) {

            ilLastName.setErrorEnabled(true);
            ilLastName.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;

        } else {
            ilLastName.setErrorEnabled(false);
        }


        if (acGenre.getText().toString().isEmpty()) {

            ilGenre.setErrorEnabled(true);
            ilGenre.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;

        } else {
            ilGenre.setErrorEnabled(false);
        }


        if (Objects.requireNonNull(etBirthDate.getText(), "Et birth date cannot be null").toString().isEmpty()) {

            ilBirthDate.setErrorEnabled(true);
            ilBirthDate.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;

        } else {

            try {

                if (Utils.isFutureDate(getApplicationContext(), etBirthDate.getText().toString())) {

                    ilBirthDate.setErrorEnabled(true);
                    ilBirthDate.setError(getString(R.string.VALIDACION_FECHA_FUTURA));
                    errorCounter++;

                } else {
                    ilBirthDate.setErrorEnabled(false);
                }

            } catch (ParseException e) {

                Log.d("FUTURE_DATE", "Parse exception");
                e.printStackTrace();
            }
        }

        if (acCity.getText().toString().isEmpty()) {

            ilCity.setErrorEnabled(true);
            ilCity.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;

        } else {
            ilCity.setErrorEnabled(false);
        }


        if (acCivilState.getText().toString().isEmpty()) {

            ilCivilState.setErrorEnabled(true);
            ilCivilState.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;

        } else {
            ilCivilState.setErrorEnabled(false);
        }


        if (Objects.requireNonNull(etNCoexistence.getText(), "Et coexistence cannot be null").toString().isEmpty()) {

            ilNCoexistence.setErrorEnabled(true);
            ilNCoexistence.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;

        } else {
            ilNCoexistence.setErrorEnabled(false);
        }


        if (switchFalls.isChecked()) {

            if (TextUtils.isEmpty(etNFalls.getText())) {

                ilNFalls.setErrorEnabled(true);
                ilNFalls.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
                errorCounter++;

            } else if (Objects.requireNonNull(etNFalls.getText(), "Et falls cannot be null").toString().equals("0")) {

                ilNFalls.setErrorEnabled(true);
                ilNFalls.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO_CERO));
                errorCounter++;

            } else {
                ilNFalls.setErrorEnabled(false);
            }
        }

        return errorCounter == 0;
    }

    @Override
    public void showSnackbar(View v, int duration, String title, String action) {
        Snackbar snackbar = Snackbar.make(v, title, duration);

        if (action != null) {

            snackbar.setAction(action, v1 -> {

                newIntervieweeViewModel.refreshCivilStates();
                newIntervieweeViewModel.refreshCities();
                newIntervieweeViewModel.refreshEducationalLevels();
                newIntervieweeViewModel.refreshCoexistenceTypes();
                newIntervieweeViewModel.refreshProfession();

                progressBar.setVisibility(View.VISIBLE);

                isSnackBarShow = false;
            });
        }

        isSnackBarShow = false;
        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();

            return true;

        } else if (item.getItemId() == R.id.menu_save) {

            if (validateFields()) {

                progressBar.setVisibility(View.VISIBLE);

                Interviewee interviewee = new Interviewee();

                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);
                int idInvestigador = sharedPreferences.getInt(getString(R.string.SHARED_PREF_INVES_ID), 0);

                interviewee.setIdResearcher(idInvestigador);
                interviewee.setName(Objects.requireNonNull(etName.getText(), "Et name cannot be null").toString());
                interviewee.setLastName(Objects.requireNonNull(etLastName.getText(), "Et last name cannot be null").toString());

                if (acGenre.getText().toString().equals(getString(R.string.SEXO_MASCULINO))) {

                    interviewee.setGender(getString(R.string.SEXO_MASCULINO_MASTER_KEY));

                } else if (acGenre.getText().toString().equals(getString(R.string.SEXO_FEMENINO))) {

                    interviewee.setGender(getString(R.string.SEXO_FEMENINO_MASTER_KEY));

                } else if (acGenre.getText().toString().equals(getString(R.string.SEXO_OTRO))) {

                    interviewee.setGender(getString(R.string.SEXO_OTRO_MASTER_KEY));

                }

                Date birthDate = null;

                try {
                    birthDate = Utils.stringToDate(getApplicationContext(), false, Objects.requireNonNull(etBirthDate.getText()).toString());
                } catch (ParseException e) {

                    Log.d("STRING_TO_DATE", "Parse exception");
                    e.printStackTrace();
                }

                interviewee.setBirthDate(birthDate);

                City city = new City();
                city.setName(acCity.getText().toString());
                interviewee.setCity(city);

                CivilState civilState = new CivilState();
                civilState.setId(Objects.requireNonNull(searchCivilStateByName(acCivilState.getText().toString()), "Ac civil state cannot be null").getId());
                interviewee.setCivilState(civilState);

                interviewee.setnCohabiting3Months(Integer.parseInt(Objects.requireNonNull(etNCoexistence.getText(), "Cohabit type cannot be null").toString()));

                interviewee.setLegalRetired(switchLegalRetire.isChecked());
                interviewee.setFalls(switchFalls.isChecked());

                if (switchFalls.isChecked()) {

                    interviewee.setNCaidas(Integer.parseInt(Objects.requireNonNull(etNFalls.getText(), "Et n falls cannot be null").toString()));
                }

                if (!acEducationalLevel.getText().toString().isEmpty() && !acEducationalLevel.getText().toString().equals(educationalLevelList.get(0).getName())) {

                    EducationalLevel educationalLevel = new EducationalLevel();
                    educationalLevel.setId(Objects.requireNonNull(searchEducationalLevelByName(acEducationalLevel.getText().toString()), "Ac educational level cannot be null").getId());
                    interviewee.setEducationalLevel(educationalLevel);
                }

                if (!acProfession.getText().toString().isEmpty() && !acProfession.getText().toString().equals(professionList.get(0).getName())) {

                    Profession profession = new Profession();
                    profession.setName(acProfession.getText().toString());
                    interviewee.setProfession(profession);
                }

                if (!acCoexistenceType.getText().toString().isEmpty() && !acCoexistenceType.getText().toString().equals(coexistenceTypeList.get(0).getName())) {

                    CohabitType cohabitType = new CohabitType();
                    cohabitType.setId(Objects.requireNonNull(searchCoexistenceTypeByName(acCoexistenceType.getText().toString()), "Ac coexistence cannot be null").getId());
                    interviewee.setCoexistenteType(cohabitType);
                }

                IntervieweeRepository.getInstance(getApplication()).registryInterviewee(interviewee);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void activateInputs(boolean activado) {

        ilName.setEnabled(activado);
        etName.setEnabled(activado);

        ilLastName.setEnabled(activado);
        etLastName.setEnabled(activado);

        ilGenre.setEnabled(activado);
        acGenre.setEnabled(activado);

        ilBirthDate.setEnabled(activado);
        etBirthDate.setEnabled(activado);

        ilCity.setEnabled(activado);
        acCity.setEnabled(activado);

        ilCivilState.setEnabled(activado);
        acCivilState.setEnabled(activado);

        ilNCoexistence.setEnabled(activado);
        etNCoexistence.setEnabled(activado);

        switchLegalRetire.setEnabled(activado);

        switchFalls.setEnabled(activado);

        ilNFalls.setEnabled(activado);
        etNFalls.setEnabled(activado);

        ilCoexistenceType.setEnabled(activado);
        acCoexistenceType.setEnabled(activado);

        ilProfession.setEnabled(activado);
        acProfession.setEnabled(activado);

        ilEducationalLevel.setEnabled(activado);
        acEducationalLevel.setEnabled(activado);
    }

    private CivilState searchCivilStateByName(String nombre) {

        for (int i = 0; i < civilStateList.size(); i++) {

            if (civilStateList.get(i).getName().equals(nombre)) {

                return civilStateList.get(i);
            }
        }
        return null;
    }

    private EducationalLevel searchEducationalLevelByName(String nombre) {

        for (int i = 0; i < educationalLevelList.size(); i++) {

            if (educationalLevelList.get(i).getName().equals(nombre)) {

                return educationalLevelList.get(i);
            }
        }
        return null;
    }

    private CohabitType searchCoexistenceTypeByName(String nombre) {

        for (int i = 0; i < coexistenceTypeList.size(); i++) {

            if (coexistenceTypeList.get(i).getName().equals(nombre)) {

                return coexistenceTypeList.get(i);
            }
        }
        return null;
    }
}
