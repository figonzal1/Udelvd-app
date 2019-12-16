package cl.udelvd.vistas.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;

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

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.adaptadores.CiudadAdapter;
import cl.udelvd.adaptadores.EstadoCivilAdapter;
import cl.udelvd.modelo.Ciudad;
import cl.udelvd.modelo.EstadoCivil;
import cl.udelvd.viewmodel.CiudadViewModel;
import cl.udelvd.viewmodel.EstadoCivilViewModel;

public class NuevoEntrevistadoActivity extends AppCompatActivity {

    private TextInputLayout ilNombre;
    private TextInputLayout ilApellido;
    private TextInputLayout ilSexo;
    private TextInputLayout ilFechaNacimiento;
    private TextInputLayout ilCiudad;
    private TextInputLayout ilEstadoCivil;
    private SwitchMaterial switchJubiladoLegal;
    private TextView tv_jubilado_value;
    private SwitchMaterial switchCaidas;
    private TextView tv_caidas_value;
    private TextInputLayout ilNCaidas;

    private TextInputEditText etNombre;
    private TextInputEditText etApellido;
    private AppCompatAutoCompleteTextView acSexo;
    private TextInputEditText etFechaNacimiento;
    private AppCompatAutoCompleteTextView acCiudad;
    private AppCompatAutoCompleteTextView acEstadoCivil;
    private TextInputEditText etNCaidas;

    //ViewModels
    private CiudadViewModel ciudadViewModel;
    private EstadoCivilViewModel estadoCivilViewModel;

    //Listados
    private List<Ciudad> ciudadList;
    private List<EstadoCivil> estadoCivilList;

    //Adaptadores
    private ArrayAdapter<Ciudad> ciudadAdapter;
    private ArrayAdapter<EstadoCivil> estadoCivilAdapter;

    public NuevoEntrevistadoActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_usuario);

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

        //Observador de estados civiles
        estadoCivilViewModel.cargarEstadosCiviles().observe(this, new Observer<List<EstadoCivil>>() {
            @Override
            public void onChanged(List<EstadoCivil> estadoCivils) {

                if (estadoCivils != null) {
                    estadoCivilList = estadoCivils;
                    estadoCivilAdapter = new EstadoCivilAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, estadoCivilList);
                    acEstadoCivil.setAdapter(estadoCivilAdapter);
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
                }
                ciudadAdapter.notifyDataSetChanged();
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
        ilNCaidas = findViewById(R.id.il_n_caidas_entrevistado);

        tv_jubilado_value = findViewById(R.id.tv_switch_jubilado_value);
        tv_caidas_value = findViewById(R.id.tv_switch_caidas);

        etNombre = findViewById(R.id.et_nombre_entrevistado);
        etApellido = findViewById(R.id.et_apellido_entrevistado);
        etFechaNacimiento = findViewById(R.id.et_fecha_nacimiento);
        etNCaidas = findViewById(R.id.et_n_caidas_entrevistado);
        acCiudad = findViewById(R.id.et_ciudad_entrevistado);
        acEstadoCivil = findViewById(R.id.et_estado_civil_entrevistado);
        acSexo = findViewById(R.id.et_sexo_entrevistado);

        switchJubiladoLegal = findViewById(R.id.switch_jubilado_legal);
        switchCaidas = findViewById(R.id.switch_caidas_entrevistado);

    }

    private void setPickerFechaNacimiento() {
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

        }
        //TODO: Configurar opcion de guardado

        return super.onOptionsItemSelected(item);
    }
}
