package cl.udelvd.vistas.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.adaptadores.EntrevistadoAdapter;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.utilidades.SnackbarInterface;
import cl.udelvd.viewmodel.EntrevistadoListaViewModel;
import cl.udelvd.vistas.activities.NuevoEntrevistadoActivity;

import static android.app.Activity.RESULT_OK;


public class EntrevistadoListaFragment extends Fragment implements SnackbarInterface {

    private static final int REQUEST_CODE_NUEVA_ENTREVISTADO = 200;
    private RecyclerView rv;
    private EntrevistadoListaViewModel entrevistadoListaViewModel;
    private EntrevistadoAdapter entrevistadoAdapter;
    private ProgressBar progressBar;
    private TextView tv_entrevistados_vacios;
    private TextView tv_n_entrevistados;
    private List<Entrevistado> entrevistadoList;
    private View v;
    private int entrevistados_totales;
    private static final int REQUEST_CODE_EDITAR_ENTREVISTADO = 300;
    private boolean isSnackBarShow = false;

    public EntrevistadoListaFragment() {
        // Required empty public constructor
    }


    public static EntrevistadoListaFragment newInstance() {
        return new EntrevistadoListaFragment();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_lista_entrevistados, container, false);

        instanciarRecursosInterfaz(v);

        obtenerDatosBundle(v);

        iniciarViewModelObservers(v);

        floatingButtonCrearEntrevistado(v);

