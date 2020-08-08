package cl.udelvd.views.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

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
import cl.udelvd.viewmodels.EditIntervieweeViewModel;

public class EditIntervieweeActivity extends AppCompatActivity implements SnackbarInterface {

    private ProgressBar progressBar;

    private Interviewee intervieweeIntent;

    private TextInputLayout ilName;
    private TextInputLayout ilLastName;
    private TextInputLayout ilGenre;
    private TextInputLayout ilBirthDate;
    private TextInputLayout ilCity;
    private TextInputLayout ilCivilState;
    private TextInputLayout ilNCoexistence;
    private TextInputLayout ilEducationalLevel;
    private TextInputLayout ilProfession;
    private TextInputLayout ilCoexistenceType;
    private TextInputLayout ilNFalls;

    private TextInputEditText etName;
    private TextInputEditText etLastName;
    private TextInputEditText etBirthDate;
    private AppCompatAutoCompleteTextView acGenre;
    private AppCompatAutoCompleteTextView acCity;
    private AppCompatAutoCompleteTextView acCivilState;
    private TextInputEditText etNCoexistence;
    private AppCompatAutoCompleteTextView acEducationalLevel;
    private AppCompatAutoCompleteTextView acProfession;
    private AppCompatAutoCompleteTextView acCoexistenceType;
    private TextInputEditText etNFalls;

    private SwitchMaterial switchLegalRetire;
    private SwitchMaterial switchFalls;

    private TextView tvSwitchFalls;
    private TextView tvSwitchRetire;

    private EditIntervieweeViewModel editIntervieweeViewModel;


    private List<City> cityList;
    private List<CivilState> civilStateList;
    private List<EducationalLevel> educationalLevelList;
    private List<Profession> professionList;
    private List<CohabitType> cohabitTypeList;


    private CityAdapter cityAdapter;
    private CivilStateAdapter civilStateAdapter;
    private EducationalLevelAdapter educationalLevelAdapter;
    private ProfessionAdapter professionAdapter;
    private CohabitTypeAdapter cohabitTypeAdapter;


    private boolean isSnackBarShow = false;
    private boolean isAutoCompleteCityReady = false;
    private boolean isAutoCompleteCivilStateReady = false;
    private boolean isAutoCompleteProfessionReady = false;
    private boolean isAutoCompleteCoexistenceTypeReady = false;
    private boolean isAutoCompleteEducationalLevelReady = false;
    private boolean isIntervieweeReady = false;


