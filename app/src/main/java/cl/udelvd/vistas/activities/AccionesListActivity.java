package cl.udelvd.vistas.activities;

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

import cl.udelvd.R;
import cl.udelvd.adaptadores.AccionAdapter;
import cl.udelvd.modelo.Accion;
import cl.udelvd.utilidades.SnackbarInterface;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.AccionesListViewModel;

public class AccionesListActivity extends AppCompatActivity implements SnackbarInterface {

    private List<Accion> accionesList;
    private ProgressBar progressBar;
    private TextView tv_acciones_vacios;
    private RecyclerView rv;
    private AccionesListViewModel accionesListaViewModel;
    private AccionAdapter accionAdapter;
    private boolean isSnackBarShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acciones_list);

        Utils.configurarToolbar(this, getApplicationContext(), 0, getString(R.string.TITULO_TOOLBAR_LISTADO_ACCIONES));

        instanciarRecursosInterfaz();

        iniciarViewModelListado();
    }

    private void instanciarRecursosInterfaz() {

        accionesList = new ArrayList<>();

        progressBar = findViewById(R.id.progress_bar_acciones);
        progressBar.setVisibility(View.VISIBLE);

        tv_acciones_vacios = findViewById(R.id.tv_acciones_vacias);

        rv = findViewById(R.id.rv_lista_acciones);

        LinearLayoutManager ly = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(ly);

        accionesListaViewModel = ViewModelProviders.of(this).get(AccionesListViewModel.class);

        accionAdapter = new AccionAdapter(
                accionesList,
                getApplicationContext()
        );

        rv.setAdapter(accionAdapter);
    }

    private void iniciarViewModelListado() {

        accionesListaViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);
                    tv_acciones_vacios.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.VISIBLE);

                    if (accionesList.size() == 0) {
                        tv_acciones_vacios.setVisibility(View.VISIBLE);
                    } else {
                        tv_acciones_vacios.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        accionesListaViewModel.cargarAcciones().observe(this, new Observer<List<Accion>>() {
            @Override
            public void onChanged(List<Accion> accions) {
                if (accions != null) {

                    accionesList = accions;
                    accionAdapter.actualizarLista(accionesList);
                    rv.setAdapter(accionAdapter);

                    progressBar.setVisibility(View.INVISIBLE);
                    if (accionesList.size() == 0) {
                        tv_acciones_vacios.setVisibility(View.VISIBLE);
                    } else {
                        tv_acciones_vacios.setVisibility(View.INVISIBLE);
                    }

                    Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ACCIONES), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                }
            }
        });

        accionesListaViewModel.mostrarMsgErrorListado().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.INVISIBLE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ACCIONES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.acciones_lista), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }
            }
        });
    }

    @Override
    public void showSnackbar(View v, int duration, String titulo, String accion) {

        Snackbar snackbar = Snackbar.make(v, titulo, duration);
        if (accion != null) {
            snackbar.setAction(accion, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Refresh listado de investigadores
                    //accionAdapter.resetPages();
                    accionesListaViewModel.refreshAcciones();

                    progressBar.setVisibility(View.VISIBLE);

                    isSnackBarShow = false;


                }
            });
        }
        snackbar.show();
        isSnackBarShow = false;
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
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_actualizar) {

            progressBar.setVisibility(View.VISIBLE);
            isSnackBarShow = false;
            accionesListaViewModel.refreshAcciones();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
