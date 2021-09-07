package cl.udelvd.views.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.repositories.ResearcherRepository;
import cl.udelvd.utils.SnackbarInterface;
import cl.udelvd.viewmodels.ResetPassViewModel;

public class ResetPassActivity extends AppCompatActivity implements SnackbarInterface {

    private TextInputLayout ilPassword;
    private TextInputLayout ilConfirmarPass;

    private TextInputEditText etPassword;
    private TextInputEditText etConfirmarPass;

    private ProgressBar progressBar;

    private ResetPassViewModel resetPassViewModel;

    private MaterialButton btnResetPass;

    private String email;

    private FirebaseCrashlytics crashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        crashlytics = FirebaseCrashlytics.getInstance();

        instantiateInterfaceResources();

        initViewModels();

        btnResetPass();
    }

    private void btnResetPass() {

        btnResetPass.setOnClickListener(v -> {

            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);
            email = sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_EMAIL), "");

            if (validateField() && !email.isEmpty()) {
                ResearcherRepository.getInstance(getApplication()).resetPassword(email, Objects.requireNonNull(etPassword.getText(), "Et password cannot be null").toString());
            }

        });
    }

    private boolean validateField() {

        int errorCounter = 0;

        if (TextUtils.isEmpty(etPassword.getText())) {

            ilPassword.setErrorEnabled(true);
            ilPassword.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;

        } else if (Objects.requireNonNull(etPassword.getText(), "Et password cannot be null").length() < 8) {

            ilPassword.setErrorEnabled(true);
            ilPassword.setError(getString(R.string.VALIDACION_PASSWORD_LARGO));
            errorCounter++;

        } else if (TextUtils.isEmpty(etConfirmarPass.getText())) {

            ilConfirmarPass.setErrorEnabled(true);
            ilConfirmarPass.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;

        } else {

            if (!etPassword.getText().toString().equals(Objects.requireNonNull(etConfirmarPass.getText(), "Et conf pass cannot be null").toString())) {

                ilConfirmarPass.setErrorEnabled(true);
                ilConfirmarPass.setError(getString(R.string.VALIDACION_PASSWORD_NO_IGUALES));
                errorCounter++;

            } else {
                ilPassword.setErrorEnabled(false);
                ilConfirmarPass.setErrorEnabled(false);
            }
        }

        return errorCounter == 0;
    }

    private void initViewModels() {

        resetPassViewModel.isLoading().observe(this, aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);

                ilPassword.setEnabled(false);
                etPassword.setEnabled(false);

                ilConfirmarPass.setEnabled(false);
                etConfirmarPass.setEnabled(false);

            } else {
                progressBar.setVisibility(View.GONE);

                ilPassword.setEnabled(true);
                etPassword.setEnabled(true);

                ilConfirmarPass.setEnabled(true);
                etConfirmarPass.setEnabled(true);
            }
        });

        resetPassViewModel.showMsgReset().observe(this, s -> {

            if (s.equals(getString(R.string.MSG_PASSWORD_RESETEADA_VM_RESULT))) {

                Log.d(getString(R.string.TAG_VOLLEY_ERR_INV_RESET), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
                crashlytics.log(getString(R.string.TAG_VOLLEY_ERR_INV_RESET) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(getString(R.string.SHARED_PREF_RESET_PASS), true).apply();

                showSnackbar(findViewById(R.id.reset_pass), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_INICIAR_SESION));
            }
        });

        resetPassViewModel.showMsgErrorReset().observe(this, s -> {

            progressBar.setVisibility(View.GONE);

            Log.d(getString(R.string.TAG_VOLLEY_ERR_INV_RESET), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VOLLEY_ERR_INV_RESET) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            showSnackbar(findViewById(R.id.reset_pass), Snackbar.LENGTH_LONG, s, null);
        });

    }

    private void instantiateInterfaceResources() {

        progressBar = findViewById(R.id.progress_horizontal_reset);

        ilPassword = findViewById(R.id.il_researcher_password);
        ilConfirmarPass = findViewById(R.id.il_research_confirm_password);

        etPassword = findViewById(R.id.et_researcher_password);
        etConfirmarPass = findViewById(R.id.et_research_confirm_password);

        btnResetPass = findViewById(R.id.btn_reset_pass);

        resetPassViewModel = new ViewModelProvider(this).get(ResetPassViewModel.class);
    }

    @Override
    public void showSnackbar(View v, int duration, String title, String action) {

        Snackbar snackbar = Snackbar.make(v, title, duration);

        if (action != null) {

            snackbar.setAction(action, v1 -> {
                Intent intent = new Intent(ResetPassActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        }

        snackbar.show();
    }
}
