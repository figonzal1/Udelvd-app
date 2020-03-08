package cl.udelvd.adaptadores;

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

import cl.udelvd.EditarAccionActivity;
import cl.udelvd.R;
import cl.udelvd.modelo.Accion;
import cl.udelvd.vistas.fragments.DeleteAccionDialogFragment;

/**
 * Adaptador para listado de acciones para ADMIN
 */
public class AccionAdapter extends RecyclerView.Adapter<AccionAdapter.AccionViewHolder> {

    private List<Accion> accionList;
    private Context context;
    private Activity activity;
    private FragmentManager fragmentManager;
    private String TAG_DELETE_DIALOG_ACCION = "DeleteAccion";
    private int REQUEST_CODE_EDITAR_ACCION;

    public AccionAdapter(List<Accion> accionList, Context context, Activity activity, FragmentManager fragmentManager, int requestCode) {
        this.accionList = accionList;
        this.context = context;
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.REQUEST_CODE_EDITAR_ACCION = requestCode;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public AccionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_accion, parent, false);
        return new AccionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AccionViewHolder holder, int position) {
        final Accion accion = accionList.get(position);

        holder.tv_accion.setText(String.format(Locale.getDefault(), context.getString(R.string.FORMATO_ACCION), (position + 1)));

        holder.tv_espanol.setText(accion.getNombreEs());
        holder.tv_ingles.setText(accion.getNombreEn());

        holder.iv_menu_accion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.iv_menu_accion);
                popupMenu.inflate(R.menu.menu_holder_accion);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == R.id.menu_editar_accion) {
                            Intent intent = new Intent(context, EditarAccionActivity.class);

                            Bundle bundle = new Bundle();
                            bundle.putInt(context.getString(R.string.KEY_ACCION_ID_LARGO), accion.getId());
                            bundle.putString(context.getString(R.string.KEY_ACCION_NOMBRE_EN), accion.getNombreEn());
                            bundle.putString(context.getString(R.string.KEY_ACCION_NOMBRE_ES), accion.getNombreEs());

                            intent.putExtras(bundle);
                            activity.startActivityForResult(intent, REQUEST_CODE_EDITAR_ACCION);
                            return true;
                        } else if (item.getItemId() == R.id.menu_eliminar_accion) {
                            DeleteAccionDialogFragment dialog = new DeleteAccionDialogFragment(accion);
                            dialog.show(fragmentManager, TAG_DELETE_DIALOG_ACCION);
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
        return accionList.size();
    }

    public void actualizarLista(List<Accion> accionesList) {
        this.accionList = accionesList;
        notifyDataSetChanged();
    }

    static class AccionViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iv_menu_accion;
        private TextView tv_accion;
        private TextView tv_ingles;
        private TextView tv_espanol;

        AccionViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_accion = itemView.findViewById(R.id.cv_tv_accion);
            tv_ingles = itemView.findViewById(R.id.tv_ingles);
            tv_espanol = itemView.findViewById(R.id.cv_tv_espanol);
            iv_menu_accion = itemView.findViewById(R.id.iv_menu_accion);
        }
    }
}
