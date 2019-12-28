package cl.udelvd.vistas.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private TextView tv_nombreCompleto;
    private TextView tv_n_entrevistas;
    private TextView tv_entrevistas_normales;
    private TextView tv_entrevistas_extraordinarias;

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

        rv = findViewById(R.id.rv_lista_entrevistas);

        LinearLayoutManager ly = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(ly);

        entrevistaAdapter = new EntrevistaAdapter(new ArrayList<Entrevista>(), getApplicationContext());
        rv.setAdapter(entrevistaAdapter);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        Entrevistado entrevistado = new Entrevistado();

        int id_entrevistado = bundle.getInt("id_entrevistado");
        String nombre = bundle.getString("nombre_entrevistado");
        String apellido = bundle.getString("apellido_entrevistado");
        int n_entrevistas = bundle.getInt("n_entrevistas");

        entrevistado.setId(id_entrevistado);
        entrevistado.setNombre(nombre);
        entrevistado.setApellido(apellido);
        entrevistado.setN_entrevistas(n_entrevistas);

        tv_nombreCompleto = findViewById(R.id.tv_entrevistado_nombre);
        tv_n_entrevistas = findViewById(R.id.tv_n_entrevistas);
        tv_entrevistas_normales = findViewById(R.id.tv_normales_value);
        tv_entrevistas_extraordinarias = findViewById(R.id.tv_extraordinarias_value);

        tv_nombreCompleto.setText(nombre + " " + apellido);
        if (n_entrevistas == 1) {
            tv_n_entrevistas.setText(n_entrevistas + " entrevista");
        } else {
            tv_n_entrevistas.setText(n_entrevistas + " entrevistas");
        }

        entrevistaViewModel = ViewModelProviders.of(this).get(EntrevistaViewModel.class);
        entrevistaViewModel.cargarEntrevistas(entrevistado).observe(this, new Observer<List<Entrevista>>() {
            @Override
            public void onChanged(List<Entrevista> entrevistas) {
                if (entrevistas != null) {

                    entrevistaAdapter = new EntrevistaAdapter(entrevistas, getApplicationContext());
                    entrevistaAdapter.notifyDataSetChanged();
                    rv.setAdapter(entrevistaAdapter);

                    //Contar tipos de entrevistas
                    Map<String, Integer> tipos = contarTipos(entrevistas);
                    tv_entrevistas_normales.setText(String.valueOf(tipos.get("normales")));
                    tv_entrevistas_extraordinarias.setText(String.valueOf(tipos.get("extraordinarias")));
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

                NewInterviewDialog fragment = new NewInterviewDialog();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, fragment)
                        .addToBackStack(null).commit();
            }
        });
    }

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
}
