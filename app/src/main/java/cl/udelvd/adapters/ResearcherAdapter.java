package cl.udelvd.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.models.Researcher;
import cl.udelvd.utils.Utils;
import cl.udelvd.viewmodels.ResearcherListViewModel;
import cl.udelvd.views.fragments.dialog.ActivateResearcherDialogFragment;

public class ResearcherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int RESEARCHER = 0;
    private static final int PROGRESS_PAGINATION = 1;
    private final String TAG_ACTIVATE_RESEARCHER = "ActivarInvestigador";
    private final Context context;
    private final FragmentManager fragmentManager;
    private final ResearcherListViewModel researcherListViewModel;
    private List<Researcher> researcherList;
    private Button btnLoadMore;
    private ProgressBar progressBar;
    private final Researcher researcher;
    private int page = 1;
    private int totalResearchers;

    public ResearcherAdapter(List<Researcher> researcherList, Context context, FragmentManager fragmentManager, ResearcherListViewModel researcherListViewModel, Researcher researcher) {

        this.researcherList = researcherList;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.researcher = researcher;
        this.researcherListViewModel = researcherListViewModel;

        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;

        View v;
        if (viewType == RESEARCHER) {

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_researcher, parent, false);
            viewHolder = new ResearcherViewHolder(v);

        } else if (viewType == PROGRESS_PAGINATION) {

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pagination_item, parent, false);
            viewHolder = new LoadingViewHolder(v);
        }

        return Objects.requireNonNull(viewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {

        if (getItemViewType(position) == RESEARCHER) {

            final ResearcherViewHolder holder = (ResearcherViewHolder) viewHolder;
            final Researcher researcher = researcherList.get(position);

            holder.tvEmail.setText(researcher.getEmail());
            holder.tvName.setText(String.format("%s %s", researcher.getName(), researcher.getLastName()));
            try {
                holder.tvRegistryDate.setText(String.format(context.getString(R.string.FORMATO_FECHA_REGISTRO), Utils.dateToString(context, false, Utils.stringToDate(context, false, researcher.getCreateTime()))));
            } catch (ParseException e) {

                Log.d("STRING_TO_DATE", "Parse exception error");
                e.printStackTrace();
            }

            if (researcher.isActivated()) {

                holder.switchActivate.setChecked(true);
                holder.switchActivate.setText(context.getString(R.string.PERFIL_ACTIVADO));
            } else {
                holder.switchActivate.setChecked(false);
                holder.switchActivate.setText(context.getString(R.string.PERFIL_NO_ACTIVADO));
            }

            holder.switchActivate.setOnClickListener(v -> {

                if (holder.switchActivate.isChecked()) {

                    ActivateResearcherDialogFragment dialogFragment = new ActivateResearcherDialogFragment(researcher, true, holder.switchActivate);
                    dialogFragment.setCancelable(false);
                    dialogFragment.show(fragmentManager, TAG_ACTIVATE_RESEARCHER);

                } else {

                    ActivateResearcherDialogFragment dialogFragment = new ActivateResearcherDialogFragment(researcher, false, holder.switchActivate);
                    dialogFragment.setCancelable(false);
                    dialogFragment.show(fragmentManager, TAG_ACTIVATE_RESEARCHER);
                }
            });

        } else if (getItemViewType(position) == PROGRESS_PAGINATION) {

            final LoadingViewHolder holder = (LoadingViewHolder) viewHolder;

            btnLoadMore = holder.btnLoadMore;
            progressBar = holder.progressBar;

            if (researcherList.size() == 0) {
                btnLoadMore.setVisibility(View.GONE);
                page = 1;
            }

            if (totalResearchers == researcherList.size()) {
                btnLoadMore.setVisibility(View.GONE);
            }

            btnLoadMore.setOnClickListener(v -> {

                page += 1;

                if (researcherListViewModel != null) {
                    researcherListViewModel.loadNextPage(page, researcher);
                }

                progressBar.setVisibility(View.VISIBLE);

                btnLoadMore.setVisibility(View.GONE);
            });
        }

    }

    @Override
    public int getItemCount() {
        return researcherList == null ? 0 : researcherList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == researcherList.size()) ? PROGRESS_PAGINATION : RESEARCHER;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void resetPages() {
        page = 1;
    }

    public void updateList(List<Researcher> researcherList) {

        this.researcherList = researcherList;
        notifyDataSetChanged();
    }

    public void addInterviewee(List<Researcher> invesSgt) {

        if (btnLoadMore != null) {

            if (invesSgt.size() == 0) {
                btnLoadMore.setVisibility(View.GONE);
            } else {
                btnLoadMore.setVisibility(View.VISIBLE);
            }
        }

        researcherList.addAll(invesSgt);
        notifyDataSetChanged();
    }

    public void hideProgress() {

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    public List<Researcher> getResearcherList() {
        return researcherList;
    }

    public void setTotalResearcher(int investigadores_totales) {
        this.totalResearchers = investigadores_totales;
    }

    static class ResearcherViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvEmail;
        private final TextView tvRegistryDate;
        private final SwitchMaterial switchActivate;

        ResearcherViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.cv_tv_researcher_complete_name);
            tvEmail = itemView.findViewById(R.id.cv_tv_researcher_email);
            tvRegistryDate = itemView.findViewById(R.id.cv_tv_researcher_registry_date);
            switchActivate = itemView.findViewById(R.id.cv_switch_activate);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {

        final MaterialButton btnLoadMore;
        final ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            btnLoadMore = itemView.findViewById(R.id.btn_load_more);
            progressBar = itemView.findViewById(R.id.progress_bar_pagination);
        }

    }
}