        return v;
    }

    private void obtenerDatosBundle(View v) {

        if (getArguments() != null) {
            String msg_login = getArguments().getString(getString(R.string.INTENT_KEY_MSG_LOGIN));

            if (msg_login != null) {
                Snackbar.make(v.findViewById(R.id.entrevistados_lista), msg_login, Snackbar.LENGTH_LONG).show();
            }

        }
    }

    private void instanciarRecursosInterfaz(View v) {

        entrevistadoList = new ArrayList<>();

        progressBar = v.findViewById(R.id.progress_bar_entrevistados);
        progressBar.setVisibility(View.VISIBLE);

        rv = v.findViewById(R.id.rv_lista_usuarios);
        rv.setVisibility(View.VISIBLE);

        LinearLayoutManager ly = new LinearLayoutManager(getContext());
        rv.setLayoutManager(ly);
        rv.setAdapter(new EntrevistadoAdapter(
                new ArrayList<Entrevistado>(),
                getContext(),
                EntrevistadoListaFragment.this,
                Objects.requireNonNull(getActivity()).getSupportFragmentManager(),
                entrevistadoListaViewModel)
        );

        tv_entrevistados_vacios = v.findViewById(R.id.tv_entrevistados_vacios);

        tv_n_entrevistados = v.findViewById(R.id.tv_n_entrevistados);
        tv_n_entrevistados.setVisibility(View.INVISIBLE);

        entrevistadoListaViewModel = ViewModelProviders.of(this).get(EntrevistadoListaViewModel.class);

        entrevistadoAdapter = new EntrevistadoAdapter(
                entrevistadoList,
                getContext(),
                EntrevistadoListaFragment.this,
                Objects.requireNonNull(getActivity()).getSupportFragmentManager(),
                entrevistadoListaViewModel
        );
    }

    /**
     * Funcion encargada de la logica del boton flotante para crear entrevistados
     *
     * @param v Vista para encontrar boton flotante en interfaz del fragmento
     */
    private void floatingButtonCrearEntrevistado(View v) {

        FloatingActionButton fbCrearUsuario = v.findViewById(R.id.fb_crear_usuario);
        fbCrearUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), NuevoEntrevistadoActivity.class);
                startActivityForResult(intent, REQUEST_CODE_NUEVA_ENTREVISTADO);
            }
        });
    }

    /**
     * Funcion que instancia
     *
     * @param v Vista para mostrar snackbar
     */
    private void iniciarViewModelObservers(final View v) {

        entrevistadoListaViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);
                    tv_entrevistados_vacios.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.INVISIBLE);

                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.VISIBLE);
                }
            }
        });

        entrevistadoListaViewModel.mostrarNEntrevistados().observe(EntrevistadoListaFragment.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                entrevistados_totales = integer;
            }
        });


        entrevistadoListaViewModel.mostrarPrimeraPagina(1).observe(this, new Observer<List<Entrevistado>>() {
            @Override
            public void onChanged(List<Entrevistado> listado) {

                entrevistadoList = listado;
                entrevistadoAdapter.actualizarLista(entrevistadoList);
                rv.setAdapter(entrevistadoAdapter);

                progressBar.setVisibility(View.INVISIBLE);
                if (entrevistadoList.size() == 0) {
                    tv_entrevistados_vacios.setVisibility(View.VISIBLE);
                } else {
                    tv_entrevistados_vacios.setVisibility(View.INVISIBLE);
                }

                tv_n_entrevistados.setVisibility(View.VISIBLE);
                tv_n_entrevistados.setText(String.format(Locale.getDefault(), getString(R.string.MOSTRAR_ENTREVISTADOS), entrevistadoAdapter.getEntrevistadoList().size(), entrevistados_totales));

                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ENTREVISTADO), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
            }
        });

        entrevistadoListaViewModel.mostrarSiguientePagina().observe(this, new Observer<List<Entrevistado>>() {
            @Override
            public void onChanged(List<Entrevistado> entrevistados) {


                entrevistadoAdapter.agregarEntrevistados(entrevistados);
                entrevistadoAdapter.ocultarProgress();

                entrevistadoList = entrevistadoAdapter.getEntrevistadoList();

                if (entrevistadoList.size() == 0) {
                    tv_entrevistados_vacios.setVisibility(View.VISIBLE);
                } else {
                    tv_entrevistados_vacios.setVisibility(View.INVISIBLE);
                }

                tv_n_entrevistados.setVisibility(View.VISIBLE);
                tv_n_entrevistados.setText(String.format(Locale.getDefault(), getString(R.string.MOSTRAR_ENTREVISTADOS), entrevistadoAdapter.getEntrevistadoList().size(), entrevistados_totales));
                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ENTREVISTADO), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG) + "PAGINA");
            }
        });

        //Manejador de Respuestas erroreas en fragment
        entrevistadoListaViewModel.mostrarMsgErrorListado().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.INVISIBLE);

                if (entrevistadoAdapter != null) {
                    entrevistadoAdapter.ocultarProgress();
                    entrevistadoAdapter.resetPages();
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(v, Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }

            }
        });

        entrevistadoListaViewModel.mostrarMsgEliminar().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (s.equals(getString(R.string.MSG_DELETE_ENTREVISTADO))) {
                    isSnackBarShow = true;
                    showSnackbar(v.findViewById(R.id.entrevistados_lista), Snackbar.LENGTH_LONG, s, null);
                    entrevistadoListaViewModel.refreshListaEntrevistados();
                }
            }
        });

        entrevistadoListaViewModel.mostrarMsgErrorEliminar().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.INVISIBLE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(v, Snackbar.LENGTH_LONG, s, null);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_actualizar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menu_actualizar) {
            progressBar.setVisibility(View.VISIBLE);
            isSnackBarShow = false;
            entrevistadoListaViewModel.refreshListaEntrevistados();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_NUEVA_ENTREVISTADO) {

            if (resultCode == RESULT_OK) {

                assert data != null;
                Bundle bundle = data.getExtras();

                assert bundle != null;
                String msg_registro = bundle.getString(getString(R.string.INTENT_KEY_MSG_REGISTRO));

                if (msg_registro != null) {
                    isSnackBarShow = true;
                    showSnackbar(v.findViewById(R.id.entrevistados_lista), Snackbar.LENGTH_LONG, msg_registro, null);
                    entrevistadoListaViewModel.refreshListaEntrevistados();
                }
            }
        } else if (requestCode == REQUEST_CODE_EDITAR_ENTREVISTADO) {
            if (resultCode == RESULT_OK) {

                assert data != null;
                Bundle bundle = data.getExtras();

                assert bundle != null;
                String msg_actualizacion = bundle.getString(getString(R.string.INTENT_KEY_MSG_ACTUALIZACION));

                if (msg_actualizacion != null) {
                    isSnackBarShow = true;
                    showSnackbar(v.findViewById(R.id.entrevistados_lista), Snackbar.LENGTH_LONG, msg_actualizacion, null);
                    entrevistadoListaViewModel.refreshListaEntrevistados();
                }
            }
        }
    }

    @Override
    public void showSnackbar(View v, int duration, String titulo, String accion) {
        Snackbar snackbar = Snackbar.make(v.findViewById(R.id.entrevistados_lista), titulo, duration);

        if (accion != null) {
            snackbar.setAction(accion, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Refresh listado de usuarios
                    entrevistadoListaViewModel.refreshListaEntrevistados();

                    progressBar.setVisibility(View.VISIBLE);

                    isSnackBarShow = false;
                }
            });
        }
        snackbar.show();
        isSnackBarShow = false;
    }
}
