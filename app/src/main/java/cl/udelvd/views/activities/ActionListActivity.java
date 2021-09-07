package cl.udelvd.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.adapters.ActionAdapter;
import cl.udelvd.models.Action;
import cl.udelvd.repositories.ActionRepository;
import cl.udelvd.utils.SnackbarInterface;
import cl.udelvd.utils.Utils;
import cl.udelvd.viewmodels.ActionListViewModel;
import cl.udelvd.views.fragments.dialog.DeleteDialogListener;

public class ActionListActivity extends AppCompatActivity implements SnackbarInterface, DeleteDialogListener {

    private static final int REQUEST_CODE_NEW_ACTION = 200;
    private static final int ACTIONS_ACTIVITY_CODE = 200;
    private static final int REQUEST_CODE_EDIT_ACTION = 201;

    private List<Action> actionList;
    private ProgressBar progressBar;
    private TextView tvEmptyAction;
    private RecyclerView rv;
    private ActionListViewModel actionListViewModel;
    private ActionAdapter actionAdapter;
    private boolean isSnackBarShow = false;
    private Snackbar snackbar;

    private FirebaseCrashlytics crashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_list);

        crashlytics = FirebaseCrashlytics.getInstance();

        Utils.configToolbar(this, getApplicationContext(), 0, getString(R.string.TITULO_TOOLBAR_LISTADO_ACCIONES));

        instantiateInterfaceResources();

        initViewModelList();

        floatingButtonNewAction();
    }

    private void instantiateInterfaceResources() {

        actionList = new ArrayList<>();

        progressBar = findViewById(R.id.progress_bar_action);
        progressBar.setVisibility(View.VISIBLE);

        tvEmptyAction = findViewById(R.id.tv_empty_action);
        tvEmptyAction.setVisibility(View.INVISIBLE);

        rv = findViewById(R.id.rv_list_action);

        LinearLayoutManager ly = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(ly);

        actionListViewModel = new ViewModelProvider(this).get(ActionListViewModel.class);

        actionAdapter = new ActionAdapter(
                actionList,
                getApplicationContext(),
                ActionListActivity.this,
                getSupportFragmentManager(),
                REQUEST_CODE_EDIT_ACTION
        );

        rv.setAdapter(actionAdapter);
    }

    private void initViewModelList() {

        actionListViewModel.isLoading().observe(this, aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);
                tvEmptyAction.setVisibility(View.INVISIBLE);
                rv.setVisibility(View.INVISIBLE);

            } else {
                progressBar.setVisibility(View.INVISIBLE);
                rv.setVisibility(View.VISIBLE);
            }
        });

        actionListViewModel.loadActions().observe(this, actions -> {

            if (actions != null) {

                actionList = actions;
                actionAdapter.updateList(actionList);

                progressBar.setVisibility(View.INVISIBLE);

                if (actionList.size() == 0) {

                    tvEmptyAction.setVisibility(View.VISIBLE);
                } else {
                    tvEmptyAction.setVisibility(View.INVISIBLE);
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ACCIONES), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_LISTA_ACCIONES) + getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
            }
        });

        actionListViewModel.showMsgErrorList().observe(this, s -> {

            progressBar.setVisibility(View.INVISIBLE);

            Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ACCIONES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_LISTA_ACCIONES) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

            if (!isSnackBarShow) {

                rv.setVisibility(View.INVISIBLE);
                isSnackBarShow = true;
                showSnackbar(findViewById(R.id.action_list), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                actionAdapter.notifyDataSetChanged();
            }
        });

        actionListViewModel.showMsgDelete().observe(this, s -> {

            progressBar.setVisibility(View.INVISIBLE);

            if (s.equals(getString(R.string.MSG_DELETE_ACCION))) {

                isSnackBarShow = true;
                showSnackbar(findViewById(R.id.action_list), Snackbar.LENGTH_LONG, s, null);
                ActionRepository.getInstance(getApplication()).getActions();
            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_ACCION), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_ACCION) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
        });

        actionListViewModel.showMsgErrorDelete().observe(this, s -> {

            progressBar.setVisibility(View.INVISIBLE);

            if (!isSnackBarShow) {

                isSnackBarShow = true;

                if (!s.equals(getString(R.string.SERVER_ERROR_MSG_VM))) {

                    rv.setVisibility(View.INVISIBLE);
                    showSnackbar(findViewById(R.id.action_list), Snackbar.LENGTH_INDEFINITE, s, null);

                } else {
                    showSnackbar(findViewById(R.id.action_list), Snackbar.LENGTH_LONG, s, null);
                }
                actionAdapter.notifyDataSetChanged();
            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_ACCION), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_ACCION) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
        });
    }

    private void floatingButtonNewAction() {

        FloatingActionButton fabNewAction = findViewById(R.id.fb_new_action);

        fabNewAction.setOnClickListener(view -> {

            Intent intent = new Intent(ActionListActivity.this, NewActionActivity.class);
            startActivityForResult(intent, REQUEST_CODE_NEW_ACTION);
        });
    }

    @Override
    public void showSnackbar(View v, int duration, String title, String action) {

        snackbar = Snackbar.make(v, title, duration);

        if (action != null) {

            snackbar.setAction(action, v1 -> {

                progressBar.setVisibility(View.VISIBLE);

                isSnackBarShow = false;

                if (snackbar != null) {
                    snackbar.dismiss();
                }

                actionListViewModel.refreshActions();
            });
        }

        snackbar.show();
        isSnackBarShow = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_update, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            Intent intent = getIntent();
            setResult(ACTIONS_ACTIVITY_CODE, intent);
            finish();

            return true;

        } else if (item.getItemId() == R.id.menu_refresh) {

            progressBar.setVisibility(View.VISIBLE);

            isSnackBarShow = false;

            if (snackbar != null) {
                snackbar.dismiss();
            }
            actionListViewModel.refreshActions();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_NEW_ACTION) {

            if (resultCode == RESULT_OK) {

                Bundle bundle = Objects.requireNonNull(data, "Get extras cannot be null").getExtras();
                String msg_registro = Objects.requireNonNull(bundle, "Msg_registro cannot be null").getString(getString(R.string.INTENT_KEY_MSG_REGISTRO));

                if (msg_registro != null) {

                    isSnackBarShow = false;
                    showSnackbar(findViewById(R.id.action_list), Snackbar.LENGTH_LONG, msg_registro, null);
                    actionListViewModel.refreshActions();
                }
            }
        } else if (requestCode == REQUEST_CODE_EDIT_ACTION) {

            if (resultCode == RESULT_OK) {

                Bundle bundle = Objects.requireNonNull(data, "Get extras cannot be null").getExtras();
                String msg_actualizacion = Objects.requireNonNull(bundle, "Msg_actualizacion cannot be null").getString(getString(R.string.INTENT_KEY_MSG_ACTUALIZACION));

                if (msg_actualizacion != null) {

                    isSnackBarShow = false;

                    showSnackbar(findViewById(R.id.action_list), Snackbar.LENGTH_LONG, msg_actualizacion, null);

                    actionListViewModel.refreshActions();

                    actionAdapter.notifyDataSetChanged();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDialogPositiveClick(Object object) {
        ActionRepository.getInstance(getApplication()).deleteAction((Action) object);
    }
}
