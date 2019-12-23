package cl.udelvd.vistas.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import cl.udelvd.repositorios.EstadoCivilRepositorio;
import cl.udelvd.repositorios.NivelEducacionalRepositorio;
import cl.udelvd.repositorios.ProfesionRepositorio;
import cl.udelvd.repositorios.TipoConvivenciaRepositorio;
import cl.udelvd.viewmodel.CiudadViewModel;
import cl.udelvd.viewmodel.EntrevistadoViewModel;
import cl.udelvd.viewmodel.EstadoCivilViewModel;
import cl.udelvd.viewmodel.NivelEducacionalViewModel;
import cl.udelvd.viewmodel.ProfesionViewModel;
import cl.udelvd.viewmodel.TipoConvivenciaViewModel;

public class EditarEntrevistadoActivity extends AppCompatActivity {

    private Entrevistado entrevistado;

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

    private SwitchMaterial switch_jubilado_legal;
    private SwitchMaterial switch_caidas;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorOnPrimary));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        actionBar.setTitle("Editar entrevistado");

        instanciarRecursosInterfaz();

        setSpinnerSexo();

        setPickerFechaNacimiento();

        setAutoCompleteCiudad();

        setAutoCompleteEstadoCivil();

        //Opcionales
        setAutoCompleteNivelEducacional();
        setAutoCompleteProfesion();
        setAutoCompleteTipoConvivencia();

        Bundle bundle = getIntent().getExtras();

        assert bundle != null;
        int id_entrevistado = bundle.getInt("id_entrevistado");
        entrevistado = new Entrevistado();
        entrevistado.setId(id_entrevistado);

        iniciarViewModelEntrevistado();

    }

    /**
     * Funcion encargada de instanciar objetos usados en interfaz
     */
    private void instanciarRecursosInterfaz() {

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

        etNombre = findViewById(R.id.et_nombre_entrevistado);
        etApellido = findViewById(R.id.et_apellido_entrevistado);
        etFechaNacimiento = findViewById(R.id.et_fecha_nacimiento);
        etNConvivientes = findViewById(R.id.et_n_convivientes_entrevistado);

        acSexo = findViewById(R.id.et_sexo_entrevistado);
        acCiudad = findViewById(R.id.et_ciudad_entrevistado);
        acEstadoCivil = findViewById(R.id.et_estado_civil_entrevistado);
        acNivelEducacional = findViewById(R.id.et_nivel_educacional_entrevistado);
        acProfesion = findViewById(R.id.et_profesion_entrevistado);
        acTipoConvivencia = findViewById(R.id.et_tipo_convivencia_entrevistado);

        switch_jubilado_legal = findViewById(R.id.switch_jubilado_legal);
        switch_caidas = findViewById(R.id.switch_caidas_entrevistado);


    }

    /**
     * Funcion encargada de configurar el Spinner de sexo
     */
    private void setSpinnerSexo() {
        //Setear autocompletado Sexo
        String[] opcionesSexo = new String[]{"Masculino", "Femenino", "Otro"};
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditarEntrevistadoActivity.this, new DatePickerDialog.OnDateSetListener() {
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditarEntrevistadoActivity.this, new DatePickerDialog.OnDateSetListener() {
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

                    Log.d("VM_CIUDAD", "Listado cargado");
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

                    Log.d("VM_ESTADO_CIVIL", "Listado cargado");
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

                    Log.d("VM_NIVEL_EDUC", "Listado cargado");
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

                    Log.d("VM_PROFESION", "Listado cargado");
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

                    Log.d("VM_TIPO_CONVIVENCIA", "Listado cargado");
                }
                tipoConvivenciaAdapter.notifyDataSetChanged();
            }
        });
    }

    private void iniciarViewModelEntrevistado() {

        entrevistadoViewModel = ViewModelProviders.of(this).get(EntrevistadoViewModel.class);

        entrevistadoViewModel.mostrarEntrevistado(entrevistado).observe(this, new Observer<Entrevistado>() {
            @Override
            public void onChanged(Entrevistado entrevistadoInternet) {
                entrevistado = entrevistadoInternet;
                Log.d("VM_ENTREVISTADO", entrevistadoInternet.toString());

                //TODO: Verificar booleanos
                if (isSpinnerSexoReady && isAutoCompleteCiudadReady && isEstadoCivilReady) {
                    setearInfoEntrevistado();
                } else {
                    Log.d("VM_ENTREVISTADO", "NO LISTO");
                }
            }
        });

    }

    /**
     * Funcion encargada de cargar datos del entrevistado en formulario de edicion
     */
    private void setearInfoEntrevistado() {
        etNombre.setText(entrevistado.getNombre());
        etApellido.setText(entrevistado.getApellido());

        acSexo.setText(entrevistado.getSexo(), false);

        //Cargar fecha de nacimiento
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String fecha_nacimiento = simpleDateFormat.format(entrevistado.getFechaNacimiento());
        etFechaNacimiento.setText(fecha_nacimiento);

        //Buscar ciudad por id en listado obtenido en setAutoCompleteCiudad()
        CiudadRepositorio ciudadRepositorio = CiudadRepositorio.getInstancia(getApplication());
        String nombreCiudad = ciudadRepositorio.buscarCiudadPorId(entrevistado.getCiudad().getId()).getNombre();
        acCiudad.setText(nombreCiudad);

        //Buscar estado civil por id en listado obtenido en setAutoCompleteEstadoCivil()
        EstadoCivilRepositorio estadoCivilRepositorio = EstadoCivilRepositorio.getInstance(getApplication());
        String nombreEstadoCivil = estadoCivilRepositorio.buscarEstadoCivilPorId(entrevistado.getEstadoCivil().getId()).getNombre();
        acEstadoCivil.setText(nombreEstadoCivil);

        etNConvivientes.setText(String.valueOf(entrevistado.getNConvivientes3Meses()));

        if (entrevistado.isJubiladoLegal()) {
            switch_jubilado_legal.setChecked(true);
        } else {
            switch_jubilado_legal.setChecked(false);
        }

        if (entrevistado.isCaidas()) {
            switch_caidas.setChecked(true);
        } else {
            switch_caidas.setChecked(false);
        }

        //OPCIONALES
        if (entrevistado.getNivelEducacional() != null) {
            NivelEducacionalRepositorio nivelEducacionalRepositorio = NivelEducacionalRepositorio.getInstancia(getApplication());
            String nombre = nivelEducacionalRepositorio.buscarNivelEducacionalPorId(entrevistado.getNivelEducacional().getId()).getNombre();
            acNivelEducacional.setText(nombre);
        }

        if (entrevistado.getProfesion() != null) {
            ProfesionRepositorio profesionRepositorio = ProfesionRepositorio.getInstancia(getApplication());
            String nombre = profesionRepositorio.buscarProfesionPorId(entrevistado.getProfesion().getId()).getNombre();
            acProfesion.setText(nombre);
        }

        if (entrevistado.getTipoConvivencia() != null) {
            TipoConvivenciaRepositorio tipoConvivenciaRepositorio = TipoConvivenciaRepositorio.getInstancia(getApplication());
            String nombre = tipoConvivenciaRepositorio.buscarTipoConvivenciaPorId(entrevistado.getTipoConvivencia().getId()).getNombre();
            acTipoConvivencia.setText(nombre);
        }
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
        }

        return super.onOptionsItemSelected(item);
    }
}
