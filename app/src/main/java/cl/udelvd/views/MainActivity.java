package cl.udelvd.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Objects;

import cl.udelvd.FragmentPageAdapter;
import cl.udelvd.R;
import cl.udelvd.model.Usuario;
import cl.udelvd.repositorios.UsuarioRepositorio;
import cl.udelvd.utils.Utils;
import cl.udelvd.viewmodel.UsuarioViewModel;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private TextView tv_nombre_apellido_investigador;
    private TextView tv_email_investigador;
    private TextView tv_nombre_rol_investigador;
    private String nombreRolInvestigador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean expired = Utils.isJWTExpired(getApplicationContext());
        if (expired) {

            Log.d("INTENT", "DESVIANDO A LOGIN ACTIVITY");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        setearToolbarViewPagerTabsDrawer();

        //viewModelObserver();
    }


    /**
     * Funcion encargada de realizar la observacion del token de autentificacion
     */
    private void viewModelObserver() {

        //TODO: Valicacion de token deberia tener su propio viewmodel
        UsuarioViewModel usuarioViewModel = ViewModelProviders.of(this).get(UsuarioViewModel.class);

        usuarioViewModel.mostrarListaUsuarios().observe(this, new Observer<List<Usuario>>() {
            @Override
            public void onChanged(List<Usuario> usuarios) {

            }
        });

        //Llamado de datos (LLamado hacerlo en fragment)
        UsuarioRepositorio usuarioRepositorio = UsuarioRepositorio.getInstance(getApplication());
        usuarioRepositorio.getUsuarios();
    }

    /**
     * Funcion encargada de setear el nombre del investigador logueado  en el header del drawer
     */
    private void configurarDrawerHeader() {

        //Obtener header de navigation drawer
        View header = navigationView.getHeaderView(0);
        tv_nombre_apellido_investigador =
                header.findViewById(R.id.tv_header_nombre_apellido_usuario);
        tv_email_investigador = header.findViewById(R.id.tv_header_email_usuario);
        tv_nombre_rol_investigador = header.findViewById(R.id.tv_header_nombre_rol);

        //Obtener datos usuario logeado
        SharedPreferences sharedPreferences = getSharedPreferences("udelvd", Context.MODE_PRIVATE);

        String nombreInvestigador = sharedPreferences.getString("nombre_investigador", "");
        String apellidoInvestigador = sharedPreferences.getString("apellido_investigador", "");
        nombreRolInvestigador = sharedPreferences.getString("nombre_rol_investigador", "");
        String emailInvestigador = sharedPreferences.getString("email_investigador", "");

        //Setear datos en pantalla
        tv_nombre_apellido_investigador.setText(nombreInvestigador + " " + apellidoInvestigador);
        tv_email_investigador.setText(emailInvestigador);
        tv_nombre_rol_investigador.setText(nombreRolInvestigador);
    }

    /**
     * Funcion encargada de configurar elementos de UI
     */
    private void setearToolbarViewPagerTabsDrawer() {

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorOnPrimary));
        setSupportActionBar(toolbar);

        //Setear toolbar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        //ViewPager
        ViewPager viewPager = findViewById(R.id.view_pager_main);
        viewPager.setAdapter(new FragmentPageAdapter(getSupportFragmentManager()));

        //TabLayout
        final TabLayout tabLayout = findViewById(R.id.tabs);
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


        //Drawer Navigation
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        //Set default tab
        Objects.requireNonNull(tabLayout.getTabAt(0)).select();
        navigationView.setCheckedItem(R.id.menu_adult_list);

        configurarDrawerHeader();

        //Si el usuario no es admin, ocultar panel
        if (!nombreRolInvestigador.equals("Administrador")) {
            navigationView.getMenu().findItem(R.id.group_admin).setVisible(false);
        }

        //Menu listener de navigations
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                //Menu general
                if (menuItem.getItemId() == R.id.menu_profile) {
                    navigationView.setCheckedItem(menuItem);
                    drawerLayout.closeDrawer(GravityCompat.START, true);
                    return true;
                } else if (menuItem.getItemId() == R.id.menu_adult_list) {
                    navigationView.setCheckedItem(menuItem);
                    Objects.requireNonNull(tabLayout.getTabAt(0)).select();
                    drawerLayout.closeDrawer(GravityCompat.START, true);
                    return true;
                } else if (menuItem.getItemId() == R.id.menu_statistics) {
                    navigationView.setCheckedItem(menuItem);
                    Objects.requireNonNull(tabLayout.getTabAt(1)).select();
                    drawerLayout.closeDrawer(GravityCompat.START, true);
                    return true;
                }

                //Menu admin
                if (menuItem.getItemId() == R.id.menu_actions) {
                    navigationView.setCheckedItem(menuItem);
                    return true;
                } else if (menuItem.getItemId() == R.id.menu_emoticons) {
                    navigationView.setCheckedItem(menuItem);
                    return true;
                }

                return false;
            }
        });

        //Menu listener de tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    navigationView.setCheckedItem(R.id.menu_adult_list);
                } else if (tab.getPosition() == 1) {
                    navigationView.setCheckedItem(R.id.menu_statistics);
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

    /**
     * Funcion que maneja el click de navigation drawer
     *
     * @param item Item que recibe el click
     * @return True | False
     */
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
    protected void onResume() {

        super.onResume();
    }
}
