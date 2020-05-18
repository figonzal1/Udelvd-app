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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cl.udelvd.R;
import cl.udelvd.adapters.ResearcherAdapter;
import cl.udelvd.models.Researcher;
import cl.udelvd.repositories.ResearcherRepository;
import cl.udelvd.utils.SnackbarInterface;
import cl.udelvd.utils.Utils;
import cl.udelvd.viewmodels.ResearcherListViewModel;
import cl.udelvd.views.fragments.dialog.ActivateAccountDialogListener;

public class ResearcherListActivity extends AppCompatActivity implements SnackbarInterface, ActivateAccountDialogListener {

    private static final int RESEARCHER_ACTIVITY_CODE = 200;

    private RecyclerView rv;
    private List<Researcher> researcherList;
    private ResearcherListViewModel researcherListViewModel;
    private ResearcherAdapter researcherAdapter;

    private ProgressBar progressBar;
    private boolean isSnackBarShow = false;
    private TextView tvEmptyResearchers;
    private TextView tvActivatedResearcher;
    private Researcher researcher;
    private int totalResearchers;
    private TextView tvNResearchers;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_researcher_list);

        Utils.configToolbar(this, getApplicationContext(), 0, getString(R.string.TITULO_TOOLBAR_LISTADO_INVESTIGADORES));

        getAdminData();

        instantiateInterfaceResources();

        initViewModelList();
    }

    private void instantiateInterfaceResources() {

        researcherList = new ArrayList<>();

        progressBar = findViewById(R.id.progress_bar_researchers);
        progressBar.setVisibility(View.VISIBLE);

        rv = findViewById(R.id.rv_researchers_list);

        LinearLayoutManager ly = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(ly);

        tvNResearchers = findViewById(R.id.tv_n_researchers);
        tvNResearchers.setVisibility(View.INVISIBLE);

        tvEmptyResearchers = findViewById(R.id.tv_empty_researchers);
        tvEmptyResearchers.setVisibility(View.INVISIBLE);

        tvActivatedResearcher = findViewById(R.id.tv_activating);
        tvActivatedResearcher.setVisibility(View.INVISIBLE);

        researcherListViewModel = new ViewModelProvider(this).get(ResearcherListViewModel.class);

        researcherAdapter = new ResearcherAdapter(
                researcherList,
                getApplicationContext(),
                getSupportFragmentManager(),
                researcherListViewModel,
                researcher
        );

        rv.setAdapter(researcherAdapter);
    }

    private void initViewModelList() {
        researcherListViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);
                    tvEmptyResearchers.setVisibility(View.INVISIBLE);
                    tvNResearchers.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.VISIBLE);
                }
            }
        });

        researcherListViewModel.showNInterviewees().observe(ResearcherListActivity.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                totalResearchers = integer;
                researcherAdapter.setTotalResearcher(totalResearchers);
            }
        });

        researcherListViewModel.loadFirstPage(1, researcher).observe(this, new Observer<List<Researcher>>() {
            @Override
            public void onChanged(List<Researcher> researchers) {

                researcherList = researchers;
                researcherAdapter.updateList(researcherList);
                rv.setAdapter(researcherAdapter);

                progressBar.setVisibility(View.INVISIBLE);
                if (researcherList.size() == 0) {
                    tvEmptyResearchers.setVisibility(View.VISIBLE);
                } else {
                    tvEmptyResearchers.setVisibility(View.INVISIBLE);
                }

                tvNResearchers.setVisibility(View.VISIBLE);
                tvNResearchers.setText(String.format(Locale.getDefault(), getString(R.string.MOSTRAR_INVESTIGADORES), researcherAdapter.getResearcherList().size(), totalResearchers));

                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_INVESTIGADORES), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));

            }
        });

        researcherListViewModel.showNextPage().observe(this, new Observer<List<Researcher>>() {
            @Override
            public void onChanged(List<Researcher> researchers) {


                researcherAdapter.addInterviewee(researchers);
                researcherAdapter.hideProgress();

                researcherList = researcherAdapter.getResearcherList();

                if (researcherList.size() == 0) {
                    tvEmptyResearchers.setVisibility(View.VISIBLE);
                } else {
                    tvEmptyResearchers.setVisibility(View.INVISIBLE);
                }
                tvNResearchers.setVisibility(View.VISIBLE);
                tvNResearchers.setText(String.format(Locale.getDefault(), getString(R.string.MOSTRAR_INVESTIGADORES), researcherAdapter.getResearcherList().size(), totalResearchers));

                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_INVESTIGADORES), getString(R.string.VIEW_MODEL_LISTA_INVESTIGADORES_MSG) + "PAGINA");
            }
        });

        researcherListViewModel.showMsgErrorList().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.INVISIBLE);

                if (!isSnackBarShow) {
                    rv.setVisibility(View.INVISIBLE);
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.researchers_list), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                    researcherAdapter.notifyDataSetChanged();
                }

                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTADO_INVESTIGADORES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });

        researcherListViewModel.activatingResearcher().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    tvActivatedResearcher.setVisibility(View.VISIBLE);

                    progressBar.setVisibility(View.VISIBLE);
                    tvNResearchers.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.INVISIBLE);
                } else {
                    tvActivatedResearcher.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.VISIBLE);
                }
            }
        });

        researcherListViewModel.showMsgActivation().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.INVISIBLE);
                tvActivatedResearcher.setVisibility(View.INVISIBLE);

                if (s.equals(getString(R.string.MSG_INVEST_CUENTA_ACTIVADA)) || s.equals(getString(R.string.MSG_INVEST_CUENTA_DESACTIVADA))) {
                    isSnackBarShow = true;
                    showSnackbar(findViewById(R.id.researchers_list), Snackbar.LENGTH_LONG, s, null);
                    ResearcherRepository.getInstance(getApplication()).getResearchers(1, researcher);
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_ACTIVACION_INVES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
            }
        });

        researcherListViewModel.showMsgErrorActivation().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.INVISIBLE);
                tvActivatedResearcher.setVisibility(View.INVISIBLE);

                if (!isSnackBarShow) {
                    isSnackBarShow = true;

                    if (!s.equals(getString(R.string.SERVER_ERROR_MSG_VM))) {
                        rv.setVisibility(View.INVISIBLE);
                        showSnackbar(findViewById(R.id.researchers_list), Snackbar.LENGTH_INDEFINITE, s, null);
                    } else {
                        showSnackbar(findViewById(R.id.researchers_list), Snackbar.LENGTH_LONG, s, null);
                    }

                    researcherAdapter.notifyDataSetChanged();
                }
                Log.d(getString(R.string.TAG_VIEW_MODEL_ACTIVACION_INVES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            }
        });
    }

    private void getAdminData() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);
        researcher = new Researcher();
        researcher.setId(sharedPreferences.getInt(getString(R.string.SHARED_PREF_INVES_ID), 0));
    }

    @Override
    public void onDialogPositiveClick(Object object, boolean activated) {
        Researcher invAdapter = (Researcher) object;
        ResearcherRepository.getInstance(getApplication()).activateAccount(invAdapter);

        if (activated) {
            tvActivatedResearcher.setText(getString(R.string.ACTIVANDO_CUENTA));
            tvActivatedResearcher.setVisibility(View.VISIBLE);
        } else {
            tvActivatedResearcher.setText(getString(R.string.DESACTIVANDO_CUENTA));
            tvActivatedResearcher.setVisibility(View.VISIBLE);
        }
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

                    researcherAdapter.resetPages();
                    researcherListViewModel.refreshResearchers(researcher);

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
            Intent intent = getIntent();
            setResult(RESEARCHER_ACTIVITY_CODE, intent);
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_update) {

            progressBar.setVisibility(View.VISIBLE);

            isSnackBarShow = false;
            if (snackbar != null) {
                snackbar.dismiss();
            }

            researcherAdapter.resetPages();
            researcherListViewModel.refreshResearchers(researcher);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
