package cl.udelvd.views.activities;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cl.udelvd.R;
import cl.udelvd.adapters.InterviewAdapter;
import cl.udelvd.models.Interview;
import cl.udelvd.models.Interviewee;
import cl.udelvd.repositories.InterviewRepository;
import cl.udelvd.utils.SnackbarInterface;
import cl.udelvd.utils.Utils;
import cl.udelvd.viewmodels.InterviewListViewModel;
import cl.udelvd.views.fragments.dialog.DeleteDialogListener;

public class InterviewsListActivity extends AppCompatActivity implements DeleteDialogListener, SnackbarInterface {

    private static final int REQUEST_CODE_NEW_INTERVIEW = 300;
    private static final int REQUEST_CODE_EDIT_INTERVIEW = 301;

    private RecyclerView rv;
    private CardView cvInfo;
    private InterviewAdapter interviewAdapter;
    private InterviewListViewModel interviewListViewModel;

    private TextView tvNInterviews;
    private TextView tvNormalInterview;
    private TextView tvExtraordinaryInterview;

    private TextView tvEmptyInterviews;

    private Interviewee interviewee;

    private Map<String, Integer> params;

    private ProgressBar progressBar;
    private List<Interview> interviewList;
    private boolean isSnackBarShow = false;
    private int annos;
    private Snackbar snackbar;

