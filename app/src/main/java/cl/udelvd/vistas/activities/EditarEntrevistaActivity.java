package cl.udelvd.vistas.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import cl.udelvd.R;
import cl.udelvd.adaptadores.TipoEntrevistaAdapter;
import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.TipoEntrevista;
import cl.udelvd.viewmodel.EntrevistaViewModel;
import cl.udelvd.viewmodel.TipoEntrevistaViewModel;

public class EditarEntrevistaActivity extends AppCompatActivity {


    private TextInputLayout ilFechaEntrevista;
    private TextInputLayout ilTipoEntrevista;

    private TextInputEditText etFechaEntrevista;
    private AppCompatAutoCompleteTextView acTipoEntrevista;

    private Entrevista entrevista;


    private EntrevistaViewModel entrevistaViewModel;
    private TipoEntrevistaViewModel tipoEntrevistaViewModel;

    private List<TipoEntrevista> tipoEntrevistasList;
    private TipoEntrevistaAdapter tipoEntrevistasAdapter;
    private boolean isAutoCompleteTipoEntrevistaReady = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_entrevista);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorOnPrimary));
        setSupportActionBar(toolbar);

        //Boton cerrar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Editar entrevista");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        instanciarRecursosInterfaz();

        setAutoCompleteTipoEntrevista();

        obtenerBundles();

        //entrevistaViewModel = ViewModelProviders.of(this).get(EntrevistaViewModel.class);
        //entrevistaViewModel.
    }

    private void obtenerBundles() {

        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();
            entrevista = new Entrevista();
            entrevista.setId(bundle.getInt("id_entrevista"));
            entrevista.setId_entrevistado(bundle.getInt("id_entrevistado"));
        }
    }

    /**
     * Funcion encargada de cargar los tipos de entrevista en el autoComplete
     */
    private void setAutoCompleteTipoEntrevista() {
        tipoEntrevistaViewModel = ViewModelProviders.of(this).get(TipoEntrevistaViewModel.class);

        tipoEntrevistaViewModel.cargarTiposEntrevistas().observe(this, new Observer<List<TipoEntrevista>>() {
            @Override
            public void onChanged(List<TipoEntrevista> tipoEntrevistas) {
                if (tipoEntrevistas != null) {
                    tipoEntrevistasList = tipoEntrevistas;
                    tipoEntrevistasAdapter = new TipoEntrevistaAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, tipoEntrevistasList);
                    acTipoEntrevista.setAdapter(tipoEntrevistasAdapter);

                    isAutoCompleteTipoEntrevistaReady = true;

                    Log.d("VM_TIPO_ENTREVISTA", "Listado cargado");
                }

                tipoEntrevistasAdapter.notifyDataSetChanged();
            }
        });
    }

    private void instanciarRecursosInterfaz() {

        ilFechaEntrevista = findViewById(R.id.il_fecha_entrevista);
        ilTipoEntrevista = findViewById(R.id.il_tipo_entrevista);

        etFechaEntrevista = findViewById(R.id.et_fecha_entrevista);
        acTipoEntrevista = findViewById(R.id.et_tipo_entrevista);
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
        }

        return false;
    }
}
