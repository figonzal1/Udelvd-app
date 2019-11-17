package cl.udelvd;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import cl.udelvd.model.Investigador;
import cl.udelvd.repositorios.RegistroRepositorio;
import cl.udelvd.viewmodel.RegistroViewModel;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout ilNombre;
    private TextInputLayout ilApellido;
    private TextInputLayout ilEmail;
    private TextInputLayout ilPassword;
    private TextInputLayout ilConfirmacionPassword;

    private TextInputEditText etNombre;
    private TextInputEditText etApellido;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmacionPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Registro");

        viewModelObserver();

        //Instancias formulario
        //Inputs Layouts
        ilNombre = findViewById(R.id.et_nombre_investigador);
        ilApellido = findViewById(R.id.et_apellido_investigador);
        ilEmail = findViewById(R.id.et_email_investigador);
        ilPassword = findViewById(R.id.et_password_investigador);
        ilConfirmacionPassword = findViewById(R.id.et_confirm_password_investigador);

        //Edit texts
        etNombre = findViewById(R.id.it_nombre_investigador);
        etApellido = findViewById(R.id.it_apellido_investigador);
        etEmail = findViewById(R.id.it_email_investigador);
        etPassword = findViewById(R.id.it_password_investigador);
        etConfirmacionPassword = findViewById(R.id.it_confirm_password_investigador);

        Button btnRegistrar = findViewById(R.id.btn_registrar);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validarCampos()) {

                    Investigador investigador = new Investigador();
                    investigador.setNombre(Objects.requireNonNull(etNombre.getText()).toString());
                    investigador.setApellido(Objects.requireNonNull(etApellido.getText()).toString());
                    investigador.setEmail(Objects.requireNonNull(etEmail.getText()).toString());
                    investigador.setPassword(Objects.requireNonNull(etPassword.getText()).toString());
                    investigador.setIdRol(1); //Admin id
                    investigador.setActivado(false);

                    RegistroRepositorio repositorio =
                            RegistroRepositorio.getInstance(getApplication());
                    repositorio.insertInvestigador(investigador);
                }
            }
        });
    }

    /**
     * Funcion encargada del manejo de ViewModels
     */
    private void viewModelObserver() {
        RegistroViewModel registroViewModel =
                ViewModelProviders.of(this).get(RegistroViewModel.class);

        registroViewModel.mostrarMsgRespuesta().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * Funcion para validaciond e campos
     *
     * @return True|False dependiendo de los campos
     */
    private boolean validarCampos() {

        boolean status = false;
        int contador_errores = 0;
        //Comprobar nombre vacio
        if (TextUtils.isEmpty(etNombre.getText())) {
            ilNombre.setErrorEnabled(true);
            ilNombre.setError("Campo requerido");
            contador_errores++;

        } else {
            ilNombre.setErrorEnabled(false);
        }

        //Comprobar apellido vacio
        if (TextUtils.isEmpty(etApellido.getText())) {

            ilApellido.setErrorEnabled(true);
            ilApellido.setError("Campo requerido");
            contador_errores++;
        } else {
            ilApellido.setErrorEnabled(false);
        }

        //Comprobar email vacio
        if (TextUtils.isEmpty(etEmail.getText())) {

            ilEmail.setErrorEnabled(true);
            ilEmail.setError("Campo requerido");
            contador_errores++;
        } else {

            //Comprobar mail valido
            if (!isValidEmail(etEmail.getText())) {
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
        }
        //Comprobar contraseña menor que 8
        else if (etPassword.getText().length() < 8) {
            ilPassword.setErrorEnabled(true);
            ilPassword.setError("Contraseña debe tener 8 carácteres mínimo");
            contador_errores++;
        }
        //Comprobar confirmacion vacia
        else if (TextUtils.isEmpty(etConfirmacionPassword.getText())) {
            ilConfirmacionPassword.setErrorEnabled(true);
            ilConfirmacionPassword.setError("Campo requerido");
            contador_errores++;
        } else {

            //Comprobar contraseñas iguales
            if (!etPassword.getText().toString().equals(etConfirmacionPassword.getText().toString())) {
                ilConfirmacionPassword.setErrorEnabled(true);
                ilConfirmacionPassword.setError("Contraseñas no coinciden");
                contador_errores++;
            } else {
                contador_errores = 0;
                ilPassword.setErrorEnabled(false);
                ilConfirmacionPassword.setErrorEnabled(false);
            }
        }

        //Si no hay errores, pasa a registro
        if (contador_errores == 0) {
            status = true;
            return status;
        }
        return status;
    }

    /**
     * Funcion que verifica validez de Email
     *
     * @param target Email objetivo
     * @return True|False según sea el caso
     */
    private boolean isValidEmail(CharSequence target) {
        boolean result =
                !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        Log.d("EMAIL_VALIDO", String.valueOf(result));
        return result;
    }
}
