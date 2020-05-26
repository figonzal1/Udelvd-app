package cl.udelvd.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import cl.udelvd.R;
import cl.udelvd.adapters.StatAdapter;
import cl.udelvd.models.Stat;
import cl.udelvd.utils.SnackbarInterface;
import cl.udelvd.viewmodels.StatViewModel;


public class StatsFragment extends Fragment implements SnackbarInterface {

    private RecyclerView rv;
    private StatViewModel statViewModel;
    private StatAdapter statAdapter;
    private ProgressBar progressBar;

    private List<Stat> statList;
    private boolean isSnackBarShow = false;
    private Snackbar snackbar;

    public StatsFragment() {
    }

    public static StatsFragment newInstance() {
        return new StatsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stats, container, false);

        instantiateInterfaceResources(v);

        initViewModelObservers(v);

        return v;
    }

    private void instantiateInterfaceResources(View v) {
        statList = new ArrayList<>();

        statViewModel = new ViewModelProvider(this).get(StatViewModel.class);

        progressBar = v.findViewById(R.id.progress_bar_stats);
        progressBar.setVisibility(View.VISIBLE);

        rv = v.findViewById(R.id.rv_stats);

        LinearLayoutManager ly = new LinearLayoutManager(getContext());
        rv.setLayoutManager(ly);

        statAdapter = new StatAdapter(statList, getContext());

        rv.setAdapter(statAdapter);
    }

    private void initViewModelObservers(final View v) {

        statViewModel.isLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.VISIBLE);
                }
            }
        });

        statViewModel.loadStats().observe(getViewLifecycleOwner(), new Observer<List<Stat>>() {
            @Override
            public void onChanged(List<Stat> stats) {

                if (stats != null) {
                    statList = stats;
                    statAdapter.updateList(statList);

                    progressBar.setVisibility(View.INVISIBLE);

                    Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ESTADISTICAS), getString(R.string.TAG_VIEW_MODEL_LISTA_ESTADISTICAS_MSG));
                }

            }
        });

        statViewModel.showMsgErrorList().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.INVISIBLE);

                Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ACCIONES), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));

                if (!isSnackBarShow) {
                    rv.setVisibility(View.INVISIBLE);
                    isSnackBarShow = true;
                    showSnackbar(v.findViewById(R.id.stats_list), Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                    statAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void showSnackbar(View v, int duration, String title, String action) {

        snackbar = Snackbar.make(v, title, duration);
        if (action != null) {
            snackbar.setAction(action, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progressBar.setVisibility(View.VISIBLE);

                    isSnackBarShow = false;
                    if (snackbar != null) {
                        snackbar.dismiss();
                    }

                    statViewModel.refreshStats();
                }
            });
        }
        snackbar.show();
        isSnackBarShow = false;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_update, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menu_refresh) {

            progressBar.setVisibility(View.VISIBLE);
            isSnackBarShow = false;
            if (snackbar != null) {
                snackbar.dismiss();
            }
            statViewModel.refreshStats();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
