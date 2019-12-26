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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;
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
import cl.udelvd.repositorios.EntrevistadoRepositorio;
import cl.udelvd.repositorios.EstadoCivilRepositorio;
import cl.udelvd.repositorios.NivelEducacionalRepositorio;
import cl.udelvd.repositorios.TipoConvivenciaRepositorio;
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

    public NuevoEntrevistadoActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_entrevistado);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorOnPrimary));
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        actionBar.setTitle("Crear entrevistado");

        instanciarRecursosInterfaz();

        iniciarViewModelObservers();

        setSpinnerSexo();

        setPickerFechaNacimiento();

        setCaidas();

        setJubilado();

    }

    private boolean validarCampos() {

        int contador_errores = 0;

        //Comprobar nombre vacio
        if (Objects.requireNonNull(etNombre.getText()).toString().isEmpty()) {
            ilNombre.setErrorEnabled(true);
            ilNombre.setError("Campo requerido");
            contador_errores++;
        } else {
            ilNombre.setErrorEnabled(false);
        }

        //Comprobar apellido
        if (Objects.requireNonNull(etApellido.getText()).toString().isEmpty()) {
            ilApellido.setErrorEnabled(true);
            ilApellido.setError("Campo requerido");
            contador_errores++;
        } else {
            ilApellido.setErrorEnabled(false);
        }

        //Comprobar Sexo
        if (acSexo.getText().toString().isEmpty()) {
            ilSexo.setErrorEnabled(true);
            ilSexo.setError("Campo requerido");
            contador_errores++;
        } else {
            ilSexo.setErrorEnabled(false);
        }

        //Comprobar fecha nacimiento
        if (Objects.requireNonNull(etFechaNacimiento.getText()).toString().isEmpty()) {
            ilFechaNacimiento.setErrorEnabled(true);
            ilFechaNacimiento.setError("Campo requerido");
            contador_errores++;
        } else {
            ilFechaNacimiento.setErrorEnabled(false);
        }

        //Comprobar ciudad
        if (acCiudad.getText().toString().isEmpty()) {
            ilCiudad.setErrorEnabled(true);
            ilCiudad.setError("Campo requerido");
            contador_errores++;
        } else {
            ilCiudad.setErrorEnabled(false);
        }

        //Comprobar estado civil
        if (acEstadoCivil.getText().toString().isEmpty()) {
            ilEstadoCivil.setErrorEnabled(true);
            ilEstadoCivil.setError("Campo requerido");
            contador_errores++;
        } else {
            ilEstadoCivil.setErrorEnabled(false);
        }

        //Comprobar n_convivientes_3_meses
        if (Objects.requireNonNull(etNConvivientes.getText()).toString().isEmpty()) {
            ilNConvivientes.setErrorEnabled(true);
            ilNConvivientes.setError("Campo requerido");
            contador_errores++;
        } else {
            ilNConvivientes.setErrorEnabled(false);
        }


        if (switchCaidas.isChecked()) {

            if (TextUtils.isEmpty(etNCaidas.getText())) {
                ilNCaidas.setErrorEnabled(true);
                ilNCaidas.setError("Campo requerido");
                contador_errores++;
            } else {
                ilNCaidas.setErrorEnabled(false);
            }
        }

        return contador_errores == 0;
    }

    /**
     * Funcion encargada de configurar la logica del switch de jubilado legal
     */
    private void setJubilado() {

        switchJubiladoLegal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    tv_jubilado_value.setText("Si");
                } else {
                    tv_jubilado_value.setText("No");
                }
            }
        });
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

                    tv_caidas_value.setText("Si");

                } else {
                    ilNCaidas.setVisibility(View.GONE);
                    etNCaidas.setVisibility(View.GONE);

                    tv_caidas_value.setText("No");
                }
            }
        });

    }

    /**
     * Funcion encargada de configurar el Spinner de sexo
     */
    private void setSpinnerSexo() {
        //Setear autocompletado Sexo
        String[] opcionesSexo = new String[]{"Masculino", "Femenino", "Otro"};
        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, opcionesSexo);
        acSexo.setAdapter(adapterSexo);
    }

    /**
     * Funcion encargada de inicializar observadores
     */
    private void iniciarViewModelObservers() {

        //Crear instancias
        ciudadViewModel = ViewModelProviders.of(this).get(CiudadViewModel.class);
        estadoCivilViewModel = ViewModelProviders.of(this).get(EstadoCivilViewModel.class);
        nivelEducacionalViewModel = ViewModelProviders.of(this).get(NivelEducacionalViewModel.class);
        tipoConvivenciaViewModel = ViewModelProviders.of(this).get(TipoConvivenciaViewModel.class);
        profesionViewModel = ViewModelProviders.of(this).get(ProfesionViewModel.class);
        entrevistadoViewModel = ViewModelProviders.of(this).get(EntrevistadoViewModel.class);

        //Observador de estados civiles
        estadoCivilViewModel.cargarEstadosCiviles().observe(this, new Observer<List<EstadoCivil>>() {
            @Override
            public void onChanged(List<EstadoCivil> estadoCivils) {

                if (estadoCivils != null) {
                    estadoCivilList = estadoCivils;
                    estadoCivilAdapter = new EstadoCivilAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, estadoCivilList);
                    acEstadoCivil.setAdapter(estadoCivilAdapter);

                    Log.d("VM_ESTADO_CIVIL", "Listado cargado");
                }

                estadoCivilAdapter.notifyDataSetChanged();
            }
        });

        //Observador de ciudades
        ciudadViewModel.cargarCiudades().observe(this, new Observer<List<Ciudad>>() {
            @Override
            public void onChanged(List<Ciudad> ciudads) {

                if (ciudads != null) {
                    ciudadList = ciudads;
                    ciudadAdapter = new CiudadAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, ciudadList);
                    acCiudad.setAdapter(ciudadAdapter);

                    Log.d("VM_CIUDAD", "Listado cargado");
                }
                ciudadAdapter.notifyDataSetChanged();
            }
        });

        //Observador de niveles educacionaes
        nivelEducacionalViewModel.cargarNivelesEduc().observe(this, new Observer<List<NivelEducacional>>() {
            @Override
            public void onChanged(List<NivelEducacional> nivelEducacionals) {
                if (nivelEducacionals != null) {
                    nivelEducacionalList = nivelEducacionals;
                    nivelEducacionalAdapter = new NivelEducacionalAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, nivelEducacionalList);
                    acNivelEducacional.setAdapter(nivelEducacionalAdapter);

                    Log.d("VM_NIVEL_EDUC", "Listado cargado");
                }
                nivelEducacionalAdapter.notifyDataSetChanged();
            }
        });

        //Observador de tipos de convivencias
        tipoConvivenciaViewModel.cargarTiposConvivencias().observe(this, new Observer<List<TipoConvivencia>>() {
            @Override
            public void onChanged(List<TipoConvivencia> list) {
                if (list != null) {
                    tipoConvivenciaList = list;
                    tipoConvivenciaAdapter = new TipoConvivenciaAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, tipoConvivenciaList);
                    acTipoConvivencia.setAdapter(tipoConvivenciaAdapter);

                    Log.d("VM_TIPO_CONVIVENCIA", "Listado cargado");
                }
                tipoConvivenciaAdapter.notifyDataSetChanged();
            }
        });

        //Observador de profesiones
        profesionViewModel.cargarProfesiones().observe(this, new Observer<List<Profesion>>() {
            @Override
            public void onChanged(List<Profesion> profesions) {
                if (profesions != null) {
                    profesionList = profesions;
                    profesionAdapter = new ProfesionAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, profesionList);
                    acProfesion.setAdapter(profesionAdapter);

                    Log.d("VM_PROFESIONES", "Listado cargado");
                }
                profesionAdapter.notifyDataSetChanged();
            }
        });

        //Observador para mensajes creacion entrevistado
        entrevistadoViewModel.mostrarRespuestaRegistro().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                Log.d("VM_NEW_ENTREVISTADO", "MSG_RESPONSE: " + s);

                //Si el registro fue correcto cerrar la actividad
                if (s.equals("¡Entrevistado registrado!")) {
                    finish();
                }
            }
        });

        //Observador para mensajes de error de registro entrevistado
        entrevistadoViewModel.mostrarErrorRespuesta().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                Log.d("VM_NEW_ENTREVISTADO", "MSG_ERROR: " + s);
            }
        });
    }

    /**
     * Instanciacion de Edittexts
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

    }

    private void setPickerFechaNacimiento() {
        //OnClick
        etFechaNacimiento.setOnClickListener(new View.OnClickListener() {
            int year;
            int month;
            int day;

            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                if (Objects.requireNonNull(etFechaNacimiento.getText()).length() > 0) {

                    String fecha = etFechaNacimiento.getText().toString();
                    String[] fecha_split = fecha.split("-");

                    year = Integer.parseInt(fecha_split[0]);
                    month = Integer.parseInt(fecha_split[1]);
                    day = Integer.parseInt(fecha_split[2]);
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(NuevoEntrevistadoActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month += 1;
                        etFechaNacimiento.setText(year + "-" + month + "-" + dayOfMonth);
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });

        //EndIconOnClick
        ilFechaNacimiento.setEndIconOnClickListener(new View.OnClickListener() {

            int year;
            int month;
            int day;

            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                if (Objects.requireNonNull(etFechaNacimiento.getText()).length() > 0) {

                    String fecha = etFechaNacimiento.getText().toString();
                    String[] fecha_split = fecha.split("-");

                    year = Integer.parseInt(fecha_split[0]);
                    month = Integer.parseInt(fecha_split[1]);
                    day = Integer.parseInt(fecha_split[2]);
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(NuevoEntrevistadoActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month += 1;
                        etFechaNacimiento.setText(year + "-" + month + "-" + dayOfMonth);
                    }
                }, year, month, day);

                datePickerDialog.show();
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
        } else if (item.getItemId() == R.id.menu_guardar) {

            if (validarCampos()) {

                progressBar.setVisibility(View.VISIBLE);

                //Recibir datos de formulario y crear objeto entrevistado
                Entrevistado entrevistado = new Entrevistado();

                SharedPreferences sharedPreferences = getSharedPreferences("udelvd", Context.MODE_PRIVATE);
                int idInvestigador = sharedPreferences.getInt("id_investigador", 0);

                entrevistado.setIdInvestigador(idInvestigador);
                entrevistado.setNombre(Objects.requireNonNull(etNombre.getText()).toString());
                entrevistado.setApellido(Objects.requireNonNull(etApellido.getText()).toString());
                entrevistado.setSexo(acSexo.getText().toString());

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date fechaNac = null;
                try {
                    fechaNac = simpleDateFormat.parse(Objects.requireNonNull(etFechaNacimiento.getText()).toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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


                EntrevistadoRepositorio entrevistadoRepositorio = EntrevistadoRepositorio.getInstance(getApplication());
                entrevistadoRepositorio.registrarEntrevistado(entrevistado);
                Log.d("ENTREVISTADO_CREATE", entrevistado.toString());
            }
        }


        return super.onOptionsItemSelected(item);
    }
}
