package cl.udelvd.vistas.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.adaptadores.FragmentPageAdapter;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.modelo.Investigador;
import cl.udelvd.repositorios.EntrevistadoRepositorio;
import cl.udelvd.servicios.MyFirebaseMessagingService;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.vistas.fragments.DeleteDialogListener;

public class MainActivity extends AppCompatActivity implements DeleteDialogListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private static final int PROFILE_ACTIVITY_CODE = 200;

    private SharedPreferences sharedPreferences;
    private TabLayout tabLayout;

    private Investigador investigador;

    private TextView tv_nombre_apellido_investigador;
    private TextView tv_email_investigador;
    private TextView tv_nombre_rol_investigador;
    private ViewPager viewPager;
    private String msg_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);

        Utils.checkPlayServices(MainActivity.this);

        Utils.checkFirebaseServices(MainActivity.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MyFirebaseMessagingService.createChannel(this);
        }

        String rol = sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_NOMBRE_ROL), getString(R.string.ROL_INVESTIGADOR));
        //Si el rol es de admin las notificaciones llegan
        if (rol.equals(getString(R.string.ROL_ADMIN_KEY_MASTER))) {
            MyFirebaseMessagingService.suscribirTema(this);
        } else {
            MyFirebaseMessagingService.eliminarSuscripcionTema(this);
        }

        Utils.handleJWT(sharedPreferences, MainActivity.this);

        Utils.configurarToolbar(this, getApplicationContext(), R.drawable.ic_menu_white_24dp, null);

        instanciarRecursosInterfaz();

        obtenerDatosBundles();

        setearViewPagerTabsDrawer();

        obtenerDesvioNotificacion();
    }

    private void obtenerDesvioNotificacion() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.containsKey(getString(R.string.NOTIFICACION_INTENT_ACTIVADO))) {

            if (bundle.getBoolean(getString(R.string.NOTIFICACION_INTENT_ACTIVADO))) {

                Intent intent = new Intent(MainActivity.this, InvestigadorListActivity.class);
                startActivity(intent);
            }
        }
    }

    private void instanciarRecursosInterfaz() {
        viewPager = findViewById(R.id.view_pager_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        tabLayout = findViewById(R.id.tabs);

        View header = navigationView.getHeaderView(0);
        tv_nombre_apellido_investigador = header.findViewById(R.id.tv_header_nombre_apellido_usuario);
        tv_email_investigador = header.findViewById(R.id.tv_header_email_usuario);
        tv_nombre_rol_investigador = header.findViewById(R.id.tv_header_nombre_rol);
    }

    private void obtenerDatosBundles() {
        investigador = new Investigador();
        investigador.setNombre(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_NOMBRE), ""));
        investigador.setApellido(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_APELLIDO), ""));
        investigador.setNombreRol(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_NOMBRE_ROL), ""));
        investigador.setEmail(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_EMAIL), ""));

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            msg_login = bundle.getString(getString(R.string.INTENT_KEY_MSG_LOGIN));
        }
    }

    /**
     * Funcion encargada de configurar elementos de UI
     */
    private void setearViewPagerTabsDrawer() {

        //ViewPager

        viewPager.setAdapter(new FragmentPageAdapter(getSupportFragmentManager(), getApplicationContext(), msg_login));

        //TabLayout

        tabLayout.setupWithViewPager(viewPager);

        //Setear iconos
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            if (i == 0) {
                tab.setIcon(R.drawable.ic_list_black_24dp);
            } else if (i == 1) {
                tab.setIcon(R.drawable.ic_show_chart_black_24dp);
            }
        }

        //Set default tab
        navigationView.setCheckedItem(R.id.menu_entrevistados);
        Objects.requireNonNull(tabLayout.getTabAt(0)).select();

        cargarDatosInvestigador();

        //Si el usuario no es admin, ocultar panel
        if (!investigador.getNombreRol().equals(getString(R.string.ROL_ADMIN_KEY_MASTER))) {
            navigationView.getMenu().findItem(R.id.group_admin).setVisible(false);
        }
        navigationListener();

        tabsListener();
    }

    private void cargarDatosInvestigador() {

        //Setear datos en pantalla
        tv_nombre_apellido_investigador.setText(String.format("%s %s", investigador.getNombre(), investigador.getApellido()));
        tv_email_investigador.setText(investigador.getEmail());

        if (investigador.getNombreRol().equals(getString(R.string.ROL_ADMIN_KEY_MASTER))) {
            tv_nombre_rol_investigador.setText(getString(R.string.ROL_ADMINITRADOR));
        } else {
            tv_nombre_rol_investigador.setText(getString(R.string.ROL_INVESTIGADOR));
        }

    }

    /**
     * Funcion encargada de la logica de items de navigation drawer
     */
    private void navigationListener() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                //Menu general
                if (menuItem.getItemId() == R.id.menu_perfil) {
                    drawerLayout.closeDrawer(GravityCompat.START, true);

                    Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
                    startActivityForResult(intent, PROFILE_ACTIVITY_CODE);

                    return true;
                } else if (menuItem.getItemId() == R.id.menu_entrevistados) {
                    Objects.requireNonNull(tabLayout.getTabAt(0)).select();
                    drawerLayout.closeDrawer(GravityCompat.START, true);
                    return true;
                } else if (menuItem.getItemId() == R.id.menu_estadisticas) {
                    Objects.requireNonNull(tabLayout.getTabAt(1)).select();
                    drawerLayout.closeDrawer(GravityCompat.START, true);
                    return true;
                }

                //Menu admin
                if (menuItem.getItemId() == R.id.menu_acciones) {
                    return true;
                } else if (menuItem.getItemId() == R.id.menu_emoticones) {
                    return true;
                } else if (menuItem.getItemId() == R.id.menu_investigadores) {
                    Intent intent = new Intent(MainActivity.this, InvestigadorListActivity.class);
                    startActivity(intent);
                    return true;
                }

                //LOGOUT
                if (menuItem.getItemId() == R.id.menu_logout) {

                    String token = sharedPreferences.getString(getString(R.string.SHARED_PREF_TOKEN_LOGIN), "");

                    if (!token.isEmpty()) {
                        Log.d(getString(R.string.TAG_TOKEN_LOGOUT), String.format("%s %s", getString(R.string.TOKEN_LOGOUT_MSG), token));
                        sharedPreferences.edit().remove(getString(R.string.SHARED_PREF_TOKEN_LOGIN)).apply();

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    return true;
                }

                return false;
            }
        });
    }

    /**
     * Funcion encargada de la logica de los tabs
     */
    private void tabsListener() {

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Log.d(getString(R.string.TAG_TAB_SELECTED), String.valueOf(tab.getPosition()));

                //Modificar navigation drawer segun tabs (Swipe de fragments)
                if (tab.getPosition() == 0) {
                    navigationView.setCheckedItem(R.id.menu_entrevistados);
                } else if (tab.getPosition() == 1) {
                    navigationView.setCheckedItem(R.id.menu_estadisticas);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //Boton para abrir Navigation Drawer
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == PROFILE_ACTIVITY_CODE) {
            navigationView.setCheckedItem(R.id.menu_entrevistados);
            Objects.requireNonNull(tabLayout.getTabAt(0)).select();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Object object) {
        EntrevistadoRepositorio.getInstance(getApplication()).eliminarEntrevistado((Entrevistado) object);
    }
}
