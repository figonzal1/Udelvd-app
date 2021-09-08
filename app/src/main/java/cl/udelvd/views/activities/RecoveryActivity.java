package cl.udelvd.views.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import cl.udelvd.models.Researcher;
import cl.udelvd.repositories.ResearcherRepository;
import cl.udelvd.utils.SnackbarInterface;
import cl.udelvd.utils.Utils;
import cl.udelvd.viewmodels.RecoveryViewModel;

public class RecoveryActivity extends AppCompatActivity implements SnackbarInterface {

    private TextInputLayout ilEmail;
    private TextInputEditText etEmail;
    private MaterialButton btnRecoveryAccount;

    private ProgressBar progressBar;
    private RecoveryViewModel recoveryViewModel;

    private FirebaseCrashlytics crashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        crashlytics = FirebaseCrashlytics.getInstance();

        instantiateInterfaceResources();

        initViewModels();

        btnRecoveryAccount();
    }

    private void btnRecoveryAccount() {

        btnRecoveryAccount.setOnClickListener(v -> {

            if (validateField()) {

                Researcher researcher = new Researcher();
                researcher.setEmail(Objects.requireNonNull(etEmail.getText(), "Et email cannot be null").toString());

                ResearcherRepository.getInstance(getApplication()).recoveryAccount(researcher);
            }
        });
    }

    private void initViewModels() {

        recoveryViewModel.isLoading().observe(this, aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);
                ilEmail.setEnabled(false);

            } else {
                progressBar.setVisibility(View.GONE);
                ilEmail.setEnabled(true);
            }
        });

        recoveryViewModel.showMsgRecovery().observe(this, stringStringMap -> {

            String email = stringStringMap.get(getString(R.string.KEY_INVES_EMAIL));
            String msgRecovery = stringStringMap.get(getString(R.string.MSG_RECOVERY));

            Log.d(getString(R.string.TAG_VOLLEY_ERR_INV_RECUPERAR), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), stringStringMap.toString()));
            crashlytics.log(getString(R.string.TAG_VOLLEY_ERR_INV_RECUPERAR) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), stringStringMap.toString()));

            progressBar.setVisibility(View.GONE);

            if (msgRecovery != null && msgRecovery.equals(getString(R.string.RECOVERY_MSG_VM_RESPONSE))) {

                showSnackbar(findViewById(R.id.researcher_recovery), Snackbar.LENGTH_INDEFINITE, msgRecovery, null);

                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);
                String sharedEmail = sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_EMAIL), "");

                if (sharedEmail != null && (!sharedEmail.equals(email) || sharedEmail.isEmpty())) {

                    sharedPreferences.edit().putString(getString(R.string.SHARED_PREF_INVES_EMAIL), email).apply();
                    Log.d("SHARED_EMAIL", "guardando email desde recuperacion en shared pref");
                }

                sharedPreferences.edit().putBoolean(getString(R.string.SHARED_PREF_RESET_PASS), false).apply();
            }
        });

        recoveryViewModel.showMsgErrorRecovery().observe(this, s -> {

            progressBar.setVisibility(View.GONE);

            Log.d(getString(R.string.TAG_VOLLEY_ERR_INV_RECUPERAR), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VOLLEY_ERR_INV_RECUPERAR) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            showSnackbar(findViewById(R.id.researcher_recovery), Snackbar.LENGTH_INDEFINITE, s, null);
        });
    }

    private void instantiateInterfaceResources() {

        ilEmail = findViewById(R.id.il_email_recovery);
        etEmail = findViewById(R.id.et_email_recovery);

        progressBar = findViewById(R.id.progress_horizontal_recovery);

        btnRecoveryAccount = findViewById(R.id.btn_recovery);

        recoveryViewModel = new ViewModelProvider(this).get(RecoveryViewModel.class);
    }

    private boolean validateField() {

        int errorCounter = 0;

        if (Objects.requireNonNull(etEmail.getText(), "Et email cannot be null").toString().isEmpty()) {

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

        return errorCounter == 0;
    }

    @Override
    public void showSnackbar(View v, int duration, String title, String action) {

        Snackbar snackbar = Snackbar.make(v, title, duration);
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
