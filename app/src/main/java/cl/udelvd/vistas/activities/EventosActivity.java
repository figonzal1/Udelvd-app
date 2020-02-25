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
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.adaptadores.FragmentStatePageAdapter;
import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.modelo.Evento;
import cl.udelvd.repositorios.EventoRepositorio;
import cl.udelvd.utilidades.SnackbarInterface;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.EventosListaViewModel;
import cl.udelvd.vistas.fragments.DeleteDialogListener;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class EventosActivity extends AppCompatActivity implements DeleteDialogListener, SnackbarInterface {


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
    private EventosListaViewModel eventosListaViewModel;

    private FragmentStatePageAdapter fragmentStatePageAdapter;
    private ViewPager viewPager;
    private ProgressBar progressBar;
    private TabLayout tabLayout;
    private static final int REQUEST_CODE_NUEVO_EVENTO = 200;
    private static final int REQUEST_CODE_EDITAR_EVENTO = 300;
    private boolean isSnackBarShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos_main);

        Utils.configurarToolbar(this, getApplicationContext(), 0, getString(R.string.TITULO_TOOLBAR_EVENTOS));

        obtenerDatosBundle();

        setearRecursosInterfaz();

        floatingButtonCrearEvento();

        iniciarViewModel();

    }

    /**
     * Cargar datos desde intent (EntrevistaListaActivity)
     */
    private void obtenerDatosBundle() {

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            entrevista = new Entrevista();
            entrevista.setId(bundle.getInt(getString(R.string.KEY_ENTREVISTA_ID_LARGO)));
            entrevista.setId_entrevistado(bundle.getInt(getString(R.string.KEY_ENTREVISTA_ID_ENTREVISTADO)));

            fecha_entrevista = bundle.getString(getString(R.string.KEY_ENTREVISTA_FECHA_ENTREVISTA));

            entrevistado = new Entrevistado();
            entrevistado.setNombre(bundle.getString(getString(R.string.KEY_ENTREVISTADO_NOMBRE_LARGO)));
            entrevistado.setApellido(bundle.getString(getString(R.string.KEY_ENTREVISTADO_APELLIDO_LARGO)));

            n_entrevistas = bundle.getInt(getString(R.string.KEY_ENTREVISTADO_N_ENTREVISTAS));
            n_normales = bundle.getString(getString(R.string.KEY_ENTREVISTA_N_NORMALES));
            n_extraordnarias = bundle.getString(getString(R.string.KEY_ENTREVISTA_N_EXTRAORDINARIAS));

        }
    }

    /**
     * Inicializar recursos de interfaz
     */
    private void setearRecursosInterfaz() {

        eventoList = new ArrayList<>();

        progressBar = findViewById(R.id.progress_bar_eventos);
        progressBar.setVisibility(View.VISIBLE);

        viewPager = findViewById(R.id.view_pager_events);
        viewPager.setVisibility(View.INVISIBLE);

        tv_eventos_vacios = findViewById(R.id.tv_eventos_vacios);
        tv_eventos_vacios.setVisibility(View.INVISIBLE);

        tabLayout = findViewById(R.id.tab_dots);
        tabLayout.setupWithViewPager(viewPager, true);

        tv_nombreApellido = findViewById(R.id.tv_entrevistado_nombre);
        tv_n_entrevistas = findViewById(R.id.tv_n_entrevistas);
        tv_normales = findViewById(R.id.tv_normales_value);
        tv_extraodrinarias = findViewById(R.id.tv_extraordinarias_value);

        tv_nombreApellido.setText(String.format("%s %s", entrevistado.getNombre(), entrevistado.getApellido()));
        tv_normales.setText(n_normales);
        tv_extraodrinarias.setText(n_extraordnarias);

        //Contar cantidad de entrevistas
        if (n_entrevistas == 1) {
            tv_n_entrevistas.setText(String.format(Locale.US, getString(R.string.FORMATO_N_ENTREVISTA), n_entrevistas));
        } else {
            tv_n_entrevistas.setText(String.format(Locale.US, getString(R.string.FORMATO_N_ENTREVISTAS), n_entrevistas));
        }

        eventosListaViewModel = ViewModelProviders.of(this).get(EventosListaViewModel.class);

        fragmentStatePageAdapter = new FragmentStatePageAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, eventoList, fecha_entrevista, EventosActivity.this, EventosActivity.this);
        viewPager.setAdapter(fragmentStatePageAdapter);
    }

    /**
     * Iniciar observadores de viewModels
     */
    private void iniciarViewModel() {


        eventosListaViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.INVISIBLE);
                    tv_eventos_vacios.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);

                    if (eventoList.size() == 0) {
                        viewPager.setVisibility(View.GONE);
                        tv_eventos_vacios.setVisibility(View.VISIBLE);
                    } else {
                        tv_eventos_vacios.setVisibility(View.INVISIBLE);
                        viewPager.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        //Observador de carga de eventos
        eventosListaViewModel.cargarEventos(entrevista).observe(this, new Observer<List<Evento>>() {
            @Override
            public void onChanged(List<Evento> eventos) {

                if (eventos != null && eventos.size() > 0) {
                    viewPager.setVisibility(View.GONE);
                    eventoList = eventos;
                    Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_EVENTOS), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), eventoList.toString()));
                    fragmentStatePageAdapter.actualizarLista(eventoList);
                    fragmentStatePageAdapter.notifyDataSetChanged();
                    viewPager.setAdapter(fragmentStatePageAdapter);
                }

            }
        });

        //Observador de error al cargar eventos
        eventosListaViewModel.mostrarMsgErrorListado().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.eventos_lista), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_EVENTOS), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
            }
        });

        //Observador para actualizacion
        eventosListaViewModel.mostrarMsgEliminar().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (s.equals(getString(R.string.MSG_DELETE_EVENTO))) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.eventos_lista), Snackbar.LENGTH_LONG, s, null);
                    EventoRepositorio.getInstancia(getApplication()).obtenerEventosEntrevista(entrevista);

                    fragmentStatePageAdapter.notifyDataSetChanged();
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_EVENTO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
            }
        });

        eventosListaViewModel.mostrarMsgErrorEliminar().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.eventos_lista), Snackbar.LENGTH_LONG, s, null);
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_EVENTO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    /**
     * Boton flotante para crear evento
     */
    private void floatingButtonCrearEvento() {

        FloatingActionButton fb_crear_evento = findViewById(R.id.fb_crear_evento);

        fb_crear_evento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventosActivity.this, NuevoEventoActivity.class);
                intent.putExtra(getString(R.string.KEY_ENTREVISTA_ID_LARGO), entrevista.getId());
                startActivityForResult(intent, REQUEST_CODE_NUEVO_EVENTO);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actualizar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_actualizar) {

            //Refrescar eventos
            progressBar.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.INVISIBLE);

            eventosListaViewModel.refreshEventos(entrevista);

            Objects.requireNonNull(viewPager.getAdapter()).notifyDataSetChanged();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showSnackbar(View v, int snack_length, String titulo, String accion) {

        Snackbar snackbar = Snackbar.make(v, titulo, snack_length);

        if (accion != null) {
            snackbar.setAction(accion, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    isSnackBarShow = false;

                    //Refresh listado de usuarios
                    eventosListaViewModel.refreshEventos(entrevista);

                    progressBar.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.INVISIBLE);
                }
            });
        }
        snackbar.show();
        isSnackBarShow = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_NUEVO_EVENTO) {

            if (resultCode == RESULT_OK) {

                assert data != null;
                Bundle bundle = data.getExtras();
                assert bundle != null;
                String msg_registro = bundle.getString(getString(R.string.INTENT_KEY_MSG_REGISTRO));

                if (msg_registro != null) {
                    isSnackBarShow = false;
                    showSnackbar(findViewById(R.id.eventos_lista), Snackbar.LENGTH_LONG, msg_registro, null);
                    eventosListaViewModel.refreshEventos(entrevista);

                    fragmentStatePageAdapter.notifyDataSetChanged();
                }
            }
        } else if (requestCode == REQUEST_CODE_EDITAR_EVENTO) {

            if (resultCode == RESULT_OK) {

                assert data != null;
                Bundle bundle = data.getExtras();
                assert bundle != null;
                String msg_actualizacion = bundle.getString(getString(R.string.INTENT_KEY_MSG_ACTUALIZACION));

                if (msg_actualizacion != null) {
                    isSnackBarShow = false;
                    showSnackbar(findViewById(R.id.eventos_lista), Snackbar.LENGTH_LONG, msg_actualizacion, null);
                    eventosListaViewModel.refreshEventos(entrevista);

                    fragmentStatePageAdapter.notifyDataSetChanged();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Object object) {
        EventoRepositorio.getInstancia(getApplication()).eliminarEvento((Evento) object);
    }
}
