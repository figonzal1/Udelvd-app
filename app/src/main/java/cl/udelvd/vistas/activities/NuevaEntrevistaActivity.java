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

import cl.udelvd.R;
import cl.udelvd.adaptadores.TipoEntrevistaAdapter;
import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.TipoEntrevista;
import cl.udelvd.repositorios.EntrevistaRepositorio;
import cl.udelvd.repositorios.TipoEntrevistaRepositorio;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorOnPrimary));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Crear entrevista");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id_entrevistado = bundle.getInt("id_entrevistado");
        }

        instanciarRecursosInterfaz();

        iniciarViewModel();

        setPickerFechaNacimiento();
    }

    private void iniciarViewModel() {

        tipoEntrevistaViewModel = ViewModelProviders.of(this).get(TipoEntrevistaViewModel.class);
        entrevistaViewModel = ViewModelProviders.of(this).get(EntrevistaViewModel.class);

        tipoEntrevistaViewModel.cargarTiposEntrevistas().observe(this, new Observer<List<TipoEntrevista>>() {
            @Override
            public void onChanged(List<TipoEntrevista> tipoEntrevistas) {
                if (tipoEntrevistas != null) {
                    tipoEntrevistaList = tipoEntrevistas;
                    tipoEntrevistaAdapter = new TipoEntrevistaAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, tipoEntrevistaList);
                    acTipoEntrevista.setAdapter(tipoEntrevistaAdapter);

                    Log.d("VM_TIPO_ENTREVISTA", "Listado cargado");

                }
                tipoEntrevistaAdapter.notifyDataSetChanged();
            }
        });

        entrevistaViewModel.mostrarRespuestaRegistro().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                Log.d("VM_NEW_ENTREVISTA", "MSG_RESPONSE: " + s);

                //Si el registro fue correcto cerrar la actividad
                if (s.equals("¡Entrevista registrada!")) {
                    finish();
                }
            }
        });

        entrevistaViewModel.mostrarRespuestaError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                Log.d("VM_NEW_ENTREVISTA", "MSG_ERROR: " + s);
            }
        });


    }

    private void instanciarRecursosInterfaz() {

        progressBar = findViewById(R.id.progress_horizontal_nueva_entrevista);

        ilFechaEntrevista = findViewById(R.id.il_fecha_entrevista);
        ilTipoEntrevista = findViewById(R.id.il_tipo_entrevista);

        etFechaEntrevista = findViewById(R.id.et_fecha_entrevista);
        acTipoEntrevista = findViewById(R.id.et_tipo_entrevista);
    }

    private void setPickerFechaNacimiento() {

        //OnClick
        etFechaEntrevista.setOnClickListener(new View.OnClickListener() {
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(NuevaEntrevistaActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month += 1;
                        etFechaEntrevista.setText(year + "-" + month + "-" + dayOfMonth);
                    }
                }, year, month, day);

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

                DatePickerDialog datePickerDialog = new DatePickerDialog(NuevaEntrevistaActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month += 1;
                        etFechaEntrevista.setText(year + "-" + month + "-" + dayOfMonth);
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });

    }

    private boolean validarCampos() {
        int contador_errores = 0;

        if (Objects.requireNonNull(etFechaEntrevista.getText()).toString().isEmpty()) {
            ilFechaEntrevista.setErrorEnabled(true);
            ilFechaEntrevista.setError("Campo requerido");
            contador_errores++;
        } else {
            ilFechaEntrevista.setErrorEnabled(false);
        }

        if (Objects.requireNonNull(acTipoEntrevista.getText()).toString().isEmpty()) {
            ilTipoEntrevista.setErrorEnabled(true);
            ilTipoEntrevista.setError("Campo requerido");
            contador_errores++;
        } else {
            ilTipoEntrevista.setErrorEnabled(false);
        }

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

                Entrevista entrevista = new Entrevista();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date fecha_entrevista = null;
                try {
                    fecha_entrevista = simpleDateFormat.parse(Objects.requireNonNull(etFechaEntrevista.getText()).toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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
