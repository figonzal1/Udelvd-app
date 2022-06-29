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
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.adapters.InterviewTypeAdapter;
import cl.udelvd.models.Interview;
import cl.udelvd.models.InterviewType;
import cl.udelvd.repositories.InterviewRepository;
import cl.udelvd.utils.SnackbarInterface;
import cl.udelvd.utils.Utils;
import cl.udelvd.viewmodels.EditInterviewViewModel;

public class EditInterviewActivity extends AppCompatActivity implements SnackbarInterface {

    private ProgressBar progressBar;

    private TextInputLayout ilInterviewDate;
    private TextInputLayout ilInterviewType;

    private TextInputEditText etInterviewDate;
    private AppCompatAutoCompleteTextView acInterviewType;

    private Interview interviewIntent;

    private EditInterviewViewModel editInterviewViewModel;

    private List<InterviewType> interviewTypeList;
    private InterviewTypeAdapter interviewTypeAdapter;

    private boolean isAutoCompleteInterviewTypeReady = false;
    private boolean isGetInterview = false;
    private boolean isSnackBarShow = false;

    private FirebaseCrashlytics crashlytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_interview);

        crashlytics = FirebaseCrashlytics.getInstance();

        Utils.configToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITULO_TOOLBAR_EDITAR_ENTREVISTA));

        instantiateInterfaceResources();

        getBundleData();

        setAutoCompleteInterviewType();

        setPickerBirthDate();

        initViewModelInterview();
    }

    private void instantiateInterfaceResources() {

        interviewTypeList = new ArrayList<>();
        progressBar = findViewById(R.id.progress_horizontal_edit_interview);
        progressBar.setVisibility(View.VISIBLE);

        ilInterviewDate = findViewById(R.id.il_interview_date);
        ilInterviewType = findViewById(R.id.il_interview_type);

        etInterviewDate = findViewById(R.id.et_interview_date);
        acInterviewType = findViewById(R.id.et_interview_type);

        editInterviewViewModel = new ViewModelProvider(this).get(EditInterviewViewModel.class);
    }

    private void getBundleData() {

        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();
            interviewIntent = new Interview();
            interviewIntent.setId(bundle.getInt(getString(R.string.KEY_ENTREVISTA_ID_LARGO)));
            interviewIntent.setIdInterviewee(bundle.getInt(getString(R.string.KEY_ENTREVISTA_ID_ENTREVISTADO)));
        }
    }


    private void setAutoCompleteInterviewType() {

        editInterviewViewModel.isLoadingInterviewTypes().observe(this, aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);


                ilInterviewType.setEnabled(false);
                acInterviewType.setEnabled(false);

                ilInterviewDate.setEnabled(false);
                etInterviewDate.setEnabled(false);

            } else {
                progressBar.setVisibility(View.GONE);


                ilInterviewType.setEnabled(true);
                acInterviewType.setEnabled(true);

                ilInterviewDate.setEnabled(true);
                etInterviewDate.setEnabled(true);
            }
        });


        editInterviewViewModel.loadInterviewTypes().observe(this, interviewTypes -> {

            if (interviewTypes != null && interviewTypes.size() > 0) {

                interviewTypeList = interviewTypes;
                interviewTypeAdapter = new InterviewTypeAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, interviewTypeList);
                acInterviewType.setAdapter(interviewTypeAdapter);
                interviewTypeAdapter.notifyDataSetChanged();

                isAutoCompleteInterviewTypeReady = true;

                progressBar.setVisibility(View.GONE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_ENTREVISTA), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_TIPO_ENTREVISTA) + getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

                setInterviewInformation();
            }
        });


        editInterviewViewModel.showMsgErrorInterviewTypeList().observe(this, s -> {

            progressBar.setVisibility(View.GONE);

            if (!isSnackBarShow) {
                isSnackBarShow = true;
                showSnackbar(findViewById(R.id.form_edit_interview), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_TIPO_ENTREVISTA) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

        });
    }

    private void initViewModelInterview() {

        editInterviewViewModel.isLoadingInterview().observe(this, aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);


                ilInterviewType.setEnabled(false);
                acInterviewType.setEnabled(false);

                ilInterviewDate.setEnabled(false);
                etInterviewDate.setEnabled(false);

            } else {
                progressBar.setVisibility(View.GONE);


                ilInterviewType.setEnabled(true);
                acInterviewType.setEnabled(true);

                ilInterviewDate.setEnabled(true);
                etInterviewDate.setEnabled(true);
            }
        });

        editInterviewViewModel.loadInterview(interviewIntent).observe(this, interviewInternet -> {

            if (interviewInternet != null) {

                interviewIntent = interviewInternet;

                isGetInterview = true;

                progressBar.setVisibility(View.GONE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), interviewIntent.toString()));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTA) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), interviewIntent.toString()));

                setInterviewInformation();
            }
        });

        editInterviewViewModel.showMsgUpdate().observe(this, s -> {

            Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTA) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

            if (s.equals(getString(R.string.MSG_UPDATE_ENTREVISTA))) {

                progressBar.setVisibility(View.GONE);

                Intent intent = getIntent();
                intent.putExtra(getString(R.string.INTENT_KEY_MSG_ACTUALIZACION), s);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        editInterviewViewModel.showMsgErrorUpdate().observe(this, s -> {

            progressBar.setVisibility(View.GONE);

            Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTA) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            if (!isSnackBarShow) {
                isSnackBarShow = true;
                showSnackbar(findViewById(R.id.form_edit_interview), Snackbar.LENGTH_LONG, s, null);
            }
        });


        editInterviewViewModel.showMsgErrorInterview().observe(this, s -> {

            progressBar.setVisibility(View.GONE);

            Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_EDITAR_ENTREVISTA) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            if (!isSnackBarShow) {
                isSnackBarShow = true;
                showSnackbar(findViewById(R.id.form_edit_interview), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
            }
        });
    }


    private void setInterviewInformation() {

        if (isAutoCompleteInterviewTypeReady && isGetInterview) {

            String date = Utils.dateToString(getApplicationContext(), false, interviewIntent.getInterviewDate());
            etInterviewDate.setText(date);

            String name = Objects.requireNonNull(searchInterviewTypeById(interviewIntent.getInterviewType().getId())).getName();
            acInterviewType.setText(name, false);

            isAutoCompleteInterviewTypeReady = false;
            isGetInterview = false;
        }
    }


    private void setPickerBirthDate() {

        etInterviewDate.setOnClickListener(v -> Utils.iniciarDatePicker(etInterviewDate, EditInterviewActivity.this, "interview"));


        ilInterviewDate.setEndIconOnClickListener(v -> Utils.iniciarDatePicker(etInterviewDate, EditInterviewActivity.this, "interview"));
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

                Interview interviewUpdate = new Interview();

                interviewUpdate.setId(interviewIntent.getId());
                interviewUpdate.setIdInterviewee(interviewIntent.getIdInterviewee());

                Date interviewDate = null;

                try {
                    interviewDate = Utils.stringToDate(getApplicationContext(), false, Objects.requireNonNull(etInterviewDate.getText(), "Et interview date cannot be null").toString());
                } catch (ParseException e) {

                    Log.d("STRING_TO_DATE", "Parse error exception");
                    e.printStackTrace();
                }

                interviewUpdate.setInterviewDate(interviewDate);

                int idInterviewType = Objects.requireNonNull(searchInterviewTypeByName(acInterviewType.getText().toString()), "Ac interview type cannot be null").getId();

                InterviewType interviewType = new InterviewType();
                interviewType.setId(idInterviewType);
                interviewUpdate.setInterviewType(interviewType);

                InterviewRepository.getInstance(getApplication()).updateInterview(interviewUpdate);
            }
            return true;
        }
        return false;
    }

    private boolean validateFields() {

        int errorCounter = 0;


        if (Objects.requireNonNull(etInterviewDate.getText(), "Et interview date cannot be null").toString().isEmpty()) {

            ilInterviewDate.setErrorEnabled(true);
            ilInterviewDate.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;

        } else {

            ilInterviewDate.setErrorEnabled(false);
        }

        if (acInterviewType.getText().toString().isEmpty()) {

            ilInterviewType.setErrorEnabled(true);
            ilInterviewType.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;

        } else {
            ilInterviewType.setErrorEnabled(false);
        }

        return errorCounter == 0;
    }

    @Override
    public void showSnackbar(View v, int tipo_snackbar, String title, String action) {

        Snackbar snackbar = Snackbar.make(v, title, tipo_snackbar);

        if (action != null) {

            snackbar.setAction(action, v1 -> {

                isAutoCompleteInterviewTypeReady = false;
                isGetInterview = false;
                isSnackBarShow = false;

                editInterviewViewModel.refreshInterviewTypes();

                editInterviewViewModel.refreshInterview(interviewIntent);

                progressBar.setVisibility(View.VISIBLE);
            });
        }

        snackbar.show();
        isSnackBarShow = false;
    }


    private InterviewType searchInterviewTypeById(int id) {

        for (int i = 0; i < interviewTypeList.size(); i++) {

            if (interviewTypeList.get(i).getId() == id) {

                return interviewTypeList.get(i);
            }
        }
        return null;
    }


    private InterviewType searchInterviewTypeByName(String name) {

        for (int i = 0; i < interviewTypeList.size(); i++) {

            if (interviewTypeList.get(i).getName().equals(name)) {

                return interviewTypeList.get(i);
            }
        }

        return null;
    }
}
