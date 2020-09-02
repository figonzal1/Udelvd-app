package cl.udelvd.views.activities;

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
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.text.ParseException;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.models.Researcher;
import cl.udelvd.utils.SnackbarInterface;
import cl.udelvd.utils.Utils;

public class ProfileActivity extends AppCompatActivity implements SnackbarInterface {

    private static final int PROFILE_ACTIVITY_CODE = 200;
    private static final int EDIT_PROFILE_CODE = 201;

    private TextView tvName;
    private TextView tvActivated;
    private TextView tvEmail;
    private TextView tvRegistryAccount;

    private Researcher researcher;

    private FirebaseCrashlytics crashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        crashlytics = FirebaseCrashlytics.getInstance();

        Utils.configToolbar(this, getApplicationContext(), R.drawable.ic_arrow_back_black_24dp, getString(R.string.TITULO_TOOLBAR_PERFIL));

        instantiateInterfaceResources();

        loadResearcherData();
    }


    private void instantiateInterfaceResources() {

        tvName = findViewById(R.id.tv_researcher_name);
        tvActivated = findViewById(R.id.tv_activated_researcher);
        tvEmail = findViewById(R.id.tv_email_researcher);
        tvRegistryAccount = findViewById(R.id.tv_registry_researcher);
    }

    private void loadResearcherData() {

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);

        researcher = new Researcher();
        researcher.setId(sharedPreferences.getInt(getString(R.string.SHARED_PREF_INVES_ID), 0));
        researcher.setName(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_NOMBRE), ""));
        researcher.setLastName(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_APELLIDO), ""));
        researcher.setEmail(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_EMAIL), ""));
        researcher.setActivated(sharedPreferences.getBoolean(getString(R.string.SHARED_PREF_INVES_ACTIVADO), false));
        researcher.setCreateTime(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_CREATE_TIME), ""));
        researcher.setPassword(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_PASSWORD), ""));

        researcher.setIdRole(sharedPreferences.getInt(getString(R.string.SHARED_PREF_INVES_ID_ROL), 0));
        researcher.setRolName(sharedPreferences.getString(getString(R.string.SHARED_PREF_INVES_NOMBRE_ROL), ""));

        tvName.setText(String.format("%s %s", researcher.getName(), researcher.getLastName()));
        tvEmail.setText(researcher.getEmail());

        if (researcher.isActivated()) {
            tvActivated.setText(R.string.PERFIL_ACTIVADO);
        } else {
            tvActivated.setText(R.string.PERFIL_NO_ACTIVADO);
        }

        try {
            tvRegistryAccount.setText(Utils.dateToString(getApplicationContext(), false, Utils.stringToDate(getApplicationContext(), false, researcher.getCreateTime())));
        } catch (ParseException e) {

            Log.d("STRING_TO_DATE", "Parse exception");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            Intent intent = getIntent();
            setResult(PROFILE_ACTIVITY_CODE, intent);
            finish();

            return true;

        } else if (item.getItemId() == R.id.menu_edit_profile) {

            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);

            intent.putExtra(getString(R.string.KEY_INVES_ID), researcher.getId());
            intent.putExtra(getString(R.string.KEY_INVES_NOMBRE), researcher.getName());
            intent.putExtra(getString(R.string.KEY_INVES_APELLIDO), researcher.getLastName());
            intent.putExtra(getString(R.string.KEY_INVES_EMAIL), researcher.getEmail());
            intent.putExtra(getString(R.string.KEY_INVES_PASSWORD), researcher.getPassword());

            intent.putExtra(getString(R.string.KEY_INVES_ID_ROL), researcher.getIdRole());
            intent.putExtra(getString(R.string.KEY_INVES_NOMBRE_ROL), researcher.getRolName());

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

                Bundle bundle = Objects.requireNonNull(data, "Extra data cannot be null").getExtras();
                showSnackbar(findViewById(R.id.researcher_profile), Snackbar.LENGTH_LONG, Objects.requireNonNull(bundle, "Msg_actualizacion cannot be null").getString(getString(R.string.INTENT_KEY_MSG_ACTUALIZACION)), null);

                loadResearcherData();

                Log.d(getString(R.string.TAG_EDIT_PROFILE_RESULT), getString(R.string.EDIT_PROFILE_RESULT_MSG));
                crashlytics.log(getString(R.string.TAG_EDIT_PROFILE_RESULT) + getString(R.string.EDIT_PROFILE_RESULT_MSG));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = getIntent();
        setResult(PROFILE_ACTIVITY_CODE, intent);
        finish();
    }

    @Override
    public void showSnackbar(View v, int duration, String title, String action) {
        Snackbar snackbar = Snackbar.make(v, title, duration);
        snackbar.show();
    }
}
