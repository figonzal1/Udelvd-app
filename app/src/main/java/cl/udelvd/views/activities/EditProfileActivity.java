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
import android.widget.CompoundButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Map;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.models.Researcher;
import cl.udelvd.repositories.ResearcherRepository;
import cl.udelvd.utils.SnackbarInterface;
import cl.udelvd.utils.Utils;
import cl.udelvd.viewmodels.EditProfileViewModel;

public class EditProfileActivity extends AppCompatActivity implements SnackbarInterface {

    private TextInputLayout ilName;
    private TextInputLayout ilLastName;
    private TextInputLayout ilEmail;
    private TextInputLayout ilPassword;
    private TextInputLayout ilConfirmacionPassword;

    private TextInputEditText etName;
    private TextInputEditText etLastName;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmacionPassword;

    private ProgressBar progressBar;
    private ConstraintLayout clPasswordOptional;

    private Researcher researcher;

    private SwitchMaterial switchCompat;
    private EditProfileViewModel editProfileViewModel;
    private boolean isSnackBarShow = false;

    private FirebaseCrashlytics crashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        crashlytics = FirebaseCrashlytics.getInstance();

        Utils.configToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITULO_TOOLBAR_EDITAR_PERFIL));

        instantiateInterfaceResources();

        initViewModel();

        getBundleData();

        loadResearcherData();

        configSwitchPasswordOptional();
    }

    private void instantiateInterfaceResources() {

        clPasswordOptional = findViewById(R.id.cl_optional_password);

        ilName = findViewById(R.id.il_researcher_name);
        ilLastName = findViewById(R.id.il_researcher_last_name);
        ilEmail = findViewById(R.id.il_researcher_email);
        ilEmail.setEnabled(false);
        ilPassword = findViewById(R.id.il_researcher_password);
        ilConfirmacionPassword = findViewById(R.id.il_research_confirm_password);


        etName = findViewById(R.id.et_researcher_name);
        etLastName = findViewById(R.id.et_researcher_last_name);
        etEmail = findViewById(R.id.et_researcher_email);
        etEmail.setEnabled(false);
        etPassword = findViewById(R.id.et_researcher_password);
        etConfirmacionPassword = findViewById(R.id.et_research_confirm_password);

        progressBar = findViewById(R.id.progress_horizontal_edit_profile);

        editProfileViewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
    }

    private void initViewModel() {


        editProfileViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (aBoolean) {

                    progressBar.setVisibility(View.VISIBLE);

                    ilName.setEnabled(false);
                    etName.setEnabled(false);

                    etLastName.setEnabled(false);
                    ilLastName.setEnabled(false);

                    ilPassword.setEnabled(false);
                    etPassword.setEnabled(false);

                    ilConfirmacionPassword.setEnabled(false);
                    etConfirmacionPassword.setEnabled(false);

                } else {
                    progressBar.setVisibility(View.GONE);

                    ilName.setEnabled(true);
                    etName.setEnabled(true);

                    etLastName.setEnabled(true);
                    ilLastName.setEnabled(true);

                    ilPassword.setEnabled(true);
                    etPassword.setEnabled(true);

                    ilConfirmacionPassword.setEnabled(true);
                    etConfirmacionPassword.setEnabled(true);
                }
            }
        });


        editProfileViewModel.showMsgUpdate().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(Map<String, Object> stringObjectMap) {

                if (stringObjectMap != null) {

                    Researcher researcher = (Researcher) stringObjectMap.get(getString(R.string.KEY_INVES_OBJECT));

                    SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    if (researcher != null) {

                        Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_PERFIL), getString(R.string.VIEW_MODEL_MSG_RESPONSE) + researcher.toString());
                        crashlytics.log(getString(R.string.TAG_VIEW_MODEL_EDITAR_PERFIL) + getString(R.string.VIEW_MODEL_MSG_RESPONSE) + researcher.toString());

                        //Guardar en sharedPref investigador con datos actualizados
                        editor.putString(getString(R.string.SHARED_PREF_INVES_NOMBRE), researcher.getName());
                        editor.putString(getString(R.string.SHARED_PREF_INVES_APELLIDO), researcher.getLastName());
                        editor.putString(getString(R.string.SHARED_PREF_INVES_EMAIL), researcher.getEmail());


                        if (switchCompat.isChecked()) {
                            //Actualizar password en sharedPref
                            editor.putString(getString(R.string.SHARED_PREF_INVES_PASSWORD), Objects.requireNonNull(etPassword.getText()).toString());
                        }
                        editor.apply();

                        String msgUpdate = (String) stringObjectMap.get(getString(R.string.UPDATE_MSG_VM));

                        progressBar.setVisibility(View.GONE);

                        if (msgUpdate != null) {

                            Log.d(getString(R.string.TAG_VIEW_MODEL_INVEST_UPDATE), msgUpdate);

                            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_INVEST_UPDATE) + msgUpdate);

                            if (msgUpdate.equals(getString(R.string.UPDATE_MSG_VM_SAVE))) {

                                Intent intent = getIntent();
                                intent.putExtra(getString(R.string.INTENT_KEY_MSG_ACTUALIZACION), msgUpdate);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                    }
                }
            }
        });


        editProfileViewModel.showMsgErrorUpdate().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.form_edit_profile), Snackbar.LENGTH_LONG, s, null);
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_PERFIL), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_EDITAR_PERFIL) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    private void getBundleData() {

        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();
            researcher = new Researcher();

            researcher.setId(bundle.getInt(getString(R.string.KEY_INVES_ID)));
            researcher.setIdRole(bundle.getInt(getString(R.string.KEY_INVES_ID_ROL)));
            researcher.setPassword(bundle.getString(getString(R.string.KEY_INVES_PASSWORD)));
            researcher.setRolName(bundle.getString(getString(R.string.KEY_INVES_NOMBRE_ROL)));
            researcher.setName(bundle.getString(getString(R.string.KEY_INVES_NOMBRE)));
            researcher.setLastName(bundle.getString(getString(R.string.KEY_INVES_APELLIDO)));
            researcher.setEmail(bundle.getString(getString(R.string.KEY_INVES_EMAIL)));
        }
    }

    private void loadResearcherData() {

        etName.setText(researcher.getName());
        etLastName.setText(researcher.getLastName());
        etEmail.setText(researcher.getEmail());
    }

    private void configSwitchPasswordOptional() {

        switchCompat = findViewById(R.id.switch_password_on);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    clPasswordOptional.setVisibility(View.VISIBLE);
                } else {
                    clPasswordOptional.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void showSnackbar(View v, int duration, String title, String action) {

        Snackbar snackbar = Snackbar.make(v, title, duration);
        snackbar.show();
        isSnackBarShow = false;
    }

    private boolean validateFields() {

        int errorCounter = 0;

        if (TextUtils.isEmpty(etName.getText())) {

            ilName.setErrorEnabled(true);
            ilName.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;

        } else {
            ilName.setErrorEnabled(false);
        }


        if (TextUtils.isEmpty(etLastName.getText())) {

            ilLastName.setErrorEnabled(true);
            ilLastName.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;

        } else {
            ilLastName.setErrorEnabled(false);
        }


        if (TextUtils.isEmpty(etEmail.getText())) {

            ilEmail.setErrorEnabled(true);
            ilEmail.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;

        } else {

            if (Utils.isInvalidEmail(etEmail.getText())) {

                ilEmail.setErrorEnabled(true);
                ilEmail.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
                errorCounter++;

            } else {
                ilEmail.setErrorEnabled(false);
            }
        }

        if (switchCompat.isChecked()) {

            if (TextUtils.isEmpty(etPassword.getText())) {

                ilPassword.setErrorEnabled(true);
                ilPassword.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
                errorCounter++;

            } else if (Objects.requireNonNull(etPassword.getText(), "Et password cannot be null").length() < 8) {

                ilPassword.setErrorEnabled(true);
                ilPassword.setError(getString(R.string.VALIDACION_PASSWORD_LARGO));
                errorCounter++;

            } else if (TextUtils.isEmpty(etConfirmacionPassword.getText())) {

                ilConfirmacionPassword.setErrorEnabled(true);
                ilConfirmacionPassword.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
                errorCounter++;

            } else {

                if (!etPassword.getText().toString().equals(Objects.requireNonNull(etConfirmacionPassword.getText(), "Et conf password cannot be null").toString())) {

                    ilConfirmacionPassword.setErrorEnabled(true);
                    ilConfirmacionPassword.setError(getString(R.string.VALIDACION_PASSWORD_NO_IGUALES));
                    errorCounter++;

                } else {
                    errorCounter = 0;
                    ilPassword.setErrorEnabled(false);
                    ilConfirmacionPassword.setErrorEnabled(false);
                }
            }
        }

        return errorCounter == 0;
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

                Researcher invesUpdate = new Researcher();

                invesUpdate.setId(researcher.getId());
                invesUpdate.setName(Objects.requireNonNull(etName.getText(), "Et name cannot be null").toString());
                invesUpdate.setLastName(Objects.requireNonNull(etLastName.getText(), "Et last name cannot be null").toString());
                invesUpdate.setEmail(Objects.requireNonNull(etEmail.getText(), "Et email cannot be null").toString());

                if (switchCompat.isChecked()) {
                    //Nueva pass
                    invesUpdate.setPassword(Objects.requireNonNull(etPassword.getText(), "Et password cannot be null").toString());
                } else {
                    //Pass antigua
                    invesUpdate.setPassword(researcher.getPassword());
                }

                invesUpdate.setIdRole(researcher.getIdRole());
                invesUpdate.setRolName(researcher.getRolName());

                ResearcherRepository.getInstance(getApplication()).updateResearcher(invesUpdate);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
