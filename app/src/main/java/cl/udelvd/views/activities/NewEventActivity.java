package cl.udelvd.views.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.adapters.ActionSelectorAdapter;
import cl.udelvd.adapters.EmoticonAdapter;
import cl.udelvd.models.Action;
import cl.udelvd.models.Emoticon;
import cl.udelvd.models.Event;
import cl.udelvd.models.Interview;
import cl.udelvd.repositories.EventRepository;
import cl.udelvd.utils.SnackbarInterface;
import cl.udelvd.utils.Utils;
import cl.udelvd.viewmodels.NewEventViewModel;

public class NewEventActivity extends AppCompatActivity implements SnackbarInterface {

    private static final int SPEECH_REQUEST_CODE = 100;

    private TextInputLayout ilAction;
    private TextInputLayout ilEventHour;
    private TextInputLayout ilJustification;

    private AppCompatAutoCompleteTextView acAction;
    private TextInputEditText etEventHour;
    private TextInputEditText etJustification;

    private NewEventViewModel newEventViewModel;

    private ActionSelectorAdapter actionSelectorAdapter;
    private EmoticonAdapter emoticonAdapter;

    private List<Action> actionList;
    private List<Emoticon> emoticonList;

    private Spinner spinner;
    private Event event;
    private Interview interviewIntent;

    private ProgressBar progressBar;
    private boolean isSnackBarShow = false;
    private String language;

    private FirebaseCrashlytics crashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        crashlytics = FirebaseCrashlytics.getInstance();

