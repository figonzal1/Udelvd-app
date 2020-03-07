package cl.udelvd.vistas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import cl.udelvd.R;
import cl.udelvd.adaptadores.AccionAdapter;
import cl.udelvd.modelo.Accion;
import cl.udelvd.repositorios.AccionRepositorio;
import cl.udelvd.utilidades.SnackbarInterface;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.AccionesListViewModel;
import cl.udelvd.vistas.fragments.DeleteDialogListener;

public class AccionesListActivity extends AppCompatActivity implements SnackbarInterface, DeleteDialogListener {

    private static final int REQUEST_CODE_CREAR_ACCION = 200;
    private List<Accion> accionesList;
    private ProgressBar progressBar;
    private TextView tv_acciones_vacios;
    private RecyclerView rv;
    private AccionesListViewModel accionesListaViewModel;
    private AccionAdapter accionAdapter;
    private boolean isSnackBarShow = false;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acciones_list);

        Utils.configurarToolbar(this, getApplicationContext(), 0, getString(R.string.TITULO_TOOLBAR_LISTADO_ACCIONES));

        instanciarRecursosInterfaz();

        iniciarViewModelListado();

        floatingButtonNuevaAccion();
    }

    private void instanciarRecursosInterfaz() {

        accionesList = new ArrayList<>();

        progressBar = findViewById(R.id.progress_bar_acciones);
        progressBar.setVisibility(View.VISIBLE);

        tv_acciones_vacios = findViewById(R.id.tv_acciones_vacias);
        tv_acciones_vacios.setVisibility(View.INVISIBLE);

        rv = findViewById(R.id.rv_lista_acciones);

        LinearLayoutManager ly = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(ly);

        accionesListaViewModel = ViewModelProviders.of(this).get(AccionesListViewModel.class);

        accionAdapter = new AccionAdapter(
                accionesList,
                getApplicationContext(),
                getSupportFragmentManager()
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
                }
            }
        });

        accionesListaViewModel.cargarAcciones().observe(this, new Observer<List<Accion>>() {
            @Override
            public void onChanged(List<Accion> accions) {
                if (accions != null) {

                    accionesList = accions;
                    accionAdapter.actualizarLista(accionesList);

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
                    rv.setVisibility(View.INVISIBLE);
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.acciones_lista), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                    accionAdapter.notifyDataSetChanged();
                }
            }
        });

        /*
        ELIMINACION
         */
        accionesListaViewModel.mostrarMsgEliminar().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.INVISIBLE);

                if (s.equals(getString(R.string.MSG_DELETE_ACCION))) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.acciones_lista), Snackbar.LENGTH_LONG, s, null);
                    AccionRepositorio.getInstancia(getApplication()).obtenerAcciones();
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_ACCION), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

            }
        });

        accionesListaViewModel.mostrarMsgErrorEliminar().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.INVISIBLE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;

                    if (!s.equals(getString(R.string.SERVER_ERROR_MSG_VM))) {
                        rv.setVisibility(View.INVISIBLE);
                        showSnackbar(findViewById(R.id.acciones_lista), Snackbar.LENGTH_INDEFINITE, s, null);
                    } else {
                        showSnackbar(findViewById(R.id.acciones_lista), Snackbar.LENGTH_LONG, s, null);
                    }
                    accionAdapter.notifyDataSetChanged();
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_ACCION), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            }
        });
    }

    /**
     * Boton flotante para crear entrevistas
     */
    private void floatingButtonNuevaAccion() {
        FloatingActionButton fabNuevaAccion = findViewById(R.id.fb_crear_accion);
        fabNuevaAccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccionesListActivity.this, NuevaAccionActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CREAR_ACCION);
            }
        });
    }

    @Override
    public void showSnackbar(View v, int duration, String titulo, String accion) {

        snackbar = Snackbar.make(v, titulo, duration);
        if (accion != null) {
            snackbar.setAction(accion, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progressBar.setVisibility(View.VISIBLE);

                    isSnackBarShow = false;
                    if (snackbar != null) {
                        snackbar.dismiss();
                    }

                    accionesListaViewModel.refreshAcciones();


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
            if (snackbar != null) {
                snackbar.dismiss();
            }
            accionesListaViewModel.refreshAcciones();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_CREAR_ACCION) {

            if (resultCode == RESULT_OK) {

                assert data != null;
                Bundle bundle = data.getExtras();
                assert bundle != null;
                String msg_registro = bundle.getString(getString(R.string.INTENT_KEY_MSG_REGISTRO));

                if (msg_registro != null) {
                    isSnackBarShow = false;
                    showSnackbar(findViewById(R.id.acciones_lista), Snackbar.LENGTH_LONG, msg_registro, null);
                    accionesListaViewModel.refreshAcciones();
                }
            }
        } /*else if (requestCode == REQUEST_CODE_EDITAR_EVENTO) {

            if (resultCode == RESULT_OK) {

                assert data != null;
                Bundle bundle = data.getExtras();
                assert bundle != null;
                String msg_actualizacion = bundle.getString(getString(R.string.INTENT_KEY_MSG_ACTUALIZACION));

                if (msg_actualizacion != null) {
                    isSnackBarShow = false;
                    showSnackbar(findViewById(R.id.eventos_lista), Snackbar.LENGTH_LONG, msg_actualizacion, null);
                    eventosListaViewModel.refreshEventos(entrevista);

                    fragmentStatePageAdapter.notifyDataSetChanged();
                }
            }
        }*/

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDialogPositiveClick(Object object) {
        AccionRepositorio.getInstancia(getApplication()).eliminarAccion((Accion) object);
    }
}
