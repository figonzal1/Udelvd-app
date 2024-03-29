package cl.udelvd.views.activities;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.adapters.FragmentStatePageAdapter;
import cl.udelvd.models.Event;
import cl.udelvd.models.Interview;
import cl.udelvd.models.Interviewee;
import cl.udelvd.repositories.EventRepository;
import cl.udelvd.utils.SnackbarInterface;
import cl.udelvd.utils.Utils;
import cl.udelvd.viewmodels.EventListViewModel;
import cl.udelvd.views.fragments.dialog.DeleteDialogListener;

public class EventsActivity extends AppCompatActivity implements DeleteDialogListener, SnackbarInterface {

    private static final int REQUEST_CODE_NEW_EVENT = 200;
    private static final int REQUEST_CODE_EDIT_EVENT = 300;

    private String nNormals;
    private String nExtraordinaries;
    private int nInterviews;
    private String interviewDate;
    private int annos;

    private TextView tvEmptyEvents;
    private CardView cvInfo;

    private Interview interview;
    private Interviewee interviewee;

    private List<Event> eventList;
    private EventListViewModel eventListViewModel;

    private FragmentStatePageAdapter fragmentStatePageAdapter;
    private ViewPager viewPager;
    private ProgressBar progressBar;

    private boolean isSnackBarShow = false;
    private Snackbar snackbar;

