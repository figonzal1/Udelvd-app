package cl.udelvd.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.models.Action;
import cl.udelvd.repositories.ActionRepository;
import cl.udelvd.utils.SnackbarInterface;
import cl.udelvd.utils.Utils;
import cl.udelvd.viewmodels.NewActionViewModel;

public class NewActionActivity extends AppCompatActivity implements SnackbarInterface {

    private ProgressBar progressBar;
    private TextInputLayout ilActionES;
    private TextInputLayout ilActionEN;
    private TextInputEditText etActionES;
    private TextInputEditText etActionEN;
    private NewActionViewModel newActionViewModel;
    private boolean isSnackBarShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_action);

        Utils.configToolbar(this, getApplicationContext(), 0, getString(R.string.TITULO_TOOLBAR_NUEVA_ACCION));

        instantiateInterfaceResources();

        initViewModel();
    }

    private void initViewModel() {
        newActionViewModel.isLoadingRegistry().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);


                    ilActionES.setEnabled(false);
                    ilActionEN.setEnabled(false);

                    etActionES.setEnabled(false);
                    etActionEN.setEnabled(false);
                } else {
                    progressBar.setVisibility(View.GONE);


                    ilActionES.setEnabled(true);
                    ilActionEN.setEnabled(true);

                    etActionES.setEnabled(true);
                    etActionEN.setEnabled(true);
                }
            }
        });

        newActionViewModel.showMsgRegistry().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(getString(R.string.TAG_VIEW_MODEL_NUEVA_ACCION), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));


                if (s.equals(getString(R.string.MSG_REGISTRO_ACCION))) {

                    progressBar.setVisibility(View.GONE);

                    Intent intent = getIntent();
                    intent.putExtra(getString(R.string.INTENT_KEY_MSG_REGISTRO), s);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        newActionViewModel.showMsgErrorRegistry().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.form_new_action), Snackbar.LENGTH_INDEFINITE, s, null);
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_NUEVA_ACCION), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });

    }

    private void instantiateInterfaceResources() {

        progressBar = findViewById(R.id.progress_horizontal_new_action);
        progressBar.setVisibility(View.VISIBLE);

        ilActionES = findViewById(R.id.il_action_spanish);
        ilActionEN = findViewById(R.id.il_action_english);

        etActionES = findViewById(R.id.et_action_spanish);
        etActionEN = findViewById(R.id.et_action_english);

        newActionViewModel = new ViewModelProvider(this).get(NewActionViewModel.class);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_save) {

            if (validateField()) {

                progressBar.setVisibility(View.VISIBLE);

                Action action = new Action();
                action.setNameEs(Objects.requireNonNull(etActionES.getText()).toString());
                action.setNameEng(Objects.requireNonNull(etActionEN.getText()).toString());

                ActionRepository.getInstance(getApplication()).registryAction(action);

            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateField() {
        int errorCounter = 0;

        if (Objects.requireNonNull(etActionES.getText()).toString().isEmpty()) {
            ilActionES.setErrorEnabled(true);
            ilActionES.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;
        } else {
            ilActionES.setErrorEnabled(false);
        }

        if (Objects.requireNonNull(etActionEN.getText()).toString().isEmpty()) {
            ilActionEN.setErrorEnabled(true);
            ilActionEN.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;
        } else {
            ilActionEN.setErrorEnabled(false);
        }

        return errorCounter == 0;
    }

    @Override
    public void showSnackbar(View v, int duration, String title, String action) {
        Snackbar snackbar = Snackbar.make(v, title, duration);
        if (action != null) {
            snackbar.setAction(action, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progressBar.setVisibility(View.VISIBLE);
                    isSnackBarShow = false;
                }
            });
        }

        isSnackBarShow = false;
        snackbar.show();
    }
}
