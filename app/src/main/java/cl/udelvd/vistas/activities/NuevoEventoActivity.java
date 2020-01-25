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
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.adaptadores.AccionAdapter;
import cl.udelvd.adaptadores.EmoticonAdapter;
import cl.udelvd.modelo.Accion;
import cl.udelvd.modelo.Emoticon;
import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.Evento;
import cl.udelvd.repositorios.EventoRepositorio;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.NuevoEventoViewModel;

public class NuevoEventoActivity extends AppCompatActivity {

    private TextInputLayout ilAcciones;
    private TextInputLayout ilHoraEvento;
    private TextInputLayout ilJustificacion;

    private AppCompatAutoCompleteTextView acAcciones;
    private TextInputEditText etHoraEvento;
    private TextInputEditText etJustificacion;

    private NuevoEventoViewModel nuevoEventoViewModel;

    private AccionAdapter accionAdapter;
    private EmoticonAdapter emoticonAdapter;

    private List<Accion> accionList;
    private List<Emoticon> emoticonList;

    private Spinner spinner;
    private Evento evento;
    private Entrevista entrevistaIntent;

    private ProgressBar progressBar;
    private static final int SPEECH_REQUEST_CODE = 100;
    private boolean isSnackBarShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_evento);

        Utils.configurarToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITULO_TOOLBAR_CREAR_EVENTO));

        obtenerDatosBundle();

        instanciarRecursosInterfaz();

        setPickerHoraEvento();

        setAutoCompleteAccion();

        setAutoCompleteEmoticon();

        configurarSpinnerEmoticones();

        configurarSpeechIntent();

        iniciarViewModelEvento();
    }


    /**
     * Obtener datos desde actividad de listado de eventos
     */
    private void obtenerDatosBundle() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            entrevistaIntent = new Entrevista();
            entrevistaIntent.setId(bundle.getInt(getString(R.string.KEY_EVENTO_ID_ENTREVISTA)));
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

        nuevoEventoViewModel = ViewModelProviders.of(this).get(NuevoEventoViewModel.class);
    }

    /**
     * Configurcion de selector de hora
     */
    private void setPickerHoraEvento() {

        //OnClick
        etHoraEvento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.iniciarHourPicker(etHoraEvento, NuevoEventoActivity.this);
            }
        });

        //EndIconOnClick
        ilHoraEvento.setEndIconOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.iniciarHourPicker(etHoraEvento, NuevoEventoActivity.this);
            }
        });

    }

    private void setAutoCompleteAccion() {

        nuevoEventoViewModel.isLoadingAcciones().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);

                    ilAcciones.setEnabled(false);
                    acAcciones.setEnabled(false);

                    spinner.setEnabled(false);

                    ilHoraEvento.setEnabled(false);
                    etHoraEvento.setEnabled(false);

                    ilJustificacion.setEnabled(false);
                    etJustificacion.setEnabled(false);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);

                    ilAcciones.setEnabled(true);
                    acAcciones.setEnabled(true);

                    spinner.setEnabled(true);

                    ilHoraEvento.setEnabled(true);
                    etHoraEvento.setEnabled(true);

                    ilJustificacion.setEnabled(true);
                    etJustificacion.setEnabled(true);
                }
            }
        });

        //Observer de listado de acciones
        nuevoEventoViewModel.cargarAcciones().observe(this, new Observer<List<Accion>>() {
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

        //Observer de errores de acciones
        nuevoEventoViewModel.mostrarMsgErrorAcciones().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.INVISIBLE);

                if (!isSnackBarShow) {
                    if (s.equals(getString(R.string.TIMEOUT_ERROR_MSG_VM)) || s.equals(getString(R.string.NETWORK_ERROR_MSG_VM))) {
                        showSnackbar(findViewById(R.id.formulario_nuevo_evento), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    } else {
                        showSnackbar(findViewById(R.id.formulario_nuevo_evento), s, null);
                        isSnackBarShow = true;
                    }
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_ACCIONES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    private void setAutoCompleteEmoticon() {

        nuevoEventoViewModel.isLoadingEmoticones().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);

                    ilAcciones.setEnabled(false);
                    acAcciones.setEnabled(false);

                    spinner.setEnabled(false);

                    ilHoraEvento.setEnabled(false);
                    etHoraEvento.setEnabled(false);

                    ilJustificacion.setEnabled(false);
                    etJustificacion.setEnabled(false);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);

                    ilAcciones.setEnabled(true);
                    acAcciones.setEnabled(true);

                    spinner.setEnabled(true);

                    ilHoraEvento.setEnabled(true);
                    etHoraEvento.setEnabled(true);

                    ilJustificacion.setEnabled(true);
                    etJustificacion.setEnabled(true);
                }
            }
        });

        //Observer con listado de emoticones
        nuevoEventoViewModel.cargarEmoticones().observe(this, new Observer<List<Emoticon>>() {
            @Override
            public void onChanged(List<Emoticon> emoticons) {
                if (emoticons != null) {

                    progressBar.setVisibility(View.GONE);

                    emoticonList = emoticons;
                    emoticonAdapter = new EmoticonAdapter(getApplicationContext(), emoticonList);
                    spinner.setAdapter(emoticonAdapter);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_EMOTICON), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                }
            }
        });

        //Observer para errores de listado de emoticones
        nuevoEventoViewModel.mostrarMsgErrorEmoticones().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.INVISIBLE);

                if (!isSnackBarShow) {
                    if (s.equals(getString(R.string.TIMEOUT_ERROR_MSG_VM)) || s.equals(getString(R.string.NETWORK_ERROR_MSG_VM))) {
                        showSnackbar(findViewById(R.id.formulario_nuevo_evento), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    } else {
                        showSnackbar(findViewById(R.id.formulario_nuevo_evento), s, null);
                        isSnackBarShow = true;
                    }
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
                evento.setEmoticon(emoticon);
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

        nuevoEventoViewModel.isLoadingEvento().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);

                    ilAcciones.setEnabled(false);
                    acAcciones.setEnabled(false);

                    spinner.setEnabled(false);

                    ilHoraEvento.setEnabled(false);
                    etHoraEvento.setEnabled(false);

                    ilJustificacion.setEnabled(false);
                    etJustificacion.setEnabled(false);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);

                    ilAcciones.setEnabled(true);
                    acAcciones.setEnabled(true);

                    spinner.setEnabled(true);

                    ilHoraEvento.setEnabled(true);
                    etHoraEvento.setEnabled(true);

                    ilJustificacion.setEnabled(true);
                    etJustificacion.setEnabled(true);
                }
            }
        });

        //Observador de mensajeria de registro
        nuevoEventoViewModel.mostrarMsgRegistro().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.INVISIBLE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_NUEVO_EVENTO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                if (s.equals(getString(R.string.MSG_REGISTRO_EVENTO))) {
                    Intent intent = getIntent();
                    intent.putExtra(getString(R.string.INTENT_KEY_MSG_REGISTRO), s);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        //Observador de mensajeria de error de registro
        nuevoEventoViewModel.mostrarMsgErrorRegistro().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.INVISIBLE);

                if (!isSnackBarShow) {
                    if (s.equals(getString(R.string.TIMEOUT_ERROR_MSG_VM)) || s.equals(getString(R.string.NETWORK_ERROR_MSG_VM))) {
                        showSnackbar(findViewById(R.id.formulario_nuevo_evento), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    } else {
                        showSnackbar(findViewById(R.id.formulario_nuevo_evento), s, null);
                        isSnackBarShow = true;
                    }
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_NUEVO_EVENTO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    /**
     * Funcion para mostrar el snackbar en fragment
     *
     * @param v      View donde se mostrara el snackbar
     * @param titulo Titulo del snackbar
     * @param accion Boton de accion del snackbar
     */
    private void showSnackbar(View v, String titulo, String accion) {

        Snackbar snackbar = Snackbar.make(v, titulo, Snackbar.LENGTH_INDEFINITE);

        if (accion != null) {
            snackbar.setAction(accion, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Refresh listado de informacion necesaria
                    nuevoEventoViewModel.refreshAcciones();
                    nuevoEventoViewModel.refreshEmoticones();

                    progressBar.setVisibility(View.VISIBLE);

                    isSnackBarShow = false;
                }
            });
        }
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
                entrevista.setId(entrevistaIntent.getId());
                evento.setEntrevista(entrevista);

                int id = Objects.requireNonNull(buscarAccionPorNombre(acAcciones.getText().toString())).getId();
                Accion accion = new Accion();
                accion.setId(id);
                evento.setAccion(accion);

                evento.setHora_evento(Utils.stringToDate(getApplicationContext(), true, Objects.requireNonNull(etHoraEvento.getText()).toString()));

                evento.setJustificacion(Objects.requireNonNull(etJustificacion.getText()).toString());

                EventoRepositorio.getInstancia(getApplication()).registrarEvento(evento);
            }

            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //SPEECH TEXT
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            assert data != null;
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            assert results != null;
            String spokenText = results.get(0);

            etJustificacion.setText(spokenText);
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

    /**
     * Funcion para buscar accion por nombre
     *
     * @param nombre Nombre de la accion a buscar
     * @return Objeto accion con toda la informacion
     */
    private Accion buscarAccionPorNombre(String nombre) {

        for (int i = 0; i < accionList.size(); i++) {
            if (accionList.get(i).getNombre().equals(nombre)) {
                return accionList.get(i);
            }
        }

        return null;
    }
}
