package cl.udelvd.vistas.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Map;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.modelo.Investigador;
import cl.udelvd.repositorios.InvestigadorRepositorio;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.InvestigadorViewModel;

public class EditarPerfilActivity extends AppCompatActivity {

    private static final int EDIT_PROFILE_CODE = 201;

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
    private ConstraintLayout cl_password_opcional;

    private Investigador investigador;

    private SwitchCompat switchCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        Utils.configurarToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITUTLO_TOOLBAR_EDITAR_PERFIL));

        instanciarRecursosInterfaz();

        iniciarViewModel();

        obtenerDatosBundles();

        cargarDatosInvestigador();

        configurarSwitchPasswordOpcional();
    }

    private void instanciarRecursosInterfaz() {

        cl_password_opcional = findViewById(R.id.cl_optional_password);

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
    }

    private void iniciarViewModel() {
        InvestigadorViewModel investigadorViewModel = ViewModelProviders.of(this).get(InvestigadorViewModel.class);

        investigadorViewModel.mostrarMsgRespuestaActualizacion().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(Map<String, Object> stringObjectMap) {

                if (stringObjectMap != null) {

                    Investigador investigador = (Investigador) stringObjectMap.get(getString(R.string.KEY_INVES_OBJECT));


                    SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    if (investigador != null) {

                        Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_PERFIL), getString(R.string.VIEW_MODEL_MSG_RESPONSE) + investigador.toString());

                        //Guardar en sharedPref investigador con datos actualizados
                        editor.putString(getString(R.string.SHARED_PREF_INVES_NOMBRE), investigador.getNombre());
                        editor.putString(getString(R.string.SHARED_PREF_INVES_APELLIDO), investigador.getApellido());
                        editor.putString(getString(R.string.SHARED_PREF_INVES_EMAIL), investigador.getEmail());

                        //TODO: Almacenar hash de password y no texto plano
                        if (switchCompat.isChecked()) {
                            //Actualizar password en sharedPref
                            editor.putString(getString(R.string.SHARED_PREF_INVES_PASSWORD), Objects.requireNonNull(etPassword.getText()).toString());
                        }
                        editor.apply();

                        String msg_update = (String) stringObjectMap.get(getString(R.string.UPDATE_MSG_VM));

                        progressBar.setVisibility(View.INVISIBLE);

                        assert msg_update != null;
                        Log.d(getString(R.string.TAG_VIEW_MODEL_INVEST_UPDATE), msg_update);

                        if (msg_update.equals(getString(R.string.MSG_INVEST_ACTUALIZADO))) {

                            Toast.makeText(getApplicationContext(), msg_update, Toast.LENGTH_LONG).show();

                            //Cerrar formulario
                            //Setear codigo OK
                            Intent intent = getIntent();
                            setResult(EDIT_PROFILE_CODE, intent);
                            finish();
                        }
                    }
                }
            }
        });
    }

    private void obtenerDatosBundles() {

        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();
            investigador = new Investigador();

            investigador.setId(bundle.getInt(getString(R.string.KEY_INVES_ID)));
            investigador.setIdRol(bundle.getInt(getString(R.string.KEY_INVES_ID_ROL)));
            investigador.setPassword(bundle.getString(getString(R.string.KEY_INVES_PASSWORD)));
            investigador.setNombreRol(bundle.getString(getString(R.string.KEY_INVES_NOMBRE_ROL)));
            investigador.setNombre(bundle.getString(getString(R.string.KEY_INVES_NOMBRE)));
            investigador.setApellido(bundle.getString(getString(R.string.KEY_INVES_APELLIDO)));
            investigador.setEmail(bundle.getString(getString(R.string.KEY_INVES_EMAIL)));
        }
    }

    private void cargarDatosInvestigador() {
        etNombre.setText(investigador.getNombre());
        etApellido.setText(investigador.getApellido());
        etEmail.setText(investigador.getEmail());
    }

    private void configurarSwitchPasswordOpcional() {
        switchCompat = findViewById(R.id.switch_password_on);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    cl_password_opcional.setVisibility(View.VISIBLE);
                } else {
                    cl_password_opcional.setVisibility(View.GONE);
                }
            }
        });
    }

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
                ilEmail.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
                contador_errores++;
            } else {
                ilEmail.setErrorEnabled(false);
            }
        }

        if (switchCompat.isChecked()) {
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
        }

        //Si no hay errores, pasa a registro
        return contador_errores == 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_guardar_datos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_guardar) {

            if (validarCampos()) {

                progressBar.setVisibility(View.VISIBLE);

                //Recibir datos desde formulario y usar hiddens
                Investigador invesActualizado = new Investigador();

                invesActualizado.setId(investigador.getId());
                invesActualizado.setNombre(Objects.requireNonNull(etNombre.getText()).toString());
                invesActualizado.setApellido(Objects.requireNonNull(etApellido.getText()).toString());
                invesActualizado.setEmail(Objects.requireNonNull(etEmail.getText()).toString());

                if (switchCompat.isChecked()) {
                    //Nueva pass
                    invesActualizado.setPassword(Objects.requireNonNull(etPassword.getText()).toString());
                } else {
                    //Pass antigua
                    invesActualizado.setPassword(investigador.getPassword());
                }

                invesActualizado.setIdRol(investigador.getIdRol());
                invesActualizado.setNombreRol(invesActualizado.getNombreRol());

                InvestigadorRepositorio.getInstance(getApplication()).actualizarInvestigador(invesActualizado);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
