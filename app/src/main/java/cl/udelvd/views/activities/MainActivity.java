package cl.udelvd.views.activities;

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
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.adapters.FragmentPageAdapter;
import cl.udelvd.models.Interviewee;
import cl.udelvd.models.Researcher;
import cl.udelvd.repositories.IntervieweeRepository;
import cl.udelvd.services.MyFirebaseMessagingService;
import cl.udelvd.utils.Utils;
import cl.udelvd.views.fragments.dialog.DeleteDialogListener;

public class MainActivity extends AppCompatActivity implements DeleteDialogListener {

    private static final int PROFILE_ACTIVITY_CODE = 200;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private SharedPreferences sharedPreferences;
    private TabLayout tabLayout;

    private Researcher researcher;

    private TextView tvResearchCompleteName;
    private TextView tvResearchEmail;
    private TextView tvResearchRoleName;
    private ViewPager viewPager;
    private String msgLogin;
    private String researchRole;

    private FirebaseCrashlytics crashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        crashlytics = FirebaseCrashlytics.getInstance();

        sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);

        Utils.checkPlayServices(MainActivity.this);

        Utils.checkFirebaseServices(MainActivity.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MyFirebaseMessagingService.createChannel(this);
        }

        researchRole = sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_NOMBRE_ROL), getString(R.string.ROL_INVESTIGADOR));
        crashlytics.setCustomKey(getString(R.string.SHARED_PREF_INVES_NOMBRE_ROL), getString(R.string.ROL_INVESTIGADOR));

        //If is Admin Role, notifications coming
        if (researchRole.equals(getString(R.string.ROL_ADMIN_KEY_MASTER))) {
            MyFirebaseMessagingService.suscriptionTheme(this);
        } else {
            MyFirebaseMessagingService.deleteSuscriptionTheme(this);
        }

        Utils.handleJWT(sharedPreferences, MainActivity.this);

        Utils.configToolbar(this, getApplicationContext(), R.drawable.ic_menu_white_24dp, null);

        instantiateInterfaceResources();

        getBundleData();

        setViewPagerTabsDrawer();

        newResearcherNotification();
    }

    private void newResearcherNotification() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.containsKey(getString(R.string.NOTIFICACION_INTENT_ACTIVADO))) {

            if (bundle.getBoolean(getString(R.string.NOTIFICACION_INTENT_ACTIVADO))) {

                Intent intent = new Intent(MainActivity.this, ResearcherListActivity.class);
                startActivity(intent);
            }
        }
    }

    private void instantiateInterfaceResources() {

        viewPager = findViewById(R.id.view_pager_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        tabLayout = findViewById(R.id.tabs);

        View header = navigationView.getHeaderView(0);
        tvResearchCompleteName = header.findViewById(R.id.tv_header_nombre_apellido_usuario);
        tvResearchEmail = header.findViewById(R.id.tv_header_email_usuario);
        tvResearchRoleName = header.findViewById(R.id.tv_header_nombre_rol);
    }

    private void getBundleData() {

        researcher = new Researcher();
        researcher.setName(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_NOMBRE), ""));
        researcher.setLastName(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_APELLIDO), ""));
        researcher.setRolName(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_NOMBRE_ROL), ""));
        researcher.setEmail(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_EMAIL), ""));

        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();
            msgLogin = bundle.getString(getString(R.string.INTENT_KEY_MSG_LOGIN));
        }
    }

    private void setViewPagerTabsDrawer() {

        viewPager.setAdapter(new FragmentPageAdapter(getSupportFragmentManager(), getApplicationContext(), msgLogin));

        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {

            TabLayout.Tab tab = tabLayout.getTabAt(i);

            if (tab != null) {

                if (i == 0) {

                    tab.setIcon(R.drawable.ic_list_black_24dp);

                } else if (i == 1) {

                    tab.setIcon(R.drawable.ic_show_chart_black_24dp);
                }
            }
        }

        navigationView.setCheckedItem(R.id.menu_interviewees);
        Objects.requireNonNull(tabLayout.getTabAt(0), "Get tab 0 cannot be null").select();

        cargarDatosInvestigador();

        //Hide admin panel
        if (!researcher.getRolName().equals(getString(R.string.ROL_ADMIN_KEY_MASTER))) {
            navigationView.getMenu().findItem(R.id.group_admin).setVisible(false);
        }

        navigationListener();

        tabsListener();
    }

    private void cargarDatosInvestigador() {

        tvResearchCompleteName.setText(String.format("%s %s", researcher.getName(), researcher.getLastName()));
        tvResearchEmail.setText(researcher.getEmail());

        if (researcher.getRolName().equals(getString(R.string.ROL_ADMIN_KEY_MASTER))) {

            tvResearchRoleName.setText(getString(R.string.ROL_ADMINITRADOR));

        } else {
            tvResearchRoleName.setText(getString(R.string.ROL_INVESTIGADOR));
        }

    }

    private void navigationListener() {

        navigationView.setNavigationItemSelectedListener(menuItem -> {

            if (menuItem.getItemId() == R.id.menu_profile) {

                navigationView.setCheckedItem(R.id.menu_profile);

                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivityForResult(intent, PROFILE_ACTIVITY_CODE);

                return true;

            } else if (menuItem.getItemId() == R.id.menu_interviewees) {

                Objects.requireNonNull(tabLayout.getTabAt(0), "get tab 0 cannot be null").select();
                drawerLayout.closeDrawer(GravityCompat.START, true);

                return true;

            } else if (menuItem.getItemId() == R.id.menu_stats) {

                Objects.requireNonNull(tabLayout.getTabAt(1), "get tab 1 cannot be null").select();
                drawerLayout.closeDrawer(GravityCompat.START, true);

                return true;
            }

            if (menuItem.getItemId() == R.id.menu_actions) {

                navigationView.setCheckedItem(R.id.menu_actions);

                Intent intent = new Intent(MainActivity.this, ActionListActivity.class);
                startActivity(intent);

                return true;

            } else if (menuItem.getItemId() == R.id.menu_emoticons) {

                return true;

            } else if (menuItem.getItemId() == R.id.menu_researchers) {

                navigationView.setCheckedItem(R.id.menu_researchers);

                Intent intent = new Intent(MainActivity.this, ResearcherListActivity.class);
                startActivity(intent);

                return true;
            }

            if (menuItem.getItemId() == R.id.menu_contact) {

                navigationView.setCheckedItem(R.id.menu_contact);

                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(intent);

                return true;
            }


            if (menuItem.getItemId() == R.id.menu_logout) {

                String token = sharedPreferences.getString(getString(R.string.SHARED_PREF_TOKEN_LOGIN), "");

                if (token != null && !token.isEmpty()) {

                    crashlytics.setCustomKey(getString(R.string.SHARED_PREF_TOKEN_LOGIN), token);


                    Log.d(getString(R.string.TAG_TOKEN_LOGOUT), String.format("%s %s", getString(R.string.TOKEN_LOGOUT_MSG), token));
                    crashlytics.log(getString(R.string.TAG_TOKEN_LOGOUT) + String.format("%s %s", getString(R.string.TOKEN_LOGOUT_MSG), token));

                    sharedPreferences.edit().remove(getString(R.string.SHARED_PREF_TOKEN_LOGIN)).apply();

                    if (researchRole.equals(getString(R.string.ROL_ADMIN_KEY_MASTER))) {
                        MyFirebaseMessagingService.deleteSuscriptionTheme(getApplicationContext());
                    }

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                    return true;
                }
            }
            return false;
        });
    }

    private void tabsListener() {

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Log.d(getString(R.string.TAG_TAB_SELECTED), String.valueOf(tab.getPosition()));


                if (tab.getPosition() == 0) {

                    navigationView.setCheckedItem(R.id.menu_interviewees);

                } else if (tab.getPosition() == 1) {

                    navigationView.setCheckedItem(R.id.menu_stats);

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

        if (item.getItemId() == android.R.id.home) {

            drawerLayout.openDrawer(GravityCompat.START);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == PROFILE_ACTIVITY_CODE) {

            navigationView.setCheckedItem(R.id.menu_interviewees);

            Objects.requireNonNull(tabLayout.getTabAt(0), "Get tab 0 cannot be null").select();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDialogPositiveClick(Object object) {
        IntervieweeRepository.getInstance(getApplication()).deleteInterviewee((Interviewee) object);
    }
}
