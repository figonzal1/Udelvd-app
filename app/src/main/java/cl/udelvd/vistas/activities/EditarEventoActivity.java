package cl.udelvd.vistas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.adaptadores.AccionSelectorAdapter;
import cl.udelvd.adaptadores.EmoticonAdapter;
import cl.udelvd.modelo.Accion;
import cl.udelvd.modelo.Emoticon;
import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.Evento;
import cl.udelvd.repositorios.EventoRepositorio;
import cl.udelvd.utilidades.SnackbarInterface;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.EditarEventoViewModel;

public class EditarEventoActivity extends AppCompatActivity implements SnackbarInterface {

    private static final int SPEECH_REQUEST_CODE = 200;
    private TextInputLayout ilAcciones;
    private TextInputLayout ilHoraEvento;
    private TextInputLayout ilJustificacion;

    private AppCompatAutoCompleteTextView acAcciones;
    private TextInputEditText etHoraEvento;
    private TextInputEditText etJustificacion;

    private Evento eventoIntent;
    private ProgressBar progressBar;
    private Spinner spinner;

    private EditarEventoViewModel editarEventoViewModel;

    private List<Accion> accionList;
    private AccionSelectorAdapter accionSelectorAdapter;

    private EmoticonAdapter emoticonAdapter;
    private List<Emoticon> emoticonList;

