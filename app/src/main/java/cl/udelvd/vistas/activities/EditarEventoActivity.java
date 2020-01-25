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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
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
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.AccionViewModel;
import cl.udelvd.viewmodel.EmoticonViewModel;
import cl.udelvd.viewmodel.EventoViewModel;

public class EditarEventoActivity extends AppCompatActivity {

    private TextInputLayout ilAcciones;
    private TextInputLayout ilHoraEvento;
    private TextInputLayout ilJustificacion;

    private AppCompatAutoCompleteTextView acAcciones;
    private TextInputEditText etHoraEvento;
    private TextInputEditText etJustificacion;

    private Evento eventoIntent;
    private ProgressBar progressBar;
    private Spinner spinner;

    private List<Accion> accionList;
    private AccionAdapter accionAdapter;
    private AccionViewModel accionViewModel;

    private EmoticonAdapter emoticonAdapter;
    private List<Emoticon> emoticonList;
    private EmoticonViewModel emoticonViewModel;

    private EventoViewModel eventoViewModel;

    private boolean isSnackBarShow = false;
    private boolean isAutoCompleteAcciones = false;
    private boolean isSpinnerEmoticones = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_evento);

        Utils.configurarToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITULO_TOOLBAR_EDITAR_EVENTO));

        instanciarRecursosInterfaz();

        obtenerDatosBundle();

        setPickerHoraEvento();

        setAutoCompleteAcciones();

        setAutoCompleteEmoticones();

        setSpinnerEmoticones();

        iniciarViewModelEvento();
    }

    private void iniciarViewModelEvento() {

        eventoViewModel = ViewModelProviders.of(this).get(EventoViewModel.class);

        //Cargar datos de evento
        eventoViewModel.cargarEvento(eventoIntent).observe(this, new Observer<Evento>() {
            @Override
            public void onChanged(Evento evento) {
                if (evento != null) {
                    eventoIntent = evento;

                    if (isSpinnerEmoticones && isAutoCompleteAcciones) {
                        setearInfoEvento();
                        Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_EVENTO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), eventoIntent.toString()));
                    }
                }
            }
        });

        eventoViewModel.mostrarErrorEvento().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    if (s.equals(getString(R.string.TIMEOUT_ERROR_MSG_VM))) {
                        showSnackbar(getWindow().getDecorView().findViewById(R.id.formulario_editar_evento), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    } else if (s.equals(getString(R.string.NETWORK_ERROR_MSG_VM))) {
                        showSnackbar(getWindow().getDecorView().findViewById(R.id.formulario_editar_evento), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    }
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_EVENTO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });

    }

    private void setearInfoEvento() {

        etHoraEvento.setText(Utils.dateToString(getApplicationContext(), true, eventoIntent.getHora_evento()));

        int posicion = buscarPosicionEmoticonoPorId(eventoIntent.getEmoticon().getId());
        spinner.setSelection(posicion);

        etJustificacion.setText(eventoIntent.getJustificacion());

        String nombreAccion = Objects.requireNonNull(buscarAccionPorId(eventoIntent.getAccion().getId())).getNombre();
        acAcciones.setText(nombreAccion, false);
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
                eventoIntent.setEmoticon(emoticon);
                spinner.setSelected(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setSelected(false);
            }
        });
    }

    private void setAutoCompleteEmoticones() {
        emoticonViewModel = ViewModelProviders.of(this).get(EmoticonViewModel.class);

        //Observer con listado de emoticones
        emoticonViewModel.cargarEmoticones().observe(this, new Observer<List<Emoticon>>() {
            @Override
            public void onChanged(List<Emoticon> emoticons) {
                if (emoticons != null) {

                    progressBar.setVisibility(View.GONE);

                    emoticonList = emoticons;
                    emoticonAdapter = new EmoticonAdapter(getApplicationContext(), emoticonList);
                    spinner.setAdapter(emoticonAdapter);

                    isSpinnerEmoticones = true;

                    //IniciarViewModel Evento
                    //iniciarViewModelEvento();

                    Log.d(getString(R.string.TAG_VIEW_MODEL_EMOTICON), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                }
            }
        });

        //Observer para errores de listado de emoticones
        emoticonViewModel.mostrarMsgError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    if (s.equals(getString(R.string.TIMEOUT_ERROR_MSG_VM))) {
                        showSnackbar(getWindow().getDecorView().findViewById(R.id.formulario_editar_evento), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    } else if (s.equals(getString(R.string.NETWORK_ERROR_MSG_VM))) {
                        showSnackbar(getWindow().getDecorView().findViewById(R.id.formulario_editar_evento), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    }
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_EMOTICON), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            }
        });
    }

    private void setAutoCompleteAcciones() {
        accionViewModel = ViewModelProviders.of(this).get(AccionViewModel.class);

        //Observer de listado de acciones
        accionViewModel.cargarAcciones().observe(this, new Observer<List<Accion>>() {
            @Override
            public void onChanged(List<Accion> list) {

                if (list != null) {
                    progressBar.setVisibility(View.GONE);

                    accionList = list;
                    accionAdapter = new AccionAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, accionList);
                    acAcciones.setAdapter(accionAdapter);

                    isAutoCompleteAcciones = true;

                    Log.d(getString(R.string.TAG_VIEW_MODEL_ACCIONES), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

                    accionAdapter.notifyDataSetChanged();
                }

            }
        });

        //Observer de errores de acciones
        accionViewModel.moestrarMsgError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    if (s.equals(getString(R.string.TIMEOUT_ERROR_MSG_VM))) {
                        showSnackbar(getWindow().getDecorView().findViewById(R.id.formulario_editar_evento), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    } else if (s.equals(getString(R.string.NETWORK_ERROR_MSG_VM))) {
                        showSnackbar(getWindow().getDecorView().findViewById(R.id.formulario_editar_evento), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    }
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_ACCIONES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    private void instanciarRecursosInterfaz() {
        progressBar = findViewById(R.id.progress_horizontal_editar_evento);
        progressBar.setVisibility(View.VISIBLE);

        ilAcciones = findViewById(R.id.il_accion_evento);
        ilHoraEvento = findViewById(R.id.il_hora_evento);
        ilJustificacion = findViewById(R.id.il_justificacion_evento);

        acAcciones = findViewById(R.id.et_accion_evento);

        etHoraEvento = findViewById(R.id.et_hora_evento);
        etJustificacion = findViewById(R.id.et_justificacion_evento);

        spinner = findViewById(R.id.spinner_emoticon);
    }

    private void obtenerDatosBundle() {

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            eventoIntent = new Evento();
            eventoIntent.setId(bundle.getInt(getString(R.string.KEY_EVENTO_ID_LARGO)));

            Entrevista entrevista = new Entrevista();
            entrevista.setId(bundle.getInt(getString(R.string.KEY_EVENTO_ID_ENTREVISTA)));
            eventoIntent.setEntrevista(entrevista);
        }
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

        TimePickerDialog timePickerDialog = new TimePickerDialog(EditarEventoActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                        accionViewModel.refreshAcciones();
                        emoticonViewModel.refreshEmoticones();

                        eventoViewModel.refreshEvento(eventoIntent);

                        progressBar.setVisibility(View.VISIBLE);

                        isSnackBarShow = false;
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


            /*if (validarCampos()) {

                progressBar.setVisibility(View.VISIBLE);

                Entrevista entrevista = new Entrevista();
                entrevista.setId(entrevistaIntent.getId());
                eventoIntent.setEntrevista(entrevista);

                int id = AccionRepositorio.getInstancia(getApplication()).buscarAccionPorNombre(acAcciones.getText().toString()).getId();
                Accion accion = new Accion();
                accion.setId(id);
                eventoIntent.setAccion(accion);

                eventoIntent.setHora_evento(Utils.stringToDate(getApplicationContext(), true, Objects.requireNonNull(etHoraEvento.getText()).toString()));

                eventoIntent.setJustificacion(Objects.requireNonNull(etJustificacion.getText()).toString());

                EventoRepositorio.getInstancia(getApplication()).registrarEvento(eventoIntent);
            }*/

        }
        return super.onOptionsItemSelected(item);
    }

    private int buscarPosicionEmoticonoPorId(int id) {
        for (int i = 0; i < emoticonList.size(); i++) {
            if (emoticonList.get(i).getId() == id) {
                return i;
            }
        }
        return 0;
    }

    private Accion buscarAccionPorId(int id) {
        for (int i = 0; i < accionList.size(); i++) {
            if (accionList.get(i).getId() == id) {
                return accionList.get(i);
            }
        }
        return null;
    }
}
