package cl.udelvd.vistas.activities;

import android.os.Bundle;
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
import cl.udelvd.modelo.Investigador;
import cl.udelvd.repositorios.InvestigadorRepositorio;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.RecuperacionViewModel;

public class RecuperacionActivity extends AppCompatActivity {

    private TextInputLayout ilEmail;
    private TextInputEditText etEmail;
    private MaterialButton btn_recuperar_cuenta;

    private ProgressBar progressBar;

    private RecuperacionViewModel recuperacionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperacion);

        instanciarRecursosInterfaz();

        iniciarViewModels();

        btnRecuperarCuenta();

    }

    private void btnRecuperarCuenta() {
        btn_recuperar_cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validarCampos()) {

                    Investigador investigador = new Investigador();
                    investigador.setEmail(Objects.requireNonNull(etEmail.getText()).toString());

                    InvestigadorRepositorio.getInstance(getApplication()).recuperarCuenta(investigador);
                }
            }
        });
    }

    private void iniciarViewModels() {
        recuperacionViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);
                    ilEmail.setEnabled(false);
                } else {
                    progressBar.setVisibility(View.GONE);
                    ilEmail.setEnabled(true);
                }
            }
        });

        recuperacionViewModel.mostrarMsgRecuperacon().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(getString(R.string.TAG_VOLLEY_ERR_INV_RECUPERAR), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                progressBar.setVisibility(View.GONE);

                if (s.equals(getString(R.string.RECOVERY_MSG_VM_RESPONSE))) {
                    showSnackbar(findViewById(R.id.recuperar_investigador), Snackbar.LENGTH_LONG, s);
                }
            }
        });

        recuperacionViewModel.mostrarMsgErrorRecuperacion().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                Log.d(getString(R.string.TAG_VOLLEY_ERR_INV_RECUPERAR), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

                showSnackbar(findViewById(R.id.recuperar_investigador), Snackbar.LENGTH_INDEFINITE, s);
            }
        });
    }

    private void instanciarRecursosInterfaz() {

        ilEmail = findViewById(R.id.il_email_recuperacion);
        etEmail = findViewById(R.id.et_email_recuperacion);

        progressBar = findViewById(R.id.progress_horizontal_recuperacion);

        btn_recuperar_cuenta = findViewById(R.id.btn_recuperar);

        recuperacionViewModel = ViewModelProviders.of(this).get(RecuperacionViewModel.class);
    }

    private boolean validarCampos() {

        int contador_errores = 0;

        //Comprobar email vacio
        if (Objects.requireNonNull(etEmail.getText()).toString().isEmpty()) {

            ilEmail.setErrorEnabled(true);
            ilEmail.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {

            //Comprobar mail valido
            if (Utils.isInValidEmail(etEmail.getText())) {
                ilEmail.setErrorEnabled(true);
                ilEmail.setError(getString(R.string.VALIDACION_EMAIL));
                contador_errores++;
            } else {
                ilEmail.setErrorEnabled(false);
            }
        }

        return contador_errores == 0;
    }

    /**
     * Funcion para mostrar el snackbar en fragment
     *
     * @param v      View donde se mostrara el snackbar
     * @param titulo Titulo del snackbar
     */
    private void showSnackbar(View v, int duration, String titulo) {

        Snackbar snackbar = Snackbar.make(v, titulo, duration);
        snackbar.show();
    }
}
