package cl.udelvd.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cl.udelvd.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView tv_nombre;
    private TextView tv_email;
    private TextView tv_activado;
    private TextView tv_registro_cuenta;

    private String nombre;
    private String apellido;
    private String email;
    private String password;

    private int id_rol;
    private int id;
    private static final int PROFILE_ACTIVITY_CODE = 200;
    private static final int EDIT_PROFILE_CODE = 201;
    private String nombreRol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.d("PROFILE_ACTIVITY", "LLAMADO DE ACTIVIDAD");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorOnPrimary));
        setSupportActionBar(toolbar);

        //Setear toolbar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        actionBar.setTitle("Perfil");

        tv_nombre = findViewById(R.id.tv_nombre_investigador);
        tv_activado = findViewById(R.id.tv_activado);
        tv_email = findViewById(R.id.tv_email_investigador);
        tv_registro_cuenta = findViewById(R.id.tv_registro);

        //Lectura de datos del investigador
        SharedPreferences sharedPreferences = getSharedPreferences("udelvd", Context.MODE_PRIVATE);
        id = sharedPreferences.getInt("id_investigador", 0);
        id_rol = sharedPreferences.getInt("id_rol_investigador", 0);
        nombreRol = sharedPreferences.getString("nombre_rol_investigador", "");
        nombre = sharedPreferences.getString("nombre_investigador", "");
        apellido = sharedPreferences.getString("apellido_investigador", "");
        email = sharedPreferences.getString("email_investigador", "");
        boolean activado = sharedPreferences.getBoolean("activado_investigador", false);
        String create_time = sharedPreferences.getString("create_time_investigador", "");
        password = sharedPreferences.getString("password_investigador", "");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.US);

        try {
            Date registro = simpleDateFormat.parse(create_time);
            assert registro != null;
            create_time = simpleDateFormat.format(registro);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tv_nombre.setText(nombre + " " + apellido);
        tv_email.setText(email);
        if (activado) {
            tv_activado.setText("Activado");
        } else {
            tv_activado.setText("No activada");
        }
        tv_registro_cuenta.setText(create_time);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);

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
        } else if (item.getItemId() == R.id.menu_edit_profile) {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);

            //Enviar datos de investigador hacia formulario de edicion
            intent.putExtra("id", id);
            intent.putExtra("nombre", nombre);
            intent.putExtra("apellido", apellido);
            intent.putExtra("email", email);
            intent.putExtra("id_rol", id_rol);
            intent.putExtra("password", password);
            intent.putExtra("nombre_rol", nombreRol);
            startActivityForResult(intent, EDIT_PROFILE_CODE);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == EDIT_PROFILE_CODE) {
            Log.d("FINISH_EDIT_PROFILE_ACT", "Cerrando formulario de edicion");
            recreate();
        }
    }

    @Override
    public void onBackPressed() {

        //Enviar codigo OK a mainActivity
        Intent intent = getIntent();
        setResult(PROFILE_ACTIVITY_CODE, intent);
        finish();

        super.onBackPressed();
    }
}
