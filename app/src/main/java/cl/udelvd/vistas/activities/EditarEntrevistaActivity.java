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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import cl.udelvd.R;
import cl.udelvd.adaptadores.TipoEntrevistaAdapter;
import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.TipoEntrevista;
import cl.udelvd.repositorios.EntrevistaRepositorio;
import cl.udelvd.repositorios.TipoEntrevistaRepositorio;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorOnPrimary));
        setSupportActionBar(toolbar);

        //Boton cerrar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Editar entrevista");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        instanciarRecursosInterfaz();

        //iniciarViewModelErrores();

        setAutoCompleteTipoEntrevista();

        setPickerFechaNacimiento();

        obtenerBundles();
    }

    private void iniciarViewModelEntrevista() {

        entrevistaViewModel = ViewModelProviders.of(this).get(EntrevistaViewModel.class);
        entrevistaViewModel.cargarEntrevista(entrevistaIntent).observe(this, new Observer<Entrevista>() {
            @Override
            public void onChanged(Entrevista entrevistaInternet) {
                entrevistaIntent = entrevistaInternet;

                Log.d("VM_ENTREVISTA", entrevistaInternet.toString());
                if (isAutoCompleteTipoEntrevistaReady) {
                    setearInfoEntrevista();
                }
            }
        });

        //Manejar respuesta de actualizacion
        entrevistaViewModel.mostrarRespuestaActualizacion().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (s.equals("¡Entrevista actualizada!")) {
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                    finish();
                }
            }
        });

        //Manejar errores de entrevista
        entrevistaViewModel.mostrarRespuestaError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Funcion encargada de cargar datos de la entrevista en formulario de edicion
     */
    private void setearInfoEntrevista() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String fecha = simpleDateFormat.format(entrevistaIntent.getFecha_entrevista());
        etFechaEntrevista.setText(fecha);

        String nombre = TipoEntrevistaRepositorio.getInstancia(getApplication()).buscarTipoEntrevistaPorId(entrevistaIntent.getTipoEntrevista().getId()).getNombre();
        acTipoEntrevista.setText(nombre, false);
    }

    private void obtenerBundles() {

        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();
            entrevistaIntent = new Entrevista();
            entrevistaIntent.setId(bundle.getInt("id_entrevista"));
            entrevistaIntent.setId_entrevistado(bundle.getInt("id_entrevistado"));
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

                    Log.d("VM_TIPO_ENTREVISTA", "Listado cargado");

                    if (isAutoCompleteTipoEntrevistaReady) {
                        iniciarViewModelEntrevista();
                    }

                }

                tipoEntrevistasAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Funcion encargada de configurar el picker de fechas
     */
    private void setPickerFechaNacimiento() {
        //OnClick
        etFechaEntrevista.setOnClickListener(new View.OnClickListener() {
            int year;
            int month;
            int day;

            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance(TimeZone.getDefault());
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                Log.d("AÑO", String.valueOf(year));
                Log.d("MES", String.valueOf(month));
                Log.d("DIA", String.valueOf(day));

                if (Objects.requireNonNull(etFechaEntrevista.getText()).length() > 0) {

                    String fecha = etFechaEntrevista.getText().toString();
                    String[] fecha_split = fecha.split("-");

                    year = Integer.parseInt(fecha_split[0]);
                    month = Integer.parseInt(fecha_split[1]);
                    day = Integer.parseInt(fecha_split[2]);
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditarEntrevistaActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month += 1;
                        etFechaEntrevista.setText(year + "-" + month + "-" + dayOfMonth);
                    }
                }, year, month - 1, day);

                datePickerDialog.show();
            }
        });

        //EndIconOnClick
        ilFechaEntrevista.setEndIconOnClickListener(new View.OnClickListener() {

            int year;
            int month;
            int day;

            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                if (Objects.requireNonNull(etFechaEntrevista.getText()).length() > 0) {

                    String fecha = etFechaEntrevista.getText().toString();
                    String[] fecha_split = fecha.split("-");

                    year = Integer.parseInt(fecha_split[0]);
                    month = Integer.parseInt(fecha_split[1]);
                    day = Integer.parseInt(fecha_split[2]);
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditarEntrevistaActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month += 1;
                        etFechaEntrevista.setText(year + "-" + month + "-" + dayOfMonth);
                    }
                }, year, month - 1, day);

                datePickerDialog.show();
            }
        });
    }

    private void instanciarRecursosInterfaz() {

        progressBar = findViewById(R.id.progress_horizontal_editar_entrevista);
        progressBar.setVisibility(View.VISIBLE);

        ilFechaEntrevista = findViewById(R.id.il_fecha_entrevista);
        ilTipoEntrevista = findViewById(R.id.il_tipo_entrevista);

        etFechaEntrevista = findViewById(R.id.et_fecha_entrevista);
        acTipoEntrevista = findViewById(R.id.et_tipo_entrevista);
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

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date fecha = null;
                try {
                    fecha = simpleDateFormat.parse(Objects.requireNonNull(etFechaEntrevista.getText()).toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                entrevistaActualizada.setFecha_entrevista(fecha);

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
            ilFechaEntrevista.setError("Campo requerido");
            contador_errores++;
        } else {
            ilFechaEntrevista.setErrorEnabled(false);
        }

        if (acTipoEntrevista.getText().toString().isEmpty()) {
            ilTipoEntrevista.setErrorEnabled(true);
            ilTipoEntrevista.setError("Campo requerido");
            contador_errores++;
        } else {
            ilTipoEntrevista.setErrorEnabled(false);
        }

        return contador_errores == 0;
    }
}