    private boolean isSnackBarShow = false;
    private boolean isAutoCompleteAcciones = false;
    private boolean isSpinnerEmoticones = false;
    private boolean isGetEvento = false;
    private String idioma;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_evento);

        Utils.configurarToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITULO_TOOLBAR_EDITAR_EVENTO));

        instanciarRecursosInterfaz();

        obtenerDatosBundle();

        iniciarViewModelEvento();

        setAutoCompleteAcciones();

        setAutoCompleteEmoticones();

        setPickerHoraEvento();

        configurarSpinnerEmoticones();

        configurarSpeechIntent();


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

        editarEventoViewModel = new ViewModelProvider(this).get(EditarEventoViewModel.class);

        idioma = Utils.obtenerIdioma(getApplicationContext());
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
                Utils.iniciarHourPicker(etHoraEvento, EditarEventoActivity.this);
            }
        });

        //EndIconOnClick
        ilHoraEvento.setEndIconOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.iniciarHourPicker(etHoraEvento, EditarEventoActivity.this);
            }
        });

    }

    private void setAutoCompleteAcciones() {

        //Observer de carga de acciones
        editarEventoViewModel.isLoadingAcciones().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (aBoolean) {

                    progressBar.setVisibility(View.VISIBLE);

                    ilHoraEvento.setEnabled(false);
                    etHoraEvento.setEnabled(false);

                    spinner.setEnabled(false);

                    ilAcciones.setEnabled(false);
                    acAcciones.setEnabled(false);

                    ilJustificacion.setEnabled(false);
                    etJustificacion.setEnabled(false);
                } else {
                    progressBar.setVisibility(View.GONE);

                    ilHoraEvento.setEnabled(true);
                    etHoraEvento.setEnabled(true);

                    spinner.setEnabled(true);

                    ilAcciones.setEnabled(true);
                    acAcciones.setEnabled(true);

                    ilJustificacion.setEnabled(true);
                    etJustificacion.setEnabled(true);
                }
            }
        });

        //Observer de listado de acciones
        editarEventoViewModel.cargarAcciones(idioma).observe(this, new Observer<List<Accion>>() {
            @Override
            public void onChanged(List<Accion> list) {

                if (list != null && list.size() > 0) {
                    progressBar.setVisibility(View.GONE);

                    accionList = list;
                    accionSelectorAdapter = new AccionSelectorAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, accionList);
                    acAcciones.setAdapter(accionSelectorAdapter);

                    isAutoCompleteAcciones = true;

                    Log.d(getString(R.string.TAG_VIEW_MODEL_ACCIONES), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

                    accionSelectorAdapter.notifyDataSetChanged();

                    setearInfoEvento();
                }

            }
        });

        //Observer de errores de acciones
        editarEventoViewModel.mostrarMsgErrorAcciones().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(getWindow().getDecorView().findViewById(R.id.formulario_editar_evento), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_ACCIONES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    private void setAutoCompleteEmoticones() {

        editarEventoViewModel.isLoadingEmoticones().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {

                    progressBar.setVisibility(View.VISIBLE);

                    ilHoraEvento.setEnabled(false);
                    etHoraEvento.setEnabled(false);

                    spinner.setEnabled(false);

                    ilAcciones.setEnabled(false);
                    acAcciones.setEnabled(false);

                    ilJustificacion.setEnabled(false);
                    etJustificacion.setEnabled(false);
                } else {
                    progressBar.setVisibility(View.GONE);

                    ilHoraEvento.setEnabled(true);
                    etHoraEvento.setEnabled(true);

                    spinner.setEnabled(true);

                    ilAcciones.setEnabled(true);
                    acAcciones.setEnabled(true);

                    ilJustificacion.setEnabled(true);
                    etJustificacion.setEnabled(true);
                }
            }
        });

        //Observer con listado de emoticones
        editarEventoViewModel.cargarEmoticones().observe(this, new Observer<List<Emoticon>>() {
            @Override
            public void onChanged(List<Emoticon> emoticons) {
                if (emoticons != null && emoticons.size() > 0) {

                    progressBar.setVisibility(View.GONE);

                    emoticonList = emoticons;
                    emoticonAdapter = new EmoticonAdapter(getApplicationContext(), emoticonList);
                    spinner.setAdapter(emoticonAdapter);

                    isSpinnerEmoticones = true;

                    setearInfoEvento();

                    Log.d(getString(R.string.TAG_VIEW_MODEL_EMOTICON), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                }
            }
        });

        //Observer para errores de listado de emoticones
        editarEventoViewModel.mostrarMsgErrorEmoticones().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(getWindow().getDecorView().findViewById(R.id.formulario_editar_evento), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_EMOTICON), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            }
        });
    }

    /**
     * Funcion encargada de configurar el spinner de emoticones en formulario de eventos
     */
    private void configurarSpinnerEmoticones() {
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

    /**
     * Intent encargado de abrir el programa Voice To Text Google
     */
    private void configurarSpeechIntent() {
        ilJustificacion.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

                startActivityForResult(intent, SPEECH_REQUEST_CODE);
            }
        });
    }

    private void iniciarViewModelEvento() {

        //Observer para oading de evento
        editarEventoViewModel.isLoadingEvento().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {

                    progressBar.setVisibility(View.VISIBLE);

                    ilHoraEvento.setEnabled(false);
                    etHoraEvento.setEnabled(false);

                    spinner.setEnabled(false);

                    ilAcciones.setEnabled(false);
                    acAcciones.setEnabled(false);

                    ilJustificacion.setEnabled(false);
                    etJustificacion.setEnabled(false);
                } else {
                    progressBar.setVisibility(View.GONE);

                    ilHoraEvento.setEnabled(true);
                    etHoraEvento.setEnabled(true);

                    spinner.setEnabled(true);

                    ilAcciones.setEnabled(true);
                    acAcciones.setEnabled(true);

                    ilJustificacion.setEnabled(true);
                    etJustificacion.setEnabled(true);
                }
            }
        });

        //Cargar datos de evento
        editarEventoViewModel.cargarEvento(eventoIntent).observe(this, new Observer<Evento>() {
            @Override
            public void onChanged(Evento evento) {
                if (evento != null) {

                    progressBar.setVisibility(View.GONE);

                    eventoIntent = evento;
                    Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_EVENTO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), eventoIntent.toString()));

                    isGetEvento = true;

                    setearInfoEvento();
                }
            }
        });

        editarEventoViewModel.mostrarMsgErrorEvento().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(getWindow().getDecorView().findViewById(R.id.formulario_editar_evento), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_EVENTO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });

        //Observador para actualizar evento
        editarEventoViewModel.mostrarMsgActualizacion().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_EVENTO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                if (s.equals(getString(R.string.MSG_UPDATE_EVENTO))) {

                    progressBar.setVisibility(View.GONE);

                    Intent intent = getIntent();
                    intent.putExtra(getString(R.string.INTENT_KEY_MSG_ACTUALIZACION), s);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        //Observador para mensaje de edicion de evento
        editarEventoViewModel.mostrarMsgErrorActualizacion().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(getWindow().getDecorView().findViewById(R.id.formulario_editar_evento), Snackbar.LENGTH_LONG, s, null);
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_EVENTO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });

    }

    private void setearInfoEvento() {

        if (isAutoCompleteAcciones && isSpinnerEmoticones && isGetEvento) {

            etHoraEvento.setText(Utils.dateToString(getApplicationContext(), true, eventoIntent.getHora_evento()));

            int posicion = buscarPosicionEmoticonoPorId(eventoIntent.getEmoticon().getId());
            spinner.setSelection(posicion);

            etJustificacion.setText(eventoIntent.getJustificacion());

            String nombreAccion = Objects.requireNonNull(buscarAccionPorId(eventoIntent.getAccion().getId())).getNombre();
            acAcciones.setText(nombreAccion, false);

            isAutoCompleteAcciones = false;
            isSpinnerEmoticones = false;
            isGetEvento = false;
        }
    }


    @Override
    public void showSnackbar(View v, int duration, String titulo, String accion) {
        Snackbar snackbar = Snackbar.make(v, titulo, duration);

        if (accion != null) {
            snackbar.setAction(accion, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    isAutoCompleteAcciones = false;
                    isSpinnerEmoticones = false;
                    isGetEvento = false;

                    //Refresh listado de informacion necesaria
                    editarEventoViewModel.refreshAcciones(idioma);
                    editarEventoViewModel.refreshEmoticones();

                    editarEventoViewModel.refreshEvento(eventoIntent);

                    progressBar.setVisibility(View.VISIBLE);

                    isSnackBarShow = false;
                }
            });
        }
        isSnackBarShow = false;
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
                entrevista.setId(eventoIntent.getEntrevista().getId());
                eventoIntent.setEntrevista(entrevista);

                int id = Objects.requireNonNull(buscarAccionPorNombre(acAcciones.getText().toString())).getId();
                Accion accion = new Accion();
                accion.setId(id);
                eventoIntent.setAccion(accion);

                eventoIntent.setHora_evento(Utils.stringToDate(getApplicationContext(), true, Objects.requireNonNull(etHoraEvento.getText()).toString()));

                eventoIntent.setJustificacion(Objects.requireNonNull(etJustificacion.getText()).toString());

                EventoRepositorio.getInstancia(getApplication()).actualizarEvento(eventoIntent);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == SPEECH_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                assert data != null;
                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                assert results != null;
                String spokenText = results.get(0);

                etJustificacion.setText(spokenText);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
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

    private Accion buscarAccionPorNombre(String nombre) {

        for (int i = 0; i < accionList.size(); i++) {
            if (accionList.get(i).getNombre().equals(nombre)) {
                return accionList.get(i);
            }
        }
        return null;
    }
}
