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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import cl.udelvd.R;
import cl.udelvd.modelo.Investigador;
import cl.udelvd.utilidades.Utils;

public class PerfilActivity extends AppCompatActivity {

    private TextView tv_nombre;
    private TextView tv_activado;
    private TextView tv_email;
    private TextView tv_registro_cuenta;

    private Investigador investigador;

    private static final int PROFILE_ACTIVITY_CODE = 200;
    private static final int EDIT_PROFILE_CODE = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Utils.configurarToolbar(this, getApplicationContext(), R.drawable.ic_arrow_back_black_24dp, getString(R.string.TITULO_TOOLBAR_PERFIL));

        instanciarRecursosInterfaz();

        cargarDatosInvestigador();
    }


    private void instanciarRecursosInterfaz() {
        tv_nombre = findViewById(R.id.tv_nombre_investigador);
        tv_activado = findViewById(R.id.tv_activado);
        tv_email = findViewById(R.id.tv_email_investigador);
        tv_registro_cuenta = findViewById(R.id.tv_registro);
    }

    private void cargarDatosInvestigador() {

        //Lectura de datos del investigador
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);

        investigador = new Investigador();
        investigador.setId(sharedPreferences.getInt(getString(R.string.SHARED_PREF_INVES_ID), 0));
        investigador.setNombre(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_NOMBRE), ""));
        investigador.setApellido(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_APELLIDO), ""));
        investigador.setEmail(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_EMAIL), ""));
        investigador.setActivado(sharedPreferences.getBoolean(getString(R.string.SHARED_PREF_INVES_ACTIVADO), false));
        investigador.setCreateTime(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_CREATE_TIME), ""));
        investigador.setPassword(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_PASSWORD), ""));

        //Rol investigador
        investigador.setIdRol(sharedPreferences.getInt(getString(R.string.SHARED_PREF_INVES_ID_ROL), 0));
        investigador.setNombreRol(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_NOMBRE_ROL), ""));

        tv_nombre.setText(String.format("%s %s", investigador.getNombre(), investigador.getApellido()));
        tv_email.setText(investigador.getEmail());
        if (investigador.isActivado()) {
            tv_activado.setText(R.string.PERFIL_ACTIVADO);
        } else {
            tv_activado.setText(R.string.PERFIL_NO_ACTIVADO);
        }
        tv_registro_cuenta.setText(Utils.dateToString(getApplicationContext(), false, Utils.stringToDate(getApplicationContext(), false, investigador.getCreateTime())));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_perfil, menu);

        return true;
    }

    /**
     * Funcion que maneja el click de parent Activity
     *
     * @param item Item que recibe el click
     * @return True | False
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //Boton para abrir Navigation Drawer
        if (item.getItemId() == android.R.id.home) {

            //Enviar codigo OK a mainActivity
            Intent intent = getIntent();
            setResult(PROFILE_ACTIVITY_CODE, intent);
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_editar_perfil) {
            Intent intent = new Intent(PerfilActivity.this, EditarPerfilActivity.class);

            //Enviar datos de investigador hacia formulario de edicion
            intent.putExtra(getString(R.string.KEY_INVES_ID), investigador.getId());
            intent.putExtra(getString(R.string.KEY_INVES_NOMBRE), investigador.getNombre());
            intent.putExtra(getString(R.string.KEY_INVES_APELLIDO), investigador.getApellido());
            intent.putExtra(getString(R.string.KEY_INVES_EMAIL), investigador.getEmail());
            intent.putExtra(getString(R.string.KEY_INVES_PASSWORD), investigador.getPassword());

            intent.putExtra(getString(R.string.KEY_INVES_ID_ROL), investigador.getIdRol());
            intent.putExtra(getString(R.string.KEY_INVES_NOMBRE_ROL), investigador.getNombreRol());

            startActivityForResult(intent, EDIT_PROFILE_CODE);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_CODE) {
            if (resultCode == RESULT_OK) {

                assert data != null;
                Bundle bundle = data.getExtras();

                assert bundle != null;

                showSnackbar(findViewById(R.id.perfil_investigador), bundle.getString("msg_update"));

                cargarDatosInvestigador();

                Log.d(getString(R.string.TAG_EDIT_PROFILE_RESULT), getString(R.string.EDIT_PROFILE_RESULT_MSG));

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //Enviar codigo OK a mainActivity para fijar navigationDrawer
        Intent intent = getIntent();
        setResult(PROFILE_ACTIVITY_CODE, intent);
        finish();
    }

    /**
     * Funcion para mostrar el snackbar en fragment
     *
     * @param v      View donde se mostrara el snackbar
     * @param titulo Titulo del snackbar
     */
    private void showSnackbar(View v, String titulo) {

        Snackbar snackbar = Snackbar.make(v, titulo, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
