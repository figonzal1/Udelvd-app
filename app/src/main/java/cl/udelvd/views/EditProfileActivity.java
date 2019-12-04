package cl.udelvd.views;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
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

public class EditProfileActivity extends AppCompatActivity {

    private TextInputLayout ilNombre;
    private TextInputLayout ilApellido;
    private TextInputLayout ilEmail;
    private static final int EDIT_PROFILE_CODE = 201;
    private TextInputLayout ilPassword;

    private TextInputEditText etNombre;
    private TextInputEditText etApellido;
    private TextInputEditText etEmail;
    private TextInputLayout ilConfirmacionPassword;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmacionPassword;

    private ProgressBar progressBar;
    private ImageView iv_password;

    private String password;
    private int id;
    private int id_rol;
    private SwitchCompat switchCompat;
    private String nombreRol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorOnPrimary));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        actionBar.setTitle("Editar Perfil");

        InvestigadorViewModel investigadorViewModel = ViewModelProviders.of(this).get(InvestigadorViewModel.class);

        investigadorViewModel.mostrarMsgRespuestaActualizacion().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(Map<String, Object> stringObjectMap) {

                Investigador investigador = (Investigador) stringObjectMap.get("investigador");

                SharedPreferences sharedPreferences = getSharedPreferences("udelvd", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (investigador != null) {
                    //Guardar en sharedPref investigador con datos actualizados
                    editor.putString("nombre_investigador", investigador.getNombre());
                    editor.putString("apellido_investigador", investigador.getApellido());
                    editor.putString("email_investigador", investigador.getEmail());

                    //TODO: Almacenar hash de password y no texto plano
                    if (switchCompat.isChecked()) {
                        //Actualizar password en sharedPref
                        editor.putString("password_investigador", Objects.requireNonNull(etPassword.getText()).toString());
                    }
                    editor.apply();

                    String msg_update = (String) stringObjectMap.get("mensaje_update");

                    progressBar.setVisibility(View.INVISIBLE);

                    assert msg_update != null;
                    Log.d("OBSERVER_UPDATE_OK", msg_update);

                    if (msg_update.equals("¡Datos actualizados!")) {

                        //Mostrar mensaje ne pantall
                        Toast.makeText(getApplicationContext(), msg_update, Toast.LENGTH_LONG).show();

                        //Cerrar formulario
                        Intent intent = getIntent();
                        setResult(EDIT_PROFILE_CODE, intent);
                        finish();
                    }
                }
            }
        });

        //Instancias formulario
        //Inputs Layouts
        ilNombre = findViewById(R.id.il_nombre_investigador);
        ilApellido = findViewById(R.id.il_apellido_investigador);
        ilEmail = findViewById(R.id.il_email_investigador);
        ilPassword = findViewById(R.id.il_password_investigador);
        ilConfirmacionPassword = findViewById(R.id.il_confirm_password_investigador);

        iv_password = findViewById(R.id.iv_icono_password);

        //Edit texts
        etNombre = findViewById(R.id.et_nombre_investigador);
        etApellido = findViewById(R.id.et_apellido_investigador);
        etEmail = findViewById(R.id.et_email_investigador);
        etPassword = findViewById(R.id.et_password_investigador);
        etConfirmacionPassword = findViewById(R.id.et_confirm_password_investigador);

        //ProgressBar
        progressBar = findViewById(R.id.progress_horizontal_registro);

        //Llenar editText con informacion usuario
        Bundle bundle = getIntent().getExtras();

        assert bundle != null;
        id = bundle.getInt("id");
        id_rol = bundle.getInt("id_rol");
        password = bundle.getString("password");
        nombreRol = bundle.getString("nombre_rol");
        etNombre.setText(bundle.getString("nombre"));
        etApellido.setText(bundle.getString("apellido"));
        etEmail.setText(bundle.getString("email"));

        switchCompat = findViewById(R.id.switch1);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    iv_password.setVisibility(View.VISIBLE);
                    ilPassword.setVisibility(View.VISIBLE);
                    ilConfirmacionPassword.setVisibility(View.VISIBLE);

                    etPassword.setVisibility(View.VISIBLE);
                    etConfirmacionPassword.setVisibility(View.VISIBLE);
                } else {
                    iv_password.setVisibility(View.GONE);
                    ilPassword.setVisibility(View.GONE);
                    ilConfirmacionPassword.setVisibility(View.GONE);

                    etPassword.setVisibility(View.GONE);
                    etConfirmacionPassword.setVisibility(View.GONE);
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
            if (!Utils.isValidEmail(etEmail.getText())) {
                ilEmail.setErrorEnabled(true);
                ilEmail.setError("Email inválido");
                contador_errores++;
            } else {
                ilEmail.setErrorEnabled(false);
            }
        }

        if (switchCompat.isChecked()) {
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
        }

        //Si no hay errores, pasa a registro
        if (contador_errores == 0) {
            status = true;
            return status;
        }
        return status;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_user_dialog_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.menu_guardar_usuario) {
            if (validarCampos()) {

                progressBar.setVisibility(View.VISIBLE);

                //Recibir datos desde formulario y usar hiddens
                Investigador investigador = new Investigador();

                investigador.setId(id);
                investigador.setNombre(Objects.requireNonNull(etNombre.getText()).toString());
                investigador.setApellido(Objects.requireNonNull(etApellido.getText())
                        .toString());
                investigador.setEmail(Objects.requireNonNull(etEmail.getText()).toString());

                if (switchCompat.isChecked()) {
                    investigador.setPassword(Objects.requireNonNull(etPassword.getText()).toString());
                } else {
                    investigador.setPassword(password);
                }
                investigador.setIdRol(id_rol);
                investigador.setNombreRol(nombreRol);

                InvestigadorRepositorio repositorio =
                        InvestigadorRepositorio.getInstance(getApplication());

                Log.d("INVESTIGADOR_UPDATE", investigador.toString());

                //Actualizar investigador
                repositorio.actualizarInvestigador(investigador);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
