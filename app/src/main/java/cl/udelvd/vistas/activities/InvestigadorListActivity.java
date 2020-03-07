package cl.udelvd.vistas.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cl.udelvd.R;
import cl.udelvd.adaptadores.InvestigadorAdapter;
import cl.udelvd.modelo.Investigador;
import cl.udelvd.repositorios.InvestigadorRepositorio;
import cl.udelvd.utilidades.SnackbarInterface;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.InvestigadorListaViewModel;
import cl.udelvd.vistas.fragments.DeleteDialogListener;

public class InvestigadorListActivity extends AppCompatActivity implements SnackbarInterface, DeleteDialogListener {

    private static final int INVESTIGADORES_ACTIVITY_CODE = 200;
    private RecyclerView rv;
    private List<Investigador> investigadorList;
    private InvestigadorListaViewModel investigadorListaViewModel;
    private InvestigadorAdapter investigadorAdapter;

    private ProgressBar progressBar;
    private boolean isSnackBarShow = false;
    private TextView tv_investigadores_vacios;
    private Investigador investigador;
    private int investigadores_totales;
    private TextView tv_n_ivestigadores;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investigadores_list);

        Utils.configurarToolbar(this, getApplicationContext(), 0, getString(R.string.TITULO_TOOLBAR_LISTADO_INVESTIGADORES));

        obtenerDatosAdmin();

        instanciarRecursosInterfaz();

        iniciarViewModelListado();
    }

    private void instanciarRecursosInterfaz() {

        investigadorList = new ArrayList<>();

        progressBar = findViewById(R.id.progress_bar_investigadores);
        progressBar.setVisibility(View.VISIBLE);

        rv = findViewById(R.id.rv_lista_investigadores);

        LinearLayoutManager ly = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(ly);

        tv_n_ivestigadores = findViewById(R.id.tv_n_investigadores);
        tv_n_ivestigadores.setVisibility(View.INVISIBLE);

        tv_investigadores_vacios = findViewById(R.id.tv_investigadores_vacios);
        tv_investigadores_vacios.setVisibility(View.INVISIBLE);

        investigadorListaViewModel = ViewModelProviders.of(this).get(InvestigadorListaViewModel.class);

        investigadorAdapter = new InvestigadorAdapter(
                investigadorList,
                getApplicationContext(),
                getSupportFragmentManager(),
                investigadorListaViewModel,
                investigador
        );

        rv.setAdapter(investigadorAdapter);
    }

    private void iniciarViewModelListado() {
        investigadorListaViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);
                    tv_investigadores_vacios.setVisibility(View.INVISIBLE);
                    tv_n_ivestigadores.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.VISIBLE);
                }
            }
        });

        investigadorListaViewModel.mostrarNEntrevistados().observe(InvestigadorListActivity.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                investigadores_totales = integer;
            }
        });

        investigadorListaViewModel.mostrarPrimeraPagina(1, investigador).observe(this, new Observer<List<Investigador>>() {
            @Override
            public void onChanged(List<Investigador> investigadors) {

                investigadorList = investigadors;
                investigadorAdapter.actualizarLista(investigadorList);
                rv.setAdapter(investigadorAdapter);

                progressBar.setVisibility(View.INVISIBLE);
                if (investigadorList.size() == 0) {
                    tv_investigadores_vacios.setVisibility(View.VISIBLE);
                } else {
                    tv_investigadores_vacios.setVisibility(View.INVISIBLE);
                }

                tv_n_ivestigadores.setVisibility(View.VISIBLE);
                tv_n_ivestigadores.setText(String.format(Locale.getDefault(), getString(R.string.MOSTRAR_INVESTIGADORES), investigadorAdapter.getInvestigadorList().size(), investigadores_totales));

                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_INVESTIGADORES), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

            }
        });

        investigadorListaViewModel.mostrarSiguientePagina().observe(this, new Observer<List<Investigador>>() {
            @Override
            public void onChanged(List<Investigador> investigadors) {


                investigadorAdapter.agregarEntrevistados(investigadors);
                investigadorAdapter.ocultarProgress();

                investigadorList = investigadorAdapter.getInvestigadorList();

                if (investigadorList.size() == 0) {
                    tv_investigadores_vacios.setVisibility(View.VISIBLE);
                } else {
                    tv_investigadores_vacios.setVisibility(View.INVISIBLE);
                }
                tv_n_ivestigadores.setVisibility(View.VISIBLE);
                tv_n_ivestigadores.setText(String.format(Locale.getDefault(), getString(R.string.MOSTRAR_INVESTIGADORES), investigadorAdapter.getInvestigadorList().size(), investigadores_totales));

                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_INVESTIGADORES), getString(R.string.VIEW_MODEL_LISTA_INVESTIGADORES_MSG) + "PAGINA");
            }
        });

        investigadorListaViewModel.mostrarMsgErrorListado().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.INVISIBLE);

                if (!isSnackBarShow) {
                    rv.setVisibility(View.INVISIBLE);
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.investigadores_lista), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                    investigadorAdapter.notifyDataSetChanged();
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTADO_INVESTIGADORES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });


        /*
        ACTIVACIONES
         */
        investigadorListaViewModel.mostrarMsgActivacion().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.INVISIBLE);

                if (s.equals(getString(R.string.MSG_INVEST_CUENTA_ACTIVADA)) || s.equals(getString(R.string.MSG_INVEST_CUENTA_DESACTIVADA))) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.investigadores_lista), Snackbar.LENGTH_LONG, s, null);
                    InvestigadorRepositorio.getInstance(getApplication()).obtenerInvestigadores(1, investigador);
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_ACTIVACION_INVES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
            }
        });

        investigadorListaViewModel.mostrarMsgErrorActivacion().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.INVISIBLE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;

                    if (!s.equals(getString(R.string.SERVER_ERROR_MSG_VM))) {
                        rv.setVisibility(View.INVISIBLE);
                        showSnackbar(findViewById(R.id.investigadores_lista), Snackbar.LENGTH_INDEFINITE, s, null);
                    } else {
                        showSnackbar(findViewById(R.id.investigadores_lista), Snackbar.LENGTH_LONG, s, null);
                    }

                    investigadorAdapter.notifyDataSetChanged();
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_ACTIVACION_INVES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    private void obtenerDatosAdmin() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);
        investigador = new Investigador();
        investigador.setId(sharedPreferences.getInt(getString(R.string.SHARED_PREF_INVES_ID), 0));
    }

    @Override
    public void showSnackbar(View v, int tipo_snackbar, String titulo, String accion) {

        snackbar = Snackbar.make(v, titulo, tipo_snackbar);
        if (accion != null) {
            snackbar.setAction(accion, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progressBar.setVisibility(View.VISIBLE);

                    isSnackBarShow = false;
                    if (snackbar != null) {
                        snackbar.dismiss();
                    }

                    //Refresh listado de investigadores
                    investigadorAdapter.resetPages();
                    investigadorListaViewModel.refreshInvestigadores(investigador);

                }
            });
        }
        snackbar.show();
        isSnackBarShow = false;
    }

    @Override
    public void onDialogPositiveClick(Object object) {
        Investigador invAdapter = (Investigador) object;
        InvestigadorRepositorio.getInstance(getApplication()).activarCuenta(invAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actualizar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = getIntent();
            setResult(INVESTIGADORES_ACTIVITY_CODE, intent);
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_actualizar) {

            progressBar.setVisibility(View.VISIBLE);

            isSnackBarShow = false;
            if (snackbar != null) {
                snackbar.dismiss();
            }

            investigadorAdapter.resetPages();
            investigadorListaViewModel.refreshInvestigadores(investigador);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
