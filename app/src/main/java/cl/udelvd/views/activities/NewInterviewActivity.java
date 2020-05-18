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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
import cl.udelvd.viewmodels.NewInterviewViewModel;

public class NewInterviewActivity extends AppCompatActivity implements SnackbarInterface {


    private ProgressBar progressBar;

    private TextInputLayout ilInterviewDate;
    private TextInputLayout ilInterviewType;

    private TextInputEditText etInterviewDate;
    private AppCompatAutoCompleteTextView acInterviewType;

    private int idInterview;

    private List<InterviewType> interviewTypeList;
    private InterviewTypeAdapter interviewTypeAdapter;

    private NewInterviewViewModel newInterviewViewModel;
    private boolean isSnackBarShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_interview);

        Utils.configToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITULO_TOOLBAR_NUEVA_ENTREVISTA));

        instantiateInterfaceResources();

        getBundleData();

        setAutoCompleteInterviewTypes();

        setPickerBirthDate();

        initViewModelInterview();
    }

    private void instantiateInterfaceResources() {

        progressBar = findViewById(R.id.progress_horizontal_new_interview);
        progressBar.setVisibility(View.VISIBLE);

        ilInterviewDate = findViewById(R.id.il_interview_date);
        ilInterviewType = findViewById(R.id.il_interview_type);

        etInterviewDate = findViewById(R.id.et_interview_date);
        acInterviewType = findViewById(R.id.et_interview_type);

        newInterviewViewModel = new ViewModelProvider(this).get(NewInterviewViewModel.class);
    }

    private void getBundleData() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                idInterview = bundle.getInt(getString(R.string.KEY_ENTREVISTADO_ID_LARGO));
            }
        }
    }

    private void setAutoCompleteInterviewTypes() {


        newInterviewViewModel.isLoadingInterviewTypes().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

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
            }
        });


        newInterviewViewModel.loadInterviewTypes().observe(this, new Observer<List<InterviewType>>() {
            @Override
            public void onChanged(List<InterviewType> interviewTypes) {

                if (interviewTypes != null && interviewTypes.size() > 0) {

                    progressBar.setVisibility(View.GONE);

                    interviewTypeList = interviewTypes;
                    interviewTypeAdapter = new InterviewTypeAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, interviewTypeList);
                    acInterviewType.setAdapter(interviewTypeAdapter);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_ENTREVISTA), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

                    interviewTypeAdapter.notifyDataSetChanged();
                }

            }
        });


        newInterviewViewModel.showMsgErrorInterviewTypes().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.form_new_interview), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_TIPO_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    private void initViewModelInterview() {

        newInterviewViewModel.isLoadingRegistryInterviews().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
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
            }
        });

        newInterviewViewModel.showMsgRegistry().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                Log.d(getString(R.string.TAG_VIEW_MODEL_NEW_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));


                if (s.equals(getString(R.string.MSG_REGISTRO_ENTREVISTA))) {


                    progressBar.setVisibility(View.GONE);

                    Intent intent = getIntent();
                    intent.putExtra(getString(R.string.INTENT_KEY_MSG_REGISTRO), s);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        newInterviewViewModel.showMsgErrorRegistry().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.GONE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.form_new_interview), Snackbar.LENGTH_LONG, s, null);
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_NEW_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    private void setPickerBirthDate() {


        etInterviewDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.iniciarDatePicker(etInterviewDate, NewInterviewActivity.this);
            }
        });


        ilInterviewDate.setEndIconOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.iniciarDatePicker(etInterviewDate, NewInterviewActivity.this);
            }
        });

    }

    private boolean validateFields() {
        int errorCounter = 0;

        if (Objects.requireNonNull(etInterviewDate.getText()).toString().isEmpty()) {
            ilInterviewDate.setErrorEnabled(true);
            ilInterviewDate.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;
        } else {
            ilInterviewDate.setErrorEnabled(false);
        }

        if (Objects.requireNonNull(acInterviewType.getText()).toString().isEmpty()) {
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
            snackbar.setAction(action, new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    newInterviewViewModel.refreshInterviewTypes();

                    progressBar.setVisibility(View.VISIBLE);

                    isSnackBarShow = false;
                }
            });
        }

        isSnackBarShow = false;
        snackbar.show();
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

                Interview interview = new Interview();

                Date interviewDate = Utils.stringToDate(getApplicationContext(), false, Objects.requireNonNull(etInterviewDate.getText()).toString());
                interview.setInterviewDate(interviewDate);

                interview.setIdInterviewee(idInterview);

                InterviewType interviewType = new InterviewType();
                int id = Objects.requireNonNull(searchInterviewTypesByName(acInterviewType.getText().toString())).getId();
                interviewType.setId(id);
                interview.setInterviewType(interviewType);

                InterviewRepository.getInstance(getApplication()).registryInterview(interview);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private InterviewType searchInterviewTypesByName(String nombre) {

        for (int i = 0; i < interviewTypeList.size(); i++) {
            if (interviewTypeList.get(i).getName().equals(nombre)) {
                return interviewTypeList.get(i);
            }
        }
        return null;
    }
}
