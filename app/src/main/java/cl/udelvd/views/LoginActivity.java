package cl.udelvd.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.Map;
import java.util.Objects;

import cl.udelvd.R;
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

        viewModelObserver();

        setearViews();

        linkRegistro();
    }

    /**
     * Funcion encargada de configurar las views de la vista Login
     */
    private void setearViews() {
        //Instancia formulario
        //Inputs Layouts
        ilEmail = findViewById(R.id.il_email_login);
        ilPassword = findViewById(R.id.il_password_login);

        //Edit texts
        etEmail = findViewById(R.id.et_email_login);
        etPassword = findViewById(R.id.et_password_login);

        //Barra de progreso horizontal
        progressBar = findViewById(R.id.progress_horizontal_login);

        //Boton de login
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validarCampos()) {
                    progressBar.setVisibility(View.VISIBLE);

                    //Obtener datos desde fomulario
                    Investigador investigador = new Investigador();
                    investigador.setEmail(Objects.requireNonNull(etEmail.getText()).toString().toLowerCase());
                    investigador.setPassword(Objects.requireNonNull(etPassword.getText()).toString());

                    InvestigadorRepositorio investigadorRepositorio =
                            InvestigadorRepositorio.getInstance(getApplication());

                    //Hacer login
                    investigadorRepositorio.loginInvestigador(investigador,
                            getApplicationContext());
                }
            }
        });
    }

    /**
     * Funcion encargada de manejar los ViewModelObservers de la actividad login
     */
    private void viewModelObserver() {

        InvestigadorViewModel investigadorViewModel =
                ViewModelProviders.of(this).get(InvestigadorViewModel.class);

        //Observador de Mensajeria Para Login Correcto
        investigadorViewModel.mostrarMsgRespuestaLogin().observe(this, new Observer<Map<String,
                Object>>() {
            @Override
            public void onChanged(Map<String, Object> stringObjectMap) {

                Investigador investigador = (Investigador) stringObjectMap.get("investigador");

                SharedPreferences sharedPreferences = getSharedPreferences("udelvd",
                        Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (investigador != null) {

                    //Guardar en sharedPref investigador recien logeado
                    editor.putInt("id_investigador", investigador.getId());
                    editor.putString("nombre_investigador", investigador.getNombre());
                    editor.putString("apellido_investigador", investigador.getApellido());
                    editor.putString("email_investigador", investigador.getEmail());
                    editor.putInt("id_rol_investigador", investigador.getIdRol());
                    editor.putString("nombre_rol_investigador", investigador.getNombreRol());
                    editor.putBoolean("activado_investigador", investigador.isActivado());
                    editor.apply();

                    String msg_login = (String) stringObjectMap.get("mensaje_login");

                    progressBar.setVisibility(View.INVISIBLE);

                    assert msg_login != null;
                    Log.d("OBSERVER_LOGIN_OK", msg_login);

                    //Si el mensaje es 'Bienvenido' se realiza login
                    if (msg_login.equals("¡Bienvenido!")) {

                        //Mostrar mensaje en pantalla
                        Toast.makeText(getApplicationContext(), msg_login, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        //Observador de Mensajeria Para Login Incorrecto
        investigadorViewModel.mostrarErrorRespuesta().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.d("OBSERVER_LOGIN_ERROR", s);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Validacion de campos para formulario Login
     *
     * @return True|False dependiendo de los errores
     */
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
        //Logica de textview de registro
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
