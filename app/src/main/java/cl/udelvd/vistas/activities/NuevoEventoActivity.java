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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
import cl.udelvd.utilidades.Utils;
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

        Utils.configurarToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITULO_TOOLBAR_CREAR_EVENTO));

        obtenerDatosBundle();

        instanciarRecursosInterfaz();

        setPickerHoraEvento();

        iniciarViewModels();

        setSpinnerEmoticones();
    }

    /**
     * Obtener datos desde actividad de listado de eventos
     */
    private void obtenerDatosBundle() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            id_entrevista = bundle.getInt(getString(R.string.KEY_EVENTO_ID_ENTREVISTA));
        }
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
     * Configurcion de selector de hora
     */
    private void setPickerHoraEvento() {

        //OnClick
        etHoraEvento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                iniciarHourPicker();
            }
        });

        //EndIconOnClick
        ilHoraEvento.setEndIconOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                iniciarHourPicker();
            }
        });

    }

    /**
     * Funcion encargada de abrir el HourPicker para escoger fecha
     */
    private void iniciarHourPicker() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);

        if (Objects.requireNonNull(etHoraEvento.getText()).length() > 0) {

            String fecha = etHoraEvento.getText().toString();
            String[] fecha_split = fecha.split(getString(R.string.REGEX_HORA));

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

                    Log.d(getString(R.string.TAG_VIEW_MODEL_ACCIONES), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

                    accionAdapter.notifyDataSetChanged();
                }

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

                    Log.d(getString(R.string.TAG_VIEW_MODEL_EMOTICON), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                }
            }
        });

        eventoViewModel = ViewModelProviders.of(this).get(EventoViewModel.class);
        eventoViewModel.mostrarRespuestaRegistro().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.INVISIBLE);

                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                Log.d(getString(R.string.TAG_VIEW_MODEL_EVENTO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                if (s.equals(getString(R.string.MSG_REGISTRO_EVENTO))) {
                    finish();
                }
            }
        });
    }

    /**
     * Funcion encargada de configurar el spinner de emoticones en formulario de eventos
     */
    private void setSpinnerEmoticones() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(getString(R.string.SPINNER_EMOTICON_SELECTED), spinner.getSelectedItem().toString());

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

                evento.setHora_evento(Utils.stringToDate(getApplicationContext(), true, Objects.requireNonNull(etHoraEvento.getText()).toString()));

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
            ilHoraEvento.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {
            ilHoraEvento.setErrorEnabled(false);
        }

        //Accion
        if (acAcciones.getText().toString().isEmpty()) {
            ilAcciones.setErrorEnabled(true);
            ilAcciones.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {
            ilAcciones.setErrorEnabled(false);
        }

        //Spinner emoticon
        if (!spinner.isSelected()) {
            Toast.makeText(getApplicationContext(), getString(R.string.VALIDACION_EMOTICON), Toast.LENGTH_LONG).show();
            contador_errores++;
        }

        //Et justificacion
        if (Objects.requireNonNull(etJustificacion.getText()).toString().isEmpty()) {
            ilJustificacion.setErrorEnabled(true);
            ilJustificacion.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {
            ilJustificacion.setErrorEnabled(false);
        }


        return contador_errores == 0;
    }
}
