package cl.udelvd.vistas.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;
import java.util.List;
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
import cl.udelvd.utilidades.SnackbarInterface;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.EditarEntrevistadoViewModel;

public class EditarEntrevistadoActivity extends AppCompatActivity implements SnackbarInterface {

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

    private EditarEntrevistadoViewModel editarEntrevistadoViewModel;

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
    private boolean isSnackBarShow = false;
    private boolean isAutoCompleteCiudadReady = false;
    private boolean isAutoCompleteEstadoCivilReady = false;
    private boolean isAutoCompleteProfesionReady = false;
    private boolean isAutoCompleteTipoConvivenciaReady = false;
    private boolean isAutoCompleteNivelEducacionalReady = false;
    private boolean isEntrevistadoReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_entrevistado);

        Utils.configurarToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITULO_TOOLBAR_EDITAR_ENTREVISTADO));

        instanciarRecursosInterfaz();

        obtenerDatosBundle();

        setSpinnerSexo();

        setPickerFechaNacimiento();

        iniciarViewModels();

        setCaidas();

    }

    private void iniciarViewModels() {
        setAutoCompleteCiudad();

        setAutoCompleteEstadoCivil();

        //Opcionales
        setAutoCompleteNivelEducacional();
        setAutoCompleteProfesion();
        setAutoCompleteTipoConvivencia();

        iniciarViewModelEntrevistado();
    }

    private void obtenerDatosBundle() {
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

        editarEntrevistadoViewModel = ViewModelProviders.of(this).get(EditarEntrevistadoViewModel.class);

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
     * Funcion encargada de configurar el picker de fechas
     */
    private void setPickerFechaNacimiento() {
        //OnClick
        etFechaNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.iniciarDatePicker(etFechaNacimiento, EditarEntrevistadoActivity.this);
            }
        });

        //EndIconOnClick
        ilFechaNacimiento.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.iniciarDatePicker(etFechaNacimiento, EditarEntrevistadoActivity.this);
            }
        });
    }

    /**
     * Funcion encargada de cargar las ciudades en el AutoComplete
     */
    private void setAutoCompleteCiudad() {

        editarEntrevistadoViewModel.isLoadingCiudades().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);

                    activarInputs(false);
                } else {
                    progressBar.setVisibility(View.GONE);

                    activarInputs(true);
                }
            }
        });

        //Cargar listado de ciudades
        editarEntrevistadoViewModel.cargarCiudades().observe(this, new Observer<List<Ciudad>>() {
            @Override
            public void onChanged(List<Ciudad> ciudads) {

                if (ciudads != null && ciudads.size() > 0) {

                    ciudadList = ciudads;
                    ciudadAdapter = new CiudadAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, ciudadList);
                    acCiudad.setAdapter(ciudadAdapter);

                    isAutoCompleteCiudadReady = true;

                    Log.d(getString(R.string.TAG_VIEW_MODEL_CIUDAD), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

                    progressBar.setVisibility(View.GONE);

                    ciudadAdapter.notifyDataSetChanged();

                    setearInfoEntrevistado();
                }

            }
        });

        //Cargar Errores de listado
        editarEntrevistadoViewModel.mostrarMsgErrorListadoCiudades().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.formulario_editar_entrevistado), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_CIUDAD), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    private void setAutoCompleteEstadoCivil() {

        editarEntrevistadoViewModel.isLoadingEstadosCiviles().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);

                    activarInputs(false);
                } else {
                    progressBar.setVisibility(View.GONE);

                    activarInputs(true);
                }
            }
        });

        //Cargar listado de estados civiles
        editarEntrevistadoViewModel.cargarEstadosCiviles().observe(this, new Observer<List<EstadoCivil>>() {
            @Override
            public void onChanged(List<EstadoCivil> estadoCivils) {

                if (estadoCivils != null && estadoCivils.size() > 0) {
                    estadoCivilList = estadoCivils;
                    estadoCivilAdapter = new EstadoCivilAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, estadoCivilList);
                    acEstadoCivil.setAdapter(estadoCivilAdapter);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_ESTADO_CIVIL), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

                    estadoCivilAdapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);

                    isAutoCompleteEstadoCivilReady = true;

                    setearInfoEntrevistado();
                }
            }
        });

        //ViewModel usado para detectar errores
        editarEntrevistadoViewModel.mostrarMsgErrorListadoEstadosCiviles().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.formulario_editar_entrevistado), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_ESTADO_CIVIL), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    private void setAutoCompleteNivelEducacional() {

        editarEntrevistadoViewModel.isLoadingNivelesEducacionales().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {

                    progressBar.setVisibility(View.VISIBLE);

                    activarInputs(false);
                } else {
                    progressBar.setVisibility(View.GONE);

                    activarInputs(true);
                }
            }
        });

        //Cargar listado de niveles educacionales
        editarEntrevistadoViewModel.cargarNivelesEducacionales().observe(this, new Observer<List<NivelEducacional>>() {
            @Override
            public void onChanged(List<NivelEducacional> nivelEducacionals) {

                if (nivelEducacionals != null && nivelEducacionals.size() > 0) {
                    nivelEducacionalList = nivelEducacionals;
                    nivelEducacionalAdapter = new NivelEducacionalAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, nivelEducacionalList);
                    acNivelEducacional.setAdapter(nivelEducacionalAdapter);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_NIVEL_EDUCACION), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

                    nivelEducacionalAdapter.notifyDataSetChanged();

                    isAutoCompleteNivelEducacionalReady = true;

                    progressBar.setVisibility(View.GONE);

                    setearInfoEntrevistado();
                }

            }
        });
        //Errores de niveles educacoanles
        editarEntrevistadoViewModel.mostrarMsgErrorNivelesEduc().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.formulario_editar_entrevistado), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_NIVEL_EDUCACION), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            }
        });
    }

    private void setAutoCompleteProfesion() {

        //Observer de loading de profesiones
        editarEntrevistadoViewModel.isLoadingProfesiones().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {

                    progressBar.setVisibility(View.VISIBLE);

                    activarInputs(false);
                } else {
                    progressBar.setVisibility(View.GONE);

                    activarInputs(true);
                }
            }
        });

        //Cargar listado de profesiones
        editarEntrevistadoViewModel.cargarProfesiones().observe(this, new Observer<List<Profesion>>() {
            @Override
            public void onChanged(List<Profesion> profesions) {
                if (profesions != null && profesions.size() > 0) {
                    profesionList = profesions;
                    profesionAdapter = new ProfesionAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, profesionList);
                    acProfesion.setAdapter(profesionAdapter);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_PROFESIONES), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

                    profesionAdapter.notifyDataSetChanged();

                    isAutoCompleteProfesionReady = true;

                    progressBar.setVisibility(View.GONE);

                    setearInfoEntrevistado();
                }

            }
        });
        //Observador de errores profesiones
        editarEntrevistadoViewModel.mostrarMsgErrorListadoProfesiones().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.formulario_editar_entrevistado), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_PROFESIONES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            }
        });
    }

    private void setAutoCompleteTipoConvivencia() {

        //Observer de loading tipos convivencia
        editarEntrevistadoViewModel.isLoadingTiposConvivencias().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);

                    activarInputs(false);
                } else {
                    progressBar.setVisibility(View.GONE);

                    activarInputs(true);
                }
            }
        });

        //Cargar listado de tipo de convivencia
        editarEntrevistadoViewModel.cargarTiposConvivencia().observe(this, new Observer<List<TipoConvivencia>>() {
            @Override
            public void onChanged(List<TipoConvivencia> list) {

                if (list != null && list.size() > 0) {
                    tipoConvivenciaList = list;
                    tipoConvivenciaAdapter = new TipoConvivenciaAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, tipoConvivenciaList);
                    acTipoConvivencia.setAdapter(tipoConvivenciaAdapter);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_CONVIVENCIA), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

                    tipoConvivenciaAdapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);

                    isAutoCompleteTipoConvivenciaReady = true;

                    setearInfoEntrevistado();
                }

            }
        });

        //Errores de tipo de convivencia
        editarEntrevistadoViewModel.mostrarMsgErrorListadoTiposConvivencias().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.formulario_editar_entrevistado), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_CONVIVENCIA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

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

        //Observer de loading entrevistado
        editarEntrevistadoViewModel.isLoadingEntrevistado().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);

                    activarInputs(false);
                } else {
                    progressBar.setVisibility(View.GONE);

                    activarInputs(true);
                }
            }
        });

        //Cargar datos entrevistado
        editarEntrevistadoViewModel.cargarEntrevistado(entrevistadoIntent).observe(this, new Observer<Entrevistado>() {
            @Override
            public void onChanged(Entrevistado entrevistadoInternet) {

                if (entrevistadoInternet != null) {
                    entrevistadoIntent = entrevistadoInternet;

                    progressBar.setVisibility(View.GONE);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), entrevistadoIntent.toString()));

                    isEntrevistadoReady = true;

                    setearInfoEntrevistado();
                }
            }

        });

        //Cargar mensaje de error
        editarEntrevistadoViewModel.mostrarMsgErrorEntrevistado().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.formulario_editar_entrevistado), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });

        //Cargar mensaje de actualizacion correcta
        editarEntrevistadoViewModel.mostrarMsgActualizacion().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                if (s.equals(getString(R.string.MSG_UPDATE_ENTREVISTADO))) {
                    Intent intent = getIntent();
                    intent.putExtra(getString(R.string.INTENT_KEY_MSG_ACTUALIZACION), s);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        editarEntrevistadoViewModel.mostrarMsgErrorActualizacion().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.formulario_editar_entrevistado), Snackbar.LENGTH_LONG, s, null);
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    /**
     * Funcion encargada de cargar datos del entrevistado en formulario de edicion
     */
    private void setearInfoEntrevistado() {

        if (isAutoCompleteCiudadReady && isAutoCompleteEstadoCivilReady && isAutoCompleteNivelEducacionalReady &&
                isAutoCompleteProfesionReady && isAutoCompleteTipoConvivenciaReady && isEntrevistadoReady) {

            etNombre.setText(entrevistadoIntent.getNombre());
            etApellido.setText(entrevistadoIntent.getApellido());

            if (entrevistadoIntent.getSexo().equals(getString(R.string.SEXO_MASCULINO))) {
                acSexo.setText(getString(R.string.SEXO_MASCULINO), false);
            } else if (entrevistadoIntent.getSexo().equals(getString(R.string.SEXO_FEMENINO))) {
                acSexo.setText(getString(R.string.SEXO_FEMENINO), false);
            } else if (entrevistadoIntent.getSexo().equals(getString(R.string.SEXO_OTRO))) {
                acSexo.setText(getString(R.string.SEXO_OTRO), false);
            }


            //Cargar fecha de nacimiento
            String fecha_nacimiento = Utils.dateToString(getApplicationContext(), false, entrevistadoIntent.getFechaNacimiento());
            etFechaNacimiento.setText(fecha_nacimiento);

            //Buscar ciudad por id en listado obtenido en setAutoCompleteCiudad()
            String nombreCiudad = Objects.requireNonNull(buscarCiudadPorId(entrevistadoIntent.getCiudad().getId())).getNombre();
            acCiudad.setText(nombreCiudad);

            //Buscar estado civil por id en listado obtenido en setAutoCompleteEstadoCivil()
            String nombreEstadoCivil = Objects.requireNonNull(buscarEstadoCivilPorId(entrevistadoIntent.getEstadoCivil().getId())).getNombre();
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
                String nombre = Objects.requireNonNull(buscarNivelEducacionalPorId(entrevistadoIntent.getNivelEducacional().getId())).getNombre();
                acNivelEducacional.setText(nombre, false);
            }

            if (entrevistadoIntent.getProfesion() != null) {
                String nombre = Objects.requireNonNull(buscarProfesionPorId(entrevistadoIntent.getProfesion().getId())).getNombre();
                acProfesion.setText(nombre);
            }

            if (entrevistadoIntent.getTipoConvivencia() != null) {
                String nombre = Objects.requireNonNull(buscarTipoConvivenciaPorId(entrevistadoIntent.getTipoConvivencia().getId())).getNombre();
                acTipoConvivencia.setText(nombre, false);
            }

            progressBar.setVisibility(View.GONE);

            isSnackBarShow = false;
            isAutoCompleteCiudadReady = false;
            isAutoCompleteEstadoCivilReady = false;
            isAutoCompleteProfesionReady = false;
            isAutoCompleteTipoConvivenciaReady = false;
            isAutoCompleteNivelEducacionalReady = false;
            isEntrevistadoReady = false;
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
            return true;
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

                Date fechaNac = Utils.stringToDate(getApplicationContext(), false, Objects.requireNonNull(etFechaNacimiento.getText()).toString());
                entrevistado.setFechaNacimiento(fechaNac);

                Ciudad ciudad = new Ciudad();
                ciudad.setNombre(acCiudad.getText().toString());
                entrevistado.setCiudad(ciudad);

                int id_estado_civil = Objects.requireNonNull(buscarEstadoCivilPorNombre(acEstadoCivil.getText().toString())).getId();
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
                    int id_nivel_educacional = Objects.requireNonNull(buscarNivelEducacionalPorNombre(acNivelEducacional.getText().toString())).getId();
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
                    int id_tipo_convivencia = Objects.requireNonNull(buscarTipoConvivenciaPorNombre(acTipoConvivencia.getText().toString())).getId();
                    TipoConvivencia tipoConvivencia = new TipoConvivencia();
                    tipoConvivencia.setId(id_tipo_convivencia);
                    entrevistado.setTipoConvivencia(tipoConvivencia);
                }

                EntrevistadoRepositorio.getInstance(getApplication()).actualizarEntrevistado(entrevistado);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showSnackbar(View v, int duration, String titulo, String accion) {

        Snackbar snackbar = Snackbar.make(v, titulo, duration);

        if (accion != null) {
            snackbar.setAction(accion, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    isSnackBarShow = false;
                    isAutoCompleteCiudadReady = false;
                    isAutoCompleteEstadoCivilReady = false;
                    isAutoCompleteProfesionReady = false;
                    isAutoCompleteTipoConvivenciaReady = false;
                    isAutoCompleteNivelEducacionalReady = false;
                    isEntrevistadoReady = false;

                    //Refresh info necesaria
                    editarEntrevistadoViewModel.refreshEstadosCiviles();
                    editarEntrevistadoViewModel.refreshCiudades();
                    editarEntrevistadoViewModel.refreshNivelesEduc();
                    editarEntrevistadoViewModel.refreshTipoConvivencia();
                    editarEntrevistadoViewModel.refreshProfesiones();

                    //Iniciar refesh del ultimo observer
                    editarEntrevistadoViewModel.refreshEntrevistado(entrevistadoIntent);

                    progressBar.setVisibility(View.VISIBLE);
                }
            });
        }
        snackbar.show();
        isSnackBarShow = false;
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
            } else if (etNCaidas.getText().toString().equals("0")) {
                ilNCaidas.setErrorEnabled(true);
                ilNCaidas.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO_CERO));
                contador_errores++;
            } else {
                ilNCaidas.setErrorEnabled(false);
            }
        }

        return contador_errores == 0;
    }

    private void activarInputs(boolean activado) {

        ilNombre.setEnabled(activado);
        etNombre.setEnabled(activado);

        ilApellido.setEnabled(activado);
        etApellido.setEnabled(activado);

        ilSexo.setEnabled(activado);
        acSexo.setEnabled(activado);

        ilFechaNacimiento.setEnabled(activado);
        etFechaNacimiento.setEnabled(activado);

        ilCiudad.setEnabled(activado);
        acCiudad.setEnabled(activado);

        ilEstadoCivil.setEnabled(activado);
        acEstadoCivil.setEnabled(activado);

        ilNConvivientes.setEnabled(activado);
        etNConvivientes.setEnabled(activado);

        switch_jubilado_legal.setEnabled(activado);

        switch_caidas.setEnabled(activado);

        ilNCaidas.setEnabled(activado);
        etNCaidas.setEnabled(activado);

        ilTipoConvivencia.setEnabled(activado);
        acTipoConvivencia.setEnabled(activado);

        ilProfesion.setEnabled(activado);
        acProfesion.setEnabled(activado);

        ilNivelEducacional.setEnabled(activado);
        acNivelEducacional.setEnabled(activado);
    }

    private TipoConvivencia buscarTipoConvivenciaPorId(int id) {

        for (int i = 0; i < tipoConvivenciaList.size(); i++) {
            if (tipoConvivenciaList.get(i).getId() == id) {
                return tipoConvivenciaList.get(i);
            }
        }
        return null;
    }

    private Profesion buscarProfesionPorId(int id) {
        for (int i = 0; i < profesionList.size(); i++) {
            if (profesionList.get(i).getId() == id) {
                return profesionList.get(i);
            }
        }

        return null;
    }

    private NivelEducacional buscarNivelEducacionalPorId(int id) {

        for (int i = 0; i < nivelEducacionalList.size(); i++) {
            if (nivelEducacionalList.get(i).getId() == id) {
                return nivelEducacionalList.get(i);
            }
        }
        return null;
    }

    private EstadoCivil buscarEstadoCivilPorId(int id) {
        for (int i = 0; i < estadoCivilList.size(); i++) {
            if (estadoCivilList.get(i).getId() == id) {
                return estadoCivilList.get(i);
            }
        }
        return null;
    }

    private Ciudad buscarCiudadPorId(int id) {

        for (int i = 0; i < ciudadList.size(); i++) {
            if (ciudadList.get(i).getId() == id) {
                return ciudadList.get(i);
            }
        }
        return null;
    }

    private NivelEducacional buscarNivelEducacionalPorNombre(String nombre) {
        for (int i = 0; i < nivelEducacionalList.size(); i++) {
            if (nivelEducacionalList.get(i).getNombre().equals(nombre)) {
                return nivelEducacionalList.get(i);
            }
        }
        return null;
    }

    private EstadoCivil buscarEstadoCivilPorNombre(String nombre) {
        for (int i = 0; i < estadoCivilList.size(); i++) {
            if (estadoCivilList.get(i).getNombre().equals(nombre)) {
                return estadoCivilList.get(i);
            }
        }
        return null;
    }

    private TipoConvivencia buscarTipoConvivenciaPorNombre(String nombre) {

        for (int i = 0; i < tipoConvivenciaList.size(); i++) {
            if (tipoConvivenciaList.get(i).getNombre().equals(nombre)) {
                return tipoConvivenciaList.get(i);
            }
        }
        return null;
    }
}
