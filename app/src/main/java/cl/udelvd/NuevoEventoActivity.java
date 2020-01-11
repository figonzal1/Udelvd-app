package cl.udelvd;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cl.udelvd.adaptadores.AccionAdapter;
import cl.udelvd.adaptadores.EmoticonAdapter;
import cl.udelvd.modelo.Accion;
import cl.udelvd.modelo.Emoticon;
import cl.udelvd.viewmodel.AccionViewModel;
import cl.udelvd.viewmodel.EmoticonViewModel;

public class NuevoEventoActivity extends AppCompatActivity {

    private int id_entrevista;
    private TextInputLayout ilAcciones;
    private TextInputLayout ilHoraEvento;
    private TextInputLayout ilEmoticones;

    private AppCompatAutoCompleteTextView acAcciones;
    private AppCompatAutoCompleteTextView acEmoticones;
    private TextInputEditText etHoraEvento;

    private AccionViewModel accionViewModel;
    private EmoticonViewModel emoticonViewModel;

    private AccionAdapter accionAdapter;
    private EmoticonAdapter emoticonAdapter;

    private List<Accion> accionList;
    private List<Emoticon> emoticonList;

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_evento);

        configurarToolbar();

        obtenerDatosBundle();

        instanciarRecursosInterfaz();


        setPickerHoraEvento();

        accionViewModel = ViewModelProviders.of(this).get(AccionViewModel.class);
        accionViewModel.cargarAcciones().observe(this, new Observer<List<Accion>>() {
            @Override
            public void onChanged(List<Accion> list) {

                if (list != null) {
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
                    emoticonList = emoticons;
                    emoticonAdapter = new EmoticonAdapter(getApplicationContext(), emoticonList);
                    spinner.setAdapter(emoticonAdapter);

                    Log.d("VM_EMOTICONES", "Listado cargado");
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
        ilAcciones = findViewById(R.id.il_accion_evento);
        ilHoraEvento = findViewById(R.id.il_hora_evento);

        acAcciones = findViewById(R.id.et_accion_evento);
        etHoraEvento = findViewById(R.id.et_hora_evento);

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
                        etHoraEvento.setText(String.format(Locale.US, "%d:%d", hourOfDay, minute));
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
                        etHoraEvento.setText(String.format(Locale.US, "%d:%d", hourOfDay, minute));
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
        }
        return super.onOptionsItemSelected(item);
    }
}
