package cl.udelvd;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import cl.udelvd.model.Investigador;
import cl.udelvd.repositorios.InvestigadorRepositorio;
import cl.udelvd.utils.Utils;
import cl.udelvd.viewmodel.InvestigadorViewModel;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout ilEmail;
    private TextInputLayout ilPassword;

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;

    private Button btn_login;

    private TextView tv_registro;

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InvestigadorViewModel investigadorViewModel =
                ViewModelProviders.of(this).get(InvestigadorViewModel.class);

        investigadorViewModel.mostrarMsgRespuesta().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.INVISIBLE);
                Log.d("VIEW_MODEL", "MSG_RESPONSE: " + s);

                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                if (s.equals("¡Bienvenido!")) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        });

        investigadorViewModel.mostrarErrorRespuesta().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.d("VIEW_MODEL", "MSG_ERROR: " + s);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        });

        //Instancia formulario
        //Inputs Layouts
        ilEmail = findViewById(R.id.il_email_login);
        ilPassword = findViewById(R.id.il_password_login);

        //Edit texts
        etEmail = findViewById(R.id.et_email_login);
        etPassword = findViewById(R.id.et_password_login);

        progressBar = findViewById(R.id.progress_horizontal_login);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validarCampos()) {
                    progressBar.setVisibility(View.VISIBLE);

                    Investigador investigador = new Investigador();
                    investigador.setEmail(Objects.requireNonNull(etEmail.getText()).toString().toLowerCase());
                    investigador.setPassword(Objects.requireNonNull(etPassword.getText()).toString());

                    InvestigadorRepositorio investigadorRepositorio =
                            InvestigadorRepositorio.getInstance(getApplication());

                    investigadorRepositorio.loginInvestigador(investigador);
                }
            }
        });

        linkRegistro();

    }

    private boolean validarCampos() {

        boolean status = false;
        int contador_errores = 0;

        //Comprobar email vacio
        if (TextUtils.isEmpty(etEmail.getText())) {
            ilEmail.setErrorEnabled(true);
            ilEmail.setError("Campo requerido");
            contador_errores++;
        } else {

            //Comprobar mail válido
            if (!Utils.isValidEmail(etEmail.getText())) {
                ilEmail.setErrorEnabled(true);
                ilEmail.setError("Email inválido");
                contador_errores++;
            } else {
                ilEmail.setErrorEnabled(false);
            }
        }

        //Comprobar contraseña vacia
        if (TextUtils.isEmpty(etPassword.getText())) {
            ilPassword.setErrorEnabled(true);
            ilPassword.setError("Campo requerido");
            contador_errores++;
        } //Comprobar contraseña menor que 8
        else if (etPassword.getText().length() < 8) {
            ilPassword.setErrorEnabled(true);
            ilPassword.setError("Contraseña debe tener 8 carácteres mínimo");
            contador_errores++;
        } else {
            ilPassword.setErrorEnabled(false);
        }

        //Si no hay errores, pasa a registro
        if (contador_errores == 0) {
            status = true;
            return status;
        }
        return status;
    }

    /**
     * Funcion encargada de manejar la logica del link azul "Registro"
     */
    private void linkRegistro() {
        //Logica de texcview de registro
        tv_registro = findViewById(R.id.tv_registro);
        tv_registro.setMovementMethod(LinkMovementMethod.getInstance());
        Spannable spans = (Spannable) tv_registro.getText();
        ClickableSpan clickSpan = new ClickableSpan() {

            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        };
        spans.setSpan(clickSpan, 19, spans.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
