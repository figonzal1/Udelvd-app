package cl.udelvd.vistas.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.modelo.Investigador;
import cl.udelvd.repositorios.InvestigadorRepositorio;
import cl.udelvd.utilidades.Utils;

public class RegistroActivity extends AppCompatActivity {

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

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        Utils.configurarToolbar(this, getApplicationContext(), 0, getString(R.string.TITULO_TOOLBAR_REGISTRO));

        //iniciarViewModel();

        instanciarRecursosInterfaz();
    }

    /**
     * Funcion encargada de configurar las views de la ista Registro
     */
    private void instanciarRecursosInterfaz() {

        //Instancias formulario
        //Inputs Layouts
        ilNombre = findViewById(R.id.il_nombre_investigador);
        ilApellido = findViewById(R.id.il_apellido_investigador);
        ilEmail = findViewById(R.id.il_email_investigador);
        ilPassword = findViewById(R.id.il_password_investigador);
        ilConfirmacionPassword = findViewById(R.id.il_confirm_password_investigador);

        //Edit texts
        etNombre = findViewById(R.id.et_nombre_investigador);
        etApellido = findViewById(R.id.et_apellido_investigador);
        etEmail = findViewById(R.id.et_email_investigador);
        etPassword = findViewById(R.id.et_password_investigador);
        etConfirmacionPassword = findViewById(R.id.et_confirm_password_investigador);

        //ProgressBar
        progressBar = findViewById(R.id.progress_horizontal_registro);

        //Boton registro
        Button btnRegistrar = findViewById(R.id.btn_registrar);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validarCampos()) {

                    progressBar.setVisibility(View.VISIBLE);

                    //Recibir datos desde formulario
                    Investigador investigador = new Investigador();
                    investigador.setNombre(Objects.requireNonNull(etNombre.getText()).toString());
                    investigador.setApellido(Objects.requireNonNull(etApellido.getText())
                            .toString());

                    investigador.setEmail(Objects.requireNonNull(etEmail.getText()).toString());
                    investigador.setPassword(Objects.requireNonNull(etPassword.getText()).toString());
                    investigador.setNombreRol(getString(R.string.ROL_INVESTIGADOR));
                    investigador.setActivado(false);

                    InvestigadorRepositorio.getInstance(getApplication()).registrarInvestigador(investigador);
                }

            }
        });
    }

    /**
     * Funcion encargada del manejo de ViewModels
     */
    /*private void iniciarViewModel() {

        final InvestigadorViewModel investigadorViewModel =
                ViewModelProviders.of(this).get(InvestigadorViewModel.class);

        //Observador mensaje positivo
        investigadorViewModel.mostrarMsgRespuestaRegistro().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                Log.d(getString(R.string.TAG_VM_INVES_REGISTRO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                //Si el registro fue correcto cerrar la actividad
                if (s.equals(getString(R.string.MSG_INVEST_REGISTRADO))) {
                    finish();
                }
            }
        });

        //Observador mensaje error
        investigadorViewModel.mostrarErrorRespuesta().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.INVISIBLE);
                Log.d(getString(R.string.TAG_VM_INVES_REGISTRO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        });

    }*/

    /**
     * Funcion para validaciond e campos
     *
     * @return True|False dependiendo de los campos
     */
    private boolean validarCampos() {

        int contador_errores = 0;

        //Comprobar nombre vacio
        if (TextUtils.isEmpty(etNombre.getText())) {
            ilNombre.setErrorEnabled(true);
            ilNombre.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;

        } else {
            ilNombre.setErrorEnabled(false);
        }

        //Comprobar apellido vacio
        if (TextUtils.isEmpty(etApellido.getText())) {

            ilApellido.setErrorEnabled(true);
            ilApellido.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {
            ilApellido.setErrorEnabled(false);
        }

        //Comprobar email vacio
        if (TextUtils.isEmpty(etEmail.getText())) {

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
        else if (TextUtils.isEmpty(etConfirmacionPassword.getText())) {
            ilConfirmacionPassword.setErrorEnabled(true);
            ilConfirmacionPassword.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {

            //Comprobar contraseñas iguales
            if (!etPassword.getText().toString().equals(etConfirmacionPassword.getText().toString())) {
                ilConfirmacionPassword.setErrorEnabled(true);
                ilConfirmacionPassword.setError(getString(R.string.VALIDACION_PASSWORD_NO_IGUALES));
                contador_errores++;
            } else {
                contador_errores = 0;
                ilPassword.setErrorEnabled(false);
                ilConfirmacionPassword.setErrorEnabled(false);
            }
        }

        //Si no hay errores, pasa a registro
        return contador_errores == 0;
    }
}
