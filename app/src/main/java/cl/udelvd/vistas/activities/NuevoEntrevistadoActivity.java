package cl.udelvd.vistas.activities;

import android.content.Context;
import android.content.Intent;
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
import cl.udelvd.viewmodel.NuevoEntrevistadoViewModel;

public class NuevoEntrevistadoActivity extends AppCompatActivity implements SnackbarInterface {

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

    //Opcionales
    private TextInputLayout ilTipoConvivencia;
    private TextInputLayout ilProfesion;
    private TextInputLayout ilNivelEducacional;

    private TextInputEditText etNombre;
    private TextInputEditText etApellido;
    private AppCompatAutoCompleteTextView acSexo;
    private TextInputEditText etFechaNacimiento;
    private TextInputEditText etNConvivientes;
    private AppCompatAutoCompleteTextView acCiudad;
    private AppCompatAutoCompleteTextView acEstadoCivil;
    private TextInputEditText etNCaidas;

    //Opcionales
    private AppCompatAutoCompleteTextView acNivelEducacional;
    private AppCompatAutoCompleteTextView acTipoConvivencia;
    private AppCompatAutoCompleteTextView acProfesion;

    private NuevoEntrevistadoViewModel nuevoEntrevistadoViewModel;

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

    private boolean isSnackBarShow = false;

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
        ilNivelEducacional = findViewById(R.id.il_nivel_educacional_entrevistado);

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

        nuevoEntrevistadoViewModel = ViewModelProviders.of(this).get(NuevoEntrevistadoViewModel.class);

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

