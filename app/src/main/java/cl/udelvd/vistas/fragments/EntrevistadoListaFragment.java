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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import cl.udelvd.R;
import cl.udelvd.adaptadores.EntrevistadoAdapter;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.viewmodel.EntrevistadoViewModel;
import cl.udelvd.vistas.activities.NuevoEntrevistadoActivity;


public class EntrevistadoListaFragment extends Fragment {

    private RecyclerView rv;
    private EntrevistadoViewModel entrevistadoViewModel;
    private EntrevistadoAdapter entrevistadoAdapter;
    private ProgressBar progressBar;


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
        final View v = inflater.inflate(R.layout.fragment_lista_entrevistados, container, false);

        instanciarRecursosInterfaz(v);

        iniciarViewModelObservers(v);

        floatingButtonCrearEntrevistado(v);

        return v;
    }

    private void instanciarRecursosInterfaz(View v) {

        progressBar = v.findViewById(R.id.progress_bar_entrevistados);
        progressBar.setVisibility(View.VISIBLE);

        rv = v.findViewById(R.id.rv_lista_usuarios);

        LinearLayoutManager ly = new LinearLayoutManager(getContext());
        rv.setLayoutManager(ly);
        rv.setAdapter(new EntrevistadoAdapter(new ArrayList<Entrevistado>(), getContext()));
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
                startActivity(intent);
            }
        });
    }

    /**
     * Funcion que instancia
     *
     * @param v Vista para mostrar snackbar
     */
    private void iniciarViewModelObservers(final View v) {
        entrevistadoViewModel = ViewModelProviders.of(this).get(EntrevistadoViewModel.class);

        //Manejador de listado de usuarios
        entrevistadoViewModel.mostrarListaEntrevistados().observe(this, new Observer<List<Entrevistado>>() {
            @Override
            public void onChanged(List<Entrevistado> entrevistadoList) {

                entrevistadoAdapter = new EntrevistadoAdapter(entrevistadoList, getContext());
                entrevistadoAdapter.notifyDataSetChanged();
                rv.setAdapter(entrevistadoAdapter);

                progressBar.setVisibility(View.INVISIBLE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ENTREVISTADO), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
            }
        });

        //Manejador de Respuestas erroreas en fragment
        entrevistadoViewModel.mostrarErrorRespuesta().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ENTREVISTADO), String.format("%s LISTADO %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                if (s.equals(getString(R.string.TIMEOUT_ERROR_MSG_VM))) {
                    showSnackbar(v, s, getString(R.string.SNACKBAR_REINTENTAR));
                } else if (s.equals(getString(R.string.NETWORK_ERROR_MSG_VM))) {
                    showSnackbar(v, s, getString(R.string.SNACKBAR_REINTENTAR));
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }


    /**
     * Funcion para mostrar el snackbar en fragment
     *
     * @param v      View donde se mostrara el snackbar
     * @param titulo Titulo del snackbar
     * @param accion Boton de accion del snackbar
     */
    private void showSnackbar(View v, String titulo, String accion) {

        Snackbar snackbar = Snackbar.make(v.findViewById(R.id.entrevistados_lista), titulo, Snackbar.LENGTH_INDEFINITE)
                .setAction(accion, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Refresh listado de usuarios
                        entrevistadoViewModel.refreshListaEntrevistados();

                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

        snackbar.show();
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
            entrevistadoViewModel.refreshListaEntrevistados();
        }
        return super.onOptionsItemSelected(item);
    }
}