    private FirebaseCrashlytics crashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_main);

        crashlytics = FirebaseCrashlytics.getInstance();

        Utils.configToolbar(this, getApplicationContext(), 0, getString(R.string.TITULO_TOOLBAR_EVENTOS));

        getBundleData();

        instantiateInterfaceResources();

        floatingButtonNewEvent();

        initViewModel();
    }

    private void getBundleData() {

        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();

            interview = new Interview();
            interview.setId(bundle.getInt(getString(R.string.KEY_ENTREVISTA_ID_LARGO)));
            interview.setIdInterviewee(bundle.getInt(getString(R.string.KEY_ENTREVISTA_ID_ENTREVISTADO)));

            interviewDate = bundle.getString(getString(R.string.KEY_ENTREVISTA_FECHA_ENTREVISTA));

            interviewee = new Interviewee();
            interviewee.setName(bundle.getString(getString(R.string.KEY_ENTREVISTADO_NOMBRE_LARGO)));
            interviewee.setLastName(bundle.getString(getString(R.string.KEY_ENTREVISTADO_APELLIDO_LARGO)));
            interviewee.setGender(bundle.getString(getString(R.string.KEY_ENTREVISTADO_SEXO_LARGO)));
            annos = bundle.getInt(getString(R.string.KEY_ENTREVISTADO_ANNOS));

            nInterviews = bundle.getInt(getString(R.string.KEY_ENTREVISTADO_N_ENTREVISTAS));
            nNormals = bundle.getString(getString(R.string.KEY_ENTREVISTA_N_NORMALES));
            nExtraordinaries = bundle.getString(getString(R.string.KEY_ENTREVISTA_N_EXTRAORDINARIAS));
        }
    }

    private void instantiateInterfaceResources() {

        eventList = new ArrayList<>();

        eventListViewModel = new ViewModelProvider(this).get(EventListViewModel.class);

        cvInfo = findViewById(R.id.cv_interviewee_info);
        cvInfo.setVisibility(View.INVISIBLE);

        progressBar = findViewById(R.id.progress_bar_eventos);
        progressBar.setVisibility(View.VISIBLE);

        viewPager = findViewById(R.id.view_pager_events);
        viewPager.setVisibility(View.INVISIBLE);

        tvEmptyEvents = findViewById(R.id.tv_eventos_vacios);
        tvEmptyEvents.setVisibility(View.INVISIBLE);

        TabLayout tabLayout = findViewById(R.id.tab_dots);
        tabLayout.setupWithViewPager(viewPager, true);

        ImageView ivPerson = findViewById(R.id.cv_iv_interviewee_person);
        TextView tvCompleteName = findViewById(R.id.tv_interviewee_name);
        TextView tvNInterviewee = findViewById(R.id.tv_n_interviews);
        TextView tvNormal = findViewById(R.id.tv_normal_interviews_value);
        TextView tvExtraordinary = findViewById(R.id.tv_extraordinary_interviews_value);

        fragmentStatePageAdapter = new FragmentStatePageAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, eventList, interviewDate, EventsActivity.this, EventsActivity.this);
        viewPager.setAdapter(fragmentStatePageAdapter);

        tvCompleteName.setText(String.format("%s %s", interviewee.getName(), interviewee.getLastName()));
        tvNormal.setText(nNormals);
        tvExtraordinary.setText(nExtraordinaries);

        tvNInterviewee.setText(getResources().getQuantityString(R.plurals.FORMATO_N_ENTREVISTA, nInterviews, nInterviews));


        Utils.configIconInterviewee(interviewee, annos, ivPerson, getApplicationContext());
    }

    private void initViewModel() {

        eventListViewModel.isLoading().observe(this, aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.INVISIBLE);
                cvInfo.setVisibility(View.INVISIBLE);

            } else {
                progressBar.setVisibility(View.INVISIBLE);
                viewPager.setVisibility(View.VISIBLE);
                cvInfo.setVisibility(View.VISIBLE);
            }
        });


        eventListViewModel.loadEvents(interview).observe(this, events -> {

            if (events != null) {

                eventList = events;
                progressBar.setVisibility(View.INVISIBLE);
                viewPager.setVisibility(View.VISIBLE);
                cvInfo.setVisibility(View.VISIBLE);

                if (eventList.size() == 0) {

                    tvEmptyEvents.setVisibility(View.VISIBLE);

                } else {
                    tvEmptyEvents.setVisibility(View.INVISIBLE);
                    fragmentStatePageAdapter.updateList(eventList);
                }
            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_EVENTOS), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), getString(R.string.VIEW_MODEL_LISTADO_CARGADO)));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_LISTA_EVENTOS) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), getString(R.string.VIEW_MODEL_LISTADO_CARGADO)));
        });


        eventListViewModel.showMsgErrorList().observe(this, s -> {

            progressBar.setVisibility(View.GONE);
            viewPager.setVisibility(View.INVISIBLE);
            cvInfo.setVisibility(View.INVISIBLE);

            if (!isSnackBarShow) {

                isSnackBarShow = true;
                showSnackbar(findViewById(R.id.event_list), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                fragmentStatePageAdapter.notifyDataSetChanged();

            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_EVENTOS), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_LISTA_EVENTOS) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
        });


        eventListViewModel.showMsgDelete().observe(this, s -> {

            if (s.equals(getString(R.string.MSG_DELETE_EVENTO))) {

                isSnackBarShow = true;
                showSnackbar(findViewById(R.id.event_list), Snackbar.LENGTH_LONG, s, null);
                EventRepository.getInstance(getApplication()).getInterviewEvents(interview);

                fragmentStatePageAdapter.notifyDataSetChanged();
                Objects.requireNonNull(viewPager.getAdapter(), "View pager cannot be null").notifyDataSetChanged();

            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_EVENTO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_EVENTO) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
        });

        eventListViewModel.showMsgErrorDelete().observe(this, s -> {

            progressBar.setVisibility(View.GONE);

            if (!isSnackBarShow) {

                isSnackBarShow = true;

                if (!s.equals(getString(R.string.SERVER_ERROR_MSG_VM))) {

                    showSnackbar(findViewById(R.id.event_list), Snackbar.LENGTH_INDEFINITE, s, null);

                } else {
                    showSnackbar(findViewById(R.id.event_list), Snackbar.LENGTH_LONG, s, null);
                }

                fragmentStatePageAdapter.notifyDataSetChanged();
            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_EVENTO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_EVENTO) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
        });
    }

    private void floatingButtonNewEvent() {

        FloatingActionButton fabNewEvent = findViewById(R.id.fb_crear_evento);

        fabNewEvent.setOnClickListener(view -> {

            Intent intent = new Intent(EventsActivity.this, NewEventActivity.class);
            intent.putExtra(getString(R.string.KEY_ENTREVISTA_ID_LARGO), interview.getId());
            startActivityForResult(intent, REQUEST_CODE_NEW_EVENT);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();

            return true;

        } else if (item.getItemId() == R.id.menu_refresh) {

            progressBar.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.INVISIBLE);

            isSnackBarShow = false;

            if (snackbar != null) {
                snackbar.dismiss();
            }

            eventListViewModel.refreshEvents(interview);
            fragmentStatePageAdapter.notifyDataSetChanged();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showSnackbar(View v, int snack_length, String title, String action) {

        snackbar = Snackbar.make(v, title, snack_length);

        if (action != null) {

            snackbar.setAction(action, v1 -> {

                progressBar.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.INVISIBLE);

                isSnackBarShow = false;

                if (snackbar != null) {
                    snackbar.dismiss();
                }

                eventListViewModel.refreshEvents(interview);
            });
        }

        snackbar.show();
        isSnackBarShow = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_NEW_EVENT) {

            if (resultCode == RESULT_OK) {

                Bundle bundle = Objects.requireNonNull(data, "Extras cannot be null").getExtras();
                String msg_registro = Objects.requireNonNull(bundle, "Msg_registro cannot be null").getString(getString(R.string.INTENT_KEY_MSG_REGISTRO));

                if (msg_registro != null) {

                    isSnackBarShow = false;
                    showSnackbar(findViewById(R.id.event_list), Snackbar.LENGTH_LONG, msg_registro, null);
                    eventListViewModel.refreshEvents(interview);

                    fragmentStatePageAdapter.notifyDataSetChanged();
                }
            }

        } else if (requestCode == REQUEST_CODE_EDIT_EVENT) {

            if (resultCode == RESULT_OK) {

                Bundle bundle = Objects.requireNonNull(data, "Extras cannot be null").getExtras();
                String msg_actualizacion = Objects.requireNonNull(bundle, "Msg_actualizacion cannot be null").getString(getString(R.string.INTENT_KEY_MSG_ACTUALIZACION));

                if (msg_actualizacion != null) {

                    isSnackBarShow = false;
                    showSnackbar(findViewById(R.id.event_list), Snackbar.LENGTH_LONG, msg_actualizacion, null);
                    eventListViewModel.refreshEvents(interview);

                    fragmentStatePageAdapter.notifyDataSetChanged();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDialogPositiveClick(Object object) {

        EventRepository.getInstance(getApplication()).deleteEvent((Event) object);
        fragmentStatePageAdapter.notifyDataSetChanged();
    }
}
