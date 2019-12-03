package cl.udelvd.views;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.model.Investigador;
import cl.udelvd.repositorios.InvestigadorRepositorio;
import cl.udelvd.utils.Utils;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputLayout ilNombre;
    private TextInputLayout ilApellido;
    private TextInputLayout ilEmail;

    private TextInputEditText etNombre;
    private TextInputEditText etApellido;
    private TextInputEditText etEmail;

    private ProgressBar progressBar;

    private String password;
    private int id;
    private int id_rol;

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

        //Instancias formulario
        //Inputs Layouts
        ilNombre = findViewById(R.id.il_nombre_investigador);
        ilApellido = findViewById(R.id.il_apellido_investigador);
        ilEmail = findViewById(R.id.il_email_investigador);

        //Edit texts
        etNombre = findViewById(R.id.et_nombre_investigador);
        etApellido = findViewById(R.id.et_apellido_investigador);
        etEmail = findViewById(R.id.et_email_investigador);

        //ProgressBar
        progressBar = findViewById(R.id.progress_horizontal_registro);

        //Llenar editText con informacion usuario
        Bundle bundle = getIntent().getExtras();

        assert bundle != null;
        id = bundle.getInt("id");
        id_rol = bundle.getInt("id_rol");
        password = bundle.getString("password");
        etNombre.setText(bundle.getString("nombre"));
        etApellido.setText(bundle.getString("apellido"));
        etEmail.setText(bundle.getString("email"));

        Button btnActualizar = findViewById(R.id.btn_actualizar);
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validarCampos()) {

                    progressBar.setVisibility(View.VISIBLE);

                    //Recibir datos desde formulario
                    Investigador investigador = new Investigador();

                    investigador.setId(id);
                    investigador.setNombre(Objects.requireNonNull(etNombre.getText()).toString());
                    investigador.setApellido(Objects.requireNonNull(etApellido.getText())
                            .toString());
                    investigador.setEmail(Objects.requireNonNull(etEmail.getText()).toString());
                    investigador.setPassword(password);
                    investigador.setIdRol(id_rol);

                    InvestigadorRepositorio repositorio =
                            InvestigadorRepositorio.getInstance(getApplication());

                    Log.d("INVESTIGADOR UPDATE", investigador.toString());
                    //Actualizar investigador
                    //repositorio.registrarInvestigador(investigador);
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

        //Comprobar contraseña vacia
        /*if (TextUtils.isEmpty(etPassword.getText())) {
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
        }*/

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
        }
        return super.onOptionsItemSelected(item);
    }
}
