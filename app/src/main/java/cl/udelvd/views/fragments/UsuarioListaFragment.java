package cl.udelvd.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import cl.udelvd.NewUserDialog;
import cl.udelvd.R;
import cl.udelvd.model.Usuario;
import cl.udelvd.viewmodel.UsuarioViewModel;


public class UsuarioListaFragment extends Fragment {

    private FloatingActionButton fbCrearUsuario;

    private RecyclerView rv;
    private UsuarioViewModel usuarioViewModel;
    private UsuarioAdapter usuarioAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;


    public UsuarioListaFragment() {
        // Required empty public constructor
    }


    public static UsuarioListaFragment newInstance() {
        return new UsuarioListaFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_list,container,false);

        rv = v.findViewById(R.id.rv_lista_usuarios);


        LinearLayoutManager ly = new LinearLayoutManager(getContext());
        rv.setLayoutManager(ly);
        rv.setAdapter(new UsuarioAdapter(new ArrayList<Usuario>()));

        usuarioViewModel = ViewModelProviders.of(this).get(UsuarioViewModel.class);

        //Manejador de listado de usuarios
        usuarioViewModel.mostrarListaUsuarios().observe(this, new Observer<List<Usuario>>() {
            @Override
            public void onChanged(List<Usuario> usuarioList) {

                usuarioAdapter = new UsuarioAdapter(usuarioList);
                usuarioAdapter.notifyDataSetChanged();
                rv.setAdapter(usuarioAdapter);

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //Manejador de Respuestas erroreas en fragment
        usuarioViewModel.mostrarErrorRespuesta().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        fbCrearUsuario = v.findViewById(R.id.fb_crear_usuario);
        fbCrearUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*NewUserDialog dialog = new NewUserDialog();
                assert getFragmentManager() != null;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "NewUserDialog");*/

                /*DialogFragment dialogFragment = new NewUserDialog();
                assert getFragmentManager() != null;
                dialogFragment.show(getFragmentManager(),"NewUserDialog");*/

                NewUserDialog fragment = new NewUserDialog ();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, fragment)
                        .addToBackStack(null).commit();
            }
        });


        swipeRefreshLayout = v.findViewById(R.id.refresh_usuarios);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorSecondary), getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                usuarioViewModel.refreshListaUsuarios();

                swipeRefreshLayout.setRefreshing(true);
            }
        });

        return v;
    }
}
