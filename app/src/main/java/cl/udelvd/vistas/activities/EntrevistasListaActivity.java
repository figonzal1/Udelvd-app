package cl.udelvd.vistas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import cl.udelvd.repositorios.EntrevistaRepositorio;
import cl.udelvd.utilidades.SnackbarInterface;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.EntrevistasListaViewModel;
import cl.udelvd.vistas.fragments.DeleteDialogListener;

public class EntrevistasListaActivity extends AppCompatActivity implements DeleteDialogListener, SnackbarInterface {

    private static final int REQUEST_CODE_CREAR_ENTREVISTA = 300;
    private static final int REQUEST_CODE_EDITAR_ENTREVISTA = 301;

    private RecyclerView rv;
    private CardView cv_entrevistas;
    private CardView cv_info;
    private EntrevistaAdapter entrevistaAdapter;
    private EntrevistasListaViewModel entrevistasListaViewModel;

    private TextView tv_n_entrevistas;
    private TextView tv_entrevistas_normales;
    private TextView tv_entrevistas_extraordinarias;

    private TextView tv_entrevistas_vacias;

    private Entrevistado entrevistado;

    //Map params para eventos
    private Map<String, Integer> params;

    private ProgressBar progressBar;
    private List<Entrevista> entrevistasList;
    private boolean isSnackBarShow = false;
    private int annos;
    private Snackbar snackbar;


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

            //Datos Bundle
            int id_entrevistado = bundle.getInt(getString(R.string.KEY_ENTREVISTADO_ID_LARGO));
            String nombre_entrevistado = bundle.getString(getString(R.string.KEY_ENTREVISTADO_NOMBRE_LARGO));
            String apellido_entrevistado = bundle.getString(getString(R.string.KEY_ENTREVISTADO_APELLIDO_LARGO));
            String sexo = bundle.getString(getString(R.string.KEY_ENTREVISTADO_SEXO_LARGO));
            annos = bundle.getInt(getString(R.string.KEY_ENTREVISTADO_ANNOS));
            String fecha_nac = bundle.getString(getString(R.string.KEY_ENTREVISTADO_FECHA_NAC));

            entrevistado.setId(id_entrevistado);
            entrevistado.setNombre(nombre_entrevistado);
            entrevistado.setApellido(apellido_entrevistado);
            entrevistado.setSexo(sexo);
            entrevistado.setFechaNacimiento(Utils.stringToDate(getApplicationContext(), false, fecha_nac));
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

        ImageView iv_persona = findViewById(R.id.cv_iv_persona);
        cv_entrevistas = findViewById(R.id.card_view_lista_entrevistas);
        cv_entrevistas.setVisibility(View.INVISIBLE);
        cv_info = findViewById(R.id.card_view_info_entrevistado);
        cv_info.setVisibility(View.INVISIBLE);

        TextView tv_nombreCompleto = findViewById(R.id.tv_entrevistado_nombre);
        tv_nombreCompleto.setText(String.format("%s %s", entrevistado.getNombre(), entrevistado.getApellido()));

        tv_n_entrevistas = findViewById(R.id.tv_n_entrevistas);

        tv_entrevistas_normales = findViewById(R.id.tv_normales_value);
        tv_entrevistas_extraordinarias = findViewById(R.id.tv_extraordinarias_value);

        entrevistasListaViewModel = new ViewModelProvider(this).get(EntrevistasListaViewModel.class);

        Utils.configurarIconoEntrevistado(entrevistado, annos, iv_persona, getApplicationContext());

