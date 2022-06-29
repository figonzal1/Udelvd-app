package cl.udelvd.views.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.adapters.IntervieweeAdapter;
import cl.udelvd.models.Interviewee;
import cl.udelvd.models.Researcher;
import cl.udelvd.utils.SnackbarInterface;
import cl.udelvd.viewmodels.IntervieweeListViewModel;
import cl.udelvd.views.activities.NewIntervieweeActivity;


public class IntervieweeListFragment extends Fragment implements SnackbarInterface, SearchView.OnQueryTextListener {

    private static final int REQUEST_CODE_NEW_INTERVIEWEE = 200;
    private static final int REQUEST_CODE_EDIT_INTERVIEWEE = 300;

    private RecyclerView rv;
    private IntervieweeListViewModel intervieweeListViewModel;
    private IntervieweeAdapter intervieweeAdapter;
    private ProgressBar progressBar;
    private TextView tvEmptyInterviewee;
    private TextView tvNInterviewees;
    private SwitchMaterial switchMaterial;
    private List<Interviewee> intervieweeList;
    private View v;
    private int totalInterviewees;
    private Researcher researcher;

    private boolean isSnackBarShow = false;
    private Snackbar snackbar;
    private boolean listadoTotal = false;

    private FirebaseCrashlytics crashlytics;

    public IntervieweeListFragment() {
    }

    public static IntervieweeListFragment newInstance() {
        return new IntervieweeListFragment();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        crashlytics = FirebaseCrashlytics.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_interviewee_list, container, false);

        getResearcherLoginData();

        instantiateInterfaceResources(v);

        getBundleData(v);

        initViewModelObservers(v);

        floatingButtonNewInterviewee(v);

