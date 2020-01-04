package cl.udelvd.vistas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cl.udelvd.R;
import cl.udelvd.adaptadores.EntrevistaAdapter;
import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.viewmodel.EntrevistaViewModel;

public class EntrevistasListaActivity extends AppCompatActivity {

    private RecyclerView rv;
    private EntrevistaAdapter entrevistaAdapter;
    private FloatingActionButton fabNuevaEntrevista;
    private EntrevistaViewModel entrevistaViewModel;

    private TextView tv_nombreCompleto;
    private TextView tv_n_entrevistas;
    private TextView tv_entrevistas_normales;
    private TextView tv_entrevistas_extraordinarias;

    private Entrevistado entrevistado;

    //Datos Bundle
    private int id_entrevistado;
    private String nombre_entrevistado;
    private String apellido_entrevistado;

    //Map params para eventos
    private Map<String, Integer> params;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrevistas_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorOnPrimary));
        setSupportActionBar(toolbar);

        //Boton atras
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Listado entrevistas");
        actionBar.setDisplayHomeAsUpEnabled(true);

        obtenerDatosBundle();

        setearRecursosInterfaz();

        iniciarSwipeRefresh();

        iniciarViewModelObservers();

        fabNuevaEntrevista = findViewById(R.id.fb_crear_entrevista);
        fabNuevaEntrevista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*NewUserDialog dialog = new NewUserDialog();
                assert getFragmentManager() != null;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "NewUserDialog");*/

                /*DialogFragment dialogFragment = new NewUserDialog();
                assert getFragmentManager() != null;
                dialogFragment.show(getFragmentManager(),"NewUserDialog");*/

                //TODO: Implementar creacion de entrevistas
                /*NewInterviewDialog fragment = new NewInterviewDialog();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, fragment)
                        .addToBackStack(null).commit();*/

                Intent intent = new Intent(EntrevistasListaActivity.this, NuevaEntrevistaActivity.class);
                intent.putExtra("id_entrevistado", entrevistado.getId());
                startActivity(intent);
            }
        });
    }

    private void setearRecursosInterfaz() {
        rv = findViewById(R.id.rv_lista_entrevistas);

        LinearLayoutManager ly = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(ly);

        entrevistaAdapter = new EntrevistaAdapter(new ArrayList<Entrevista>(), getApplicationContext(), entrevistado, params);
        rv.setAdapter(entrevistaAdapter);

        tv_nombreCompleto = findViewById(R.id.tv_entrevistado_nombre);
        tv_n_entrevistas = findViewById(R.id.tv_n_entrevistas);
        tv_entrevistas_normales = findViewById(R.id.tv_normales_value);
        tv_entrevistas_extraordinarias = findViewById(R.id.tv_extraordinarias_value);

        tv_nombreCompleto.setText(nombre_entrevistado + " " + apellido_entrevistado);
    }

    /**
     * Funcion encargada de obtener los datos desde el intent de la actividad padre
     */
    private void obtenerDatosBundle() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            entrevistado = new Entrevistado();

            id_entrevistado = bundle.getInt("id_entrevistado");
            nombre_entrevistado = bundle.getString("nombre_entrevistado");
            apellido_entrevistado = bundle.getString("apellido_entrevistado");

            entrevistado.setId(id_entrevistado);
            entrevistado.setNombre(nombre_entrevistado);
            entrevistado.setApellido(apellido_entrevistado);
        } else {
            Log.d("BUNDLE_STATUS", "bundle vacio");
        }
    }

    /**
     * Funcion encargada de la lógica del swipe refresh de entrevistas
     */
    private void iniciarSwipeRefresh() {
        swipeRefreshLayout = findViewById(R.id.refresh_entrevistas);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorSecondary), getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Forzar refresh entrevistas
                entrevistaViewModel.refreshEntrevistas(entrevistado);
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    /**
     * Funcion encargada de inicializar los viewModelsObservers
     */
    private void iniciarViewModelObservers() {
        entrevistaViewModel = ViewModelProviders.of(this).get(EntrevistaViewModel.class);

        //Observador de listado de entrevistas
        entrevistaViewModel.cargarEntrevistas(entrevistado).observe(this, new Observer<List<Entrevista>>() {
            @Override
            public void onChanged(List<Entrevista> entrevistas) {
                if (entrevistas != null) {

                    //Contar cantidad de entrevistas
                    if (entrevistas.size() == 1) {
                        tv_n_entrevistas.setText(entrevistas.size() + " entrevista");
                    } else {
                        tv_n_entrevistas.setText(entrevistas.size() + " entrevistas");
                    }

                    //Contar tipos de entrevistas
                    Map<String, Integer> tipos = contarTipos(entrevistas);
                    tv_entrevistas_normales.setText(String.valueOf(tipos.get("normales")));
                    tv_entrevistas_extraordinarias.setText(String.valueOf(tipos.get("extraordinarias")));

                    params = new HashMap<>();
                    params.put("n_entrevistas", entrevistas.size());
                    params.put("n_normales", tipos.get("normales"));
                    params.put("n_extraodrinarias", tipos.get("extraordinarias"));

                    entrevistaAdapter = new EntrevistaAdapter(entrevistas, getApplicationContext(), entrevistado, params);
                    entrevistaAdapter.notifyDataSetChanged();
                    rv.setAdapter(entrevistaAdapter);


                    swipeRefreshLayout.setRefreshing(false);

                    Log.d("VM_LISTA_ENTREVISTAS", "MSG_RESPONSE: " + entrevistas.toString());
                }
            }
        });

        //Observador para errores
        entrevistaViewModel.mostrarRespuestaError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                showSnackbar(findViewById(R.id.entrevistas_list), s, "Reintentar");

                swipeRefreshLayout.setRefreshing(false);

                Log.d("VM_LISTA_ENTREVISTAS", "MSG_ERROR: " + s);
            }
        });
    }

    /**
     * Funcion encargada de contar las entrevistas normales y extraordinarias
     *
     * @param entrevistas Listadp de entrevistas
     * @return Mapa con conteo de normales y extraordinadias
     */
    private Map<String, Integer> contarTipos(List<Entrevista> entrevistas) {
        int normales = 0;
        int extraordinarias = 0;
        for (int i = 0; i < entrevistas.size(); i++) {
            if (entrevistas.get(i).getTipoEntrevista().getNombre().equals("Normal")) {
                normales++;
            } else if (entrevistas.get(i).getTipoEntrevista().getNombre().equals("Extraordinaria")) {
                extraordinarias++;
            }
        }

        Map<String, Integer> map = new HashMap<>();
        map.put("normales", normales);
        map.put("extraordinarias", extraordinarias);
        return map;
    }

    /**
     * Funcion para mostrar el snackbar en la actividad
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

                        //Refresh listado de usuarios
                        entrevistaViewModel.refreshEntrevistas(entrevistado);

                        swipeRefreshLayout.setRefreshing(true);
                    }
                });

        snackbar.show();
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