        Utils.configToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITULO_TOOLBAR_CREAR_EVENTO));

        getBundleData();

        instantiateInterfaceResources();

        setPickerEventHour();

        setAutoCompleteAction();

        setAutoCompleteEmoticon();

        configSpinnerEmoticones();

        configSpeechIntent();

        initViewModelEvent();
    }

    private void getBundleData() {

        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();
            interviewIntent = new Interview();
            interviewIntent.setId(bundle.getInt(getString(R.string.KEY_EVENTO_ID_ENTREVISTA)));
        }
    }

    private void instantiateInterfaceResources() {

        event = new Event();

        progressBar = findViewById(R.id.progress_horizontal_new_event);
        progressBar.setVisibility(View.VISIBLE);

        ilAction = findViewById(R.id.il_action_event);
        ilEventHour = findViewById(R.id.il_event_hour);
        ilJustification = findViewById(R.id.il_justification_event);

        acAction = findViewById(R.id.et_action_event);
        etEventHour = findViewById(R.id.et_event_hour);
        etJustification = findViewById(R.id.et_justification_event);

        spinner = findViewById(R.id.spinner_emoticon);

        newEventViewModel = new ViewModelProvider(this).get(NewEventViewModel.class);

        language = Utils.getLanguage(getApplicationContext());
    }

    private void setPickerEventHour() {

        etEventHour.setOnClickListener(v -> Utils.initHourPicker(etEventHour, NewEventActivity.this));


        ilEventHour.setEndIconOnClickListener(v -> Utils.initHourPicker(etEventHour, NewEventActivity.this));

    }

    private void setAutoCompleteAction() {

        newEventViewModel.isLoadingActions().observe(this, aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);

                ilAction.setEnabled(false);
                acAction.setEnabled(false);

                spinner.setEnabled(false);

                ilEventHour.setEnabled(false);
                etEventHour.setEnabled(false);

                ilJustification.setEnabled(false);
                etJustification.setEnabled(false);

            } else {
                progressBar.setVisibility(View.GONE);

                ilAction.setEnabled(true);
                acAction.setEnabled(true);

                spinner.setEnabled(true);

                ilEventHour.setEnabled(true);
                etEventHour.setEnabled(true);

                ilJustification.setEnabled(true);
                etJustification.setEnabled(true);
            }
        });


        newEventViewModel.loadActions(language).observe(this, list -> {

            if (list != null && list.size() > 0) {

                progressBar.setVisibility(View.GONE);

                actionList = list;
                actionSelectorAdapter = new ActionSelectorAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, actionList);
                acAction.setAdapter(actionSelectorAdapter);

                actionSelectorAdapter.notifyDataSetChanged();

            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_ACCIONES), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_ACCIONES) + getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

        });


        newEventViewModel.showMsgErrorActions().observe(this, s -> {

            progressBar.setVisibility(View.GONE);

            if (!isSnackBarShow) {

                showSnackbar(findViewById(R.id.form_new_event), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                isSnackBarShow = true;
            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_ACCIONES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_ACCIONES) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
        });
    }

    private void setAutoCompleteEmoticon() {

        newEventViewModel.isLoadingEmoticons().observe(this, aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);

                ilAction.setEnabled(false);
                acAction.setEnabled(false);

                spinner.setEnabled(false);

                ilEventHour.setEnabled(false);
                etEventHour.setEnabled(false);

                ilJustification.setEnabled(false);
                etJustification.setEnabled(false);

            } else {
                progressBar.setVisibility(View.GONE);

                ilAction.setEnabled(true);
                acAction.setEnabled(true);

                spinner.setEnabled(true);

                ilEventHour.setEnabled(true);
                etEventHour.setEnabled(true);

                ilJustification.setEnabled(true);
                etJustification.setEnabled(true);
            }
        });


        newEventViewModel.loadEmoticons().observe(this, emoticons -> {

            if (emoticons != null && emoticons.size() > 0) {

                progressBar.setVisibility(View.GONE);

                emoticonList = emoticons;
                emoticonAdapter = new EmoticonAdapter(getApplicationContext(), emoticonList);
                spinner.setAdapter(emoticonAdapter);

                Log.d(getString(R.string.TAG_VIEW_MODEL_EMOTICON), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_EMOTICON) + getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
            }
        });


        newEventViewModel.showMsgErrorEmoticons().observe(this, s -> {

            progressBar.setVisibility(View.GONE);

            if (!isSnackBarShow) {

                showSnackbar(findViewById(R.id.form_new_event), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                isSnackBarShow = true;
            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_EMOTICON), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_EMOTICON) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
        });
    }

    /**
     * Function in charge of configuring the emoticon spinner in event form
     */
    private void configSpinnerEmoticones() {

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d(getString(R.string.SPINNER_EMOTICON_SELECTED), spinner.getSelectedItem().toString());
                crashlytics.log(getString(R.string.SPINNER_EMOTICON_SELECTED) + spinner.getSelectedItem().toString());

                Emoticon emoticon = emoticonList.get(position);
                event.setEmoticon(emoticon);
                spinner.setSelected(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setSelected(false);
            }
        });
    }

    /**
     * Try to open the Voice To Text Google program
     */
    private void configSpeechIntent() {

        ilJustification.setEndIconOnClickListener(v -> {

            try {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

                startActivityForResult(intent, SPEECH_REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
                Log.e("SPEECH", "No activity handler for speech to text");
                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.SPEECH_TO_TEXT_NOT_AVAILABLE), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initViewModelEvent() {

        newEventViewModel.isLoadingEvents().observe(this, aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);

                ilAction.setEnabled(false);
                acAction.setEnabled(false);

                spinner.setEnabled(false);

                ilEventHour.setEnabled(false);
                etEventHour.setEnabled(false);

                ilJustification.setEnabled(false);
                etJustification.setEnabled(false);

            } else {
                progressBar.setVisibility(View.GONE);

                ilAction.setEnabled(true);
                acAction.setEnabled(true);

                spinner.setEnabled(true);

                ilEventHour.setEnabled(true);
                etEventHour.setEnabled(true);

                ilJustification.setEnabled(true);
                etJustification.setEnabled(true);
            }
        });


        newEventViewModel.showMsgRegistry().observe(this, s -> {

            Log.d(getString(R.string.TAG_VIEW_MODEL_NUEVO_EVENTO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_NUEVO_EVENTO) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

            if (s.equals(getString(R.string.MSG_REGISTRO_EVENTO))) {

                progressBar.setVisibility(View.GONE);

                Intent intent = getIntent();
                intent.putExtra(getString(R.string.INTENT_KEY_MSG_REGISTRO), s);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        newEventViewModel.showMsgErrorRegistry().observe(this, s -> {

            progressBar.setVisibility(View.GONE);

            if (!isSnackBarShow) {

                isSnackBarShow = true;
                showSnackbar(findViewById(R.id.form_new_event), Snackbar.LENGTH_LONG, s, null);
            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_NUEVO_EVENTO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_NUEVO_EVENTO) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
        });
    }


    @Override
    public void showSnackbar(View v, int duration, String title, String action) {

        Snackbar snackbar = Snackbar.make(v, title, duration);

        if (action != null) {

            snackbar.setAction(action, v1 -> {

                newEventViewModel.refreshActions(language);
                newEventViewModel.refreshEmoticons();

                progressBar.setVisibility(View.VISIBLE);

                isSnackBarShow = false;
            });
        }

        snackbar.show();
        isSnackBarShow = false;
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
                interview.setId(interviewIntent.getId());
                event.setInterview(interview);

                int id = Objects.requireNonNull(searchActionByName(acAction.getText().toString()), "Id action cannot be null").getId();
                Action action = new Action();
                action.setId(id);
                event.setAction(action);

                try {
                    event.setEventHour(Utils.stringToDate(getApplicationContext(), true, Objects.requireNonNull(etEventHour.getText(), "Et event hour cannot be null").toString()));
                } catch (ParseException e) {

                    Log.d("STRING_TO_DATE", "Parse error");
                    e.printStackTrace();
                }

                event.setJustification(Objects.requireNonNull(etJustification.getText(), "Et justification cannot be null").toString());

                EventRepository.getInstance(getApplication()).registryEvent(event);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {

            List<String> results = Objects.requireNonNull(data, "Data cannot be null").getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = Objects.requireNonNull(results, "Result 0 cannot be null").get(0);

            etJustification.setText(spokenText);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean validateFields() {

        int errorCounter = 0;

        if (Objects.requireNonNull(etEventHour.getText(), "Et event hour cannot be null").toString().isEmpty()) {

            ilEventHour.setErrorEnabled(true);
            ilEventHour.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;

        } else {
            ilEventHour.setErrorEnabled(false);
        }


        if (acAction.getText().toString().isEmpty()) {

            ilAction.setErrorEnabled(true);
            ilAction.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;

        } else if (searchActionByName(acAction.getText().toString()) == null) {

            ilAction.setErrorEnabled(true);
            ilAction.setError("Por favor, elige una acción de la lista");
            errorCounter++;

        } else {
            ilAction.setErrorEnabled(false);
        }


        if (!spinner.isSelected()) {

            Toast.makeText(getApplicationContext(), getString(R.string.VALIDACION_EMOTICON), Toast.LENGTH_LONG).show();
            errorCounter++;
        }


        if (Objects.requireNonNull(etJustification.getText(), "Et justification cannot be null").toString().isEmpty()) {

            ilJustification.setErrorEnabled(true);
            ilJustification.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;

        } else {
            ilJustification.setErrorEnabled(false);
        }


        return errorCounter == 0;
    }

    private Action searchActionByName(String name) {

        for (int i = 0; i < actionList.size(); i++) {

            if (actionList.get(i).getName().equals(name)) {

                return actionList.get(i);
            }
        }

        return null;
    }
}
