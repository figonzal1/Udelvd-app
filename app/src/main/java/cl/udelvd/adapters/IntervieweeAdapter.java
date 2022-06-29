package cl.udelvd.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.models.Interviewee;
import cl.udelvd.models.Researcher;
import cl.udelvd.utils.Utils;
import cl.udelvd.viewmodels.IntervieweeListViewModel;
import cl.udelvd.views.activities.EditIntervieweeActivity;
import cl.udelvd.views.activities.InterviewsListActivity;
import cl.udelvd.views.fragments.IntervieweeListFragment;
import cl.udelvd.views.fragments.dialog.DeleteIntervieweeDialogFragment;

public class IntervieweeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG_DELETE_DIALOG_INTERVIEWEE = "EliminarEntrevistado";
    private static final int REQUEST_CODE_EDIT_INTERVIEWEE = 300;

    private static final int INTERVIEWEE = 0;
    private static final int PROGRESS_PAGINATION = 1;
    private final Context context;
    private final IntervieweeListFragment intervieweeListFragment;
    private final FragmentManager fragmentManager;
    private final IntervieweeListViewModel intervieweeListViewModel;
    private final Researcher researcher;
    private List<Interviewee> intervieweeList;
    private int page = 1;
    private Button btnLoadMore;
    private ProgressBar progressBar;
    private int totalInterviewees;
    private boolean totalList;

    public IntervieweeAdapter(List<Interviewee> intervieweeList, Context context, IntervieweeListFragment intervieweeListFragment, FragmentManager fragmentManager, IntervieweeListViewModel intervieweeListViewModel, Researcher researcher, boolean totalList) {

        this.intervieweeList = intervieweeList;
        this.context = context;
        this.intervieweeListFragment = intervieweeListFragment;
        this.fragmentManager = fragmentManager;
        this.intervieweeListViewModel = intervieweeListViewModel;
        this.researcher = researcher;
        this.totalList = totalList;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;

        View v;
        if (viewType == INTERVIEWEE) {

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_interviewee, parent, false);
            viewHolder = new IntervieweeViewHolder(v);

        } else if (viewType == PROGRESS_PAGINATION) {

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pagination_item, parent, false);
            viewHolder = new LoadingViewHolder(v);
        }

        return Objects.requireNonNull(viewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {

        if (getItemViewType(position) == INTERVIEWEE) {

            final IntervieweeViewHolder holder = (IntervieweeViewHolder) viewHolder;

            final Interviewee interviewee = intervieweeList.get(position);

            holder.tvNameLastName.setText(String.format("%s %s", interviewee.getName(), interviewee.getLastName()));

            final int annos = Utils.calculateYearsOld(interviewee.getBirthDate());
            holder.tvBirthDate.setText(context.getResources().getQuantityString(R.plurals.FORMATO_FECHA_NAC, annos, Utils.dateToString(context.getApplicationContext(), false, interviewee.getBirthDate()), annos));

            holder.tvNInterviewees.setText(context.getResources().getQuantityString(R.plurals.FORMATO_N_ENTREVISTA, interviewee.getNInterviews(), interviewee.getNInterviews()));

            if (totalList) {
                holder.tvResearcherCharge.setVisibility(View.VISIBLE);
                holder.tvResearcherCharge.setText(String.format(context.getString(R.string.FORMAT_REGISTRADO_POR), interviewee.getResearcherName(), interviewee.getLastNameResearcher()));
            }

            Utils.configIconInterviewee(interviewee, annos, holder.ivIconPerson, context);

            holder.cardViewInterviewee.setOnClickListener(v -> {

                Intent intent = new Intent(context, InterviewsListActivity.class);

                Bundle bundle = new Bundle();
                bundle.putInt(context.getString(R.string.KEY_ENTREVISTADO_ID_LARGO), interviewee.getId());
                bundle.putString(context.getString(R.string.KEY_ENTREVISTADO_NOMBRE_LARGO), interviewee.getName());
                bundle.putString(context.getString(R.string.KEY_ENTREVISTADO_APELLIDO_LARGO), interviewee.getLastName());
                bundle.putString(context.getString(R.string.KEY_ENTREVISTADO_FECHA_NAC), Utils.dateToString(context, false, interviewee.getBirthDate()));
                bundle.putString(context.getString(R.string.KEY_ENTREVISTADO_SEXO_LARGO), interviewee.getGender());
                bundle.putInt(context.getString(R.string.KEY_ENTREVISTADO_ANNOS), annos);
                intent.putExtras(bundle);

                context.startActivity(intent, bundle);
            });

            holder.ivMenuInterviewee.setOnClickListener(v -> {

                PopupMenu popupMenu = new PopupMenu(context, holder.ivMenuInterviewee);
                popupMenu.inflate(R.menu.menu_holder_interviewee);

                popupMenu.setOnMenuItemClickListener(item -> {

                    //EDIT INTERVIEWEE
                    if (item.getItemId() == R.id.menu_edit_interviewee) {

                        Intent intent = new Intent(context, EditIntervieweeActivity.class);
                        intent.putExtra(context.getString(R.string.KEY_ENTREVISTADO_ID_LARGO), interviewee.getId());
                        intervieweeListFragment.startActivityForResult(intent, REQUEST_CODE_EDIT_INTERVIEWEE);

                        return true;

                    }

                    //SEE INTERVIEWS
                    else if (item.getItemId() == R.id.menu_see_interviews) {

                        Intent intent = new Intent(context, InterviewsListActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putInt(context.getString(R.string.KEY_ENTREVISTADO_ID_LARGO), interviewee.getId());
                        bundle.putString(context.getString(R.string.KEY_ENTREVISTADO_NOMBRE_LARGO), interviewee.getName());
                        bundle.putString(context.getString(R.string.KEY_ENTREVISTADO_APELLIDO_LARGO), interviewee.getLastName());
                        bundle.putString(context.getString(R.string.KEY_ENTREVISTADO_FECHA_NAC), Utils.dateToString(context, false, interviewee.getBirthDate()));
                        bundle.putString(context.getString(R.string.KEY_ENTREVISTADO_SEXO_LARGO), interviewee.getGender());
                        bundle.putInt(context.getString(R.string.KEY_ENTREVISTADO_ANNOS), annos);
                        intent.putExtras(bundle);

                        context.startActivity(intent);

                        return true;
                    }

                    //DELETE INTERVIEWEE
                    else if (item.getItemId() == R.id.menu_delete_interviewee) {

                        DeleteIntervieweeDialogFragment dialog = new DeleteIntervieweeDialogFragment(interviewee);
                        dialog.show(fragmentManager, TAG_DELETE_DIALOG_INTERVIEWEE);

                        return true;
                    }
                    return false;
                });

                popupMenu.show();
            });
        } else if (getItemViewType(position) == PROGRESS_PAGINATION) {

            final LoadingViewHolder holder = (LoadingViewHolder) viewHolder;

            btnLoadMore = holder.btnLoadMore;
            progressBar = holder.progressBar;

            if (intervieweeList.size() == 0) {

                btnLoadMore.setVisibility(View.GONE);
                page = 1;
            }

            if (totalInterviewees == intervieweeList.size()) {
                btnLoadMore.setVisibility(View.GONE);
            }

            btnLoadMore.setOnClickListener(v -> {
                page += 1;

                if (intervieweeListViewModel != null) {
                    intervieweeListViewModel.loadNextPage(page, researcher, totalList);
                }

                progressBar.setVisibility(View.VISIBLE);

                btnLoadMore.setVisibility(View.GONE);
            });
        }

    }


    @Override
    public int getItemCount() {
        return intervieweeList == null ? 0 : intervieweeList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        return (position == intervieweeList.size()) ? PROGRESS_PAGINATION : INTERVIEWEE;
    }

    public void addInterviewees(List<Interviewee> entrevSgt) {

        if (btnLoadMore != null) {

            if (entrevSgt.size() == 0) {

                btnLoadMore.setVisibility(View.GONE);
            } else {
                btnLoadMore.setVisibility(View.VISIBLE);
            }
        }
        intervieweeList.addAll(entrevSgt);

        notifyDataSetChanged();
    }

    public List<Interviewee> getIntervieweeList() {
        return intervieweeList;
    }

    public void hideProgress() {

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void updateList(List<Interviewee> intervieweeList) {

        this.intervieweeList = intervieweeList;
        notifyDataSetChanged();
    }

    public void resetPages() {
        page = 1;
    }

    public void setTotalInterviewee(int entrevistados_totales) {
        this.totalInterviewees = entrevistados_totales;
    }

    public void setTotalList(boolean totalList) {

        this.totalList = totalList;
        notifyDataSetChanged();
    }

    public void filterList(List<Interviewee> intervieweeList) {

        this.intervieweeList.clear();
        this.intervieweeList = intervieweeList;

        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class IntervieweeViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvNameLastName;
        private final TextView tvBirthDate;
        private final TextView tvNInterviewees;
        private final ImageView ivMenuInterviewee;
        private final ImageView ivIconPerson;
        private final TextView tvResearcherCharge;

        private final View cardViewInterviewee;


        IntervieweeViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNameLastName = itemView.findViewById(R.id.cv_tv_interviewee_complete_name);
            tvBirthDate = itemView.findViewById(R.id.cv_tv_interviewee_birth_date);
            tvNInterviewees = itemView.findViewById(R.id.tv_n_interviews);
            ivMenuInterviewee = itemView.findViewById(R.id.iv_menu_interviewee);
            ivIconPerson = itemView.findViewById(R.id.cv_iv_interviewee_person);

            tvResearcherCharge = itemView.findViewById(R.id.tv_reasearcher_in_charge);

            cardViewInterviewee = itemView.findViewById(R.id.card_view_interviewee);
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
