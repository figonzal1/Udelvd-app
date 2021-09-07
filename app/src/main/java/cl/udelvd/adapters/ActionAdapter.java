package cl.udelvd.adapters;

import android.app.Activity;
import android.content.Context;
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

import cl.udelvd.R;
import cl.udelvd.models.Action;
import cl.udelvd.views.activities.EditActionActivity;
import cl.udelvd.views.fragments.dialog.DeleteActionDialogFragment;

/**
 * Action listing adapter for ADMIN
 */
public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ActionViewHolder> {

    private List<Action> actionsList;
    private final Context context;
    private final Activity activity;
    private final FragmentManager fragmentManager;
    private final String TAG_DELETE_DIALOG_ACTION = "DeleteAccion";
    private final int REQUEST_CODE_EDITAR_ACTION;

    public ActionAdapter(List<Action> actionsList, Context context, Activity activity, FragmentManager fragmentManager, int requestCode) {
        this.actionsList = actionsList;
        this.context = context;
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.REQUEST_CODE_EDITAR_ACTION = requestCode;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ActionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_action, parent, false);
        return new ActionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ActionViewHolder holder, int position) {
        final Action action = actionsList.get(position);

        holder.tvAction.setText(String.format(Locale.getDefault(), context.getString(R.string.FORMATO_ACCION), (position + 1)));

        holder.tvSpanish.setText(action.getNameEs());
        holder.tvEnglish.setText(action.getNameEng());

        holder.ivMenuAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context, holder.ivMenuAction);
                popupMenu.inflate(R.menu.menu_holder_action);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == R.id.menu_edit_action) {

                            Intent intent = new Intent(context, EditActionActivity.class);

                            Bundle bundle = new Bundle();
                            bundle.putInt(context.getString(R.string.KEY_ACCION_ID_LARGO), action.getId());
                            bundle.putString(context.getString(R.string.KEY_ACCION_NOMBRE_EN), action.getNameEng());
                            bundle.putString(context.getString(R.string.KEY_ACCION_NOMBRE_ES), action.getNameEs());

                            intent.putExtras(bundle);
                            activity.startActivityForResult(intent, REQUEST_CODE_EDITAR_ACTION);

                            return true;

                        } else if (item.getItemId() == R.id.menu_delete_action) {

                            DeleteActionDialogFragment dialog = new DeleteActionDialogFragment(action);
                            dialog.show(fragmentManager, TAG_DELETE_DIALOG_ACTION);

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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return actionsList.size();
    }

    public void updateList(List<Action> accionesList) {

        this.actionsList = accionesList;

        notifyDataSetChanged();
    }

    static class ActionViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivMenuAction;
        private final TextView tvAction;
        private final TextView tvEnglish;
        private final TextView tvSpanish;

        ActionViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAction = itemView.findViewById(R.id.tv_action);
            tvEnglish = itemView.findViewById(R.id.tv_english);
            tvSpanish = itemView.findViewById(R.id.tv_spanish);
            ivMenuAction = itemView.findViewById(R.id.iv_menu_action);
        }
    }
}
