package cl.udelvd.vistas.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.adaptadores.CiudadAdapter;
import cl.udelvd.adaptadores.EstadoCivilAdapter;
import cl.udelvd.adaptadores.NivelEducacionalAdapter;
import cl.udelvd.adaptadores.ProfesionAdapter;
import cl.udelvd.adaptadores.TipoConvivenciaAdapter;
import cl.udelvd.modelo.Ciudad;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.modelo.EstadoCivil;
import cl.udelvd.modelo.NivelEducacional;
import cl.udelvd.modelo.Profesion;
import cl.udelvd.modelo.TipoConvivencia;
import cl.udelvd.repositorios.EntrevistadoRepositorio;
import cl.udelvd.repositorios.EstadoCivilRepositorio;
import cl.udelvd.repositorios.NivelEducacionalRepositorio;
import cl.udelvd.repositorios.TipoConvivenciaRepositorio;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.CiudadViewModel;
import cl.udelvd.viewmodel.EntrevistadoViewModel;
import cl.udelvd.viewmodel.EstadoCivilViewModel;
import cl.udelvd.viewmodel.NivelEducacionalViewModel;
import cl.udelvd.viewmodel.ProfesionViewModel;
import cl.udelvd.viewmodel.TipoConvivenciaViewModel;

public class NuevoEntrevistadoActivity extends AppCompatActivity {

    private TextInputLayout ilNombre;
    private TextInputLayout ilApellido;
    private TextInputLayout ilSexo;
    private TextInputLayout ilFechaNacimiento;
    private TextInputLayout ilCiudad;
    private TextInputLayout ilEstadoCivil;
    private TextInputLayout ilNConvivientes;
    private SwitchMaterial switchJubiladoLegal;
    private TextView tv_jubilado_value;
    private SwitchMaterial switchCaidas;
    private TextView tv_caidas_value;
    private TextInputLayout ilNCaidas;
    private TextInputLayout ilTipoConvivencia;
    private TextInputLayout ilProfesion;

    private TextInputEditText etNombre;
    private TextInputEditText etApellido;
    private AppCompatAutoCompleteTextView acSexo;
    private TextInputEditText etFechaNacimiento;
    private TextInputEditText etNConvivientes;
    private AppCompatAutoCompleteTextView acCiudad;
    private AppCompatAutoCompleteTextView acNivelEducacional;
    private AppCompatAutoCompleteTextView acEstadoCivil;
    private TextInputEditText etNCaidas;
    private AppCompatAutoCompleteTextView acTipoConvivencia;
    private AppCompatAutoCompleteTextView acProfesion;

    //ViewModels
    private CiudadViewModel ciudadViewModel;
    private EstadoCivilViewModel estadoCivilViewModel;
    private NivelEducacionalViewModel nivelEducacionalViewModel;
    private TipoConvivenciaViewModel tipoConvivenciaViewModel;
    private ProfesionViewModel profesionViewModel;
    private EntrevistadoViewModel entrevistadoViewModel;

    //Listados
    private List<Ciudad> ciudadList;
    private List<EstadoCivil> estadoCivilList;
    private List<NivelEducacional> nivelEducacionalList;
    private List<TipoConvivencia> tipoConvivenciaList;
    private List<Profesion> profesionList;

    //Adaptadores
    private ArrayAdapter<Ciudad> ciudadAdapter;
    private ArrayAdapter<EstadoCivil> estadoCivilAdapter;
    private ArrayAdapter<NivelEducacional> nivelEducacionalAdapter;
    private ArrayAdapter<TipoConvivencia> tipoConvivenciaAdapter;
    private ArrayAdapter<Profesion> profesionAdapter;

    private ProgressBar progressBar;

    boolean isSnackBarShow = false;

