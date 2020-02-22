package cl.udelvd.vistas.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import cl.udelvd.repositorios.InvestigadorRepositorio;
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
    private Investigador investigador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investigadores_list);

        Utils.configurarToolbar(this, getApplicationContext(), 0, "Listado Investigadores");

        instanciarRecursosInterfaz();

        obtenerDatosAdmin();

        iniciarViewModelListado();
    }

    private void iniciarViewModelListado() {
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

        investigadorListaViewModel.cargarInvestigadores(investigador).observe(this, new Observer<List<Investigador>>() {
            @Override
            public void onChanged(List<Investigador> investigadors) {

                if (investigadors != null && investigadors.size() > 0) {
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
            }
        });

        investigadorListaViewModel.mostrarMsgErrorListado().

                observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        progressBar.setVisibility(View.INVISIBLE);

                        if (!isSnackBarShow) {
                            isSnackBarShow = true;
                            showSnackbar(findViewById(R.id.investigadores_lista), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                        }

                        Log.d(getString(R.string.TAG_VIEW_MODEL_LISTADO_INVESTIGADORES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
                    }
                });


        investigadorListaViewModel.mostrarMsgActivacion().

                observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d(getString(R.string.TAG_VIEW_MODEL_LISTADO_INVESTIGADORES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                        if (s.equals(getString(R.string.MSG_INVEST_CUENTA_ACTIVADA)) || s.equals(getString(R.string.MSG_INVEST_CUENTA_DESACTIVADA))) {

                            //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            showSnackbar(findViewById(R.id.investigadores_lista), Snackbar.LENGTH_LONG, s, null);
                        }

                    }
                });

        investigadorListaViewModel.mostrarMsgErrorActivacion().

                observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        progressBar.setVisibility(View.INVISIBLE);

                        if (!isSnackBarShow) {
                            isSnackBarShow = true;
                            showSnackbar(findViewById(R.id.investigadores_lista), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                        }

                        Log.d(getString(R.string.TAG_VIEW_MODEL_LISTADO_INVESTIGADORES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
                    }
                });
    }

    private void obtenerDatosAdmin() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);
        investigador = new Investigador();
        investigador.setId(sharedPreferences.getInt(getString(R.string.SHARED_PREF_INVES_ID), 0));
    }

    private void instanciarRecursosInterfaz() {

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
    }

    @Override
    public void showSnackbar(View v, int tipo_snackbar, String titulo, String accion) {

        Snackbar snackbar = Snackbar.make(v, titulo, tipo_snackbar);
        if (accion != null) {
            snackbar.setAction(accion, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Refresh listado de investigadores
                    investigadorListaViewModel.refreshInvestigadores(investigador);

                    progressBar.setVisibility(View.VISIBLE);

                    isSnackBarShow = false;


                }
            });
        }
        snackbar.show();
        isSnackBarShow = false;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Object object) {
        Investigador invAdapter = (Investigador) object;
        InvestigadorRepositorio.getInstance(getApplication()).activarCuenta(invAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
