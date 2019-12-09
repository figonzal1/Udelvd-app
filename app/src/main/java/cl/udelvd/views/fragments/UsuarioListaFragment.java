package cl.udelvd.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import cl.udelvd.NewUserDialog;
import cl.udelvd.R;
import cl.udelvd.repositorios.UsuarioRepositorio;
import cl.udelvd.viewmodel.UsuarioViewModel;


public class UsuarioListaFragment extends Fragment {

    private FloatingActionButton fbCrearUsuario;

    private RecyclerView rv;
    private UsuarioViewModel usuarioViewModel;


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

        /*usuarioViewModel = ViewModelProviders.of(this).get(UsuarioViewModel.class);

        usuarioViewModel.mostrarListaUsuarios().observe(this, new Observer<List<Usuario>>() {
            @Override
            public void onChanged(List<Usuario> usuarioList) {
                Log.d("VIEW_MODEl_LIST", String.valueOf(usuarioList));
            }
        });*/

        UsuarioRepositorio usuarioRepositorio = UsuarioRepositorio.getInstance(Objects.requireNonNull(getActivity()).getApplication());
        usuarioRepositorio.getUsuarios();


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

        return v;
    }
}