    public NuevoEntrevistadoActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_entrevistado);

        Utils.configurarToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITULO_TOOLBAR_NUEVO_ENTREVISTADO));

        instanciarRecursosInterfaz();

        iniciarViewModelObservers();

        setSpinnerSexo();

        setPickerFechaNacimiento();

        setCaidas();

        setJubilado();

    }

    /**
     * Instanciacion de atributos
     */
    private void instanciarRecursosInterfaz() {
        ilNombre = findViewById(R.id.il_nombre_entrevistado);
        ilApellido = findViewById(R.id.il_apellido_entrevistado);
        ilSexo = findViewById(R.id.il_sexo_entrevistado);
        ilFechaNacimiento = findViewById(R.id.il_fecha_nacimiento);
        ilCiudad = findViewById(R.id.il_ciudad_entrevistado);
        ilEstadoCivil = findViewById(R.id.il_estado_civil_entrevistado);
        ilNConvivientes = findViewById(R.id.il_n_convivientes_entrevistado);
        ilNCaidas = findViewById(R.id.il_n_caidas_entrevistado);
        ilTipoConvivencia = findViewById(R.id.il_tipo_convivencia_entrevistado);
        ilProfesion = findViewById(R.id.il_profesion_entrevistado);

        tv_jubilado_value = findViewById(R.id.tv_switch_jubilado_value);
        tv_caidas_value = findViewById(R.id.tv_switch_caidas);

        etNombre = findViewById(R.id.et_nombre_entrevistado);
        etApellido = findViewById(R.id.et_apellido_entrevistado);
        etFechaNacimiento = findViewById(R.id.et_fecha_nacimiento);
        etNCaidas = findViewById(R.id.et_n_caidas_entrevistado);
        etNConvivientes = findViewById(R.id.et_n_convivientes_entrevistado);

        acCiudad = findViewById(R.id.et_ciudad_entrevistado);
        acEstadoCivil = findViewById(R.id.et_estado_civil_entrevistado);
        acSexo = findViewById(R.id.et_sexo_entrevistado);
        acNivelEducacional = findViewById(R.id.et_nivel_educacional_entrevistado);
        acTipoConvivencia = findViewById(R.id.et_tipo_convivencia_entrevistado);
        acProfesion = findViewById(R.id.et_profesion_entrevistado);

        switchJubiladoLegal = findViewById(R.id.switch_jubilado_legal);
        switchCaidas = findViewById(R.id.switch_caidas_entrevistado);

        progressBar = findViewById(R.id.progress_horizontal_registro_entrevistado);
        progressBar.setVisibility(View.VISIBLE);

    }

    /**
     * Funcion encargada de inicializar observadores
     */
    private void iniciarViewModelObservers() {

        viewModelEntrevistado();

        viewModelEstadoCivil();

        viewModelCiudad();

        viewModelNivelEduc();

        viewModelTipoConvivencia();

        viewModelProfesiones();
    }

    private void viewModelEntrevistado() {

        entrevistadoViewModel = ViewModelProviders.of(this).get(EntrevistadoViewModel.class);

        //Observador para mensajes creacion entrevistado
        entrevistadoViewModel.mostrarRespuestaRegistro().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                Log.d(getString(R.string.TAG_VIEW_MODEL_NUEVO_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                //Si el registro fue correcto cerrar la actividad
                if (s.equals(getString(R.string.MSG_REGISTRO_ENTREVISTADO))) {
                    finish();
                }
            }
        });

        //Observador para mensajes de error de registro entrevistado
        entrevistadoViewModel.mostrarErrorRespuesta().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {

                    if (s.equals(getString(R.string.TIMEOUT_ERROR_MSG_VM))) {
                        showSnackbar(findViewById(R.id.formulario_nuevo_entrevistado), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    } else if (s.equals(getString(R.string.NETWORK_ERROR_MSG_VM))) {
                        showSnackbar(findViewById(R.id.formulario_nuevo_entrevistado), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    }
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_NUEVO_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });

    }

    private void viewModelEstadoCivil() {
        estadoCivilViewModel = ViewModelProviders.of(this).get(EstadoCivilViewModel.class);
        //Observador de estados civiles
        estadoCivilViewModel.cargarEstadosCiviles().observe(this, new Observer<List<EstadoCivil>>() {
            @Override
            public void onChanged(List<EstadoCivil> estadoCivils) {

                if (estadoCivils != null) {
                    estadoCivilList = estadoCivils;
                    estadoCivilAdapter = new EstadoCivilAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, estadoCivilList);
                    acEstadoCivil.setAdapter(estadoCivilAdapter);

                    estadoCivilAdapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_ESTADO_CIVIL), getString(R.string.VIEW_MODEL_LISTADO_CARGADO));
                }
            }
        });

        //Estado civil errores
        //ViewModel usado para detectar errores
        estadoCivilViewModel.mostrarMsgError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {

                    if (s.equals(getString(R.string.TIMEOUT_ERROR_MSG_VM))) {
                        showSnackbar(findViewById(R.id.formulario_nuevo_entrevistado), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    } else if (s.equals(getString(R.string.NETWORK_ERROR_MSG_VM))) {
                        showSnackbar(findViewById(R.id.formulario_nuevo_entrevistado), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    }
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_ESTADO_CIVIL), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    private void viewModelCiudad() {
        ciudadViewModel = ViewModelProviders.of(this).get(CiudadViewModel.class);
        //Observador de ciudades
        ciudadViewModel.cargarCiudades().observe(this, new Observer<List<Ciudad>>() {
            @Override
            public void onChanged(List<Ciudad> ciudads) {

                if (ciudads != null) {
                    ciudadList = ciudads;
                    ciudadAdapter = new CiudadAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, ciudadList);
                    acCiudad.setAdapter(ciudadAdapter);

                    ciudadAdapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_CIUDAD), getString(R.string.VIEW_MODEL_LISTADO_CARGADO));
                }

            }
        });

        ciudadViewModel.mostrarMsgError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (!isSnackBarShow) {
                    if (s.equals(getString(R.string.TIMEOUT_ERROR_MSG_VM))) {
                        showSnackbar(findViewById(R.id.formulario_nuevo_entrevistado), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    } else if (s.equals(getString(R.string.NETWORK_ERROR_MSG_VM))) {
                        showSnackbar(findViewById(R.id.formulario_nuevo_entrevistado), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    }
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_CIUDAD), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    private void viewModelNivelEduc() {
        nivelEducacionalViewModel = ViewModelProviders.of(this).get(NivelEducacionalViewModel.class);
        //Observador de niveles educacionaes
        nivelEducacionalViewModel.cargarNivelesEduc().observe(this, new Observer<List<NivelEducacional>>() {
            @Override
            public void onChanged(List<NivelEducacional> nivelEducacionals) {
                if (nivelEducacionals != null) {
                    nivelEducacionalList = nivelEducacionals;
                    nivelEducacionalAdapter = new NivelEducacionalAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, nivelEducacionalList);
                    acNivelEducacional.setAdapter(nivelEducacionalAdapter);

                    nivelEducacionalAdapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_NIVEL_EDUCACION), getString(R.string.VIEW_MODEL_LISTADO_CARGADO));
                }

            }
        });
        //Errores de niveles educacoanles
        nivelEducacionalViewModel.mostrarMsgError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (!isSnackBarShow) {
                    if (s.equals(getString(R.string.TIMEOUT_ERROR_MSG_VM))) {
                        showSnackbar(findViewById(R.id.formulario_nuevo_entrevistado), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    } else if (s.equals(getString(R.string.NETWORK_ERROR_MSG_VM))) {
                        showSnackbar(findViewById(R.id.formulario_nuevo_entrevistado), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    }
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_NIVEL_EDUCACION), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            }
        });
    }

    private void viewModelTipoConvivencia() {
        tipoConvivenciaViewModel = ViewModelProviders.of(this).get(TipoConvivenciaViewModel.class);
        //Observador de tipos de convivencias
        tipoConvivenciaViewModel.cargarTiposConvivencias().observe(this, new Observer<List<TipoConvivencia>>() {
            @Override
            public void onChanged(List<TipoConvivencia> list) {
                if (list != null) {
                    tipoConvivenciaList = list;
                    tipoConvivenciaAdapter = new TipoConvivenciaAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, tipoConvivenciaList);
                    acTipoConvivencia.setAdapter(tipoConvivenciaAdapter);

                    tipoConvivenciaAdapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_CONVIVENCIA), getString(R.string.VIEW_MODEL_LISTADO_CARGADO));
                }

            }
        });

        //Errores de tipo de convivencia
        tipoConvivenciaViewModel.mostrarMsgError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!isSnackBarShow) {
                    if (s.equals(getString(R.string.TIMEOUT_ERROR_MSG_VM))) {
                        showSnackbar(findViewById(R.id.formulario_nuevo_entrevistado), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    } else if (s.equals(getString(R.string.NETWORK_ERROR_MSG_VM))) {
                        showSnackbar(findViewById(R.id.formulario_nuevo_entrevistado), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    }
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_CONVIVENCIA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            }
        });
    }

    private void viewModelProfesiones() {
        profesionViewModel = ViewModelProviders.of(this).get(ProfesionViewModel.class);
        //Observador de profesiones
        profesionViewModel.cargarProfesiones().observe(this, new Observer<List<Profesion>>() {
            @Override
            public void onChanged(List<Profesion> profesions) {
                if (profesions != null) {
                    profesionList = profesions;
                    profesionAdapter = new ProfesionAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, profesionList);
                    acProfesion.setAdapter(profesionAdapter);

                    profesionAdapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_PROFESIONES), getString(R.string.VIEW_MODEL_LISTADO_CARGADO));
                }
            }
        });

        //Observador de errores profesiones
        profesionViewModel.mostrarMsgError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!isSnackBarShow) {
                    if (s.equals(getString(R.string.TIMEOUT_ERROR_MSG_VM))) {
                        showSnackbar(findViewById(R.id.formulario_nuevo_entrevistado), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    } else if (s.equals(getString(R.string.NETWORK_ERROR_MSG_VM))) {
                        showSnackbar(findViewById(R.id.formulario_nuevo_entrevistado), s, getString(R.string.SNACKBAR_REINTENTAR));
                        isSnackBarShow = true;
                    }
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_PROFESIONES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            }
        });
    }

    /**
     * Funcion encargada de configurar el Spinner de sexo
     */
    private void setSpinnerSexo() {
        //Setear autocompletado Sexo
        String[] opcionesSexo = new String[]{getString(R.string.SEXO_MASCULINO), getString(R.string.SEXO_FEMENINO), getString(R.string.SEXO_OTRO)};
        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, opcionesSexo);
        acSexo.setAdapter(adapterSexo);
    }

    /**
     * Funcion encargada de configurar logica del picker de fecha
     */
    private void setPickerFechaNacimiento() {
        //OnClick
        etFechaNacimiento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                iniciarDatePicker();
            }
        });

        //EndIconOnClick
        ilFechaNacimiento.setEndIconOnClickListener(new View.OnClickListener() {
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

        if (Objects.requireNonNull(etFechaNacimiento.getText()).length() > 0) {

            String fecha = etFechaNacimiento.getText().toString();
            String[] fecha_split = fecha.split(getString(R.string.REGEX_FECHA));

            year = Integer.parseInt(fecha_split[0]);
            month = Integer.parseInt(fecha_split[1]);
            day = Integer.parseInt(fecha_split[2]);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(NuevoEntrevistadoActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                etFechaNacimiento.setText(String.format(Locale.US, "%d-%d-%d", year, month, dayOfMonth));
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    /**
     * Funcion encargada de configurar la logica del switch de caidas
     */
    private void setCaidas() {

        switchCaidas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ilNCaidas.setVisibility(View.VISIBLE);
                    etNCaidas.setVisibility(View.VISIBLE);

                    tv_caidas_value.setText(R.string.SI);

                } else {
                    ilNCaidas.setVisibility(View.GONE);
                    etNCaidas.setVisibility(View.GONE);

                    tv_caidas_value.setText(R.string.NO);
                }
            }
        });

    }

    /**
     * Funcion encargada de configurar la logica del switch de jubilado legal
     */
    private void setJubilado() {

        switchJubiladoLegal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    tv_jubilado_value.setText(getString(R.string.SI));
                } else {
                    tv_jubilado_value.setText(getString(R.string.NO));
                }
            }
        });
    }

    /**
     * Validacion de campos del formulario
     *
     * @return True | False segun resultado
     */
    private boolean validarCampos() {

        int contador_errores = 0;

        //Comprobar nombre vacio
        if (Objects.requireNonNull(etNombre.getText()).toString().isEmpty()) {
            ilNombre.setErrorEnabled(true);
            ilNombre.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {
            ilNombre.setErrorEnabled(false);
        }

        //Comprobar apellido
        if (Objects.requireNonNull(etApellido.getText()).toString().isEmpty()) {
            ilApellido.setErrorEnabled(true);
            ilApellido.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {
            ilApellido.setErrorEnabled(false);
        }

        //Comprobar Sexo
        if (acSexo.getText().toString().isEmpty()) {
            ilSexo.setErrorEnabled(true);
            ilSexo.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {
            ilSexo.setErrorEnabled(false);
        }

        //Comprobar fecha nacimiento
        if (Objects.requireNonNull(etFechaNacimiento.getText()).toString().isEmpty()) {
            ilFechaNacimiento.setErrorEnabled(true);
            ilFechaNacimiento.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {
            ilFechaNacimiento.setErrorEnabled(false);
        }

        //Comprobar ciudad
        if (acCiudad.getText().toString().isEmpty()) {
            ilCiudad.setErrorEnabled(true);
            ilCiudad.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {
            ilCiudad.setErrorEnabled(false);
        }

        //Comprobar estado civil
        if (acEstadoCivil.getText().toString().isEmpty()) {
            ilEstadoCivil.setErrorEnabled(true);
            ilEstadoCivil.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {
            ilEstadoCivil.setErrorEnabled(false);
        }

        //Comprobar n_convivientes_3_meses
        if (Objects.requireNonNull(etNConvivientes.getText()).toString().isEmpty()) {
            ilNConvivientes.setErrorEnabled(true);
            ilNConvivientes.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {
            ilNConvivientes.setErrorEnabled(false);
        }


        if (switchCaidas.isChecked()) {

            if (TextUtils.isEmpty(etNCaidas.getText())) {
                ilNCaidas.setErrorEnabled(true);
                ilNCaidas.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
                contador_errores++;
            } else {
                ilNCaidas.setErrorEnabled(false);
            }
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
                        estadoCivilViewModel.refreshEstadosCiviles();
                        ciudadViewModel.refreshCiudades();
                        nivelEducacionalViewModel.refreshNivelesEduc();
                        tipoConvivenciaViewModel.refreshTipoConvivencia();
                        profesionViewModel.refreshProfesiones();

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

            if (validarCampos()) {

                progressBar.setVisibility(View.VISIBLE);

                //Recibir datos de formulario y crear objeto entrevistado
                Entrevistado entrevistado = new Entrevistado();

                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);
                int idInvestigador = sharedPreferences.getInt(getString(R.string.SHARED_PREF_INVES_ID), 0);

                entrevistado.setIdInvestigador(idInvestigador);
                entrevistado.setNombre(Objects.requireNonNull(etNombre.getText()).toString());
                entrevistado.setApellido(Objects.requireNonNull(etApellido.getText()).toString());
                entrevistado.setSexo(acSexo.getText().toString());

                Date fechaNac = Utils.stringToDate(getApplicationContext(), false, Objects.requireNonNull(etFechaNacimiento.getText()).toString());
                entrevistado.setFechaNacimiento(fechaNac);

                Ciudad ciudad = new Ciudad();
                ciudad.setNombre(acCiudad.getText().toString());
                entrevistado.setCiudad(ciudad);

                EstadoCivilRepositorio estadoCivilRepositorio = EstadoCivilRepositorio.getInstance(getApplication());
                EstadoCivil estadoCivil = new EstadoCivil();
                estadoCivil.setId(
                        estadoCivilRepositorio.buscarEstadoCivilPorNombre(
                                acEstadoCivil.getText().toString()
                        ).getId());
                entrevistado.setEstadoCivil(estadoCivil);

                entrevistado.setnConvivientes3Meses(Integer.parseInt(Objects.requireNonNull(etNConvivientes.getText()).toString()));

                entrevistado.setJubiladoLegal(switchJubiladoLegal.isChecked());
                entrevistado.setCaidas(switchCaidas.isChecked());
                if (switchCaidas.isChecked()) {
                    entrevistado.setNCaidas(Integer.parseInt(Objects.requireNonNull(etNCaidas.getText()).toString()));
                }

                if (!acNivelEducacional.getText().toString().isEmpty()) {

                    NivelEducacionalRepositorio nivelEducacionalRepositorio = NivelEducacionalRepositorio.getInstancia(getApplication());
                    NivelEducacional nivelEducacional = new NivelEducacional();
                    nivelEducacional.setId(
                            nivelEducacionalRepositorio.buscarNivelEducacionalPorNombre(
                                    acNivelEducacional.getText().toString()
                            ).getId());
                    entrevistado.setNivelEducacional(nivelEducacional);
                }

                if (!acProfesion.getText().toString().isEmpty()) {

                    Profesion profesion = new Profesion();
                    profesion.setNombre(acProfesion.getText().toString());
                    entrevistado.setProfesion(profesion);
                }

                if (!acTipoConvivencia.getText().toString().isEmpty()) {

                    TipoConvivenciaRepositorio tipoConvivenciaRepositorio = TipoConvivenciaRepositorio.getInstancia(getApplication());

                    TipoConvivencia tipoConvivencia = new TipoConvivencia();
                    tipoConvivencia.setId(
                            tipoConvivenciaRepositorio.buscarTipoConvivenciaPorNombre(
                                    acTipoConvivencia.getText().toString()
                            ).getId());
                    entrevistado.setTipoConvivencia(tipoConvivencia);
                }

                EntrevistadoRepositorio.getInstance(getApplication()).registrarEntrevistado(entrevistado);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
