package cl.udelvd.vistas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.modelo.Accion;
import cl.udelvd.repositorios.AccionRepositorio;
import cl.udelvd.utilidades.SnackbarInterface;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.NuevaAccionViewModel;

public class NuevaAccionActivity extends AppCompatActivity implements SnackbarInterface {

    private ProgressBar progressBar;
    private TextInputLayout ilAccionEspanol;
    private TextInputLayout ilAccionIngles;
    private TextInputEditText etAccionEspanol;
    private TextInputEditText etAccionIngles;
    private NuevaAccionViewModel nuevaAccionViewModel;
    private boolean isSnackBarShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_accion);

        Utils.configurarToolbar(this, getApplicationContext(), 0, "Nueva acción");

        instanciarRecursosInterfaz();

        iniciarViewModel();
    }

    private void iniciarViewModel() {
        nuevaAccionViewModel.isLoadingRegistro().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);

                    //Desactivar entradas
                    ilAccionEspanol.setEnabled(false);
                    ilAccionIngles.setEnabled(false);

                    etAccionEspanol.setEnabled(false);
                    etAccionIngles.setEnabled(false);
                } else {
                    progressBar.setVisibility(View.GONE);

                    //Activar entradas
                    ilAccionEspanol.setEnabled(true);
                    ilAccionIngles.setEnabled(true);

                    etAccionEspanol.setEnabled(true);
                    etAccionIngles.setEnabled(true);
                }
            }
        });

        nuevaAccionViewModel.mostrarMsgRegistro().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(getString(R.string.TAG_VIEW_MODEL_NUEVA_ACCION), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                //Si el registro fue correcto cerrar la actividad
                if (s.equals(getString(R.string.MSG_REGISTRO_ACCION))) {

                    progressBar.setVisibility(View.GONE);

                    Intent intent = getIntent();
                    intent.putExtra(getString(R.string.INTENT_KEY_MSG_REGISTRO), s);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        nuevaAccionViewModel.mostrarMsgErrorRegistro().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.formulario_nueva_entrevista), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_NUEVA_ACCION), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });

    }

    private void instanciarRecursosInterfaz() {

        progressBar = findViewById(R.id.progress_horizontal_nueva_accion);
        progressBar.setVisibility(View.VISIBLE);

        ilAccionEspanol = findViewById(R.id.il_accion_espanol);
        ilAccionIngles = findViewById(R.id.il_accion_ingles);

        etAccionEspanol = findViewById(R.id.et_accion_espanol);
        etAccionIngles = findViewById(R.id.et_accion_ingles);

        nuevaAccionViewModel = ViewModelProviders.of(this).get(NuevaAccionViewModel.class);
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

                Accion accion = new Accion();
                accion.setNombreEs(Objects.requireNonNull(etAccionEspanol.getText()).toString());
                accion.setNombreEn(Objects.requireNonNull(etAccionIngles.getText()).toString());

                AccionRepositorio.getInstancia(getApplication()).registrarAccion(accion);

            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validarCampos() {
        int contador_errores = 0;

        if (Objects.requireNonNull(etAccionEspanol.getText()).toString().isEmpty()) {
            ilAccionEspanol.setErrorEnabled(true);
            ilAccionEspanol.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {
            ilAccionEspanol.setErrorEnabled(false);
        }

        if (Objects.requireNonNull(etAccionIngles.getText()).toString().isEmpty()) {
            ilAccionIngles.setErrorEnabled(true);
            ilAccionIngles.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {
            ilAccionIngles.setErrorEnabled(false);
        }

        return contador_errores == 0;
    }

    @Override
    public void showSnackbar(View v, int duration, String titulo, String accion) {
        Snackbar snackbar = Snackbar.make(v, titulo, duration);
        if (accion != null) {
            snackbar.setAction(accion, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progressBar.setVisibility(View.VISIBLE);

                    isSnackBarShow = false;
                }
            });
        }

        isSnackBarShow = false;
        snackbar.show();
    }
}
