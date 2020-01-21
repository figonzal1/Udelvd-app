package cl.udelvd.adaptadores;

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
import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.vistas.activities.EditarEntrevistaActivity;
import cl.udelvd.vistas.activities.EventosActivity;
import cl.udelvd.vistas.fragments.DeleteDialogFragment;

public class EntrevistaAdapter extends RecyclerView.Adapter<EntrevistaAdapter.EntrevistaViewHolder> {

    private static final String TAG_DELETE_DIALOG_NAME = "DeleteDialogFragment";
    private final List<Entrevista> entrevistaList;
    private FragmentManager fragmentManager;
    private Entrevistado entrevistado;
    private Map<String, Integer> params;
    private final int REQUEST_CODE_EDITAR_ENTREVISTA;
    private Activity activity;

    public EntrevistaAdapter(List<Entrevista> entrevistaList, Activity activity, FragmentManager fragmentManager, Entrevistado entrevistado, Map<String, Integer> params, int requestCodeEditarEntrevista) {
        this.entrevistaList = entrevistaList;
        this.fragmentManager = fragmentManager;
        this.entrevistado = entrevistado;
        this.params = params;
        this.activity = activity;
        this.REQUEST_CODE_EDITAR_ENTREVISTA = requestCodeEditarEntrevista;
    }

    @NonNull
    @Override
    public EntrevistaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_entrevista, parent, false);
        return new EntrevistaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final EntrevistaViewHolder holder, int position) {

        final Entrevista entrevista = entrevistaList.get(position);

        holder.tv_entrevista_nombre.setText(String.format(Locale.US, activity.getString(R.string.FORMATO_ENTREVISTA_N), position + 1));

        holder.tv_tipo_entrevista.setText(entrevista.getTipoEntrevista().getNombre());

        final String fechaEntrevista = Utils.dateToString(activity, false, entrevista.getFecha_entrevista());

        holder.tv_fecha_registro.setText(fechaEntrevista);

        //Click de cardview
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, EventosActivity.class);

                Bundle bundle = new Bundle();
                bundle.putInt(activity.getString(R.string.KEY_ENTREVISTA_ID_LARGO), entrevista.getId());
                bundle.putInt(activity.getString(R.string.KEY_ENTREVISTA_ID_ENTREVISTADO), entrevista.getId_entrevistado());

                bundle.putString(activity.getString(R.string.KEY_ENTREVISTA_FECHA_ENTREVISTA), fechaEntrevista);

                bundle.putString(activity.getString(R.string.KEY_ENTREVISTADO_NOMBRE_LARGO), entrevistado.getNombre());
                bundle.putString(activity.getString(R.string.KEY_ENTREVISTADO_APELLIDO_LARGO), entrevistado.getApellido());

                bundle.putInt(activity.getString(R.string.KEY_ENTREVISTADO_N_ENTREVISTAS), Objects.requireNonNull(params.get(activity.getString(R.string.KEY_ENTREVISTADO_N_ENTREVISTAS))));
                bundle.putString(activity.getString(R.string.KEY_ENTREVISTA_N_NORMALES), String.valueOf(params.get(activity.getString(R.string.KEY_ENTREVISTA_N_NORMALES))));
                bundle.putString(activity.getString(R.string.KEY_ENTREVISTA_N_EXTRAORDINARIAS), String.valueOf(params.get(activity.getString(R.string.KEY_ENTREVISTA_N_EXTRAORDINARIAS))));

                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
        });

        //Click en el submenu
        holder.iv_menu_entrevista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(activity, holder.iv_menu_entrevista);
                popupMenu.inflate(R.menu.menu_holder_entrevista);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        //VER EVENTOS DE ENTREVISTA
                        if (item.getItemId() == R.id.menu_ver_eventos) {
                            Intent intent = new Intent(activity, EventosActivity.class);

                            Bundle bundle = new Bundle();
                            bundle.putInt(activity.getString(R.string.KEY_ENTREVISTA_ID_LARGO), entrevista.getId());
                            bundle.putInt(activity.getString(R.string.KEY_ENTREVISTA_ID_ENTREVISTADO), entrevista.getId_entrevistado());

                            bundle.putString(activity.getString(R.string.KEY_ENTREVISTA_FECHA_ENTREVISTA), fechaEntrevista);

                            bundle.putString(activity.getString(R.string.KEY_ENTREVISTADO_NOMBRE_LARGO), entrevistado.getNombre());
                            bundle.putString(activity.getString(R.string.KEY_ENTREVISTADO_APELLIDO_LARGO), entrevistado.getApellido());

                            bundle.putInt(activity.getString(R.string.KEY_ENTREVISTADO_N_ENTREVISTAS), Objects.requireNonNull(params.get(activity.getString(R.string.KEY_ENTREVISTADO_N_ENTREVISTAS))));
                            bundle.putString(activity.getString(R.string.KEY_ENTREVISTA_N_NORMALES), String.valueOf(params.get(activity.getString(R.string.KEY_ENTREVISTA_N_NORMALES))));
                            bundle.putString(activity.getString(R.string.KEY_ENTREVISTA_N_EXTRAORDINARIAS), String.valueOf(params.get(activity.getString(R.string.KEY_ENTREVISTA_N_EXTRAORDINARIAS))));

                            intent.putExtras(bundle);
                            activity.startActivity(intent);
                            return true;
                        }

                        //EDITAR ENTREVISTA
                        else if (item.getItemId() == R.id.menu_editar_entrevista) {

                            Intent intent = new Intent(activity.getApplicationContext(), EditarEntrevistaActivity.class);

                            Bundle bundle = new Bundle();
                            bundle.putInt(activity.getString(R.string.KEY_ENTREVISTA_ID_LARGO), entrevista.getId());
                            bundle.putInt(activity.getString(R.string.KEY_ENTREVISTA_ID_ENTREVISTADO), entrevista.getId_entrevistado());
                            intent.putExtras(bundle);
                            activity.startActivityForResult(intent, REQUEST_CODE_EDITAR_ENTREVISTA);

                            return true;
                        }

                        //ELIMINAR ENTREVISTA
                        else if (item.getItemId() == R.id.menu_eliminar_entrevista) {
                            DeleteDialogFragment dialog = new DeleteDialogFragment(entrevista);
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
        return entrevistaList.size();
    }

    static class EntrevistaViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_entrevista_nombre;
        private final TextView tv_fecha_registro;
        private final TextView tv_tipo_entrevista;
        private final View card_view;
        private final ImageView iv_menu_entrevista;

        EntrevistaViewHolder(@NonNull View itemView) {
            super(itemView);

            card_view = itemView.findViewById(R.id.card_view);
            tv_entrevista_nombre = itemView.findViewById(R.id.tv_entrevista_nombre);
            tv_fecha_registro = itemView.findViewById(R.id.tv_entrevista_fecha_registro_value);
            tv_tipo_entrevista = itemView.findViewById(R.id.tv_entrevista_tipo);
            iv_menu_entrevista = itemView.findViewById(R.id.iv_menu_entrevista);
        }
    }
}
