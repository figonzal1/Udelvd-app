package cl.udelvd;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;

import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.modelo.Evento;
import cl.udelvd.viewmodel.EventoViewModel;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class EventsActivity extends AppCompatActivity {


    private String n_normales;
    private String n_extraordnarias;
    private int n_entrevistas;
    private String fecha_entrevista;

    private TextView tv_normales;
    private TextView tv_extraodrinarias;
    private TextView tv_nombreApellido;
    private TextView tv_n_entrevistas;
    private TextView tv_eventos_vacios;

    private Entrevista entrevista;
    private Entrevistado entrevistado;

    private List<Evento> eventoList;
    private EventoViewModel eventoViewModel;
    private FragmentStatePageAdapter fragmentStatePageAdapter;
    private ViewPager viewPager;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_main);

        configuracionToolbar();

        obtenerDatosBundle();

        setearRecursosInterfaz();

        iniciarViewModel();

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

    private void configuracionToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorOnPrimary));

        setSupportActionBar(toolbar);

        //Boton atras
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Eventos");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void iniciarViewModel() {
        eventoViewModel = ViewModelProviders.of(this).get(EventoViewModel.class);
        eventoViewModel.cargarEventos(entrevista).observe(this, new Observer<List<Evento>>() {
            @Override
            public void onChanged(List<Evento> eventos) {
                if (eventos != null) {

                    progressBar.setVisibility(View.INVISIBLE);

                    eventoList = eventos;

                    Log.d("EVENTOS_VM", eventoList.toString());

                    if (eventoList.size() > 0) {
                        fragmentStatePageAdapter = new FragmentStatePageAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, eventoList, fecha_entrevista);
                        viewPager.setAdapter(fragmentStatePageAdapter);
                        viewPager.setVisibility(View.VISIBLE);

                    } else {
                        Log.d("HOLA", "EVENTOS VACIOS");
                        viewPager.setVisibility(View.GONE);
                        tv_eventos_vacios.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        eventoViewModel.mostrarErrorRespuesta().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.INVISIBLE);

                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setearRecursosInterfaz() {

        progressBar = findViewById(R.id.progress_bar_eventos);
        progressBar.setVisibility(View.VISIBLE);

        viewPager = findViewById(R.id.view_pager_events);

        tv_nombreApellido = findViewById(R.id.tv_entrevistado_nombre);
        tv_n_entrevistas = findViewById(R.id.tv_n_entrevistas);
        tv_normales = findViewById(R.id.tv_normales_value);
        tv_extraodrinarias = findViewById(R.id.tv_extraordinarias_value);

        tv_nombreApellido.setText(String.format("%s %s", entrevistado.getNombre(), entrevistado.getApellido()));
        tv_normales.setText(n_normales);
        tv_extraodrinarias.setText(n_extraordnarias);

        //Contar cantidad de entrevistas
        if (n_entrevistas == 1) {
            tv_n_entrevistas.setText(String.format(Locale.US, "%d entrevista", n_entrevistas));
        } else {
            tv_n_entrevistas.setText(String.format(Locale.US, "%d entrevistas", n_entrevistas));
        }

        tv_eventos_vacios = findViewById(R.id.tv_eventos_vacios);

    }

    private void obtenerDatosBundle() {

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            entrevista = new Entrevista();
            entrevista.setId(bundle.getInt("id_entrevista"));
            entrevista.setId_entrevistado(bundle.getInt("id_entrevistado"));

            fecha_entrevista = bundle.getString("fecha_entrevista");

            entrevistado = new Entrevistado();
            entrevistado.setNombre(bundle.getString("nombre_entrevistado"));
            entrevistado.setApellido(bundle.getString("apellido_entrevistado"));

            n_entrevistas = bundle.getInt("n_entrevistas");
            n_normales = bundle.getString("n_normales");
            n_extraordnarias = bundle.getString("n_extraodrinarias");

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
