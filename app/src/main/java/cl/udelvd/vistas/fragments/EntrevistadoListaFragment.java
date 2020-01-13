package cl.udelvd.vistas.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private SwipeRefreshLayout swipeRefreshLayout;


    public EntrevistadoListaFragment() {
        // Required empty public constructor
    }


    public static EntrevistadoListaFragment newInstance() {
        return new EntrevistadoListaFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_lista_entrevistados, container, false);

        configurarRecyclerView(v);

        iniciarViewModelObservers(v);

        floatingButtonCrearEntrevistado(v);

        iniciarSwipeRefresh(v);

        return v;
    }

    private void configurarRecyclerView(View v) {
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
                /*NewUserDialog dialog = new NewUserDialog();
                assert getFragmentManager() != null;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "NewUserDialog");*/

                /*DialogFragment dialogFragment = new NewUserDialog();
                assert getFragmentManager() != null;
                dialogFragment.show(getFragmentManager(),"NewUserDialog");*/

                /*NewUserDialog fragment = new NewUserDialog();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, fragment)
                        .addToBackStack(null).commit();*/

            }
        });
    }

    /**
     * Funcion encargada de configurar la lógica del SwipeRefresh
     *
     * @param v Vista usada para buscar swipeRefresh en fragment
     */
    private void iniciarSwipeRefresh(View v) {
        swipeRefreshLayout = v.findViewById(R.id.refresh_usuarios);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorSecondary), getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Forzar refresh
                entrevistadoViewModel.refreshListaEntrevistados();
                swipeRefreshLayout.setRefreshing(true);
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

                swipeRefreshLayout.setRefreshing(false);

                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ENTREVISTADO), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
            }
        });

        //Manejador de Respuestas erroreas en fragment
        entrevistadoViewModel.mostrarErrorRespuesta().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (s.equals(getString(R.string.TIMEOUT_ERROR_MSG_VM))) {
                    showSnackbar(v, s, getString(R.string.SNACKBAR_REINTENTAR));
                } else if (s.equals(getString(R.string.NETWORK_ERROR_MSG_VM))) {
                    showSnackbar(v, s, getString(R.string.SNACKBAR_REINTENTAR));
                }
                swipeRefreshLayout.setRefreshing(false);
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

        Snackbar snackbar = Snackbar.make(v.findViewById(R.id.fragment_list), titulo, Snackbar.LENGTH_INDEFINITE)
                .setAction(accion, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Refresh listado de usuarios
                        entrevistadoViewModel.refreshListaEntrevistados();

                        swipeRefreshLayout.setRefreshing(true);
                    }
                });

        snackbar.show();
    }
}
