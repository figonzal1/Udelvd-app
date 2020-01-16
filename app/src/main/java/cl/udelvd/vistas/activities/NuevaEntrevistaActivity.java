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

import com.google.android.material.snackbar.Snackbar;
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

public class NuevaEntrevistaActivity extends AppCompatActivity {


    private ProgressBar progressBar;

    private TextInputLayout ilFechaEntrevista;
    private TextInputLayout ilTipoEntrevista;

    private TextInputEditText etFechaEntrevista;
    private AppCompatAutoCompleteTextView acTipoEntrevista;

    private int id_entrevistado;

    private List<TipoEntrevista> tipoEntrevistaList;
    private TipoEntrevistaAdapter tipoEntrevistaAdapter;
    private TipoEntrevistaViewModel tipoEntrevistaViewModel;

    private EntrevistaViewModel entrevistaViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_entrevista);

        Utils.configurarToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITULO_TOOLBAR_NUEVA_ENTREVISTA));

        instanciarRecursosInterfaz();

        obtenerDatosBundles();

        iniciarViewModel();

        setPickerFechaNacimiento();
    }

    private void instanciarRecursosInterfaz() {

        progressBar = findViewById(R.id.progress_horizontal_nueva_entrevista);
        progressBar.setVisibility(View.VISIBLE);

        ilFechaEntrevista = findViewById(R.id.il_fecha_entrevista);
        ilTipoEntrevista = findViewById(R.id.il_tipo_entrevista);

        etFechaEntrevista = findViewById(R.id.et_fecha_entrevista);
        acTipoEntrevista = findViewById(R.id.et_tipo_entrevista);
    }

    private void obtenerDatosBundles() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                id_entrevistado = bundle.getInt(getString(R.string.KEY_ENTREVISTADO_ID_LARGO));
            }
        }
    }

    private void iniciarViewModel() {

        tipoEntrevistaViewModel = ViewModelProviders.of(this).get(TipoEntrevistaViewModel.class);
        entrevistaViewModel = ViewModelProviders.of(this).get(EntrevistaViewModel.class);

        //Cargar listado de tipos de entrevista
        tipoEntrevistaViewModel.cargarTiposEntrevistas().observe(this, new Observer<List<TipoEntrevista>>() {
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
        tipoEntrevistaViewModel.mostrarMsgError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (s.equals(getString(R.string.TIMEOUT_ERROR_MSG_VM))) {
                    showSnackbar(findViewById(R.id.formulario_nueva_entrevista), s, getString(R.string.SNACKBAR_REINTENTAR));
                } else if (s.equals(getString(R.string.NETWORK_ERROR_MSG_VM))) {
                    showSnackbar(findViewById(R.id.formulario_nueva_entrevista), s, getString(R.string.SNACKBAR_REINTENTAR));
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });

        entrevistaViewModel.mostrarRespuestaRegistro().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                Log.d(getString(R.string.TAG_VIEW_MODEL_NEW_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                //Si el registro fue correcto cerrar la actividad
                if (s.equals(getString(R.string.MSG_REGISTRO_ENTREVISTA))) {
                    finish();
                }
            }
        });

        entrevistaViewModel.mostrarRespuestaError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                Log.d(getString(R.string.TAG_VIEW_MODEL_NEW_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });


    }

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

        DatePickerDialog datePickerDialog = new DatePickerDialog(NuevaEntrevistaActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                etFechaEntrevista.setText(String.format(Locale.US, "%d-%d-%d", year, month, dayOfMonth));
            }
        }, year, month, day);

        datePickerDialog.show();
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

    /**
     * Funcion para mostrar el snackbar en fragment
     *
     * @param v      View donde se mostrara el snackbar
     * @param titulo Titulo del snackbar
     * @param accion Boton de accion del snackbar
     */
    private void showSnackbar(View v, String titulo, String accion) {

        Snackbar snackbar = Snackbar.make(v, titulo, Snackbar.LENGTH_INDEFINITE)
                .setAction(accion, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Refresh listado de informacion necesaria
                        tipoEntrevistaViewModel.refreshTipoEntrevistas();

                        progressBar.setVisibility(View.VISIBLE);
                    }
                });

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
                TipoEntrevistaRepositorio repositorio = TipoEntrevistaRepositorio.getInstancia(getApplication());
                int id = repositorio.buscarTipoEntrevistaPorNombre(acTipoEntrevista.getText().toString()).getId();
                tipoEntrevista.setId(id);
                entrevista.setTipoEntrevista(tipoEntrevista);

                EntrevistaRepositorio.getInstancia(getApplication()).registrarEntrevista(entrevista);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
