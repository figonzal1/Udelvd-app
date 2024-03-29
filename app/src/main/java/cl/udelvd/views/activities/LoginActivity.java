package cl.udelvd.views.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.models.Researcher;
import cl.udelvd.repositories.ResearcherRepository;
import cl.udelvd.utils.SnackbarInterface;
import cl.udelvd.utils.Utils;
import cl.udelvd.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity implements SnackbarInterface {

    private static final int REGISTRY_RESEARCHER_CODE = 200;
    private TextInputLayout ilEmail;
    private TextInputLayout ilPassword;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private ProgressBar progressBar;

    private LoginViewModel loginViewModel;
    private boolean isNotification = false;

    private FirebaseCrashlytics crashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        crashlytics = FirebaseCrashlytics.getInstance();

        instantiateInterfaceResources();

        configRegistryLink();

        configRecoveryLink();

        dynamicLinkRecovery();

        getDeviationNotification();
    }

    private void instantiateInterfaceResources() {

        final SharedPreferences sharedPreferences = getSharedPreferences(getApplicationContext().getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);

        //Card View Animation
        MaterialCardView materialCardView = findViewById(R.id.cv_login);
        Animation cvAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card_view_animation);
        cvAnimation.setStartOffset(200);
        materialCardView.startAnimation(cvAnimation);

        //Title & Icon Animation
        ImageView ivLogo = findViewById(R.id.iv_logo_login);
        TextView tvAppName = findViewById(R.id.tv_name_app);

        Animation fadeAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_animation);
        fadeAnimation.setStartOffset(500);

        ivLogo.startAnimation(fadeAnimation);
        tvAppName.startAnimation(fadeAnimation);

        //Set form
        ilEmail = findViewById(R.id.il_email_login);
        ilPassword = findViewById(R.id.il_password_login);

        etEmail = findViewById(R.id.et_email_login);
        String sharedEmail = sharedPreferences.getString("login_email", "");
        if (sharedEmail != null && !sharedEmail.isEmpty()) {
            etEmail.setText(sharedEmail);
        }
        etPassword = findViewById(R.id.et_password_login);

        progressBar = findViewById(R.id.progress_horizontal_login);

        Button btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(view -> {

            if (validateFields()) {

                progressBar.setVisibility(View.VISIBLE);

                Researcher researcher = new Researcher();
                researcher.setEmail(Objects.requireNonNull(etEmail.getText()).toString().toLowerCase());

                researcher.setPassword(Objects.requireNonNull(etPassword.getText()).toString());

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("login_email", etEmail.getText().toString());
                editor.apply();

                ResearcherRepository.getInstance(getApplication()).loginResearcher(researcher);
            }
        });

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        initViewModels();
    }

    private void initViewModels() {


        loginViewModel.isLoading().observe(this, aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);

                ilEmail.setEnabled(false);
                etEmail.setEnabled(false);

                ilPassword.setEnabled(false);
                etPassword.setEnabled(false);

            } else {
                progressBar.setVisibility(View.INVISIBLE);

                ilEmail.setEnabled(true);
                etEmail.setEnabled(true);

                ilPassword.setEnabled(true);
                etPassword.setEnabled(true);
            }
        });


        loginViewModel.showMsgLogin().observe(this, stringObjectMap -> {

            Researcher researcher = (Researcher) stringObjectMap.get(getString(R.string.KEY_INVES_OBJECT));

            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY),
                    Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            if (researcher != null) {

                Log.d(getString(R.string.TAG_VM_INVES_LOGIN), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), researcher.toString()));
                crashlytics.log(getString(R.string.TAG_VM_INVES_LOGIN) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), researcher.toString()));

                editor.putInt(getString(R.string.SHARED_PREF_INVES_ID), researcher.getId());
                editor.putString(getString(R.string.SHARED_PREF_INVES_NOMBRE), researcher.getName());
                editor.putString(getString(R.string.SHARED_PREF_INVES_APELLIDO), researcher.getLastName());
                editor.putString(getString(R.string.SHARED_PREF_INVES_EMAIL), researcher.getEmail());
                editor.putInt(getString(R.string.SHARED_PREF_INVES_ID_ROL), researcher.getIdRole());
                editor.putString(getString(R.string.SHARED_PREF_INVES_NOMBRE_ROL), researcher.getRolName());
                editor.putBoolean(getString(R.string.SHARED_PREF_INVES_ACTIVADO), researcher.isActivated());
                editor.putString(getString(R.string.SHARED_PREF_INVES_CREATE_TIME), researcher.getCreateTime());

                editor.putString(getString(R.string.SHARED_PREF_INVES_PASSWORD), Objects.requireNonNull(etPassword.getText()).toString());
                editor.apply();

                String msg_login = (String) stringObjectMap.get(getString(R.string.LOGIN_MSG_VM));

                progressBar.setVisibility(View.INVISIBLE);

                if (msg_login != null && msg_login.equals(getString(R.string.MSG_INVEST_LOGIN))) {

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(getString(R.string.INTENT_KEY_MSG_LOGIN), msg_login);
                    intent.putExtra(getString(R.string.NOTIFICACION_INTENT_ACTIVADO), isNotification);
                    startActivity(intent);

                    finish();
                }
            }
        });


        loginViewModel.showMsgErrorLogin().observe(this, s -> {

            progressBar.setVisibility(View.INVISIBLE);

            Log.d(getString(R.string.TAG_VM_INVES_LOGIN), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VM_INVES_LOGIN) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            showSnackbar(findViewById(R.id.login_researcher), Snackbar.LENGTH_LONG, s, null);
        });
    }

    private void configRegistryLink() {

        TextView tvRegistryLink = findViewById(R.id.tv_registry_researcher_link);
        tvRegistryLink.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableStringBuilder spanned = new SpannableStringBuilder(tvRegistryLink.getText());
        ClickableSpan clickSpan = new ClickableSpan() {

            @Override
            public void onClick(@NonNull View widget) {

                Intent intent = new Intent(LoginActivity.this, RegistryActivity.class);
                startActivityForResult(intent, REGISTRY_RESEARCHER_CODE);
            }
        };
        spanned.setSpan(clickSpan, 0, spanned.length(), 0);
        tvRegistryLink.setText(spanned, TextView.BufferType.SPANNABLE);
    }


    private void configRecoveryLink() {

        TextView tvRecovery = findViewById(R.id.tv_recovery);
        tvRecovery.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableStringBuilder spans = new SpannableStringBuilder(tvRecovery.getText());

        ClickableSpan clickSpan = new ClickableSpan() {

            @Override
            public void onClick(@NonNull View widget) {

                Intent intent = new Intent(LoginActivity.this, RecoveryActivity.class);
                startActivity(intent);
            }
        };

        spans.setSpan(clickSpan, 0, spans.length(), 0);
        tvRecovery.setText(spans);
    }

    private void dynamicLinkRecovery() {

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {

                    Uri deepLink;
                    if (pendingDynamicLinkData != null) {

                        deepLink = pendingDynamicLinkData.getLink();

                        Log.d(getString(R.string.TAG_DYNAMIC_LINK_FIREBASE), String.valueOf(deepLink));
                        crashlytics.log(getString(R.string.TAG_DYNAMIC_LINK_FIREBASE) + deepLink);

                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);

                        boolean isReset = sharedPreferences.getBoolean(getString(R.string.SHARED_PREF_RESET_PASS), false);

                        if (!isReset) {

                            Intent intent = new Intent(LoginActivity.this, ResetPassActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            showSnackbar(findViewById(R.id.login_researcher), Snackbar.LENGTH_INDEFINITE, getString(R.string.DYNAMIC_LINK_INVALIDO), getString(R.string.SNACKBAR_SOLICITAR_RECUPERACION));
                        }
                    }

                })
                .addOnFailureListener(this, e -> {

                    Log.w(getString(R.string.TAG_DYNAMIC_LINK_FIREBASE), "getDynamicLink:onFailure", e);
                    crashlytics.log(getString(R.string.TAG_DYNAMIC_LINK_FIREBASE) + "getDynamicLink:onFailure" + e);
                });
    }

    private void getDeviationNotification() {

        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.containsKey(getString(R.string.NOTIFICACION_INTENT_ACTIVADO))) {
            isNotification = bundle.getBoolean(getString(R.string.NOTIFICACION_INTENT_ACTIVADO));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REGISTRY_RESEARCHER_CODE) {

            if (resultCode == RESULT_OK) {

                Bundle bundle = Objects.requireNonNull(data, "Data cannot be null").getExtras();
                showSnackbar(findViewById(R.id.login_researcher), Snackbar.LENGTH_INDEFINITE, Objects.requireNonNull(bundle, "Msg_registro cannot be null").getString(getString(R.string.INTENT_KEY_MSG_REGISTRO)), null);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showSnackbar(View v, int largo_snackbar, String title, String action) {

        Snackbar snackbar = Snackbar.make(v, title, largo_snackbar);

        if (action != null) {

            snackbar.setAction(action, v1 -> {

                Intent intent = new Intent(LoginActivity.this, RecoveryActivity.class);
                startActivity(intent);
            });
        }

        snackbar.show();

    }

    private boolean validateFields() {

        int errorCounter = 0;

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

        } else {
            ilPassword.setErrorEnabled(false);
        }

        return errorCounter == 0;
    }
}
