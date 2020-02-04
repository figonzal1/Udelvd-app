package cl.udelvd.vistas.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.repositorios.InvestigadorRepositorio;
import cl.udelvd.viewmodel.ResetearPassViewModel;

public class ResetearPassActivity extends AppCompatActivity {

    private TextInputLayout ilPassword;
    private TextInputLayout ilConfirmarPass;

    private TextInputEditText etPassword;
    private TextInputEditText etConfirmarPass;

    private ProgressBar progressBar;

    private ResetearPassViewModel resetearPassViewModel;

    private MaterialButton btn_resetear_pass;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetear_pass);

        instanciarRecursosInterfaz();

        iniciarViewModels();

        btnResetPass();


    }

    private void btnResetPass() {

        btn_resetear_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);
                email = sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_EMAIL), "");

                if (validarCampos() && !email.isEmpty()) {
                    InvestigadorRepositorio.getInstance(getApplication()).resetearPassword(email, Objects.requireNonNull(etPassword.getText()).toString());
                }

            }
        });
    }

    private boolean validarCampos() {

        int contador_errores = 0;

        //Comprobar contraseña vacia
        if (TextUtils.isEmpty(etPassword.getText())) {
            ilPassword.setErrorEnabled(true);
            ilPassword.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        }
        //Comprobar contraseña menor que 8
        else if (etPassword.getText().length() < 8) {
            ilPassword.setErrorEnabled(true);
            ilPassword.setError(getString(R.string.VALIDACION_PASSWORD_LARGO));
            contador_errores++;
        }
        //Comprobar confirmacion vacia
        else if (TextUtils.isEmpty(etConfirmarPass.getText())) {
            ilConfirmarPass.setErrorEnabled(true);
            ilConfirmarPass.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {

            //Comprobar contraseñas iguales
            if (!etPassword.getText().toString().equals(etConfirmarPass.getText().toString())) {
                ilConfirmarPass.setErrorEnabled(true);
                ilConfirmarPass.setError(getString(R.string.VALIDACION_PASSWORD_NO_IGUALES));
                contador_errores++;
            } else {
                ilPassword.setErrorEnabled(false);
                ilConfirmarPass.setErrorEnabled(false);
            }
        }

        return contador_errores == 0;
    }

    private void iniciarViewModels() {
        resetearPassViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
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
            }
        });


        resetearPassViewModel.mostrarMsgReseteo().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (s.equals(getString(R.string.MSG_PASSWORD_RESETEADA_VM_RESULT))) {

                    SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean(getString(R.string.SHARED_PREF_RESET_PASS), true).apply();
                    showSnackbar(findViewById(R.id.resetear_pass), s, "Iniciar sesión");
                }
            }
        });

        resetearPassViewModel.mostrarMsgErrorReseteo().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.GONE);

                Log.d(getString(R.string.TAG_VOLLEY_ERR_INV_RECUPERAR), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

                showSnackbar(findViewById(R.id.resetear_pass), s, null);
            }
        });

    }

    /**
     * Funcion para mostrar el snackbar en fragment
     *
     * @param v      View donde se mostrara el snackbar
     * @param titulo Titulo del snackbar
     */
    private void showSnackbar(View v, String titulo, String accion) {

        Snackbar snackbar = Snackbar.make(v, titulo, Snackbar.LENGTH_INDEFINITE);

        if (accion != null) {
            snackbar.setAction(accion, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ResetearPassActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        snackbar.show();
    }

    private void instanciarRecursosInterfaz() {

        progressBar = findViewById(R.id.progress_horizontal_reset);

        ilPassword = findViewById(R.id.il_password_investigador);
        ilConfirmarPass = findViewById(R.id.il_confirm_password_investigador);

        etPassword = findViewById(R.id.et_password_investigador);
        etConfirmarPass = findViewById(R.id.et_confirm_password_investigador);

        btn_resetear_pass = findViewById(R.id.btn_resetear_pass);

        resetearPassViewModel = ViewModelProviders.of(this).get(ResetearPassViewModel.class);
    }
}
