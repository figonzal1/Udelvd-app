package cl.udelvd.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.models.Interview;
import cl.udelvd.models.Interviewee;
import cl.udelvd.utils.Utils;
import cl.udelvd.views.activities.EditInterviewActivity;
import cl.udelvd.views.activities.EventsActivity;
import cl.udelvd.views.fragments.dialog.DeleteInterviewDialogFragment;

public class InterviewAdapter extends RecyclerView.Adapter<InterviewAdapter.InterviewViewHolder> {

    private static final String TAG_DELETE_DIALOG_NAME = "DeleteEntrevistaDialogFragment";
    private final int REQUEST_CODE_EDIT_INTERVIEW;
    private final Interviewee interviewee;
    private final FragmentManager fragmentManager;
    private List<Interview> interviewList;
    private Map<String, Integer> params;

    private final Activity activity;

    public InterviewAdapter(List<Interview> interviewList, Activity activity, FragmentManager fragmentManager, Interviewee interviewee, Map<String, Integer> params, int requestCodeEditarEntrevista) {
        this.interviewList = interviewList;
        this.fragmentManager = fragmentManager;
        this.interviewee = interviewee;
        this.params = params;
        this.activity = activity;
        this.REQUEST_CODE_EDIT_INTERVIEW = requestCodeEditarEntrevista;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public InterviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_interview, parent, false);
        return new InterviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final InterviewViewHolder holder, int position) {

        final Interview interview = interviewList.get(position);

        holder.tvInterviewName.setText(String.format(Locale.US, activity.getString(R.string.FORMATO_ENTREVISTA_N), position + 1));

        holder.tvInterviewType.setText(interview.getInterviewType().getName());

        final String fechaEntrevista = Utils.dateToString(activity, false, interview.getInterviewDate());

        holder.tvInterviewRegistryDate.setText(fechaEntrevista);

        final int annos = Utils.calculateYearsOld(interviewee.getBirthDate());

        holder.cardViewInterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, EventsActivity.class);

                Bundle bundle = new Bundle();
                bundle.putInt(activity.getString(R.string.KEY_ENTREVISTA_ID_LARGO), interview.getId());
                bundle.putInt(activity.getString(R.string.KEY_ENTREVISTA_ID_ENTREVISTADO), interview.getIdInterviewee());

                bundle.putString(activity.getString(R.string.KEY_ENTREVISTA_FECHA_ENTREVISTA), fechaEntrevista);

                bundle.putString(activity.getString(R.string.KEY_ENTREVISTADO_NOMBRE_LARGO), interviewee.getName());
                bundle.putString(activity.getString(R.string.KEY_ENTREVISTADO_APELLIDO_LARGO), interviewee.getLastName());
                bundle.putString(activity.getString(R.string.KEY_ENTREVISTADO_SEXO_LARGO), interviewee.getGenre());
                bundle.putInt(activity.getString(R.string.KEY_ENTREVISTADO_ANNOS), annos);

                bundle.putInt(activity.getString(R.string.KEY_ENTREVISTADO_N_ENTREVISTAS), Objects.requireNonNull(params.get(activity.getString(R.string.KEY_ENTREVISTADO_N_ENTREVISTAS))));
                bundle.putString(activity.getString(R.string.KEY_ENTREVISTA_N_NORMALES), String.valueOf(params.get(activity.getString(R.string.KEY_ENTREVISTA_N_NORMALES))));
                bundle.putString(activity.getString(R.string.KEY_ENTREVISTA_N_EXTRAORDINARIAS), String.valueOf(params.get(activity.getString(R.string.KEY_ENTREVISTA_N_EXTRAORDINARIAS))));

                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
        });

        holder.ivInterviewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(activity, holder.ivInterviewMenu);
                popupMenu.inflate(R.menu.menu_holder_interview);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        //SEE EVENTS FROM INTERVIEW
                        if (item.getItemId() == R.id.menu_see_events) {
                            Intent intent = new Intent(activity, EventsActivity.class);

                            Bundle bundle = new Bundle();
                            bundle.putInt(activity.getString(R.string.KEY_ENTREVISTA_ID_LARGO), interview.getId());
                            bundle.putInt(activity.getString(R.string.KEY_ENTREVISTA_ID_ENTREVISTADO), interview.getIdInterviewee());

                            bundle.putString(activity.getString(R.string.KEY_ENTREVISTA_FECHA_ENTREVISTA), fechaEntrevista);

                            bundle.putString(activity.getString(R.string.KEY_ENTREVISTADO_NOMBRE_LARGO), interviewee.getName());
                            bundle.putString(activity.getString(R.string.KEY_ENTREVISTADO_APELLIDO_LARGO), interviewee.getLastName());
                            bundle.putString(activity.getString(R.string.KEY_ENTREVISTADO_SEXO_LARGO), interviewee.getGenre());
                            bundle.putInt(activity.getString(R.string.KEY_ENTREVISTADO_ANNOS), annos);

                            bundle.putInt(activity.getString(R.string.KEY_ENTREVISTADO_N_ENTREVISTAS), Objects.requireNonNull(params.get(activity.getString(R.string.KEY_ENTREVISTADO_N_ENTREVISTAS))));
                            bundle.putString(activity.getString(R.string.KEY_ENTREVISTA_N_NORMALES), String.valueOf(params.get(activity.getString(R.string.KEY_ENTREVISTA_N_NORMALES))));
                            bundle.putString(activity.getString(R.string.KEY_ENTREVISTA_N_EXTRAORDINARIAS), String.valueOf(params.get(activity.getString(R.string.KEY_ENTREVISTA_N_EXTRAORDINARIAS))));

                            intent.putExtras(bundle);
                            activity.startActivity(intent);
                            return true;
                        }

                        //EDIT INTERVIEW
                        else if (item.getItemId() == R.id.menu_edit_interview) {

                            Intent intent = new Intent(activity, EditInterviewActivity.class);

                            Bundle bundle = new Bundle();
                            bundle.putInt(activity.getString(R.string.KEY_ENTREVISTA_ID_LARGO), interview.getId());
                            bundle.putInt(activity.getString(R.string.KEY_ENTREVISTA_ID_ENTREVISTADO), interview.getIdInterviewee());
                            intent.putExtras(bundle);
                            activity.startActivityForResult(intent, REQUEST_CODE_EDIT_INTERVIEW);

                            return true;
                        }

                        //DELETE INTERVIEW
                        else if (item.getItemId() == R.id.menu_delete_interview) {
                            DeleteInterviewDialogFragment dialog = new DeleteInterviewDialogFragment(interview);
                            dialog.show(fragmentManager, TAG_DELETE_DIALOG_NAME);

                            return true;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return interviewList.size();
    }

    public void updateList(List<Interview> interviewList) {
        this.interviewList = interviewList;
        notifyDataSetChanged();
    }

    public void setParams(Map<String, Integer> params) {
        this.params = params;
        notifyDataSetChanged();
    }

    static class InterviewViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvInterviewName;
        private final TextView tvInterviewRegistryDate;
        private final TextView tvInterviewType;
        private final View cardViewInterView;
        private final ImageView ivInterviewMenu;

        InterviewViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewInterView = itemView.findViewById(R.id.card_view_interview);
            tvInterviewName = itemView.findViewById(R.id.tv_interview_name);
            tvInterviewRegistryDate = itemView.findViewById(R.id.tv_interview_registry_date);
            tvInterviewType = itemView.findViewById(R.id.tv_interview_type);
            ivInterviewMenu = itemView.findViewById(R.id.iv_interview_menu);
        }
    }
}
