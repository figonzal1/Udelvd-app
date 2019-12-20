package cl.udelvd.vistas.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.adaptadores.FragmentPageAdapter;
import cl.udelvd.utilidades.Utils;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private static final int PROFILE_ACTIVITY_CODE = 200;

    private String nombreRolInvestigador;

    private SharedPreferences sharedPreferences;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("udelvd", Context.MODE_PRIVATE);

        Utils.checkJWT(sharedPreferences, MainActivity.this);

        setearToolbarViewPagerTabsDrawer();

    }


    /**
     * Funcion encargada de setear el nombre del investigador logueado  en el header del drawer
     */
    private void configurarDrawerHeader() {

        //Obtener header de navigation drawer
        View header = navigationView.getHeaderView(0);
        TextView tv_nombre_apellido_investigador = header.findViewById(R.id.tv_header_nombre_apellido_usuario);
        TextView tv_email_investigador = header.findViewById(R.id.tv_header_email_usuario);
        TextView tv_nombre_rol_investigador = header.findViewById(R.id.tv_header_nombre_rol);

        //Obtener datos usuario logeado

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

        Toolbar toolbar = findViewById(R.id.toolbar);
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
        tabLayout = findViewById(R.id.tabs);
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
        navigationView.setCheckedItem(R.id.menu_entrevistados);
        Objects.requireNonNull(tabLayout.getTabAt(0)).select();

        configurarDrawerHeader();

        //Si el usuario no es admin, ocultar panel
        if (!nombreRolInvestigador.equals("Administrador")) {
            navigationView.getMenu().findItem(R.id.group_admin).setVisible(false);
        }

        navigationListener();

        tabsListener();

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
                }

                //LOGOUT
                if (menuItem.getItemId() == R.id.menu_logout) {

                    String token = sharedPreferences.getString("TOKEN_LOGIN", "");

                    if (!token.isEmpty()) {
                        Log.d("LOGOUT", "BORRANDO TOKEN: " + token);
                        sharedPreferences.edit().remove("TOKEN_LOGIN").apply();

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

                Log.d("TAB SELECTED", String.valueOf(tab.getPosition()));

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_ACTIVITY_CODE) {
            Log.d("FINISH_PROFILE_ACTIVITY", "Seteando navigation en listado");
            navigationView.setCheckedItem(R.id.menu_entrevistados);
            Objects.requireNonNull(tabLayout.getTabAt(0)).select();
        }
    }
}
