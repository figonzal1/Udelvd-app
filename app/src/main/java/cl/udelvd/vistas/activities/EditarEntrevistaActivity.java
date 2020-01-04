package cl.udelvd.vistas.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import cl.udelvd.R;
import cl.udelvd.adaptadores.TipoEntrevistaAdapter;
import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.TipoEntrevista;
import cl.udelvd.repositorios.TipoEntrevistaRepositorio;
import cl.udelvd.viewmodel.EntrevistaViewModel;
import cl.udelvd.viewmodel.TipoEntrevistaViewModel;

public class EditarEntrevistaActivity extends AppCompatActivity {


    private TextInputLayout ilFechaEntrevista;
    private TextInputLayout ilTipoEntrevista;

    private TextInputEditText etFechaEntrevista;
    private AppCompatAutoCompleteTextView acTipoEntrevista;

    private Entrevista entrevista;


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

        iniciarViewModelErrores();

        setPickerFechaNacimiento();

        setAutoCompleteTipoEntrevista();

        obtenerBundles();

    }

    private void iniciarViewModelErrores() {
        entrevistaViewModel = ViewModelProviders.of(this).get(EntrevistaViewModel.class);
        tipoEntrevistaViewModel = ViewModelProviders.of(this).get(TipoEntrevistaViewModel.class);

        entrevistaViewModel.mostrarRespuestaError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        });

        //TODO: MANEJO DE CONEXIONES
        //tipoEntrevistaViewModel.
    }

    private void iniciarViewModelEntrevista() {
        entrevistaViewModel.cargarEntrevista(entrevista).observe(this, new Observer<Entrevista>() {
            @Override
            public void onChanged(Entrevista entrevistaInternet) {
                entrevista = entrevistaInternet;

                Log.d("VM_ENTREVISTA", entrevistaInternet.toString());
                if (isAutoCompleteTipoEntrevistaReady) {
                    setearInfoEntrevista();
                }
            }
        });
    }

    /**
     * Funcion encargada de cargar datos de la entrevista en formulario de edicion
     */
    private void setearInfoEntrevista() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String fecha = simpleDateFormat.format(entrevista.getFecha_entrevista());
        etFechaEntrevista.setText(fecha);

        String nombre = TipoEntrevistaRepositorio.getInstancia(getApplication()).buscarTipoEntrevistaPorId(entrevista.getTipoEntrevista().getId()).getNombre();
        acTipoEntrevista.setText(nombre, false);
    }

    private void obtenerBundles() {

        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();
            entrevista = new Entrevista();
            entrevista.setId(bundle.getInt("id_entrevista"));
            entrevista.setId_entrevistado(bundle.getInt("id_entrevistado"));
        }
    }

    /**
     * Funcion encargada de cargar los tipos de entrevista en el autoComplete
     */
    private void setAutoCompleteTipoEntrevista() {

        tipoEntrevistaViewModel.cargarTiposEntrevistas().observe(this, new Observer<List<TipoEntrevista>>() {
            @Override
            public void onChanged(List<TipoEntrevista> tipoEntrevistas) {
                if (tipoEntrevistas != null) {
                    tipoEntrevistasList = tipoEntrevistas;
                    tipoEntrevistasAdapter = new TipoEntrevistaAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, tipoEntrevistasList);
                    acTipoEntrevista.setAdapter(tipoEntrevistasAdapter);

                    isAutoCompleteTipoEntrevistaReady = true;

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
        }

        return false;
    }
}