        //Observador de loading post boton guardar
        nuevoEntrevistadoViewModel.isLoadingEntrevistado().observe(this, new Observer<Boolean>() {
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

        //Observador para mensajes creacion entrevistado
        nuevoEntrevistadoViewModel.mostrarMsgRegistro().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_NUEVO_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                //Si el registro fue correcto cerrar la actividad
                if (s.equals(getString(R.string.MSG_REGISTRO_ENTREVISTADO))) {

                    Intent intent = getIntent();
                    intent.putExtra(getString(R.string.INTENT_KEY_MSG_REGISTRO), s);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        //Observador para mensajes de error de registro entrevistado
        nuevoEntrevistadoViewModel.mostrarMsgErrorRegistro().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.formulario_nuevo_entrevistado), Snackbar.LENGTH_LONG, s, null);
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_NUEVO_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });

    }

    private void viewModelEstadoCivil() {

        //Observador loading para estado civil
        nuevoEntrevistadoViewModel.isLoadingEstadosCiviles().observe(this, new Observer<Boolean>() {
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

        //Observador de estados civiles
        nuevoEntrevistadoViewModel.cargarEstadosCiviles().observe(this, new Observer<List<EstadoCivil>>() {
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
        nuevoEntrevistadoViewModel.mostrarMsgErrorListadoEstadosCiviles().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.formulario_nuevo_entrevistado), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));

                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_ESTADO_CIVIL), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    private void viewModelCiudad() {

        //Observador de loading ciudades
        nuevoEntrevistadoViewModel.isLoadingCiudades().observe(this, new Observer<Boolean>() {
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

        //Observador de ciudades
        nuevoEntrevistadoViewModel.cargarCiudades().observe(this, new Observer<List<Ciudad>>() {
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

        nuevoEntrevistadoViewModel.mostrarMsgErrorListadoCiudades().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.formulario_nuevo_entrevistado), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));

                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_CIUDAD), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    private void viewModelNivelEduc() {

        //Observador loading para niveles educacionales
        nuevoEntrevistadoViewModel.isLoadingNivelesEducacionales().observe(this, new Observer<Boolean>() {
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

        //Observador de niveles educacionaes
        nuevoEntrevistadoViewModel.cargarNivelesEducacionales().observe(this, new Observer<List<NivelEducacional>>() {
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
        nuevoEntrevistadoViewModel.mostrarMsgErrorListadoNivelesEduc().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.formulario_nuevo_entrevistado), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));

                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_NIVEL_EDUCACION), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            }
        });
    }

    private void viewModelTipoConvivencia() {

        //Observer loading de tipos de convivencia
        nuevoEntrevistadoViewModel.isLoadingTiposConvivencias().observe(this, new Observer<Boolean>() {
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

        //Observador de tipos de convivencias
        nuevoEntrevistadoViewModel.cargarTiposConvivencia().observe(this, new Observer<List<TipoConvivencia>>() {
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
        nuevoEntrevistadoViewModel.mostrarMsgErrorListadoTiposConvivencias().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.formulario_nuevo_entrevistado), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));

                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_CONVIVENCIA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            }
        });
    }

    private void viewModelProfesiones() {

        nuevoEntrevistadoViewModel.isLoadingProfesiones().observe(this, new Observer<Boolean>() {
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

        //Observador de profesiones
        nuevoEntrevistadoViewModel.cargarProfesiones().observe(this, new Observer<List<Profesion>>() {
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
        nuevoEntrevistadoViewModel.mostrarMsgErrorListadoProfesiones().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.formulario_nuevo_entrevistado), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));

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
                Utils.iniciarDatePicker(etFechaNacimiento, NuevoEntrevistadoActivity.this);
            }
        });

        //EndIconOnClick
        ilFechaNacimiento.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.iniciarDatePicker(etFechaNacimiento, NuevoEntrevistadoActivity.this);
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

    @Override
    public void showSnackbar(View v, int duration, String titulo, String accion) {
        Snackbar snackbar = Snackbar.make(v, titulo, duration);

        if (accion != null) {
            snackbar.setAction(accion, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Refresh listado de informacion necesaria
                    nuevoEntrevistadoViewModel.refreshEstadosCiviles();
                    nuevoEntrevistadoViewModel.refreshCiudades();
                    nuevoEntrevistadoViewModel.refreshNivelesEduc();
                    nuevoEntrevistadoViewModel.refreshTipoConvivencia();
                    nuevoEntrevistadoViewModel.refreshProfesiones();

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

                EstadoCivil estadoCivil = new EstadoCivil();
                estadoCivil.setId(Objects.requireNonNull(buscarEstadoCivilPorNombre(acEstadoCivil.getText().toString())).getId());
                entrevistado.setEstadoCivil(estadoCivil);

                entrevistado.setnConvivientes3Meses(Integer.parseInt(Objects.requireNonNull(etNConvivientes.getText()).toString()));

                entrevistado.setJubiladoLegal(switchJubiladoLegal.isChecked());
                entrevistado.setCaidas(switchCaidas.isChecked());
                if (switchCaidas.isChecked()) {
                    entrevistado.setNCaidas(Integer.parseInt(Objects.requireNonNull(etNCaidas.getText()).toString()));
                }

                if (!acNivelEducacional.getText().toString().isEmpty()) {

                    NivelEducacional nivelEducacional = new NivelEducacional();
                    nivelEducacional.setId(Objects.requireNonNull(buscarNivelEducacionalPorNombre(acNivelEducacional.getText().toString())).getId());
                    entrevistado.setNivelEducacional(nivelEducacional);
                }

                if (!acProfesion.getText().toString().isEmpty()) {

                    Profesion profesion = new Profesion();
                    profesion.setNombre(acProfesion.getText().toString());
                    entrevistado.setProfesion(profesion);
                }

                if (!acTipoConvivencia.getText().toString().isEmpty()) {

                    TipoConvivencia tipoConvivencia = new TipoConvivencia();
                    tipoConvivencia.setId(Objects.requireNonNull(buscarTipoConvivenciaPorNombre(acTipoConvivencia.getText().toString())).getId());
                    entrevistado.setTipoConvivencia(tipoConvivencia);
                }

                EntrevistadoRepositorio.getInstance(getApplication()).registrarEntrevistado(entrevistado);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        switchJubiladoLegal.setEnabled(activado);

        switchCaidas.setEnabled(activado);

        ilNCaidas.setEnabled(activado);
        etNCaidas.setEnabled(activado);

        ilTipoConvivencia.setEnabled(activado);
        acTipoConvivencia.setEnabled(activado);

        ilProfesion.setEnabled(activado);
        acProfesion.setEnabled(activado);

        ilNivelEducacional.setEnabled(activado);
        acNivelEducacional.setEnabled(activado);
    }

    private EstadoCivil buscarEstadoCivilPorNombre(String nombre) {

        for (int i = 0; i < estadoCivilList.size(); i++) {
            if (estadoCivilList.get(i).getNombre().equals(nombre)) {
                return estadoCivilList.get(i);
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

    private TipoConvivencia buscarTipoConvivenciaPorNombre(String nombre) {
        for (int i = 0; i < tipoConvivenciaList.size(); i++) {
            if (tipoConvivenciaList.get(i).getNombre().equals(nombre)) {
                return tipoConvivenciaList.get(i);
            }
        }
        return null;
    }
}
