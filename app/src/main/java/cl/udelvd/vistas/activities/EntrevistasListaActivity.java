package cl.udelvd.vistas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cl.udelvd.R;
import cl.udelvd.adaptadores.EntrevistaAdapter;
import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.EntrevistaViewModel;

public class EntrevistasListaActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CREAR_ENTREVISTA = 300;

    private RecyclerView rv;
    private EntrevistaAdapter entrevistaAdapter;
    private EntrevistaViewModel entrevistaViewModel;

    private TextView tv_nombreCompleto;
    private TextView tv_n_entrevistas;
    private TextView tv_entrevistas_normales;
    private TextView tv_entrevistas_extraordinarias;

    private CardView cv_lista_entrevistas;
    private TextView tv_entrevistas_vacias;

    private Entrevistado entrevistado;

    //Datos Bundle
    private int id_entrevistado;
    private String nombre_entrevistado;
    private String apellido_entrevistado;

    //Map params para eventos
    private Map<String, Integer> params;

    private ProgressBar progressBar;
    private List<Entrevista> entrevistasList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrevistas_list);

        Utils.configurarToolbar(this, getApplicationContext(), 0, getString(R.string.TITULO_TOOLBAR_LISTA_ENTREVISTAS));

        obtenerDatosBundle();

        setearRecursosInterfaz();

        iniciarViewModelObservers();

        floatingButtonNuevaEntrevista();

    }

    /**
     * Funcion encargada de obtener los datos desde el intent de la actividad padre
     */
    private void obtenerDatosBundle() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            entrevistado = new Entrevistado();

            id_entrevistado = bundle.getInt(getString(R.string.KEY_ENTREVISTADO_ID_LARGO));
            nombre_entrevistado = bundle.getString(getString(R.string.KEY_ENTREVISTADO_NOMBRE_LARGO));
            apellido_entrevistado = bundle.getString(getString(R.string.KEY_ENTREVISTADO_APELLIDO_LARGO));

            entrevistado.setId(id_entrevistado);
            entrevistado.setNombre(nombre_entrevistado);
            entrevistado.setApellido(apellido_entrevistado);
        } else {
            Log.d("BUNDLE_STATUS", "bundle vacio");
        }
    }

    private void setearRecursosInterfaz() {
        entrevistasList = new ArrayList<>();

        progressBar = findViewById(R.id.progress_bar_entrevistas);
        progressBar.setVisibility(View.VISIBLE);

        rv = findViewById(R.id.rv_lista_entrevistas);

        LinearLayoutManager ly = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(ly);

        tv_entrevistas_vacias = findViewById(R.id.tv_entrevistas_vacios);
        tv_entrevistas_vacias.setVisibility(View.INVISIBLE);

        tv_nombreCompleto = findViewById(R.id.tv_entrevistado_nombre);
        tv_n_entrevistas = findViewById(R.id.tv_n_entrevistas);
        tv_entrevistas_normales = findViewById(R.id.tv_normales_value);
        tv_entrevistas_extraordinarias = findViewById(R.id.tv_extraordinarias_value);

        tv_nombreCompleto.setText(String.format("%s %s", nombre_entrevistado, apellido_entrevistado));

        cv_lista_entrevistas = findViewById(R.id.card_view_lista_entrevistas);
        cv_lista_entrevistas.setVisibility(View.INVISIBLE);

    }

    /**
     * Funcion encargada de inicializar los viewModelsObservers
     */
    private void iniciarViewModelObservers() {
        entrevistaViewModel = ViewModelProviders.of(this).get(EntrevistaViewModel.class);

        entrevistaViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);
                    cv_lista_entrevistas.setVisibility(View.INVISIBLE);
                    tv_entrevistas_vacias.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    cv_lista_entrevistas.setVisibility(View.VISIBLE);

                    if (entrevistasList.size() == 0) {
                        tv_entrevistas_vacias.setVisibility(View.VISIBLE);
                    } else {
                        tv_entrevistas_vacias.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        //Observador de listado de entrevistas
        entrevistaViewModel.cargarEntrevistas(entrevistado).observe(this, new Observer<List<Entrevista>>() {
            @Override
            public void onChanged(List<Entrevista> entrevistas) {
                if (entrevistas != null) {

                    entrevistasList = entrevistas;
                    //Contar cantidad de entrevistas
                    if (entrevistas.size() == 1) {
                        tv_n_entrevistas.setText(String.format(Locale.US, "%d entrevista", entrevistas.size()));
                    } else {
                        tv_n_entrevistas.setText(String.format(Locale.US, "%d entrevistas", entrevistas.size()));
                    }

                    //Contar tipos de entrevistas
                    Map<String, Integer> tipos = contarTipos(entrevistas);
                    tv_entrevistas_normales.setText(String.valueOf(tipos.get("normales")));
                    tv_entrevistas_extraordinarias.setText(String.valueOf(tipos.get("extraordinarias")));

                    params = new HashMap<>();
                    params.put(getString(R.string.KEY_ENTREVISTADO_N_ENTREVISTAS), entrevistas.size());
                    params.put(getString(R.string.KEY_ENTREVISTA_N_NORMALES), tipos.get("normales"));
                    params.put(getString(R.string.KEY_ENTREVISTA_N_EXTRAORDINARIAS), tipos.get("extraordinarias"));

                    entrevistaAdapter = new EntrevistaAdapter(entrevistas, getApplicationContext(), entrevistado, params);
                    entrevistaAdapter.notifyDataSetChanged();
                    rv.setAdapter(entrevistaAdapter);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ENTREVISTAS), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), entrevistas.toString()));
                }
            }
        });

        //Observador para errores
        entrevistaViewModel.mostrarRespuestaError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                showSnackbar(findViewById(R.id.entrevistas_list), s, getString(R.string.SNACKBAR_REINTENTAR));

                progressBar.setVisibility(View.INVISIBLE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ENTREVISTAS), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    /**
     * Boton flotante para crear entrevistas
     */
    private void floatingButtonNuevaEntrevista() {
        FloatingActionButton fabNuevaEntrevista = findViewById(R.id.fb_crear_entrevista);
        fabNuevaEntrevista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EntrevistasListaActivity.this, NuevaEntrevistaActivity.class);
                intent.putExtra(getString(R.string.KEY_ENTREVISTADO_ID_LARGO), entrevistado.getId());
                startActivityForResult(intent, REQUEST_CODE_CREAR_ENTREVISTA);
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

                        cv_lista_entrevistas.setVisibility(View.INVISIBLE);

                        progressBar.setVisibility(View.VISIBLE);

                    }
                });

        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actualizar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_actualizar) {

            cv_lista_entrevistas.setVisibility(View.INVISIBLE);

            progressBar.setVisibility(View.VISIBLE);

            entrevistaViewModel.refreshEntrevistas(entrevistado);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_CREAR_ENTREVISTA) {
            if (resultCode == RESULT_OK) {
                entrevistaViewModel.refreshEntrevistas(entrevistado);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
