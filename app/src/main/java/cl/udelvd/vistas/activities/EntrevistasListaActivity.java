package cl.udelvd.vistas.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import cl.udelvd.NewInterviewDialog;
import cl.udelvd.R;
import cl.udelvd.adaptadores.EntrevistaAdapter;
import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.viewmodel.EntrevistaViewModel;

public class EntrevistasListaActivity extends AppCompatActivity {

    private RecyclerView rv;
    private EntrevistaAdapter entrevistaAdapter;
    private FloatingActionButton fab;
    private EntrevistaViewModel entrevistaViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interviews_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorOnPrimary));
        setSupportActionBar(toolbar);

        //Boton atras
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Listado entrevistas");
        actionBar.setDisplayHomeAsUpEnabled(true);

        rv = findViewById(R.id.rv_lista_entrevistas);

        LinearLayoutManager ly = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(ly);

        entrevistaAdapter = new EntrevistaAdapter(new ArrayList<Entrevista>(), getApplicationContext());
        rv.setAdapter(entrevistaAdapter);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        int id_entrevistado = bundle.getInt("id_entrevistado");
        Entrevistado entrevistado = new Entrevistado();
        entrevistado.setId(id_entrevistado);

        entrevistaViewModel = ViewModelProviders.of(this).get(EntrevistaViewModel.class);
        entrevistaViewModel.cargarEntrevistas(entrevistado).observe(this, new Observer<List<Entrevista>>() {
            @Override
            public void onChanged(List<Entrevista> entrevistas) {
                if (entrevistas != null) {

                    entrevistaAdapter = new EntrevistaAdapter(entrevistas, getApplicationContext());
                    entrevistaAdapter.notifyDataSetChanged();
                    rv.setAdapter(entrevistaAdapter);
                }
            }
        });


        fab = findViewById(R.id.fb_crear_entrevista);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*NewUserDialog dialog = new NewUserDialog();
                assert getFragmentManager() != null;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "NewUserDialog");*/

                /*DialogFragment dialogFragment = new NewUserDialog();
                assert getFragmentManager() != null;
                dialogFragment.show(getFragmentManager(),"NewUserDialog");*/

                NewInterviewDialog fragment = new NewInterviewDialog ();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, fragment)
                        .addToBackStack(null).commit();
            }
        });
    }
}
