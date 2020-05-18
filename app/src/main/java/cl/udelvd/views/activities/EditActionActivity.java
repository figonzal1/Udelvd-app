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
import cl.udelvd.viewmodels.EditActionViewModel;

public class EditActionActivity extends AppCompatActivity implements SnackbarInterface {

    private ProgressBar progressBar;
    private TextInputLayout ilActionEsp;
    private TextInputLayout ilActionEng;
    private TextInputEditText etActionEsp;
    private TextInputEditText etActionEng;
    private EditActionViewModel editActionViewModel;
    private boolean isSnackBarShow = false;
    private Action actionIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_action);

        Utils.configToolbar(this, getApplicationContext(), 0, getString(R.string.TITULO_TOOLBAR_EDITAR_ACCION));

        instantiateInterfaceResources();

        getActionData();

        initViewModel();
    }

    private void getActionData() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            actionIntent = new Action();
            actionIntent.setId(bundle.getInt(getString(R.string.KEY_ACCION_ID_LARGO)));
            actionIntent.setNameEs(bundle.getString(getString(R.string.KEY_ACCION_NOMBRE_ES)));
            actionIntent.setNameEng(bundle.getString(getString(R.string.KEY_ACCION_NOMBRE_EN)));

            etActionEsp.setText(actionIntent.getNameEs());
            etActionEng.setText(actionIntent.getNameEng());
        }
    }

    private void instantiateInterfaceResources() {

        progressBar = findViewById(R.id.progress_horizontal_edit_action);
        progressBar.setVisibility(View.VISIBLE);

        ilActionEsp = findViewById(R.id.il_action_spanish);
        ilActionEng = findViewById(R.id.il_action_english);

        etActionEsp = findViewById(R.id.et_action_spanish);
        etActionEng = findViewById(R.id.et_action_english);

        editActionViewModel = new ViewModelProvider(this).get(EditActionViewModel.class);
    }

    private void initViewModel() {
        editActionViewModel.isLoadingUpdate().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);
                    ilActionEng.setEnabled(false);
                    ilActionEsp.setEnabled(false);

                    etActionEng.setEnabled(false);
                    etActionEsp.setEnabled(false);
                } else {
                    progressBar.setVisibility(View.GONE);
                    ilActionEng.setEnabled(true);
                    ilActionEsp.setEnabled(true);

                    etActionEng.setEnabled(true);
                    etActionEsp.setEnabled(true);
                }
            }
        });

        editActionViewModel.showMsgUpdate().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ACCION), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

                if (s.equals(getString(R.string.MSG_UPDATE_ACCION))) {

                    progressBar.setVisibility(View.GONE);

                    Intent intent = getIntent();
                    intent.putExtra(getString(R.string.INTENT_KEY_MSG_ACTUALIZACION), s);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        editActionViewModel.showMsgErrorUpdate().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.form_edit_action), Snackbar.LENGTH_LONG, s, null);
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ACCION), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
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

            if (validateFields()) {

                progressBar.setVisibility(View.VISIBLE);

                Action editAction = new Action();
                editAction.setId(actionIntent.getId());
                editAction.setNameEs(Objects.requireNonNull(etActionEsp.getText()).toString());
                editAction.setNameEng(Objects.requireNonNull(etActionEng.getText()).toString());

                ActionRepository.getInstance(getApplication()).updateAction(editAction);

            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateFields() {
        int errorCounter = 0;

        if (Objects.requireNonNull(etActionEsp.getText()).toString().isEmpty()) {
            ilActionEsp.setErrorEnabled(true);
            ilActionEsp.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;
        } else {
            ilActionEsp.setErrorEnabled(false);
        }

        if (Objects.requireNonNull(etActionEng.getText()).toString().isEmpty()) {
            ilActionEng.setErrorEnabled(true);
            ilActionEng.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;
        } else {
            ilActionEng.setErrorEnabled(false);
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
