package cl.udelvd.vistas.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.adaptadores.TipoEntrevistaAdapter;
import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.TipoEntrevista;
import cl.udelvd.repositorios.EntrevistaRepositorio;
import cl.udelvd.repositorios.TipoEntrevistaRepositorio;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.EntrevistaViewModel;
import cl.udelvd.viewmodel.TipoEntrevistaViewModel;

public class EditarEntrevistaActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    private TextInputLayout ilFechaEntrevista;
    private TextInputLayout ilTipoEntrevista;

    private TextInputEditText etFechaEntrevista;
    private AppCompatAutoCompleteTextView acTipoEntrevista;

    private Entrevista entrevistaIntent;


    private EntrevistaViewModel entrevistaViewModel;
    private TipoEntrevistaViewModel tipoEntrevistaViewModel;

    private List<TipoEntrevista> tipoEntrevistasList;
    private TipoEntrevistaAdapter tipoEntrevistasAdapter;
    private boolean isAutoCompleteTipoEntrevistaReady = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_entrevista);

        Utils.configurarToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITULO_TOOLBAR_EDITAR_ENTREVISTA));

        instanciarRecursosInterfaz();

        setAutoCompleteTipoEntrevista();

        setPickerFechaNacimiento();

        obtenerDatosBundles();
    }

    private void instanciarRecursosInterfaz() {

        progressBar = findViewById(R.id.progress_horizontal_editar_entrevista);
        progressBar.setVisibility(View.VISIBLE);

        ilFechaEntrevista = findViewById(R.id.il_fecha_entrevista);
        ilTipoEntrevista = findViewById(R.id.il_tipo_entrevista);

        etFechaEntrevista = findViewById(R.id.et_fecha_entrevista);
        acTipoEntrevista = findViewById(R.id.et_tipo_entrevista);
    }

    private void iniciarViewModelEntrevista() {

        entrevistaViewModel = ViewModelProviders.of(this).get(EntrevistaViewModel.class);
        entrevistaViewModel.cargarEntrevista(entrevistaIntent).observe(this, new Observer<Entrevista>() {
            @Override
            public void onChanged(Entrevista entrevistaInternet) {
                entrevistaIntent = entrevistaInternet;
                if (isAutoCompleteTipoEntrevistaReady) {
                    setearInfoEntrevista();
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), entrevistaInternet.toString()));
            }
        });

        //Manejar respuesta de actualizacion
        entrevistaViewModel.mostrarRespuestaActualizacion().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                if (s.equals(getString(R.string.MSG_UPDATE_ENTREVISTA))) {
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                    finish();
                }
            }
        });

        //Manejar errores de entrevista
        entrevistaViewModel.mostrarRespuestaError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Funcion encargada de cargar datos de la entrevista en formulario de edicion
     */
    private void setearInfoEntrevista() {

        String fecha = Utils.dateToString(getApplicationContext(), false, entrevistaIntent.getFecha_entrevista());
        etFechaEntrevista.setText(fecha);

        String nombre = TipoEntrevistaRepositorio.getInstancia(getApplication()).buscarTipoEntrevistaPorId(entrevistaIntent.getTipoEntrevista().getId()).getNombre();
        acTipoEntrevista.setText(nombre, false);
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

        tipoEntrevistaViewModel = ViewModelProviders.of(this).get(TipoEntrevistaViewModel.class);
        tipoEntrevistaViewModel.cargarTiposEntrevistas().observe(this, new Observer<List<TipoEntrevista>>() {
            @Override
            public void onChanged(List<TipoEntrevista> tipoEntrevistas) {
                if (tipoEntrevistas != null) {
                    tipoEntrevistasList = tipoEntrevistas;
                    tipoEntrevistasAdapter = new TipoEntrevistaAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, tipoEntrevistasList);
                    acTipoEntrevista.setAdapter(tipoEntrevistasAdapter);

                    isAutoCompleteTipoEntrevistaReady = true;

                    progressBar.setVisibility(View.GONE);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_ENTREVISTA), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

                    if (isAutoCompleteTipoEntrevistaReady) {
                        iniciarViewModelEntrevista();
                    }

                    tipoEntrevistasAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * Funcion encargada de configurar el picker de fechas
     */
    private void setPickerFechaNacimiento() {
        //OnClick
        etFechaEntrevista.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                iniciarDatePicker();
            }
        });

        //EndIconOnClick
        ilFechaEntrevista.setEndIconOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                iniciarDatePicker();
            }
        });
    }

    /**
     * Funcion encargada de abrir el DatePicker para escoger fecha
     */
    private void iniciarDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        if (Objects.requireNonNull(etFechaEntrevista.getText()).length() > 0) {

            String fecha = etFechaEntrevista.getText().toString();
            String[] fecha_split = fecha.split(getString(R.string.REGEX_FECHA));

            year = Integer.parseInt(fecha_split[0]);
            month = Integer.parseInt(fecha_split[1]);
            day = Integer.parseInt(fecha_split[2]);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(EditarEntrevistaActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                etFechaEntrevista.setText(String.format(Locale.US, "%d-%d-%d", year, month, dayOfMonth));
            }
        }, year, month, day);

        datePickerDialog.show();
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

                int id_tipo_entrevista = TipoEntrevistaRepositorio.getInstancia(getApplication()).buscarTipoEntrevistaPorNombre(acTipoEntrevista.getText().toString()).getId();

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
}
