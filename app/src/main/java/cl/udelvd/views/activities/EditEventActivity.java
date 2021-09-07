package cl.udelvd.views.activities;

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
import cl.udelvd.viewmodels.EditEventViewModel;

public class EditEventActivity extends AppCompatActivity implements SnackbarInterface {

    private static final int SPEECH_REQUEST_CODE = 200;

    private TextInputLayout ilAction;
    private TextInputLayout ilEventHour;
    private TextInputLayout ilJustification;

    private AppCompatAutoCompleteTextView acActions;
    private TextInputEditText etEventHour;
    private TextInputEditText etJustification;

    private Event eventIntent;
    private ProgressBar progressBar;
    private Spinner spinner;

    private EditEventViewModel editEventViewModel;

    private List<Action> actionList;
    private ActionSelectorAdapter actionSelectorAdapter;

    private EmoticonAdapter emoticonAdapter;
    private List<Emoticon> emoticonList;

    private boolean isSnackBarShow = false;
    private boolean isAutoCompleteAction = false;
    private boolean isSpinnerEmoticons = false;
    private boolean isGetEvent = false;
    private String language;

    private FirebaseCrashlytics crashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        crashlytics = FirebaseCrashlytics.getInstance();

        Utils.configToolbar(this, getApplicationContext(), R.drawable.ic_close_white_24dp, getString(R.string.TITULO_TOOLBAR_EDITAR_EVENTO));

        instantiateInterfaceResources();

        getBundleData();

        initViewModelEvent();

        setAutoCompleteActions();

        setAutoCompleteEmoticons();

        setPickerEventHour();

        configSpinnerEmoticons();