        entrevistaAdapter = new EntrevistaAdapter(
                entrevistasList,
                EntrevistasListaActivity.this,
                getSupportFragmentManager(),
                entrevistado,
                params,
                REQUEST_CODE_EDITAR_ENTREVISTA);
        rv.setAdapter(entrevistaAdapter);
    }

    /**
     * Funcion encargada de inicializar los viewModelsObservers
     */
    private void iniciarViewModelObservers() {

        entrevistasListaViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);
                    tv_entrevistas_vacias.setVisibility(View.INVISIBLE);
                    cv_entrevistas.setVisibility(View.INVISIBLE);
                    cv_info.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.VISIBLE);
                    cv_entrevistas.setVisibility(View.VISIBLE);
                    cv_info.setVisibility(View.VISIBLE);
                }
            }
        });

        //Observador de listado de entrevistas
        entrevistasListaViewModel.cargarEntrevistas(entrevistado).observe(this, new Observer<List<Entrevista>>() {
            @Override
            public void onChanged(List<Entrevista> entrevistas) {
                if (entrevistas != null) {

                    entrevistasList = entrevistas;

                    //Contar cantidad de entrevistas
                    if (entrevistas.size() == 1) {
                        tv_n_entrevistas.setText(String.format(Locale.US, getString(R.string.FORMATO_N_ENTREVISTA), entrevistas.size()));
                    } else {
                        tv_n_entrevistas.setText(String.format(Locale.US, getString(R.string.FORMATO_N_ENTREVISTAS), entrevistas.size()));
                    }

                    progressBar.setVisibility(View.INVISIBLE);
                    cv_entrevistas.setVisibility(View.VISIBLE);
                    cv_info.setVisibility(View.VISIBLE);

                    if (entrevistasList.size() == 0) {
                        tv_entrevistas_vacias.setVisibility(View.VISIBLE);
                    } else {
                        tv_entrevistas_vacias.setVisibility(View.INVISIBLE);
                    }

                    //Contar tipos de entrevistas
                    Map<String, Integer> tipos = contarTipos(entrevistas);
                    tv_entrevistas_normales.setText(String.valueOf(tipos.get(getString(R.string.INTENT_KEY_NORMALES))));
                    tv_entrevistas_extraordinarias.setText(String.valueOf(tipos.get(getString(R.string.INTENT_KEY_EXTRAORDINARIAS))));

                    params = new HashMap<>();
                    params.put(getString(R.string.KEY_ENTREVISTADO_N_ENTREVISTAS), entrevistas.size());
                    params.put(getString(R.string.KEY_ENTREVISTA_N_NORMALES), tipos.get(getString(R.string.INTENT_KEY_NORMALES)));
                    params.put(getString(R.string.KEY_ENTREVISTA_N_EXTRAORDINARIAS), tipos.get(getString(R.string.INTENT_KEY_EXTRAORDINARIAS)));

                    entrevistaAdapter.setParams(params);
                    entrevistaAdapter.actualizarLista(entrevistasList);
                    rv.setAdapter(entrevistaAdapter);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ENTREVISTAS), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), entrevistas.toString()));
                }
            }
        });

        //Observador para errores
        entrevistasListaViewModel.mostrarMsgErrorListado().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.INVISIBLE);
                cv_entrevistas.setVisibility(View.INVISIBLE);
                cv_info.setVisibility(View.INVISIBLE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.entrevistas_list), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                    entrevistaAdapter.notifyDataSetChanged();
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ENTREVISTAS), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });

        //Observador para eliminacion de entrevista
        entrevistasListaViewModel.mostrarMsgEliminar().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.INVISIBLE);

                if (s.equals(getString(R.string.MSG_DELETE_ENTREVISTA))) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.entrevistas_list), Snackbar.LENGTH_LONG, s, null);
                    EntrevistaRepositorio.getInstancia(getApplication()).obtenerEntrevistasPersonales(entrevistado);
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
            }
        });

        //Observador para errores al eliminar
        entrevistasListaViewModel.mostrarMsgErrorEliminar().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.INVISIBLE);

                if (!isSnackBarShow) {

                    if (!s.equals(getString(R.string.SERVER_ERROR_MSG_VM))) {
                        showSnackbar(findViewById(R.id.entrevistas_list), Snackbar.LENGTH_INDEFINITE, s, null);
                    } else {
                        showSnackbar(findViewById(R.id.entrevistas_list), Snackbar.LENGTH_LONG, s, null);
                    }
                    entrevistaAdapter.notifyDataSetChanged();
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
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
            if (entrevistas.get(i).getTipoEntrevista().getNombre().equals(getString(R.string.NORMAL))) {
                normales++;
            } else if (entrevistas.get(i).getTipoEntrevista().getNombre().equals(getString(R.string.EXTRAORDINARIA))) {
                extraordinarias++;
            }
        }

        Map<String, Integer> map = new HashMap<>();
        map.put(getString(R.string.INTENT_KEY_NORMALES), normales);
        map.put(getString(R.string.INTENT_KEY_EXTRAORDINARIAS), extraordinarias);
        return map;
    }

    @Override
    public void showSnackbar(View v, int tipo_snackbar, String titulo, String accion) {

        snackbar = Snackbar.make(v, titulo, tipo_snackbar);
        if (accion != null) {
            snackbar.setAction(accion, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progressBar.setVisibility(View.VISIBLE);

                    isSnackBarShow = false;
                    if (snackbar != null) {
                        snackbar.dismiss();
                    }

                    //Refresh listado de usuarios
                    entrevistasListaViewModel.refreshEntrevistas(entrevistado);
                }
            });
        }
        snackbar.show();
        isSnackBarShow = false;
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

            progressBar.setVisibility(View.VISIBLE);
            isSnackBarShow = false;
            if (snackbar != null) {
                snackbar.dismiss();
            }
            entrevistasListaViewModel.refreshEntrevistas(entrevistado);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_CREAR_ENTREVISTA) {
            if (resultCode == RESULT_OK) {

                assert data != null;
                Bundle bundle = data.getExtras();
                assert bundle != null;
                showSnackbar(findViewById(R.id.entrevistas_list), Snackbar.LENGTH_LONG, bundle.getString(getString(R.string.INTENT_KEY_MSG_REGISTRO)), null);

                entrevistasListaViewModel.refreshEntrevistas(entrevistado);
            }
        } else if (requestCode == REQUEST_CODE_EDITAR_ENTREVISTA) {
            if (resultCode == RESULT_OK) {

                assert data != null;
                Bundle bundle = data.getExtras();
                assert bundle != null;
                showSnackbar(findViewById(R.id.entrevistas_list), Snackbar.LENGTH_LONG, bundle.getString(getString(R.string.INTENT_KEY_MSG_ACTUALIZACION)), null);

                entrevistasListaViewModel.refreshEntrevistas(entrevistado);
            }
        }
        isSnackBarShow = false;
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDialogPositiveClick(Object object) {
        EntrevistaRepositorio.getInstancia(getApplication()).eliminarEntrevista((Entrevista) object);
    }
}
