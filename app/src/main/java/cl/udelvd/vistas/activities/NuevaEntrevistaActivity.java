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
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.adaptadores.TipoEntrevistaAdapter;
import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.TipoEntrevista;
import cl.udelvd.repositorios.EntrevistaRepositorio;
import cl.udelvd.utilidades.SnackbarInterface;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.NuevaEntrevistaViewModel;

public class NuevaEntrevistaActivity extends AppCompatActivity implements SnackbarInterface {


    private ProgressBar progressBar;

    private TextInputLayout ilFechaEntrevista;
    private TextInputLayout ilTipoEntrevista;

    private TextInputEditText etFechaEntrevista;
    private AppCompatAutoCompleteTextView acTipoEntrevista;

    private int id_entrevistado;

    private List<TipoEntrevista> tipoEntrevistaList;
    private TipoEntrevistaAdapter tipoEntrevistaAdapter;

    private NuevaEntrevistaViewModel nuevaEntrevistaViewModel;
    private boolean isSnackBarShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_entrevista);

        Utils.configurarToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITULO_TOOLBAR_NUEVA_ENTREVISTA));

        instanciarRecursosInterfaz();

        obtenerDatosBundles();

        setAutoCompleteTiposEntrevistas();

        setPickerFechaNacimiento();

        iniciarViewModelEntrevista();
    }

    private void instanciarRecursosInterfaz() {

        progressBar = findViewById(R.id.progress_horizontal_nueva_entrevista);
        progressBar.setVisibility(View.VISIBLE);

        ilFechaEntrevista = findViewById(R.id.il_fecha_entrevista);
        ilTipoEntrevista = findViewById(R.id.il_tipo_entrevista);

        etFechaEntrevista = findViewById(R.id.et_fecha_entrevista);
        acTipoEntrevista = findViewById(R.id.et_tipo_entrevista);

        nuevaEntrevistaViewModel = ViewModelProviders.of(this).get(NuevaEntrevistaViewModel.class);
    }

    private void obtenerDatosBundles() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                id_entrevistado = bundle.getInt(getString(R.string.KEY_ENTREVISTADO_ID_LARGO));
            }
        }
    }

    private void setAutoCompleteTiposEntrevistas() {

        //Observador de loading
        nuevaEntrevistaViewModel.isLoadingTiposEntrevistas().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);

                    //Desactivar entradas
                    ilTipoEntrevista.setEnabled(false);
                    acTipoEntrevista.setEnabled(false);

                    ilFechaEntrevista.setEnabled(false);
                    etFechaEntrevista.setEnabled(false);
                } else {
                    progressBar.setVisibility(View.GONE);

                    //Activar entradas
                    ilTipoEntrevista.setEnabled(true);
                    acTipoEntrevista.setEnabled(true);

                    ilFechaEntrevista.setEnabled(true);
                    etFechaEntrevista.setEnabled(true);
                }
            }
        });

        //Cargar listado de tipos de entrevista
        nuevaEntrevistaViewModel.cargarTiposEntrevistas().observe(this, new Observer<List<TipoEntrevista>>() {
            @Override
            public void onChanged(List<TipoEntrevista> tipoEntrevistas) {
                if (tipoEntrevistas != null) {

                    progressBar.setVisibility(View.GONE);

                    tipoEntrevistaList = tipoEntrevistas;
                    tipoEntrevistaAdapter = new TipoEntrevistaAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, tipoEntrevistaList);
                    acTipoEntrevista.setAdapter(tipoEntrevistaAdapter);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_ENTREVISTA), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

                    tipoEntrevistaAdapter.notifyDataSetChanged();
                }

            }
        });

        //Cargar errores de tipos de entrevistas
        nuevaEntrevistaViewModel.mostrarMsgErrorTiposEntrevistas().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    showSnackbar(findViewById(R.id.formulario_nueva_entrevista), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                    isSnackBarShow = true;
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    private void iniciarViewModelEntrevista() {

        nuevaEntrevistaViewModel.isLoadingRegistroEntrevista().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);

                    //Desactivar entradas
                    ilTipoEntrevista.setEnabled(false);
                    acTipoEntrevista.setEnabled(false);

                    ilFechaEntrevista.setEnabled(false);
                    etFechaEntrevista.setEnabled(false);
                } else {
                    progressBar.setVisibility(View.GONE);

                    //Desactivar entradas
                    ilTipoEntrevista.setEnabled(true);
                    acTipoEntrevista.setEnabled(true);

                    ilFechaEntrevista.setEnabled(true);
                    etFechaEntrevista.setEnabled(true);
                }
            }
        });

        nuevaEntrevistaViewModel.mostrarMsgRegistro().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                Log.d(getString(R.string.TAG_VIEW_MODEL_NEW_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                //Si el registro fue correcto cerrar la actividad
                if (s.equals(getString(R.string.MSG_REGISTRO_ENTREVISTA))) {

                    //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);

                    Intent intent = getIntent();
                    intent.putExtra(getString(R.string.INTENT_KEY_MSG_REGISTRO), s);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        nuevaEntrevistaViewModel.mostrarMsgErrorRegistro().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    showSnackbar(findViewById(R.id.formulario_nueva_entrevista), Snackbar.LENGTH_LONG, s, null);
                    isSnackBarShow = true;
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_NEW_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    private void setPickerFechaNacimiento() {

        //OnClick
        etFechaEntrevista.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.iniciarDatePicker(etFechaEntrevista, NuevaEntrevistaActivity.this);
            }
        });

        //EndIconOnClick
        ilFechaEntrevista.setEndIconOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.iniciarDatePicker(etFechaEntrevista, NuevaEntrevistaActivity.this);
            }
        });

    }

    private boolean validarCampos() {
        int contador_errores = 0;

        if (Objects.requireNonNull(etFechaEntrevista.getText()).toString().isEmpty()) {
            ilFechaEntrevista.setErrorEnabled(true);
            ilFechaEntrevista.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {
            ilFechaEntrevista.setErrorEnabled(false);
        }

        if (Objects.requireNonNull(acTipoEntrevista.getText()).toString().isEmpty()) {
            ilTipoEntrevista.setErrorEnabled(true);
            ilTipoEntrevista.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {
            ilTipoEntrevista.setErrorEnabled(false);
        }

        return contador_errores == 0;
    }

    @Override
    public void showSnackbar(View v, int tipo_snackbar, String titulo, String accion) {

        Snackbar snackbar = Snackbar.make(v, titulo, tipo_snackbar);
        if (accion != null) {
            snackbar.setAction(accion, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Refresh listado de informacion necesaria
                    nuevaEntrevistaViewModel.refreshTipoEntrevistas();

                    progressBar.setVisibility(View.VISIBLE);
                }
            });
        }

        snackbar.show();
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

                Entrevista entrevista = new Entrevista();

                Date fecha_entrevista = Utils.stringToDate(getApplicationContext(), false, Objects.requireNonNull(etFechaEntrevista.getText()).toString());
                entrevista.setFecha_entrevista(fecha_entrevista);

                entrevista.setId_entrevistado(id_entrevistado);

                TipoEntrevista tipoEntrevista = new TipoEntrevista();
                int id = Objects.requireNonNull(buscarTipoEntrevistaPorNombre(acTipoEntrevista.getText().toString())).getId();
                tipoEntrevista.setId(id);
                entrevista.setTipoEntrevista(tipoEntrevista);

                EntrevistaRepositorio.getInstancia(getApplication()).registrarEntrevista(entrevista);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Funcion encargada de buscar una TipoEntrevista dado parametro
     *
     * @param nombre Nombre del tipo entrevista
     * @return Objeto tipoEntrevista
     */
    private TipoEntrevista buscarTipoEntrevistaPorNombre(String nombre) {

        for (int i = 0; i < tipoEntrevistaList.size(); i++) {
            if (tipoEntrevistaList.get(i).getNombre().equals(nombre)) {
                return tipoEntrevistaList.get(i);
            }
        }
        return null;
    }
}