        configSpeechIntent();
    }

    private void instantiateInterfaceResources() {

        progressBar = findViewById(R.id.progress_horizontal_edit_event);
        progressBar.setVisibility(View.VISIBLE);

        ilAction = findViewById(R.id.il_action_event);
        ilEventHour = findViewById(R.id.il_event_hour);
        ilJustification = findViewById(R.id.il_justification_event);

        acActions = findViewById(R.id.et_action_event);

        etEventHour = findViewById(R.id.et_event_hour);
        etJustification = findViewById(R.id.et_justification_event);

        spinner = findViewById(R.id.spinner_emoticon);

        editEventViewModel = new ViewModelProvider(this).get(EditEventViewModel.class);

        language = Utils.getLanguage(getApplicationContext());
    }

    private void getBundleData() {

        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();

            eventIntent = new Event();
            eventIntent.setId(bundle.getInt(getString(R.string.KEY_EVENTO_ID_LARGO)));

            Interview interview = new Interview();
            interview.setId(bundle.getInt(getString(R.string.KEY_EVENTO_ID_ENTREVISTA)));
            eventIntent.setInterview(interview);
        }
    }

    private void setPickerEventHour() {

        etEventHour.setOnClickListener(v -> Utils.initHourPicker(etEventHour, EditEventActivity.this));

        ilEventHour.setEndIconOnClickListener(v -> Utils.initHourPicker(etEventHour, EditEventActivity.this));

    }

    private void setAutoCompleteActions() {

        editEventViewModel.isLoadingAction().observe(this, aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);

                ilEventHour.setEnabled(false);
                etEventHour.setEnabled(false);

                spinner.setEnabled(false);

                ilAction.setEnabled(false);
                acActions.setEnabled(false);

                ilJustification.setEnabled(false);
                etJustification.setEnabled(false);

            } else {
                progressBar.setVisibility(View.GONE);

                ilEventHour.setEnabled(true);
                etEventHour.setEnabled(true);

                spinner.setEnabled(true);

                ilAction.setEnabled(true);
                acActions.setEnabled(true);

                ilJustification.setEnabled(true);
                etJustification.setEnabled(true);
            }
        });

        editEventViewModel.loadAction(language).observe(this, list -> {

            if (list != null && list.size() > 0) {

                progressBar.setVisibility(View.GONE);

                actionList = list;

                actionSelectorAdapter = new ActionSelectorAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, actionList);
                acActions.setAdapter(actionSelectorAdapter);

                isAutoCompleteAction = true;

                Log.d(getString(R.string.TAG_VIEW_MODEL_ACCIONES), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_ACCIONES) + getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

                actionSelectorAdapter.notifyDataSetChanged();

                setInfoEvent();
            }

        });

        editEventViewModel.showMsgErrorAction().observe(this, s -> {

            progressBar.setVisibility(View.GONE);

            if (!isSnackBarShow) {

                isSnackBarShow = true;
                showSnackbar(getWindow().getDecorView().findViewById(R.id.form_edit_event), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_ACCIONES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_ACCIONES) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
        });
    }

    private void setAutoCompleteEmoticons() {

        editEventViewModel.isLoadingEmoticons().observe(this, aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);

                ilEventHour.setEnabled(false);
                etEventHour.setEnabled(false);

                spinner.setEnabled(false);

                ilAction.setEnabled(false);
                acActions.setEnabled(false);

                ilJustification.setEnabled(false);
                etJustification.setEnabled(false);

            } else {
                progressBar.setVisibility(View.GONE);

                ilEventHour.setEnabled(true);
                etEventHour.setEnabled(true);

                spinner.setEnabled(true);

                ilAction.setEnabled(true);
                acActions.setEnabled(true);

                ilJustification.setEnabled(true);
                etJustification.setEnabled(true);
            }
        });

        editEventViewModel.loadEmoticons().observe(this, emoticons -> {

            if (emoticons != null && emoticons.size() > 0) {

                progressBar.setVisibility(View.GONE);

                emoticonList = emoticons;

                emoticonAdapter = new EmoticonAdapter(getApplicationContext(), emoticonList);
                spinner.setAdapter(emoticonAdapter);

                isSpinnerEmoticons = true;

                setInfoEvent();

                Log.d(getString(R.string.TAG_VIEW_MODEL_EMOTICON), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_EMOTICON) + getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
            }
        });

        editEventViewModel.showMsgErrorEmoticons().observe(this, s -> {

            progressBar.setVisibility(View.GONE);

            if (!isSnackBarShow) {

                isSnackBarShow = true;
                showSnackbar(getWindow().getDecorView().findViewById(R.id.form_edit_event), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_EMOTICON), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_EMOTICON) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

        });
    }

    private void configSpinnerEmoticons() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d(getString(R.string.SPINNER_EMOTICON_SELECTED), spinner.getSelectedItem().toString());
                crashlytics.log(getString(R.string.SPINNER_EMOTICON_SELECTED) + spinner.getSelectedItem().toString());

                Emoticon emoticon = emoticonList.get(position);
                eventIntent.setEmoticon(emoticon);
                spinner.setSelected(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setSelected(false);
            }
        });
    }

    private void configSpeechIntent() {
        ilJustification.setEndIconOnClickListener(v -> {

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        });
    }

    private void initViewModelEvent() {

        editEventViewModel.isLoadingEvent().observe(this, aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);

                ilEventHour.setEnabled(false);
                etEventHour.setEnabled(false);

                spinner.setEnabled(false);

                ilAction.setEnabled(false);
                acActions.setEnabled(false);

                ilJustification.setEnabled(false);
                etJustification.setEnabled(false);

            } else {
                progressBar.setVisibility(View.GONE);

                ilEventHour.setEnabled(true);
                etEventHour.setEnabled(true);

                spinner.setEnabled(true);

                ilAction.setEnabled(true);
                acActions.setEnabled(true);

                ilJustification.setEnabled(true);
                etJustification.setEnabled(true);
            }
        });

        editEventViewModel.loadEvent(eventIntent).observe(this, event -> {

            if (event != null) {

                progressBar.setVisibility(View.GONE);

                eventIntent = event;

                Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_EVENTO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), eventIntent.toString()));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_EDITAR_EVENTO) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), eventIntent.toString()));

                isGetEvent = true;

                setInfoEvent();
            }
        });

        editEventViewModel.showMsgErrorEvent().observe(this, s -> {

            progressBar.setVisibility(View.GONE);

            if (!isSnackBarShow) {

                isSnackBarShow = true;
                showSnackbar(getWindow().getDecorView().findViewById(R.id.form_edit_event), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_EVENTO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_EDITAR_EVENTO) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
        });


        editEventViewModel.showMsgUpdate().observe(this, s -> {

            Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_EVENTO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_EDITAR_EVENTO) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));

            if (s.equals(getString(R.string.MSG_UPDATE_EVENTO))) {

                progressBar.setVisibility(View.GONE);

                Intent intent = getIntent();
                intent.putExtra(getString(R.string.INTENT_KEY_MSG_ACTUALIZACION), s);
                setResult(RESULT_OK, intent);

                finish();
            }
        });


        editEventViewModel.showMsgErrorUpdate().observe(this, s -> {

            progressBar.setVisibility(View.GONE);

            if (!isSnackBarShow) {

                isSnackBarShow = true;
                showSnackbar(getWindow().getDecorView().findViewById(R.id.form_edit_event), Snackbar.LENGTH_LONG, s, null);
            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_EDITAR_EVENTO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_EDITAR_EVENTO) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
        });

    }

    private void setInfoEvent() {

        if (isAutoCompleteAction && isSpinnerEmoticons && isGetEvent) {

            etEventHour.setText(Utils.dateToString(getApplicationContext(), true, eventIntent.getEventHour()));

            int position = searchEmoticonPositionById(eventIntent.getEmoticon().getId());
            spinner.setSelection(position);

            etJustification.setText(eventIntent.getJustification());

            String nombreAccion = Objects.requireNonNull(searchActionById(eventIntent.getAction().getId())).getName();
            acActions.setText(nombreAccion, false);

            isAutoCompleteAction = false;
            isSpinnerEmoticons = false;
            isGetEvent = false;
        }
    }


    @Override
    public void showSnackbar(View v, int duration, String title, String action) {

        Snackbar snackbar = Snackbar.make(v, title, duration);

        if (action != null) {

            snackbar.setAction(action, v1 -> {

                isAutoCompleteAction = false;
                isSpinnerEmoticons = false;
                isGetEvent = false;


                editEventViewModel.refreshAction(language);
                editEventViewModel.refreshEmoticons();

                editEventViewModel.refreshEvent(eventIntent);

                progressBar.setVisibility(View.VISIBLE);

                isSnackBarShow = false;
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
                interview.setId(eventIntent.getInterview().getId());
                eventIntent.setInterview(interview);

                int id = Objects.requireNonNull(searchActionByName(acActions.getText().toString())).getId();
                Action action = new Action();
                action.setId(id);
                eventIntent.setAction(action);

                try {
                    eventIntent.setEventHour(Utils.stringToDate(getApplicationContext(), true, Objects.requireNonNull(etEventHour.getText(), "Et event hour cannot be null").toString()));

                } catch (ParseException e) {

                    Log.d("STRING_TO_DATE", "Parse exception");
                    e.printStackTrace();
                }

                eventIntent.setJustification(Objects.requireNonNull(etJustification.getText()).toString());

                EventRepository.getInstance(getApplication()).updateEvent(eventIntent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == SPEECH_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                List<String> results = Objects.requireNonNull(data, "Extra cannot be null").getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String spokenText = Objects.requireNonNull(results, "Google speech text cannot be null").get(0);

                etJustification.setText(spokenText);
            }
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


        if (acActions.getText().toString().isEmpty()) {

            ilAction.setErrorEnabled(true);
            ilAction.setError(getString(R.string.VALIDACION_CAMPO_REQUERIDO));
            errorCounter++;

        } else if (searchActionByName(acActions.getText().toString()) == null) {

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

    private int searchEmoticonPositionById(int id) {

        for (int i = 0; i < emoticonList.size(); i++) {

            if (emoticonList.get(i).getId() == id) {
                return i;
            }
        }

        return 0;
    }

    private Action searchActionById(int id) {

        for (int i = 0; i < actionList.size(); i++) {

            if (actionList.get(i).getId() == id) {
                return actionList.get(i);
            }
        }

        return null;
    }

    private Action searchActionByName(String nombre) {

        for (int i = 0; i < actionList.size(); i++) {
            if (actionList.get(i).getName().equals(nombre)) {
                return actionList.get(i);
            }
        }
        return null;
    }
}
