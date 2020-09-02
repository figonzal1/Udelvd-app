package cl.udelvd.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
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
import cl.udelvd.viewmodels.RegistryViewModel;

public class RegistryActivity extends AppCompatActivity implements SnackbarInterface {

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

    private RegistryViewModel registryViewModel;

    private FirebaseCrashlytics crashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);

        crashlytics = FirebaseCrashlytics.getInstance();

        Utils.configToolbar(this, getApplicationContext(), 0, getString(R.string.TITULO_TOOLBAR_REGISTRO));

        instantiateInterfaceResources();

        initViewModel();

        botonRegistry();
    }

    private void botonRegistry() {
        Button btnRegistry = findViewById(R.id.btn_registry);

        btnRegistry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateField()) {

                    progressBar.setVisibility(View.VISIBLE);

                    Researcher researcher = new Researcher();
                    researcher.setName(Objects.requireNonNull(etName.getText(), "Et name cannot be null").toString());
                    researcher.setLastName(Objects.requireNonNull(etLastName.getText(), "Et last name cannot be null").toString());

                    researcher.setEmail(Objects.requireNonNull(etEmail.getText(), "Et email cannot be null").toString());
                    researcher.setPassword(Objects.requireNonNull(etPassword.getText(), "Et password cannot be null").toString());
                    researcher.setRolName(getString(R.string.ROL_INVESTIG_KEY_MASTER));
                    researcher.setActivated(false);

                    ResearcherRepository.getInstance(getApplication()).registryResearcher(researcher);
                }

            }
        });
    }

    private void instantiateInterfaceResources() {

        progressBar = findViewById(R.id.progress_horizontal_registry_researcher);

        ilName = findViewById(R.id.il_researcher_name);
        ilLastName = findViewById(R.id.il_researcher_last_name);
        ilEmail = findViewById(R.id.il_researcher_email);
        ilPassword = findViewById(R.id.il_researcher_password);
        ilConfirmacionPassword = findViewById(R.id.il_research_confirm_password);

        etName = findViewById(R.id.et_researcher_name);
        etLastName = findViewById(R.id.et_researcher_last_name);
        etEmail = findViewById(R.id.et_researcher_email);
        etPassword = findViewById(R.id.et_researcher_password);
        etConfirmacionPassword = findViewById(R.id.et_research_confirm_password);

        registryViewModel = new ViewModelProvider(this).get(RegistryViewModel.class);
    }

    private void initViewModel() {

        registryViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (aBoolean) {

                    progressBar.setVisibility(View.VISIBLE);

                    ilName.setEnabled(false);
                    etName.setEnabled(false);

                    ilLastName.setEnabled(false);
                    etLastName.setEnabled(false);

                    ilEmail.setEnabled(false);
                    etEmail.setEnabled(false);

                    ilPassword.setEnabled(false);
                    ilConfirmacionPassword.setEnabled(false);

                } else {
                    progressBar.setVisibility(View.GONE);

                    ilName.setEnabled(true);
                    etName.setEnabled(true);

                    ilLastName.setEnabled(true);
                    etLastName.setEnabled(true);

                    ilEmail.setEnabled(true);
                    etEmail.setEnabled(true);

                    ilPassword.setEnabled(true);
                    ilConfirmacionPassword.setEnabled(true);
                }
            }
        });

        registryViewModel.showMsgRegistry().observe(this, new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> stringStringMap) {

                String msg_registro = stringStringMap.get(getString(R.string.INTENT_KEY_MSG_REGISTRO));
                String activado = stringStringMap.get(getString(R.string.INTENT_KEY_INVES_ACTIVADO));

                Log.d(getString(R.string.TAG_VM_INVES_REGISTRO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), msg_registro));
                crashlytics.log(getString(R.string.TAG_VM_INVES_REGISTRO) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), msg_registro));

                progressBar.setVisibility(View.GONE);

                if (msg_registro != null && msg_registro.equals(getString(R.string.MSG_INVEST_REGISTRADO))) {

                    Intent intent = getIntent();

                    if (activado != null) {

                        if (activado.equals("0")) {

                            intent.putExtra(getString(R.string.INTENT_KEY_MSG_REGISTRO), msg_registro + getString(R.string.SNACKBAR_ACTIVACION));

                        } else if (activado.equals("1")) {

                            intent.putExtra(getString(R.string.INTENT_KEY_MSG_REGISTRO), msg_registro);
                        }
                    }

                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        registryViewModel.showMsgErrorRegistry().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);
                showSnackbar(findViewById(R.id.registry_researcher), Snackbar.LENGTH_LONG, s, null);

                Log.d(getString(R.string.TAG_VM_INVES_REGISTRO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
                crashlytics.log(getString(R.string.TAG_VM_INVES_REGISTRO) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });

    }

    private boolean validateField() {

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
                ilEmail.setError(getString(R.string.VALIDACION_EMAIL));
                errorCounter++;

            } else {
                ilEmail.setErrorEnabled(false);
            }
        }

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

        return errorCounter == 0;
    }

    @Override
    public void showSnackbar(View v, int duration, String title, String action) {

        Snackbar snackbar = Snackbar.make(v, title, duration);
        snackbar.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
