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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
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
import cl.udelvd.viewmodel.EditarEntrevistaViewModel;

public class EditarEntrevistaActivity extends AppCompatActivity implements SnackbarInterface {

    private ProgressBar progressBar;

    private TextInputLayout ilFechaEntrevista;
    private TextInputLayout ilTipoEntrevista;

    private TextInputEditText etFechaEntrevista;
    private AppCompatAutoCompleteTextView acTipoEntrevista;

    private Entrevista entrevistaIntent;

    private EditarEntrevistaViewModel editarEntrevistaViewModel;

    private List<TipoEntrevista> tipoEntrevistasList;
    private TipoEntrevistaAdapter tipoEntrevistasAdapter;

    private boolean isAutoCompleteTipoEntrevistaReady = false;
    private boolean isGetEntrevista = false;
    private boolean isSnackBarShow = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_entrevista);

        Utils.configurarToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITULO_TOOLBAR_EDITAR_ENTREVISTA));

        instanciarRecursosInterfaz();

        obtenerDatosBundles();

        setAutoCompleteTipoEntrevista();

        setPickerFechaNacimiento();

        iniciarViewModelEntrevista();
    }

    private void instanciarRecursosInterfaz() {

        tipoEntrevistasList = new ArrayList<>();
        progressBar = findViewById(R.id.progress_horizontal_editar_entrevista);
        progressBar.setVisibility(View.VISIBLE);

        ilFechaEntrevista = findViewById(R.id.il_fecha_entrevista);
        ilTipoEntrevista = findViewById(R.id.il_tipo_entrevista);

        etFechaEntrevista = findViewById(R.id.et_fecha_entrevista);
        acTipoEntrevista = findViewById(R.id.et_tipo_entrevista);

        editarEntrevistaViewModel = new ViewModelProvider(this).get(EditarEntrevistaViewModel.class);
    }

    private void obtenerDatosBundles() {

        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();
            entrevistaIntent = new Entrevista();
            entrevistaIntent.setId(bundle.getInt(getString(R.string.KEY_ENTREVISTA_ID_LARGO)));
            entrevistaIntent.setId_entrevistado(bundle.getInt(getString(R.string.KEY_ENTREVISTA_ID_ENTREVISTADO)));
        }
    }

    /**
     * Funcion encargada de cargar los tipos de entrevista en el autoComplete
     */
    private void setAutoCompleteTipoEntrevista() {

        editarEntrevistaViewModel.isLoadingTiposEntrevistas().observe(this, new Observer<Boolean>() {
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

        //Cargar listado de tipos de entrevistas
        editarEntrevistaViewModel.cargarTiposEntrevistas().observe(this, new Observer<List<TipoEntrevista>>() {
            @Override
            public void onChanged(List<TipoEntrevista> tipoEntrevistas) {

                if (tipoEntrevistas != null && tipoEntrevistas.size() > 0) {

                    tipoEntrevistasList = tipoEntrevistas;
                    tipoEntrevistasAdapter = new TipoEntrevistaAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, tipoEntrevistasList);
                    acTipoEntrevista.setAdapter(tipoEntrevistasAdapter);
                    tipoEntrevistasAdapter.notifyDataSetChanged();

                    isAutoCompleteTipoEntrevistaReady = true;

                    progressBar.setVisibility(View.GONE);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_ENTREVISTA), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

                    setearInfoEntrevista();
                }
            }
        });

        //Cargar errores de tipos de entrevista
        editarEntrevistaViewModel.mostrarMsgErrorTipoEntrevistaListado().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.formulario_editar_entrevista), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            }
        });
    }

    private void iniciarViewModelEntrevista() {

        editarEntrevistaViewModel.isLoadingEntrevista().observe(this, new Observer<Boolean>() {
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

        editarEntrevistaViewModel.cargarEntrevista(entrevistaIntent).observe(this, new Observer<Entrevista>() {
            @Override
            public void onChanged(Entrevista entrevistaInternet) {

                if (entrevistaInternet != null) {
                    entrevistaIntent = entrevistaInternet;

                    isGetEntrevista = true;

                    progressBar.setVisibility(View.GONE);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), entrevistaIntent.toString()));

                    setearInfoEntrevista();
                }
            }
        });

        //Manejar respuesta de actualizacion de entrevista
        editarEntrevistaViewModel.mostrarMsgActualizacion().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                if (s.equals(getString(R.string.MSG_UPDATE_ENTREVISTA))) {

                    progressBar.setVisibility(View.GONE);

                    Intent intent = getIntent();
                    intent.putExtra(getString(R.string.INTENT_KEY_MSG_ACTUALIZACION), s);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        //Manejar errores de actualizacion de entrevista
        editarEntrevistaViewModel.mostrarMsgErrorActualizacion().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.formulario_editar_entrevista), Snackbar.LENGTH_LONG, s, null);
                }
            }
        });

        //Manejar errores de carga de entrevista
        editarEntrevistaViewModel.mostrarMsgErrorEntrevista().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.formulario_editar_entrevista), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }
            }
        });
    }

    /**
     * Funcion encargada de cargar datos de la entrevista en formulario de edicion
     */
    private void setearInfoEntrevista() {

        if (isAutoCompleteTipoEntrevistaReady && isGetEntrevista) {
            String fecha = Utils.dateToString(getApplicationContext(), false, entrevistaIntent.getFecha_entrevista());
            etFechaEntrevista.setText(fecha);

            String nombre = Objects.requireNonNull(buscarTipoEntrevistaPorId(entrevistaIntent.getTipoEntrevista().getId())).getNombre();
            acTipoEntrevista.setText(nombre, false);

            isAutoCompleteTipoEntrevistaReady = false;
            isGetEntrevista = false;
        }
    }

    /**
     * Funcion encargada de configurar el picker de fechas
     */
    private void setPickerFechaNacimiento() {
        //OnClick
        etFechaEntrevista.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.iniciarDatePicker(etFechaEntrevista, EditarEntrevistaActivity.this);
            }
        });

        //EndIconOnClick
        ilFechaEntrevista.setEndIconOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.iniciarDatePicker(etFechaEntrevista, EditarEntrevistaActivity.this);
            }
        });
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

                Entrevista entrevistaActualizada = new Entrevista();

                entrevistaActualizada.setId(entrevistaIntent.getId());
                entrevistaActualizada.setId_entrevistado(entrevistaIntent.getId_entrevistado());

                Date fechaEntrevista = Utils.stringToDate(getApplicationContext(), false, Objects.requireNonNull(etFechaEntrevista.getText()).toString());
                entrevistaActualizada.setFecha_entrevista(fechaEntrevista);

                int id_tipo_entrevista = Objects.requireNonNull(buscarTipoEntrevistaPorNombre(acTipoEntrevista.getText().toString())).getId();

                TipoEntrevista tipoEntrevista = new TipoEntrevista();
                tipoEntrevista.setId(id_tipo_entrevista);
                entrevistaActualizada.setTipoEntrevista(tipoEntrevista);

                EntrevistaRepositorio.getInstancia(getApplication()).actualizarEntrevista(entrevistaActualizada);
            }
            return true;
        }
        return false;
    }

    /**
     * Funcion encargada de validar todos los campos del fomulario
     *
     * @return True|False según sea el caso
     */
    private boolean validarCampos() {

        int contador_errores = 0;

        //Checkear fecha entrevistaIntent
        if (Objects.requireNonNull(etFechaEntrevista.getText()).toString().isEmpty()) {
            ilFechaEntrevista.setErrorEnabled(true);
            ilFechaEntrevista.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {
            ilFechaEntrevista.setErrorEnabled(false);
        }

        if (acTipoEntrevista.getText().toString().isEmpty()) {
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
                    isAutoCompleteTipoEntrevistaReady = false;
                    isGetEntrevista = false;
                    isSnackBarShow = false;

                    editarEntrevistaViewModel.refreshTipoEntrevistas();

                    editarEntrevistaViewModel.refreshEntrevista(entrevistaIntent);

                    progressBar.setVisibility(View.VISIBLE);
                }
            });
        }
        snackbar.show();
        isSnackBarShow = false;
    }

    /**
     * Funcion encargada de buscar una TipoEntrevista dado parametro
     *
     * @param id Id del tipo entrevista
     * @return Objeto tipoEntrevista
     */
    private TipoEntrevista buscarTipoEntrevistaPorId(int id) {

        for (int i = 0; i < tipoEntrevistasList.size(); i++) {
            if (tipoEntrevistasList.get(i).getId() == id) {
                return tipoEntrevistasList.get(i);
            }
        }
        return null;
    }

    /**
     * Funcion encargada de buscar una TipoEntrevista dado parametro
     *
     * @param nombre Nombre del tipo entrevista
     * @return Objeto tipoEntrevista
     */
    private TipoEntrevista buscarTipoEntrevistaPorNombre(String nombre) {

        for (int i = 0; i < tipoEntrevistasList.size(); i++) {
            if (tipoEntrevistasList.get(i).getNombre().equals(nombre)) {
                return tipoEntrevistasList.get(i);
            }
        }
        return null;
    }
}