    private FirebaseCrashlytics crashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_interviewee);

        crashlytics = FirebaseCrashlytics.getInstance();

        Utils.configToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITULO_TOOLBAR_EDITAR_ENTREVISTADO));

        instantiateInterfaceResources();

        getBundleData();

        setSpinnerGenre();

        setPickerBirthDate();

        initViewModels();

        setSwitchFalls();

        setSwitchRetire();
    }

    private void initViewModels() {
        setAutoCompleteCity();

        setAutoCompleteCivilState();

        //Optionals
        setAutoCompleteEducationalLevel();
        setAutoCompleteProfession();
        setAutoCompleteCoexistenceType();

        initViewModelInterviewee();
    }

    private void getBundleData() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            int id_entrevistado = bundle.getInt(getString(R.string.KEY_ENTREVISTADO_ID_LARGO));
            intervieweeIntent = new Interviewee();
            intervieweeIntent.setId(id_entrevistado);
        }
    }

    private void instantiateInterfaceResources() {

        progressBar = findViewById(R.id.progress_horizontal_edit_interviewee);
        progressBar.setVisibility(View.VISIBLE);

        ilName = findViewById(R.id.il_interview_name);
        ilLastName = findViewById(R.id.il_interview_last_name);
        ilGenre = findViewById(R.id.il_interviewee_genre);
        ilBirthDate = findViewById(R.id.il_birth_date);
        ilCity = findViewById(R.id.il_interview_city);
        ilCivilState = findViewById(R.id.il_interviewee_civil_state);
        ilNCoexistence = findViewById(R.id.il_n_coexistence_interviewee);
        ilEducationalLevel = findViewById(R.id.il_interviewee_educational_level);
        ilProfession = findViewById(R.id.il_interviewee_profession);
        ilCoexistenceType = findViewById(R.id.il_interviewee_coexistence_type);
        ilNFalls = findViewById(R.id.il_n_falls_interviewee);

        etName = findViewById(R.id.et_interview_name);
        etLastName = findViewById(R.id.et_interview_last_name);
        etBirthDate = findViewById(R.id.et_birth_date);
        etNCoexistence = findViewById(R.id.et_n_coexistence_interviewee);
        etNFalls = findViewById(R.id.et_n_falls_interviewee);

        acGenre = findViewById(R.id.et_interviewee_genre);
        acCity = findViewById(R.id.et_interview_city);
        acCivilState = findViewById(R.id.et_interviewee_civil_state);

        //OPTIONALS
        acEducationalLevel = findViewById(R.id.et_interviewee_educational_level);
        acProfession = findViewById(R.id.et_interviewee_profession);
        acCoexistenceType = findViewById(R.id.et_interviewee_coexistence_type);

        switchLegalRetire = findViewById(R.id.switch_retire_legal);
        switchFalls = findViewById(R.id.switch_interviewee_falls);

        tvSwitchFalls = findViewById(R.id.tv_switch_falls);
        tvSwitchRetire = findViewById(R.id.tv_switch_retire_value);

        configEndIcons();

        editIntervieweeViewModel = new ViewModelProvider(this).get(EditIntervieweeViewModel.class);

    }

    private void configEndIcons() {
        acCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().isEmpty()) {
                    ilCity.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
                    ilCity.setEndIconDrawable(R.drawable.ic_close_white_24dp);
                    ilCity.setEndIconOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            acCity.setText("");
                        }
                    });
                } else {
                    ilCity.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU);
                }
            }
        });

        //OPTIONALS
        acEducationalLevel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().isEmpty()) {
                    ilEducationalLevel.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
                    ilEducationalLevel.setEndIconDrawable(R.drawable.ic_close_white_24dp);
                    ilEducationalLevel.setEndIconOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            acEducationalLevel.setText("");
                        }
                    });
                } else {
                    ilEducationalLevel.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU);
                }
            }
        });
        acCoexistenceType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().isEmpty()) {
                    ilCoexistenceType.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
                    ilCoexistenceType.setEndIconDrawable(R.drawable.ic_close_white_24dp);
                    ilCoexistenceType.setEndIconOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            acCoexistenceType.setText("");
                        }
                    });
                } else {
                    ilCoexistenceType.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU);
                }
            }
        });
        acProfession.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().isEmpty()) {
                    ilProfession.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
                    ilProfession.setEndIconDrawable(R.drawable.ic_close_white_24dp);
                    ilProfession.setEndIconOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            acProfession.setText("");
                        }
                    });
                } else {
                    ilProfession.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU);
                }
            }
        });

    }

    private void setSpinnerGenre() {

        String[] opcionesSexo = new String[]{getString(R.string.SEXO_MASCULINO), getString(R.string.SEXO_FEMENINO), getString(R.string.SEXO_OTRO)};
        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, opcionesSexo);
        acGenre.setAdapter(adapterSexo);

    }

    private void setPickerBirthDate() {

        etBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.iniciarDatePicker(etBirthDate, EditIntervieweeActivity.this, "interviewee");
            }
        });


        ilBirthDate.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.iniciarDatePicker(etBirthDate, EditIntervieweeActivity.this, "interviewee");
            }
        });
    }

    private void setAutoCompleteCity() {

        editIntervieweeViewModel.isLoadingCities().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);

                    activateInputs(false);
                } else {
                    progressBar.setVisibility(View.GONE);

                    activateInputs(true);
                }
            }
        });


        editIntervieweeViewModel.loadCities().observe(this, new Observer<List<City>>() {
            @Override
            public void onChanged(List<City> cities) {

                if (cities != null && cities.size() > 0) {

                    cityList = cities;
                    cityAdapter = new CityAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, cityList);
                    acCity.setAdapter(cityAdapter);

                    isAutoCompleteCityReady = true;

                    Log.d(getString(R.string.TAG_VIEW_MODEL_CIUDAD), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                    crashlytics.log(getString(R.string.TAG_VIEW_MODEL_CIUDAD) + getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

                    progressBar.setVisibility(View.GONE);

                    cityAdapter.notifyDataSetChanged();

                    setIntervieweeInfo();
                }

            }
        });


        editIntervieweeViewModel.showMsgErrorCityList().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.form_edit_interviewee), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_CIUDAD), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_CIUDAD) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    private void setAutoCompleteCivilState() {

        editIntervieweeViewModel.isLoadingCivilState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);

                    activateInputs(false);
                } else {
                    progressBar.setVisibility(View.GONE);

                    activateInputs(true);
                }
            }
        });

        editIntervieweeViewModel.loadCivilState().observe(this, new Observer<List<CivilState>>() {
            @Override
            public void onChanged(List<CivilState> civilStates) {

                if (civilStates != null && civilStates.size() > 0) {
                    civilStateList = civilStates;
                    civilStateAdapter = new CivilStateAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, civilStateList);
                    acCivilState.setAdapter(civilStateAdapter);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_ESTADO_CIVIL), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                    crashlytics.log(getString(R.string.TAG_VIEW_MODEL_ESTADO_CIVIL) + getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

                    civilStateAdapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);

                    isAutoCompleteCivilStateReady = true;

                    setIntervieweeInfo();
                }
            }
        });

        editIntervieweeViewModel.showMsgErrorListCivilState().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.form_edit_interviewee), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_ESTADO_CIVIL), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_ESTADO_CIVIL) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    private void setAutoCompleteEducationalLevel() {

        editIntervieweeViewModel.isLoadingEducationalLevel().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {

                    progressBar.setVisibility(View.VISIBLE);

                    activateInputs(false);
                } else {
                    progressBar.setVisibility(View.GONE);

                    activateInputs(true);
                }
            }
        });


        editIntervieweeViewModel.loadEducationalLevel().observe(this, new Observer<List<EducationalLevel>>() {
            @Override
            public void onChanged(List<EducationalLevel> educationalLevels) {

                if (educationalLevels != null && educationalLevels.size() > 0) {
                    educationalLevelList = educationalLevels;
                    educationalLevelAdapter = new EducationalLevelAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, educationalLevelList);
                    acEducationalLevel.setAdapter(educationalLevelAdapter);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_NIVEL_EDUCACION), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                    crashlytics.log(getString(R.string.TAG_VIEW_MODEL_NIVEL_EDUCACION) + getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

                    educationalLevelAdapter.notifyDataSetChanged();

                    isAutoCompleteEducationalLevelReady = true;

                    progressBar.setVisibility(View.GONE);

                    setIntervieweeInfo();
                }

            }
        });

        editIntervieweeViewModel.showMsgErrorEducationalLevel().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.form_edit_interviewee), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_NIVEL_EDUCACION), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_NIVEL_EDUCACION) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            }
        });
    }

    private void setAutoCompleteProfession() {

        editIntervieweeViewModel.isLoadingProfessions().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {

                    progressBar.setVisibility(View.VISIBLE);

                    activateInputs(false);
                } else {
                    progressBar.setVisibility(View.GONE);

                    activateInputs(true);
                }
            }
        });

        editIntervieweeViewModel.loadProfession().observe(this, new Observer<List<Profession>>() {
            @Override
            public void onChanged(List<Profession> professions) {
                if (professions != null && professions.size() > 0) {
                    professionList = professions;
                    professionAdapter = new ProfessionAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, professionList);
                    acProfession.setAdapter(professionAdapter);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_PROFESIONES), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                    crashlytics.log(getString(R.string.TAG_VIEW_MODEL_PROFESIONES) + getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

                    professionAdapter.notifyDataSetChanged();

                    isAutoCompleteProfessionReady = true;

                    progressBar.setVisibility(View.GONE);

                    setIntervieweeInfo();
                }

            }
        });

        editIntervieweeViewModel.showMsgErrorListProfessions().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.form_edit_interviewee), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_PROFESIONES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_PROFESIONES) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            }
        });
    }

    private void setAutoCompleteCoexistenceType() {

        editIntervieweeViewModel.isLoadingCoexistenceType().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);

                    activateInputs(false);
                } else {
                    progressBar.setVisibility(View.GONE);

                    activateInputs(true);
                }
            }
        });

        editIntervieweeViewModel.loadCoexitenceType().observe(this, new Observer<List<CohabitType>>() {
            @Override
            public void onChanged(List<CohabitType> list) {

                if (list != null && list.size() > 0) {
                    cohabitTypeList = list;
                    cohabitTypeAdapter = new CohabitTypeAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, cohabitTypeList);
                    acCoexistenceType.setAdapter(cohabitTypeAdapter);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_CONVIVENCIA), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                    crashlytics.log(getString(R.string.TAG_VIEW_MODEL_TIPO_CONVIVENCIA) + getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

                    cohabitTypeAdapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);

                    isAutoCompleteCoexistenceTypeReady = true;

                    setIntervieweeInfo();
                }

            }
        });


        editIntervieweeViewModel.showMsgErrorListCoexistenceType().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.form_edit_interviewee), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_CONVIVENCIA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_TIPO_CONVIVENCIA) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            }
        });
    }

    private void setSwitchFalls() {

        switchFalls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ilNFalls.setVisibility(View.VISIBLE);
                    etNFalls.setVisibility(View.VISIBLE);

                    tvSwitchFalls.setText(getString(R.string.SI));

                } else {
                    ilNFalls.setVisibility(View.GONE);
                    etNFalls.setVisibility(View.GONE);

                    tvSwitchFalls.setText(getString(R.string.NO));
                }
            }
        });

    }

    private void setSwitchRetire() {

        switchLegalRetire.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    tvSwitchRetire.setText(getString(R.string.SI));
                } else {
                    tvSwitchRetire.setText(getString(R.string.NO));
                }
            }
        });
    }

    private void initViewModelInterviewee() {


        editIntervieweeViewModel.isLoadingInterviewee().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);

                    activateInputs(false);
                } else {
                    progressBar.setVisibility(View.GONE);

                    activateInputs(true);
                }
            }
        });

        editIntervieweeViewModel.loadInterviewee(intervieweeIntent).observe(this, new Observer<Interviewee>() {
            @Override
            public void onChanged(Interviewee intervieweeInternet) {

                if (intervieweeInternet != null) {
                    intervieweeIntent = intervieweeInternet;

                    progressBar.setVisibility(View.GONE);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), intervieweeIntent.toString()));
                    crashlytics.log(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTADO) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), intervieweeIntent.toString()));

                    isIntervieweeReady = true;

                    setIntervieweeInfo();
                }
            }

        });

        editIntervieweeViewModel.showMsgErrorInterviewee().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.form_edit_interviewee), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTADO) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });

        editIntervieweeViewModel.showMsgUpdate().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTADO) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                if (s.equals(getString(R.string.MSG_UPDATE_ENTREVISTADO))) {
                    Intent intent = getIntent();
                    intent.putExtra(getString(R.string.INTENT_KEY_MSG_ACTUALIZACION), s);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        editIntervieweeViewModel.showMsgErrorUpdate().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.form_edit_interviewee), Snackbar.LENGTH_LONG, s, null);
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTADO) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }


    private void setIntervieweeInfo() {

        if (isAutoCompleteCityReady && isAutoCompleteCivilStateReady && isAutoCompleteEducationalLevelReady &&
                isAutoCompleteProfessionReady && isAutoCompleteCoexistenceTypeReady && isIntervieweeReady) {

            etName.setText(intervieweeIntent.getName());
            etLastName.setText(intervieweeIntent.getLastName());

            if (intervieweeIntent.getGender().equals(getString(R.string.SEXO_MASCULINO_MASTER_KEY))) {
                acGenre.setText(getString(R.string.SEXO_MASCULINO), false);
            } else if (intervieweeIntent.getGender().equals(getString(R.string.SEXO_FEMENINO_MASTER_KEY))) {
                acGenre.setText(getString(R.string.SEXO_FEMENINO), false);
            } else if (intervieweeIntent.getGender().equals(getString(R.string.SEXO_OTRO_MASTER_KEY))) {
                acGenre.setText(getString(R.string.SEXO_OTRO), false);
            }

            String birthDate = Utils.dateToString(getApplicationContext(), false, intervieweeIntent.getBirthDate());
            etBirthDate.setText(birthDate);


            String cityName = Objects.requireNonNull(searchCityById(intervieweeIntent.getCity().getId())).getName();
            acCity.setText(cityName);


            String civilStateName = Objects.requireNonNull(searchCivilStateById(intervieweeIntent.getCivilState().getId())).getName();
            acCivilState.setText(civilStateName, false);

            etNCoexistence.setText(String.valueOf(intervieweeIntent.getNConvivientes3Meses()));

            //LEGAL RETIREE
            if (intervieweeIntent.isLegalRetired()) {
                switchLegalRetire.setChecked(true);
                tvSwitchRetire.setText(getString(R.string.SI));
            } else {
                switchLegalRetire.setChecked(false);
                tvSwitchRetire.setText(getString(R.string.NO));
            }

            //FALLS
            if (intervieweeIntent.isFalls()) {
                switchFalls.setChecked(true);
                etNFalls.setVisibility(View.VISIBLE);
                ilNFalls.setVisibility(View.VISIBLE);
                etNFalls.setText(String.valueOf(intervieweeIntent.getNCaidas()));
                tvSwitchFalls.setText(getString(R.string.SI));
            } else {
                switchFalls.setChecked(false);
                etNFalls.setVisibility(View.GONE);
                ilNFalls.setVisibility(View.GONE);
                tvSwitchFalls.setText(getString(R.string.NO));
            }


            if (intervieweeIntent.getEducationalLevel() != null) {
                String nombre = Objects.requireNonNull(searchEducationalLevelById(intervieweeIntent.getEducationalLevel().getId())).getName();
                acEducationalLevel.setText(nombre, false);
            }

            if (intervieweeIntent.getProfession() != null) {
                String nombre = Objects.requireNonNull(searchProfessionById(intervieweeIntent.getProfession().getId())).getName();
                acProfession.setText(nombre);
            }

            if (intervieweeIntent.getCoexistenteType() != null) {
                String nombre = Objects.requireNonNull(searchCoexistenceTypeById(intervieweeIntent.getCoexistenteType().getId())).getName();
                acCoexistenceType.setText(nombre, false);
            }

            progressBar.setVisibility(View.GONE);

            isSnackBarShow = false;
            isAutoCompleteCityReady = false;
            isAutoCompleteCivilStateReady = false;
            isAutoCompleteProfessionReady = false;
            isAutoCompleteCoexistenceTypeReady = false;
            isAutoCompleteEducationalLevelReady = false;
            isIntervieweeReady = false;
        }
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
                int idResearcher = sharedPreferences.getInt(getString(R.string.KEY_ENTREVISTADO_ID_INVESTIGADOR), 0);

                interviewee.setId(intervieweeIntent.getId());
                interviewee.setIdResearcher(idResearcher);
                interviewee.setName(Objects.requireNonNull(etName.getText()).toString());
                interviewee.setLastName(Objects.requireNonNull(etLastName.getText()).toString());

                if (acGenre.getText().toString().equals(getString(R.string.SEXO_MASCULINO))) {
                    interviewee.setGender(getString(R.string.SEXO_MASCULINO_MASTER_KEY));
                } else if (acGenre.getText().toString().equals(getString(R.string.SEXO_FEMENINO))) {
                    interviewee.setGender(getString(R.string.SEXO_FEMENINO_MASTER_KEY));
                } else if (acGenre.getText().toString().equals(getString(R.string.SEXO_OTRO))) {
                    interviewee.setGender(getString(R.string.SEXO_OTRO_MASTER_KEY));
                }

                Date birthDate = Utils.stringToDate(getApplicationContext(), false, Objects.requireNonNull(etBirthDate.getText()).toString());
                interviewee.setBirthDate(birthDate);

                City city = new City();
                city.setName(acCity.getText().toString());
                interviewee.setCity(city);

                int idCivilState = Objects.requireNonNull(searchCivilStateByName(acCivilState.getText().toString())).getId();
                CivilState civilState = new CivilState();
                civilState.setId(idCivilState);
                interviewee.setCivilState(civilState);

                interviewee.setnCohabiting3Months(Integer.parseInt(Objects.requireNonNull(etNCoexistence.getText()).toString()));

                if (switchLegalRetire.isChecked()) {
                    interviewee.setLegalRetired(true);
                } else {
                    interviewee.setLegalRetired(false);
                }

                if (switchFalls.isChecked()) {
                    interviewee.setFalls(true);

                    interviewee.setNCaidas(Integer.parseInt(Objects.requireNonNull(etNFalls.getText()).toString()));
                } else {
                    interviewee.setFalls(false);
                }

                if (!acEducationalLevel.getText().toString().isEmpty()) {
                    int id_nivel_educacional = Objects.requireNonNull(searchEducationalLevelByName(acEducationalLevel.getText().toString())).getId();
                    EducationalLevel educationalLevel = new EducationalLevel();
                    educationalLevel.setId(id_nivel_educacional);
                    interviewee.setEducationalLevel(educationalLevel);
                }

                if (!acProfession.getText().toString().isEmpty()) {
                    Profession profession = new Profession();
                    profession.setName(acProfession.getText().toString());
                    interviewee.setProfession(profession);
                }

                if (!acCoexistenceType.getText().toString().isEmpty()) {
                    int id_tipo_convivencia = Objects.requireNonNull(searchCoexistenceTypeByName(acCoexistenceType.getText().toString())).getId();
                    CohabitType cohabitType = new CohabitType();
                    cohabitType.setId(id_tipo_convivencia);
                    interviewee.setCoexistenteType(cohabitType);
                }

                IntervieweeRepository.getInstance(getApplication()).updateInterviewee(interviewee);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showSnackbar(View v, int duration, String title, String action) {

        Snackbar snackbar = Snackbar.make(v, title, duration);

        if (action != null) {
            snackbar.setAction(action, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    isSnackBarShow = false;
                    isAutoCompleteCityReady = false;
                    isAutoCompleteCivilStateReady = false;
                    isAutoCompleteProfessionReady = false;
                    isAutoCompleteCoexistenceTypeReady = false;
                    isAutoCompleteEducationalLevelReady = false;
                    isIntervieweeReady = false;


                    editIntervieweeViewModel.refreshCivilState();
                    editIntervieweeViewModel.refreshCities();
                    editIntervieweeViewModel.refreshEducationalLevel();
                    editIntervieweeViewModel.refreshCoexistenceType();
                    editIntervieweeViewModel.refreshProfession();


                    editIntervieweeViewModel.refreshInterviewee(intervieweeIntent);

                    progressBar.setVisibility(View.VISIBLE);
                }
            });
        }
        snackbar.show();
        isSnackBarShow = false;
    }

    private boolean validateFields() {

        int errorCounter = 0;

        if (Objects.requireNonNull(etName.getText()).toString().isEmpty()) {
            ilName.setErrorEnabled(true);
            ilName.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;
        } else {
            ilName.setErrorEnabled(false);
        }

        if (Objects.requireNonNull(etLastName.getText()).toString().isEmpty()) {
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


        if (Objects.requireNonNull(etBirthDate.getText()).toString().isEmpty()) {
            ilBirthDate.setErrorEnabled(true);
            ilBirthDate.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;
        } else if (Utils.isFutureDate(getApplicationContext(), etBirthDate.getText().toString())) {
            ilBirthDate.setErrorEnabled(true);
            ilBirthDate.setError(getString(R.string.VALIDACION_FECHA_FUTURA));
            errorCounter++;
        } else {
            ilBirthDate.setErrorEnabled(false);
        }


        if (acCivilState.getText().toString().isEmpty()) {
            ilCivilState.setErrorEnabled(true);
            ilCivilState.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;
        } else {
            ilCivilState.setErrorEnabled(false);
        }


        if (Objects.requireNonNull(etNCoexistence.getText()).toString().isEmpty()) {
            ilNCoexistence.setErrorEnabled(true);
            ilNCoexistence.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;
        } else {
            ilNCoexistence.setErrorEnabled(false);
        }

        if (switchFalls.isChecked()) {

            if (Objects.requireNonNull(etNFalls.getText()).toString().isEmpty()) {
                ilNFalls.setErrorEnabled(true);
                ilNFalls.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
                errorCounter++;
            } else if (etNFalls.getText().toString().equals("0")) {
                ilNFalls.setErrorEnabled(true);
                ilNFalls.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO_CERO));
                errorCounter++;
            } else {
                ilNFalls.setErrorEnabled(false);
            }
        }

        return errorCounter == 0;
    }

    private void activateInputs(boolean activate) {

        ilName.setEnabled(activate);
        etName.setEnabled(activate);

        ilLastName.setEnabled(activate);
        etLastName.setEnabled(activate);

        ilGenre.setEnabled(activate);
        acGenre.setEnabled(activate);

        ilBirthDate.setEnabled(activate);
        etBirthDate.setEnabled(activate);

        ilCity.setEnabled(activate);
        acCity.setEnabled(activate);

        ilCivilState.setEnabled(activate);
        acCivilState.setEnabled(activate);

        ilNCoexistence.setEnabled(activate);
        etNCoexistence.setEnabled(activate);

        switchLegalRetire.setEnabled(activate);

        switchFalls.setEnabled(activate);

        ilNFalls.setEnabled(activate);
        etNFalls.setEnabled(activate);

        ilCoexistenceType.setEnabled(activate);
        acCoexistenceType.setEnabled(activate);

        ilProfession.setEnabled(activate);
        acProfession.setEnabled(activate);

        ilEducationalLevel.setEnabled(activate);
        acEducationalLevel.setEnabled(activate);
    }

    private CohabitType searchCoexistenceTypeById(int id) {

        for (int i = 0; i < cohabitTypeList.size(); i++) {
            if (cohabitTypeList.get(i).getId() == id) {
                return cohabitTypeList.get(i);
            }
        }
        return null;
    }

    private Profession searchProfessionById(int id) {
        for (int i = 0; i < professionList.size(); i++) {
            if (professionList.get(i).getId() == id) {
                return professionList.get(i);
            }
        }

        return null;
    }

    private EducationalLevel searchEducationalLevelById(int id) {

        for (int i = 0; i < educationalLevelList.size(); i++) {
            if (educationalLevelList.get(i).getId() == id) {
                return educationalLevelList.get(i);
            }
        }
        return null;
    }

    private CivilState searchCivilStateById(int id) {
        for (int i = 0; i < civilStateList.size(); i++) {
            if (civilStateList.get(i).getId() == id) {
                return civilStateList.get(i);
            }
        }
        return null;
    }

    private City searchCityById(int id) {

        for (int i = 0; i < cityList.size(); i++) {
            if (cityList.get(i).getId() == id) {
                return cityList.get(i);
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

    private CivilState searchCivilStateByName(String nombre) {
        for (int i = 0; i < civilStateList.size(); i++) {
            if (civilStateList.get(i).getName().equals(nombre)) {
                return civilStateList.get(i);
            }
        }
        return null;
    }

    private CohabitType searchCoexistenceTypeByName(String nombre) {

        for (int i = 0; i < cohabitTypeList.size(); i++) {
            if (cohabitTypeList.get(i).getName().equals(nombre)) {
                return cohabitTypeList.get(i);
            }
        }
        return null;
    }
}
