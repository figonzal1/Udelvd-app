package cl.udelvd.vistas.activities;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.adaptadores.AccionAdapter;
import cl.udelvd.adaptadores.EmoticonAdapter;
import cl.udelvd.modelo.Accion;
import cl.udelvd.modelo.Emoticon;
import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.Evento;
import cl.udelvd.repositorios.AccionRepositorio;
import cl.udelvd.repositorios.EventoRepositorio;
import cl.udelvd.viewmodel.AccionViewModel;
import cl.udelvd.viewmodel.EmoticonViewModel;
import cl.udelvd.viewmodel.EventoViewModel;

public class NuevoEventoActivity extends AppCompatActivity {

    private int id_entrevista;
    private TextInputLayout ilAcciones;
    private TextInputLayout ilHoraEvento;
    private TextInputLayout ilJustificacion;

    private AppCompatAutoCompleteTextView acAcciones;
    private TextInputEditText etHoraEvento;
    private TextInputEditText etJustificacion;

    private AccionViewModel accionViewModel;
    private EmoticonViewModel emoticonViewModel;
    private EventoViewModel eventoViewModel;

    private AccionAdapter accionAdapter;
    private EmoticonAdapter emoticonAdapter;

    private List<Accion> accionList;
    private List<Emoticon> emoticonList;

