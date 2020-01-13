package cl.udelvd.vistas.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.google.android.material.switchmaterial.SwitchMaterial;
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
import cl.udelvd.repositorios.CiudadRepositorio;
import cl.udelvd.repositorios.EntrevistadoRepositorio;
import cl.udelvd.repositorios.EstadoCivilRepositorio;
import cl.udelvd.repositorios.NivelEducacionalRepositorio;
import cl.udelvd.repositorios.ProfesionRepositorio;
import cl.udelvd.repositorios.TipoConvivenciaRepositorio;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.CiudadViewModel;
import cl.udelvd.viewmodel.EntrevistadoViewModel;
import cl.udelvd.viewmodel.EstadoCivilViewModel;
import cl.udelvd.viewmodel.NivelEducacionalViewModel;
import cl.udelvd.viewmodel.ProfesionViewModel;
import cl.udelvd.viewmodel.TipoConvivenciaViewModel;

public class EditarEntrevistadoActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    private Entrevistado entrevistadoIntent;

    private TextInputLayout ilNombre;
    private TextInputLayout ilApellido;
    private TextInputLayout ilSexo;
    private TextInputLayout ilFechaNacimiento;
    private TextInputLayout ilCiudad;
    private TextInputLayout ilEstadoCivil;
    private TextInputLayout ilNConvivientes;
    private TextInputLayout ilNivelEducacional;
    private TextInputLayout ilProfesion;
    private TextInputLayout ilTipoConvivencia;
    private TextInputLayout ilNCaidas;

    private TextInputEditText etNombre;
    private TextInputEditText etApellido;
    private TextInputEditText etFechaNacimiento;
    private AppCompatAutoCompleteTextView acSexo;
    private AppCompatAutoCompleteTextView acCiudad;
    private AppCompatAutoCompleteTextView acEstadoCivil;
    private TextInputEditText etNConvivientes;
    private AppCompatAutoCompleteTextView acNivelEducacional;
    private AppCompatAutoCompleteTextView acProfesion;
    private AppCompatAutoCompleteTextView acTipoConvivencia;
    private TextInputEditText etNCaidas;

    private SwitchMaterial switch_jubilado_legal;
    private SwitchMaterial switch_caidas;

    private TextView tv_switch_caidas;
    private TextView tv_switch_jubilado;

    //ViewModel
    private EntrevistadoViewModel entrevistadoViewModel;
    private CiudadViewModel ciudadViewModel;
    private EstadoCivilViewModel estadoCivilViewModel;
    private NivelEducacionalViewModel nivelEducacionalViewModel;
    private ProfesionViewModel profesionViewModel;
    private TipoConvivenciaViewModel tipoConvivenciaViewModel;

    //Listados
    private List<Ciudad> ciudadList;
    private List<EstadoCivil> estadoCivilList;
    private List<NivelEducacional> nivelEducacionalList;
    private List<Profesion> profesionList;
    private List<TipoConvivencia> tipoConvivenciaList;

    //Adaprters
    private CiudadAdapter ciudadAdapter;
    private EstadoCivilAdapter estadoCivilAdapter;
    private NivelEducacionalAdapter nivelEducacionalAdapter;
    private ProfesionAdapter profesionAdapter;
    private TipoConvivenciaAdapter tipoConvivenciaAdapter;

    //Boleanos de seguridad
    private boolean isSpinnerSexoReady = false;
    private boolean isAutoCompleteCiudadReady = false;
    private boolean isEstadoCivilReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_entrevistado);

        Utils.configurarToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITULO_TOOLBAR_EDITAR_ENTREVISTADO));

        instanciarRecursosInterfaz();

        obtenerBundle();

        setSpinnerSexo();

        setPickerFechaNacimiento();

        setAutoCompleteCiudad();

        setAutoCompleteEstadoCivil();

        setCaidas();

        //Opcionales
        setAutoCompleteNivelEducacional();
        setAutoCompleteProfesion();
        setAutoCompleteTipoConvivencia();

        iniciarViewModelEntrevistado();

    }

    private void obtenerBundle() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            int id_entrevistado = bundle.getInt(getString(R.string.KEY_ENTREVISTADO_ID_LARGO));
            entrevistadoIntent = new Entrevistado();
            entrevistadoIntent.setId(id_entrevistado);
        }
    }

    /**
     * Funcion encargada de instanciar objetos usados en interfaz
     */
    private void instanciarRecursosInterfaz() {

        progressBar = findViewById(R.id.progress_horizontal_editar_entrevistado);
        progressBar.setVisibility(View.VISIBLE);

        ilNombre = findViewById(R.id.il_nombre_entrevistado);
        ilApellido = findViewById(R.id.il_apellido_entrevistado);
        ilSexo = findViewById(R.id.il_sexo_entrevistado);
        ilFechaNacimiento = findViewById(R.id.il_fecha_nacimiento);
        ilCiudad = findViewById(R.id.il_ciudad_entrevistado);
        ilEstadoCivil = findViewById(R.id.il_estado_civil_entrevistado);
        ilNConvivientes = findViewById(R.id.il_n_convivientes_entrevistado);
        ilNivelEducacional = findViewById(R.id.il_nivel_educacional_entrevistado);
        ilProfesion = findViewById(R.id.il_profesion_entrevistado);
        ilTipoConvivencia = findViewById(R.id.il_tipo_convivencia_entrevistado);
        ilNCaidas = findViewById(R.id.il_n_caidas_entrevistado);

        etNombre = findViewById(R.id.et_nombre_entrevistado);
        etApellido = findViewById(R.id.et_apellido_entrevistado);
        etFechaNacimiento = findViewById(R.id.et_fecha_nacimiento);
        etNConvivientes = findViewById(R.id.et_n_convivientes_entrevistado);
        etNCaidas = findViewById(R.id.et_n_caidas_entrevistado);

        acSexo = findViewById(R.id.et_sexo_entrevistado);
        acCiudad = findViewById(R.id.et_ciudad_entrevistado);
        acEstadoCivil = findViewById(R.id.et_estado_civil_entrevistado);
        acNivelEducacional = findViewById(R.id.et_nivel_educacional_entrevistado);
        acProfesion = findViewById(R.id.et_profesion_entrevistado);
        acTipoConvivencia = findViewById(R.id.et_tipo_convivencia_entrevistado);

        switch_jubilado_legal = findViewById(R.id.switch_jubilado_legal);
        switch_caidas = findViewById(R.id.switch_caidas_entrevistado);

        tv_switch_caidas = findViewById(R.id.tv_switch_caidas);
        tv_switch_jubilado = findViewById(R.id.tv_switch_jubilado_value);

    }

    /**
     * Funcion encargada de configurar el Spinner de sexo
     */
    private void setSpinnerSexo() {
        //Setear autocompletado Sexo
        String[] opcionesSexo = new String[]{getString(R.string.SEXO_MASCULINO), getString(R.string.SEXO_FEMENINO), getString(R.string.SEXO_OTRO)};
        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, opcionesSexo);
        acSexo.setAdapter(adapterSexo);

        isSpinnerSexoReady = true;
    }

    /**
     * Funcion encargada de configurar el picker de fechas
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(EditarEntrevistadoActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                etFechaNacimiento.setText(String.format(Locale.US, "%d-%d-%d", year, month, dayOfMonth));
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    /**
     * Funcion encargada de cargar las ciudades en el AutoComplete
     */
    private void setAutoCompleteCiudad() {
        ciudadViewModel = ViewModelProviders.of(this).get(CiudadViewModel.class);

        ciudadViewModel.cargarCiudades().observe(this, new Observer<List<Ciudad>>() {
            @Override
            public void onChanged(List<Ciudad> ciudads) {
                if (ciudads != null) {
                    ciudadList = ciudads;
                    ciudadAdapter = new CiudadAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, ciudadList);
                    acCiudad.setAdapter(ciudadAdapter);

                    isAutoCompleteCiudadReady = true;

                    Log.d(getString(R.string.TAG_VIEW_MODEL_CIUDAD), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                }
                ciudadAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setAutoCompleteEstadoCivil() {

        estadoCivilViewModel = ViewModelProviders.of(this).get(EstadoCivilViewModel.class);

        estadoCivilViewModel.cargarEstadosCiviles().observe(this, new Observer<List<EstadoCivil>>() {
            @Override
            public void onChanged(List<EstadoCivil> estadoCivils) {

                if (estadoCivils != null) {
                    estadoCivilList = estadoCivils;
                    estadoCivilAdapter = new EstadoCivilAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, estadoCivilList);
                    acEstadoCivil.setAdapter(estadoCivilAdapter);

                    isEstadoCivilReady = true;

                    Log.d(getString(R.string.TAG_VIEW_MODEL_ESTADO_CIVIL), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                }

                estadoCivilAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setAutoCompleteNivelEducacional() {
        nivelEducacionalViewModel = ViewModelProviders.of(this).get(NivelEducacionalViewModel.class);

        nivelEducacionalViewModel.cargarNivelesEduc().observe(this, new Observer<List<NivelEducacional>>() {
            @Override
            public void onChanged(List<NivelEducacional> nivelEducacionals) {
                if (nivelEducacionals != null) {
                    nivelEducacionalList = nivelEducacionals;
                    nivelEducacionalAdapter = new NivelEducacionalAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, nivelEducacionalList);
                    acNivelEducacional.setAdapter(nivelEducacionalAdapter);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_NIVEL_EDUCACION), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                }
                nivelEducacionalAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setAutoCompleteProfesion() {

        profesionViewModel = ViewModelProviders.of(this).get(ProfesionViewModel.class);

        profesionViewModel.cargarProfesiones().observe(this, new Observer<List<Profesion>>() {
            @Override
            public void onChanged(List<Profesion> profesions) {
                if (profesions != null) {
                    profesionList = profesions;
                    profesionAdapter = new ProfesionAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, profesionList);
                    acProfesion.setAdapter(profesionAdapter);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_PROFESIONES), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                }
                profesionAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setAutoCompleteTipoConvivencia() {

        tipoConvivenciaViewModel = ViewModelProviders.of(this).get(TipoConvivenciaViewModel.class);

        tipoConvivenciaViewModel.cargarTiposConvivencias().observe(this, new Observer<List<TipoConvivencia>>() {
            @Override
            public void onChanged(List<TipoConvivencia> list) {
                if (list != null) {
                    tipoConvivenciaList = list;
                    tipoConvivenciaAdapter = new TipoConvivenciaAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, tipoConvivenciaList);
                    acTipoConvivencia.setAdapter(tipoConvivenciaAdapter);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_CONVIVENCIA), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                }
                tipoConvivenciaAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Funcion encargada de configurar la logica del switch de caidas
     */
    private void setCaidas() {

        switch_caidas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ilNCaidas.setVisibility(View.VISIBLE);
                    etNCaidas.setVisibility(View.VISIBLE);

                    tv_switch_caidas.setText(getString(R.string.SI));

                } else {
                    ilNCaidas.setVisibility(View.GONE);
                    etNCaidas.setVisibility(View.GONE);

                    tv_switch_caidas.setText(getString(R.string.NO));
                }
            }
        });

    }

    /**
     * Funcion que reune las instancias de observadores viewModel
     */
    private void iniciarViewModelEntrevistado() {

        entrevistadoViewModel = ViewModelProviders.of(this).get(EntrevistadoViewModel.class);

        entrevistadoViewModel.mostrarEntrevistado(entrevistadoIntent).observe(this, new Observer<Entrevistado>() {
            @Override
            public void onChanged(Entrevistado entrevistadoInternet) {
                entrevistadoIntent = entrevistadoInternet;

                if (isSpinnerSexoReady && isAutoCompleteCiudadReady && isEstadoCivilReady) {
                    setearInfoEntrevistado();
                }
            }
        });

        entrevistadoViewModel.mostrarErrorRespuesta().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });

        entrevistadoViewModel.mostrarRespuestaActualizacion().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

                if (s.equals(getString(R.string.MSG_UPDATE_ENTREVISTADO))) {
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                    //Cerrar activity
                    finish();
                }
            }
        });


    }

    /**
     * Funcion encargada de cargar datos del entrevistado en formulario de edicion
     */
    private void setearInfoEntrevistado() {
        etNombre.setText(entrevistadoIntent.getNombre());
        etApellido.setText(entrevistadoIntent.getApellido());

        acSexo.setText(entrevistadoIntent.getSexo(), false);

        //Cargar fecha de nacimiento
        String fecha_nacimiento = Utils.dateToString(getApplicationContext(), entrevistadoIntent.getFechaNacimiento());
        etFechaNacimiento.setText(fecha_nacimiento);

        //Buscar ciudad por id en listado obtenido en setAutoCompleteCiudad()
        CiudadRepositorio ciudadRepositorio = CiudadRepositorio.getInstancia(getApplication());
        String nombreCiudad = ciudadRepositorio.buscarCiudadPorId(entrevistadoIntent.getCiudad().getId()).getNombre();
        acCiudad.setText(nombreCiudad);

        //Buscar estado civil por id en listado obtenido en setAutoCompleteEstadoCivil()
        EstadoCivilRepositorio estadoCivilRepositorio = EstadoCivilRepositorio.getInstance(getApplication());
        String nombreEstadoCivil = estadoCivilRepositorio.buscarEstadoCivilPorId(entrevistadoIntent.getEstadoCivil().getId()).getNombre();
        acEstadoCivil.setText(nombreEstadoCivil, false);

        etNConvivientes.setText(String.valueOf(entrevistadoIntent.getNConvivientes3Meses()));

        if (entrevistadoIntent.isJubiladoLegal()) {
            switch_jubilado_legal.setChecked(true);
            tv_switch_jubilado.setText(getString(R.string.SI));
        } else {
            switch_jubilado_legal.setChecked(false);
            tv_switch_jubilado.setText(getString(R.string.NO));
        }

        if (entrevistadoIntent.isCaidas()) {
            switch_caidas.setChecked(true);
            etNCaidas.setVisibility(View.VISIBLE);
            ilNCaidas.setVisibility(View.VISIBLE);
            etNCaidas.setText(String.valueOf(entrevistadoIntent.getNCaidas()));
            tv_switch_caidas.setText(getString(R.string.SI));
        } else {
            switch_caidas.setChecked(false);
            etNCaidas.setVisibility(View.GONE);
            ilNCaidas.setVisibility(View.GONE);
            tv_switch_caidas.setText(getString(R.string.NO));
        }

        //OPCIONALES
        if (entrevistadoIntent.getNivelEducacional() != null) {
            NivelEducacionalRepositorio nivelEducacionalRepositorio = NivelEducacionalRepositorio.getInstancia(getApplication());
            String nombre = nivelEducacionalRepositorio.buscarNivelEducacionalPorId(entrevistadoIntent.getNivelEducacional().getId()).getNombre();
            acNivelEducacional.setText(nombre, false);
        }

        if (entrevistadoIntent.getProfesion() != null) {
            ProfesionRepositorio profesionRepositorio = ProfesionRepositorio.getInstancia(getApplication());
            String nombre = profesionRepositorio.buscarProfesionPorId(entrevistadoIntent.getProfesion().getId()).getNombre();
            acProfesion.setText(nombre);
        }

        if (entrevistadoIntent.getTipoConvivencia() != null) {
            TipoConvivenciaRepositorio tipoConvivenciaRepositorio = TipoConvivenciaRepositorio.getInstancia(getApplication());
            String nombre = tipoConvivenciaRepositorio.buscarTipoConvivenciaPorId(entrevistadoIntent.getTipoConvivencia().getId()).getNombre();
            acTipoConvivencia.setText(nombre, false);
        }

        progressBar.setVisibility(View.GONE);
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
        } else if (item.getItemId() == R.id.menu_guardar) {

            if (validarCampos()) {

                progressBar.setVisibility(View.VISIBLE);

                Entrevistado entrevistado = new Entrevistado();

                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);
                int id_investigador = sharedPreferences.getInt(getString(R.string.KEY_ENTREVISTADO_ID_INVESTIGADOR), 0);

                entrevistado.setId(entrevistadoIntent.getId());
                entrevistado.setIdInvestigador(id_investigador);
                entrevistado.setNombre(Objects.requireNonNull(etNombre.getText()).toString());
                entrevistado.setApellido(Objects.requireNonNull(etApellido.getText()).toString());
                entrevistado.setSexo(acSexo.getText().toString());

                Date fechaNac = Utils.stringToDate(getApplicationContext(), Objects.requireNonNull(etFechaNacimiento.getText()).toString());
                entrevistado.setFechaNacimiento(fechaNac);

                Ciudad ciudad = new Ciudad();
                ciudad.setNombre(acCiudad.getText().toString());
                entrevistado.setCiudad(ciudad);

                EstadoCivilRepositorio estadoCivilRepositorio = EstadoCivilRepositorio.getInstance(getApplication());
                int id_estado_civil = estadoCivilRepositorio.buscarEstadoCivilPorNombre(acEstadoCivil.getText().toString()).getId();
                EstadoCivil estadoCivil = new EstadoCivil();
                estadoCivil.setId(id_estado_civil);
                entrevistado.setEstadoCivil(estadoCivil);

                entrevistado.setnConvivientes3Meses(Integer.parseInt(Objects.requireNonNull(etNConvivientes.getText()).toString()));

                if (switch_jubilado_legal.isChecked()) {
                    entrevistado.setJubiladoLegal(true);
                } else {
                    entrevistado.setJubiladoLegal(false);
                }

                if (switch_caidas.isChecked()) {
                    entrevistado.setCaidas(true);

                    entrevistado.setNCaidas(Integer.parseInt(Objects.requireNonNull(etNCaidas.getText()).toString()));
                } else {
                    entrevistado.setCaidas(false);
                }

                if (!acNivelEducacional.getText().toString().isEmpty()) {
                    NivelEducacionalRepositorio nivelEducacionalRepositorio = NivelEducacionalRepositorio.getInstancia(getApplication());
                    int id_nivel_educacional = nivelEducacionalRepositorio.buscarNivelEducacionalPorNombre(acNivelEducacional.getText().toString()).getId();
                    NivelEducacional nivelEducacional = new NivelEducacional();
                    nivelEducacional.setId(id_nivel_educacional);
                    entrevistado.setNivelEducacional(nivelEducacional);
                }

                if (!acProfesion.getText().toString().isEmpty()) {
                    Profesion profesion = new Profesion();
                    profesion.setNombre(acProfesion.getText().toString());
                    entrevistado.setProfesion(profesion);
                }

                if (!acTipoConvivencia.getText().toString().isEmpty()) {
                    TipoConvivenciaRepositorio tipoConvivenciaRepositorio = TipoConvivenciaRepositorio.getInstancia(getApplication());
                    int id_tipo_convivencia = tipoConvivenciaRepositorio.buscarTipoConvivenciaPorNombre(acTipoConvivencia.getText().toString()).getId();
                    TipoConvivencia tipoConvivencia = new TipoConvivencia();
                    tipoConvivencia.setId(id_tipo_convivencia);
                    entrevistado.setTipoConvivencia(tipoConvivencia);
                }

                EntrevistadoRepositorio entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
                entrevistadoRepositorio.actualizarEntrevistado(entrevistado);
            }
        }

        return super.onOptionsItemSelected(item);
    }

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


        //Comprobar sexo
        if (acSexo.getText().toString().isEmpty()) {
            ilSexo.setErrorEnabled(true);
            ilSexo.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {
            ilSexo.setErrorEnabled(false);
        }

        //Comprobar Fecha nacimiento
        if (Objects.requireNonNull(etFechaNacimiento.getText()).toString().isEmpty()) {
            ilFechaNacimiento.setErrorEnabled(true);
            ilFechaNacimiento.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            contador_errores++;
        } else {
            ilFechaNacimiento.setErrorEnabled(false);
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

        if (switch_caidas.isChecked()) {

            if (Objects.requireNonNull(etNCaidas.getText()).toString().isEmpty()) {
                ilNCaidas.setErrorEnabled(true);
                ilNCaidas.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
                contador_errores++;
            } else {
                ilNCaidas.setErrorEnabled(false);
            }
        }

        return contador_errores == 0;
    }
}