    private FirebaseCrashlytics crashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_list);

        crashlytics = FirebaseCrashlytics.getInstance();

        Utils.configToolbar(this, getApplicationContext(), 0, getString(R.string.TITULO_TOOLBAR_LISTA_ENTREVISTAS));

        getBundleData();

        instantiateInterfaceResources();

        initViewModelObservers();

        floatingButtonNewInterview();

    }

    private void getBundleData() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            interviewee = new Interviewee();

            int idInterview = bundle.getInt(getString(R.string.KEY_ENTREVISTADO_ID_LARGO));
            String nameInterview = bundle.getString(getString(R.string.KEY_ENTREVISTADO_NOMBRE_LARGO));
            String lastNameInterview = bundle.getString(getString(R.string.KEY_ENTREVISTADO_APELLIDO_LARGO));
            String genre = bundle.getString(getString(R.string.KEY_ENTREVISTADO_SEXO_LARGO));
            annos = bundle.getInt(getString(R.string.KEY_ENTREVISTADO_ANNOS));
            String birthDate = bundle.getString(getString(R.string.KEY_ENTREVISTADO_FECHA_NAC));

            interviewee.setId(idInterview);
            interviewee.setName(nameInterview);
            interviewee.setLastName(lastNameInterview);
            interviewee.setGender(genre);
            interviewee.setBirthDate(Utils.stringToDate(getApplicationContext(), false, birthDate));
        }
    }

    private void instantiateInterfaceResources() {
        interviewList = new ArrayList<>();

        progressBar = findViewById(R.id.progress_bar_interviews);
        progressBar.setVisibility(View.VISIBLE);

        rv = findViewById(R.id.rv_interview_list);

        LinearLayoutManager ly = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(ly);

        tvEmptyInterviews = findViewById(R.id.tv_empty_interviews);
        tvEmptyInterviews.setVisibility(View.INVISIBLE);

        ImageView ivInterview = findViewById(R.id.cv_iv_interviewee_person);
        rv.setVisibility(View.INVISIBLE);
        cvInfo = findViewById(R.id.cv_interviewee_info);
        cvInfo.setVisibility(View.INVISIBLE);

        TextView tvCompleteName = findViewById(R.id.tv_interviewee_name);
        tvCompleteName.setText(String.format("%s %s", interviewee.getName(), interviewee.getLastName()));

        tvNInterviews = findViewById(R.id.tv_n_interviews);

        tvNormalInterview = findViewById(R.id.tv_normal_interviews_value);
        tvExtraordinaryInterview = findViewById(R.id.tv_extraordinary_interviews_value);

        interviewListViewModel = new ViewModelProvider(this).get(InterviewListViewModel.class);

        Utils.configIconInterviewee(interviewee, annos, ivInterview, getApplicationContext());

        interviewAdapter = new InterviewAdapter(
                interviewList,
                InterviewsListActivity.this,
                getSupportFragmentManager(),
                interviewee,
                params,
                REQUEST_CODE_EDIT_INTERVIEW);
        rv.setAdapter(interviewAdapter);
    }

    private void initViewModelObservers() {

        interviewListViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);
                    tvEmptyInterviews.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.INVISIBLE);
                    cvInfo.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.VISIBLE);
                    cvInfo.setVisibility(View.VISIBLE);
                }
            }
        });


        interviewListViewModel.loadInterviews(interviewee).observe(this, new Observer<List<Interview>>() {
            @Override
            public void onChanged(List<Interview> interviews) {
                if (interviews != null) {

                    interviewList = interviews;


                    if (interviews.size() == 1) {
                        tvNInterviews.setText(String.format(Locale.US, getString(R.string.FORMATO_N_ENTREVISTA), interviews.size()));
                    } else {
                        tvNInterviews.setText(String.format(Locale.US, getString(R.string.FORMATO_N_ENTREVISTAS), interviews.size()));
                    }

                    progressBar.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.VISIBLE);
                    cvInfo.setVisibility(View.VISIBLE);

                    if (interviewList.size() == 0) {
                        tvEmptyInterviews.setVisibility(View.VISIBLE);
                    } else {
                        tvEmptyInterviews.setVisibility(View.INVISIBLE);
                    }


                    Map<String, Integer> types = countTypes(interviews);
                    tvNormalInterview.setText(String.valueOf(types.get(getString(R.string.INTENT_KEY_NORMALES))));
                    tvExtraordinaryInterview.setText(String.valueOf(types.get(getString(R.string.INTENT_KEY_EXTRAORDINARIAS))));

                    params = new HashMap<>();
                    params.put(getString(R.string.KEY_ENTREVISTADO_N_ENTREVISTAS), interviews.size());
                    params.put(getString(R.string.KEY_ENTREVISTA_N_NORMALES), types.get(getString(R.string.INTENT_KEY_NORMALES)));
                    params.put(getString(R.string.KEY_ENTREVISTA_N_EXTRAORDINARIAS), types.get(getString(R.string.INTENT_KEY_EXTRAORDINARIAS)));

                    interviewAdapter.setParams(params);
                    interviewAdapter.updateList(interviewList);
                    rv.setAdapter(interviewAdapter);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ENTREVISTAS), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), interviews.toString()));
                    crashlytics.log(getString(R.string.TAG_VIEW_MODEL_LISTA_ENTREVISTAS) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), interviews.toString()));
                }
            }
        });


        interviewListViewModel.showMsgErrorList().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.INVISIBLE);
                rv.setVisibility(View.INVISIBLE);
                cvInfo.setVisibility(View.INVISIBLE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.interviewees_list), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                    interviewAdapter.notifyDataSetChanged();
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ENTREVISTAS), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_LISTA_ENTREVISTAS) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });


        interviewListViewModel.mostrarMsgDelete().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.INVISIBLE);

                if (s.equals(getString(R.string.MSG_DELETE_ENTREVISTA))) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.interviewees_list), Snackbar.LENGTH_LONG, s, null);
                    InterviewRepository.getInstance(getApplication()).getPersonalInterviews(interviewee);
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_ENTREVISTA) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
            }
        });


        interviewListViewModel.showMsgErrorDelete().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                progressBar.setVisibility(View.INVISIBLE);

                if (!isSnackBarShow) {

                    if (!s.equals(getString(R.string.SERVER_ERROR_MSG_VM))) {
                        showSnackbar(findViewById(R.id.interviewees_list), Snackbar.LENGTH_INDEFINITE, s, null);
                    } else {
                        showSnackbar(findViewById(R.id.interviewees_list), Snackbar.LENGTH_LONG, s, null);
                    }
                    interviewAdapter.notifyDataSetChanged();
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_ENTREVISTA), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
                crashlytics.log(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_ENTREVISTA) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    private void floatingButtonNewInterview() {
        FloatingActionButton fabNewInterviews = findViewById(R.id.fb_new_interview);
        fabNewInterviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InterviewsListActivity.this, NewInterviewActivity.class);
                intent.putExtra(getString(R.string.KEY_ENTREVISTADO_ID_LARGO), interviewee.getId());
                startActivityForResult(intent, REQUEST_CODE_NEW_INTERVIEW);
            }
        });
    }

    /**
     * Function responsible for counting normal and extraordinary interviews
     *
     * @param interviews Interview list
     * @return Map with normal and extraordinary counts
     */
    private Map<String, Integer> countTypes(List<Interview> interviews) {
        int normals = 0;
        int extraordinaries = 0;
        for (int i = 0; i < interviews.size(); i++) {
            if (interviews.get(i).getInterviewType().getName().equals(getString(R.string.NORMAL))) {
                normals++;
            } else if (interviews.get(i).getInterviewType().getName().equals(getString(R.string.EXTRAORDINARIA))) {
                extraordinaries++;
            }
        }

        Map<String, Integer> map = new HashMap<>();
        map.put(getString(R.string.INTENT_KEY_NORMALES), normals);
        map.put(getString(R.string.INTENT_KEY_EXTRAORDINARIAS), extraordinaries);
        return map;
    }

    @Override
    public void showSnackbar(View v, int tipo_snackbar, String title, String action) {

        snackbar = Snackbar.make(v, title, tipo_snackbar);
        if (action != null) {
            snackbar.setAction(action, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progressBar.setVisibility(View.VISIBLE);

                    isSnackBarShow = false;
                    if (snackbar != null) {
                        snackbar.dismiss();
                    }


                    interviewListViewModel.refreshInterviews(interviewee);
                }
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
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_refresh) {

            progressBar.setVisibility(View.VISIBLE);
            isSnackBarShow = false;
            if (snackbar != null) {
                snackbar.dismiss();
            }
            interviewListViewModel.refreshInterviews(interviewee);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_NEW_INTERVIEW) {
            if (resultCode == RESULT_OK) {

                assert data != null;
                Bundle bundle = data.getExtras();
                assert bundle != null;
                showSnackbar(findViewById(R.id.interviewees_list), Snackbar.LENGTH_LONG, bundle.getString(getString(R.string.INTENT_KEY_MSG_REGISTRO)), null);

                interviewListViewModel.refreshInterviews(interviewee);
            }
        } else if (requestCode == REQUEST_CODE_EDIT_INTERVIEW) {
            if (resultCode == RESULT_OK) {

                assert data != null;
                Bundle bundle = data.getExtras();
                assert bundle != null;
                showSnackbar(findViewById(R.id.interviewees_list), Snackbar.LENGTH_LONG, bundle.getString(getString(R.string.INTENT_KEY_MSG_ACTUALIZACION)), null);

                interviewListViewModel.refreshInterviews(interviewee);
            }
        }
        isSnackBarShow = false;
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDialogPositiveClick(Object object) {
        InterviewRepository.getInstance(getApplication()).deleteInterview((Interview) object);
    }
}