        return v;
    }

    private void getResearcherLoginData() {

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);
        int idResearcher = sharedPreferences.getInt(getString(R.string.SHARED_PREF_INVES_ID), 0);
        researcher = new Researcher();
        researcher.setId(idResearcher);
    }

    private void getBundleData(View v) {

        if (getArguments() != null) {

            String msgLogin = getArguments().getString(getString(R.string.INTENT_KEY_MSG_LOGIN));

            if (msgLogin != null) {
                Snackbar.make(v.findViewById(R.id.interviewees_list), msgLogin, Snackbar.LENGTH_LONG).show();
            }

        }
    }

    private void instantiateInterfaceResources(View v) {

        intervieweeList = new ArrayList<>();

        intervieweeListViewModel = new ViewModelProvider(this).get(IntervieweeListViewModel.class);

        progressBar = v.findViewById(R.id.progress_bar_interviewees);
        progressBar.setVisibility(View.VISIBLE);

        rv = v.findViewById(R.id.rv_interviewee_list);

        LinearLayoutManager ly = new LinearLayoutManager(getContext());
        rv.setLayoutManager(ly);

        tvEmptyInterviewee = v.findViewById(R.id.tv_empty_interviewees);
        tvEmptyInterviewee.setVisibility(View.INVISIBLE);

        tvNInterviewees = v.findViewById(R.id.tv_n_interviewee);
        tvNInterviewees.setVisibility(View.INVISIBLE);

        switchMaterial = v.findViewById(R.id.switch_list);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(requireContext().getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);
        String rolAdmin = sharedPreferences.getString(requireContext().getString(R.string.SHARED_PREF_INVES_NOMBRE_ROL), "");

        if (rolAdmin != null) {

            if (rolAdmin.equals(requireContext().getString(R.string.ROL_ADMIN_KEY_MASTER))) {

                switchMaterial.setVisibility(View.VISIBLE);
                listadoTotal = switchMaterial.isChecked();

                if (listadoTotal) {

                    switchMaterial.setText(R.string.TODOS);
                    crashlytics.setCustomKey("interviewee_list_switch", getString(R.string.TODOS));

                } else {
                    switchMaterial.setText(R.string.MI_CUENTA);
                    crashlytics.setCustomKey("interviewee_list_switch", getString(R.string.MI_CUENTA));
                }

                switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {

                    if (isChecked) {

                        switchMaterial.setText(getString(R.string.TODOS));
                        crashlytics.setCustomKey("interviewee_list_switch", getString(R.string.TODOS));

                    } else {
                        switchMaterial.setText(getString(R.string.MI_CUENTA));
                        crashlytics.setCustomKey("interviewee_list_switch", getString(R.string.MI_CUENTA));
                    }

                    listadoTotal = isChecked;
                    intervieweeAdapter.setTotalList(listadoTotal);
                    intervieweeListViewModel.refreshIntervieweeList(researcher, listadoTotal);

                });
            } else {
                switchMaterial.setVisibility(View.GONE);
            }
        }

        intervieweeAdapter = new IntervieweeAdapter(
                intervieweeList,
                getContext(),
                IntervieweeListFragment.this,
                requireActivity().getSupportFragmentManager(),
                intervieweeListViewModel,
                researcher,
                listadoTotal)
        ;
        rv.setAdapter(intervieweeAdapter);
    }

    private void floatingButtonNewInterviewee(View v) {

        FloatingActionButton fbCrearUsuario = v.findViewById(R.id.fb_new_interviewee);

        fbCrearUsuario.setOnClickListener(view -> {

            Intent intent = new Intent(getActivity(), NewIntervieweeActivity.class);
            startActivityForResult(intent, REQUEST_CODE_NEW_INTERVIEWEE);
        });
    }

    private void initViewModelObservers(final View v) {

        intervieweeListViewModel.isLoading().observe(getViewLifecycleOwner(), aBoolean -> {

            if (aBoolean) {

                progressBar.setVisibility(View.VISIBLE);
                tvEmptyInterviewee.setVisibility(View.INVISIBLE);
                tvNInterviewees.setVisibility(View.INVISIBLE);
                rv.setVisibility(View.INVISIBLE);

            } else {
                progressBar.setVisibility(View.INVISIBLE);
                rv.setVisibility(View.VISIBLE);
            }
        });

        intervieweeListViewModel.showNInterviewees().observe(getViewLifecycleOwner(), integer -> {

            totalInterviewees = integer;
            intervieweeAdapter.setTotalInterviewee(totalInterviewees);
        });


        intervieweeListViewModel.loadFirstPage(1, researcher, listadoTotal).observe(getViewLifecycleOwner(), listado -> {

            intervieweeList = listado;
            intervieweeAdapter.updateList(intervieweeList);
            rv.setAdapter(intervieweeAdapter);

            progressBar.setVisibility(View.INVISIBLE);

            if (intervieweeList.size() == 0) {
                tvEmptyInterviewee.setVisibility(View.VISIBLE);

            } else {
                tvEmptyInterviewee.setVisibility(View.INVISIBLE);
            }

            tvNInterviewees.setVisibility(View.VISIBLE);
            tvNInterviewees.setText(getResources().getQuantityString(R.plurals.MOSTRAR_ENTREVISTADOS, totalInterviewees, intervieweeAdapter.getIntervieweeList().size(), totalInterviewees));

            Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ENTREVISTADO), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_LISTA_ENTREVISTADO) + getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG));
        });

        intervieweeListViewModel.showNextPage().observe(getViewLifecycleOwner(), interviewees -> {

            intervieweeAdapter.addInterviewees(interviewees);
            intervieweeAdapter.hideProgress();

            intervieweeList = intervieweeAdapter.getIntervieweeList();

            if (intervieweeList.size() == 0) {

                tvEmptyInterviewee.setVisibility(View.VISIBLE);
            } else {
                tvEmptyInterviewee.setVisibility(View.INVISIBLE);
            }

            tvNInterviewees.setVisibility(View.VISIBLE);
            tvNInterviewees.setText(getResources().getQuantityString(R.plurals.MOSTRAR_ENTREVISTADOS, totalInterviewees, intervieweeAdapter.getIntervieweeList().size(), totalInterviewees));

            Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ENTREVISTADO), getString(R.string.VIEW_MODEL_LISTA_ENTREVISTADO_MSG) + "PAGINA");
        });

        intervieweeListViewModel.showFilteredIntervieweeList().observe(requireActivity(), list -> {

            intervieweeList = list;
            intervieweeAdapter.filterList(intervieweeList);
        });

        intervieweeListViewModel.showMsgErrorList().observe(getViewLifecycleOwner(), s -> {

            progressBar.setVisibility(View.INVISIBLE);

            if (intervieweeAdapter != null) {

                intervieweeAdapter.hideProgress();
                intervieweeAdapter.resetPages();
            }

            if (!isSnackBarShow) {

                rv.setVisibility(View.INVISIBLE);
                isSnackBarShow = true;
                showSnackbar(v, Snackbar.LENGTH_INDEFINITE, s, getString(R.string.SNACKBAR_REINTENTAR));
                intervieweeAdapter.notifyDataSetChanged();

            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_LISTA_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_LISTA_ENTREVISTADO) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE_ERROR), s));
        });

        intervieweeListViewModel.showMsgDelete().observe(getViewLifecycleOwner(), s -> {

            progressBar.setVisibility(View.INVISIBLE);

            if (s.equals(getString(R.string.MSG_DELETE_ENTREVISTADO))) {

                isSnackBarShow = true;
                showSnackbar(v.findViewById(R.id.interviewees_list), Snackbar.LENGTH_LONG, s, null);
                intervieweeAdapter.resetPages();
                intervieweeListViewModel.refreshIntervieweeList(researcher, listadoTotal);
            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_ENTREVISTADO) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
        });

        intervieweeListViewModel.showMsgErrorDelete().observe(getViewLifecycleOwner(), s -> {

            progressBar.setVisibility(View.INVISIBLE);

            if (!isSnackBarShow) {
                isSnackBarShow = true;

                if (!s.equals(getString(R.string.SERVER_ERROR_MSG_VM))) {

                    showSnackbar(v, Snackbar.LENGTH_INDEFINITE, s, null);
                } else {

                    showSnackbar(v, Snackbar.LENGTH_LONG, s, null);
                }

                intervieweeAdapter.notifyDataSetChanged();
            }

            Log.d(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_ENTREVISTADO), String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
            crashlytics.log(getString(R.string.TAG_VIEW_MODEL_ELIMINAR_ENTREVISTADO) + String.format("%s %s", getString(R.string.VIEW_MODEL_MSG_RESPONSE), s));
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        MenuItem.OnActionExpandListener onActionExpandListener =
                new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        menu.findItem(R.id.menu_search).setVisible(false);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {

                        intervieweeListViewModel.refreshIntervieweeList(researcher, listadoTotal);

                        menu.findItem(R.id.menu_search).setVisible(true);
                        requireActivity().invalidateOptionsMenu();
                        return true;
                    }
                };
        menuItem.setOnActionExpandListener(onActionExpandListener);

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
            intervieweeAdapter.resetPages();
            intervieweeListViewModel.refreshIntervieweeList(researcher, listadoTotal);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_NEW_INTERVIEWEE) {

            if (resultCode == RESULT_OK) {

                Bundle bundle = Objects.requireNonNull(data, "Data cannot be null").getExtras();
                String msg_registro = Objects.requireNonNull(bundle, "Msg_registro cannot be null").getString(getString(R.string.INTENT_KEY_MSG_REGISTRO));

                if (msg_registro != null) {

                    isSnackBarShow = true;
                    showSnackbar(v.findViewById(R.id.interviewees_list), Snackbar.LENGTH_LONG, msg_registro, null);
                    intervieweeAdapter.resetPages();
                    intervieweeListViewModel.refreshIntervieweeList(researcher, listadoTotal);

                }
            }

        } else if (requestCode == REQUEST_CODE_EDIT_INTERVIEWEE) {

            if (resultCode == RESULT_OK) {

                Bundle bundle = Objects.requireNonNull(data, "Data cannot be null").getExtras();
                String msgUpdate = Objects.requireNonNull(bundle, "Msg_actualizacion cannot be null").getString(getString(R.string.INTENT_KEY_MSG_ACTUALIZACION));

                if (msgUpdate != null) {

                    isSnackBarShow = true;
                    showSnackbar(v.findViewById(R.id.interviewees_list), Snackbar.LENGTH_LONG, msgUpdate, null);
                    intervieweeAdapter.resetPages();
                    intervieweeListViewModel.refreshIntervieweeList(researcher, listadoTotal);

                }
            }
        }
    }

    @Override
    public void showSnackbar(View v, int duration, String title, String action) {

        snackbar = Snackbar.make(v.findViewById(R.id.interviewees_list), title, duration);

        if (action != null) {

            snackbar.setAction(action, v1 -> {

                progressBar.setVisibility(View.VISIBLE);
                isSnackBarShow = false;

                if (snackbar != null) {
                    snackbar.dismiss();
                }

                intervieweeAdapter.resetPages();
                intervieweeListViewModel.refreshIntervieweeList(researcher, listadoTotal);
            });
        }

        snackbar.show();
        isSnackBarShow = false;
    }

    @Override
    public void onResume() {
        super.onResume();

        intervieweeAdapter.resetPages();
        intervieweeListViewModel.refreshIntervieweeList(researcher, listadoTotal);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        String input = query.toLowerCase();
        intervieweeListViewModel.doSearch(intervieweeList, input);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
