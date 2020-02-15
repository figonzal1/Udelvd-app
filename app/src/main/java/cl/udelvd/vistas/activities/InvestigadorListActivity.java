package cl.udelvd.vistas.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import cl.udelvd.R;
import cl.udelvd.adaptadores.InvestigadorAdapter;
import cl.udelvd.modelo.Investigador;
import cl.udelvd.utilidades.SnackbarInterface;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.InvestigadorListaViewModel;
import cl.udelvd.vistas.fragments.DeleteDialogListener;

public class InvestigadorListActivity extends AppCompatActivity implements SnackbarInterface, DeleteDialogListener {

    private RecyclerView rv;
    private List<Investigador> investigadorList;
    private InvestigadorListaViewModel investigadorListaViewModel;
    private InvestigadorAdapter investigadorAdapter;

    private ProgressBar progressBar;
    private boolean isSnackBarShow = false;
    private TextView tv_investigadores_vacios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investigadores_list);

        Utils.configurarToolbar(this, getApplicationContext(), 0, "Listado Investigadores");

        investigadorList = new ArrayList<>();

        progressBar = findViewById(R.id.progress_bar_investigadores);
        progressBar.setVisibility(View.VISIBLE);

        tv_investigadores_vacios = findViewById(R.id.tv_investigadores_vacios);

        rv = findViewById(R.id.rv_lista_investigadores);

        LinearLayoutManager ly = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(ly);

        investigadorListaViewModel = ViewModelProviders.of(this).get(InvestigadorListaViewModel.class);

        investigadorAdapter = new InvestigadorAdapter(
                investigadorList,
                getApplicationContext(),
                InvestigadorListActivity.this,
                getSupportFragmentManager()
        );

        rv.setAdapter(investigadorAdapter);


        investigadorListaViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);
                    tv_investigadores_vacios.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.VISIBLE);
                }
            }
        });

        investigadorListaViewModel.cargarInvestigadores().observe(this, new Observer<List<Investigador>>() {
            @Override
            public void onChanged(List<Investigador> investigadors) {

                investigadorList = investigadors;
                investigadorAdapter = new InvestigadorAdapter(
                        investigadorList,
                        getApplicationContext(),
                        InvestigadorListActivity.this,
                        getSupportFragmentManager()
                );
                investigadorAdapter.notifyDataSetChanged();
                rv.setAdapter(investigadorAdapter);

                if (investigadorList.size() == 0) {
                    tv_investigadores_vacios.setVisibility(View.VISIBLE);
                } else {
                    tv_investigadores_vacios.setVisibility(View.INVISIBLE);
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_INVESTIGADORES), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
            }
        });

        investigadorListaViewModel.mostrarMsgErrorListado().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.INVISIBLE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.investigadores_lista), Snackbar.LENGTH_LONG, s, null);
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTADO_INVESTIGADORES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    @Override
    public void showSnackbar(View v, int tipo_snackbar, String titulo, String accion) {

        Snackbar snackbar = Snackbar.make(v, titulo, tipo_snackbar);
        if (accion != null) {
            snackbar.setAction(accion, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Refresh listado de usuarios
                    //entrevistasListaViewModel.refreshEntrevistas(entrevistado);

                    //cv_lista_entrevistas.setVisibility(View.INVISIBLE);

                    //progressBar.setVisibility(View.VISIBLE);

                    //isSnackBarShow = false;


                }
            });
        }
        snackbar.show();
        //isSnackBarShow = false;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Object object) {
        Toast.makeText(getApplicationContext(), "Activar Inves", Toast.LENGTH_LONG).show();
    }
}
