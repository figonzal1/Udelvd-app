package cl.udelvd;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.modelo.Evento;
import cl.udelvd.viewmodel.EventoViewModel;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class EventsActivity extends AppCompatActivity {


    private String n_normales;
    private String n_extraordnarias;
    private int n_entrevistas;

    private TextView tv_normales;
    private TextView tv_extraodrinarias;
    private TextView tv_nombreApellido;
    private TextView tv_n_entrevistas;

    private Entrevista entrevista;
    private Entrevistado entrevistado;

    private List<Evento> eventoList;
    private EventoViewModel eventoViewModel;
    private FragmentStatePageAdapter fragmentStatePageAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorOnPrimary));

        setSupportActionBar(toolbar);

        //Boton atras
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Eventos");
        actionBar.setDisplayHomeAsUpEnabled(true);

        obtenerDatosBundle();

        setearRecursosInterfaz();

        viewPager = findViewById(R.id.view_pager_events);

        FloatingActionButton fb = findViewById(R.id.fb_crear_evento);

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewEventDialog fragment = new NewEventDialog();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, fragment)
                        .addToBackStack(null).commit();
            }
        });
    }

    private void setearRecursosInterfaz() {
        tv_nombreApellido = findViewById(R.id.tv_entrevistado_nombre);
        tv_n_entrevistas = findViewById(R.id.tv_n_entrevistas);
        tv_normales = findViewById(R.id.tv_normales_value);
        tv_extraodrinarias = findViewById(R.id.tv_extraordinarias_value);

        tv_nombreApellido.setText(entrevistado.getNombre() + " " + entrevistado.getApellido());
        tv_normales.setText(n_normales);
        tv_extraodrinarias.setText(n_extraordnarias);

        //Contar cantidad de entrevistas
        if (n_entrevistas == 1) {
            tv_n_entrevistas.setText(n_entrevistas + " entrevista");
        } else {
            tv_n_entrevistas.setText(n_entrevistas + " entrevistas");
        }

    }

    private void obtenerDatosBundle() {

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            entrevista = new Entrevista();
            entrevista.setId(bundle.getInt("id_entrevista"));
            entrevista.setId_entrevistado(bundle.getInt("id_entrevistado"));

            entrevistado = new Entrevistado();
            entrevistado.setNombre(bundle.getString("nombre_entrevistado"));
            entrevistado.setApellido(bundle.getString("apellido_entrevistado"));

            n_entrevistas = bundle.getInt("n_entrevistas");
            n_normales = bundle.getString("n_normales");
            n_extraordnarias = bundle.getString("n_extraodrinarias");

            eventoViewModel = ViewModelProviders.of(this).get(EventoViewModel.class);
            eventoViewModel.cargarEventos(entrevista).observe(this, new Observer<List<Evento>>() {
                @Override
                public void onChanged(List<Evento> eventos) {
                    if (eventos != null) {
                        eventoList = eventos;

                        Log.d("EVENTOS_VM", eventoList.toString());
                        fragmentStatePageAdapter = new FragmentStatePageAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, eventoList);
                        viewPager.setAdapter(fragmentStatePageAdapter);
                    }
                }
            });
        } else {
            Log.d("BUNDLE_STATUS_EVENTOS", "VACIO");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