    private Spinner spinner;
    private Evento evento;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_evento);

        configurarToolbar();

        obtenerDatosBundle();

        instanciarRecursosInterfaz();

        setPickerHoraEvento();

        iniciarViewModels();

        setSpinnerEmoticones();


    }

    private void setSpinnerEmoticones() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("SELECT ITEM", spinner.getSelectedItem().toString());

                Emoticon emoticon = emoticonList.get(position);
                evento.setEmoticon(emoticon);
                spinner.setSelected(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                spinner.setSelected(false);
            }
        });
    }

    private void iniciarViewModels() {

        accionViewModel = ViewModelProviders.of(this).get(AccionViewModel.class);
        accionViewModel.cargarAcciones().observe(this, new Observer<List<Accion>>() {
            @Override
            public void onChanged(List<Accion> list) {

                if (list != null) {
                    progressBar.setVisibility(View.INVISIBLE);

                    accionList = list;
                    accionAdapter = new AccionAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, accionList);
                    acAcciones.setAdapter(accionAdapter);

                    Log.d("VM_ACCIONES", "Listado cargado");
                }
                accionAdapter.notifyDataSetChanged();
            }
        });


        emoticonViewModel = ViewModelProviders.of(this).get(EmoticonViewModel.class);
        emoticonViewModel.cargarEmoticones().observe(this, new Observer<List<Emoticon>>() {
            @Override
            public void onChanged(List<Emoticon> emoticons) {
                if (emoticons != null) {


                    progressBar.setVisibility(View.INVISIBLE);

                    emoticonList = emoticons;
                    emoticonAdapter = new EmoticonAdapter(getApplicationContext(), emoticonList);
                    spinner.setAdapter(emoticonAdapter);

                    Log.d("VM_EMOTICONES", "Listado cargado");
                }
            }
        });

        eventoViewModel = ViewModelProviders.of(this).get(EventoViewModel.class);
        eventoViewModel.mostrarRespuestaRegistro().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.INVISIBLE);

                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                if (s.equals("¡Evento registrado!")) {
                    finish();
                }
            }
        });
    }

    /**
     * Configuracion basica del toolbar de la actividad
     */
    private void configurarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorOnPrimary));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Crear evento");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
    }

    /**
     * Iniciar recursos usao e interfaz
     */
    private void instanciarRecursosInterfaz() {

        evento = new Evento();

        progressBar = findViewById(R.id.progress_horizontal_nuevo_evento);
        progressBar.setVisibility(View.VISIBLE);

        ilAcciones = findViewById(R.id.il_accion_evento);
        ilHoraEvento = findViewById(R.id.il_hora_evento);
        ilJustificacion = findViewById(R.id.il_justificacion_evento);

        acAcciones = findViewById(R.id.et_accion_evento);
        etHoraEvento = findViewById(R.id.et_hora_evento);
        etJustificacion = findViewById(R.id.et_justificacion_evento);

        spinner = findViewById(R.id.spinner_emoticon);
    }

    /**
     * Obtener datos desde actividad de listado de eventos
     */
    private void obtenerDatosBundle() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            id_entrevista = bundle.getInt("id_entrevista");
        }
    }

    /**
     * Configurcion de selector de hora
     */
    private void setPickerHoraEvento() {

        //OnClick
        etHoraEvento.setOnClickListener(new View.OnClickListener() {
            int hour;
            int minute;

            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR);
                minute = c.get(Calendar.MINUTE);

                if (Objects.requireNonNull(etHoraEvento.getText()).length() > 0) {

                    String fecha = etHoraEvento.getText().toString();
                    String[] fecha_split = fecha.split(":");

                    hour = Integer.parseInt(fecha_split[0]);
                    minute = Integer.parseInt(fecha_split[1]);
                }

                TimePickerDialog timePickerDialog = new TimePickerDialog(NuevoEventoActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (minute <= 9) {
                            etHoraEvento.setText(String.format(Locale.US, "%d:0%d", hourOfDay, minute));
                        } else {
                            etHoraEvento.setText(String.format(Locale.US, "%d:%d", hourOfDay, minute));
                        }
                    }
                }, hour, minute, true);

                timePickerDialog.show();
            }
        });

        //EndIconOnClick
        ilHoraEvento.setEndIconOnClickListener(new View.OnClickListener() {
            int hour;
            int minute;

            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR);
                minute = c.get(Calendar.MINUTE);

                if (Objects.requireNonNull(etHoraEvento.getText()).length() > 0) {

                    String fecha = etHoraEvento.getText().toString();
                    String[] fecha_split = fecha.split(":");

                    hour = Integer.parseInt(fecha_split[0]);
                    minute = Integer.parseInt(fecha_split[1]);
                }

                TimePickerDialog timePickerDialog = new TimePickerDialog(NuevoEventoActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (minute <= 9) {
                            etHoraEvento.setText(String.format(Locale.US, "%d:0%d", hourOfDay, minute));
                        } else {
                            etHoraEvento.setText(String.format(Locale.US, "%d:%d", hourOfDay, minute));
                        }

                    }
                }, hour, minute, true);

                timePickerDialog.show();
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

            progressBar.setVisibility(View.INVISIBLE);

            if (validarCampos()) {

                progressBar.setVisibility(View.VISIBLE);

                Entrevista entrevista = new Entrevista();
                entrevista.setId(id_entrevista);
                evento.setEntrevista(entrevista);

                int id = AccionRepositorio.getInstancia(getApplication()).buscarAccionPorNombre(acAcciones.getText().toString()).getId();
                Accion accion = new Accion();
                accion.setId(id);
                evento.setAccion(accion);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
                try {
                    evento.setHora_evento(simpleDateFormat.parse(Objects.requireNonNull(etHoraEvento.getText()).toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                evento.setJustificacion(Objects.requireNonNull(etJustificacion.getText()).toString());

                EventoRepositorio.getInstancia(getApplication()).registrarEvento(evento);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validarCampos() {

        int contador_errores = 0;

        //Hora evento
        if (Objects.requireNonNull(etHoraEvento.getText()).toString().isEmpty()) {
            ilHoraEvento.setErrorEnabled(true);
            ilHoraEvento.setError("Campo requerido");
            contador_errores++;
        } else {
            ilHoraEvento.setErrorEnabled(false);
        }

        //Accion
        if (acAcciones.getText().toString().isEmpty()) {
            ilAcciones.setErrorEnabled(true);
            ilAcciones.setError("Campo requerido");
            contador_errores++;
        } else {
            ilAcciones.setErrorEnabled(false);
        }

        //Spinner emoticon
        if (!spinner.isSelected()) {
            Toast.makeText(getApplicationContext(), "Elige un emoticón", Toast.LENGTH_LONG).show();
            contador_errores++;
        }

        //Et justificacion
        if (Objects.requireNonNull(etJustificacion.getText()).toString().isEmpty()) {
            ilJustificacion.setErrorEnabled(true);
            ilJustificacion.setError("Campo requerido");
            contador_errores++;
        } else {
            ilJustificacion.setErrorEnabled(false);
        }


        return contador_errores == 0;
    }
}
