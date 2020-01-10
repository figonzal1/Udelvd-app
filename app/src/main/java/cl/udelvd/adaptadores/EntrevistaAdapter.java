package cl.udelvd.adaptadores;

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
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.modelo.Entrevista;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.vistas.activities.EditarEntrevistaActivity;
import cl.udelvd.vistas.activities.EventosActivity;

public class EntrevistaAdapter extends RecyclerView.Adapter<EntrevistaAdapter.QuakeViewHolder> {

    private final List<Entrevista> entrevistaList;
    private Context context;
    private Entrevistado entrevistado;
    private Map<String, Integer> params;

    public EntrevistaAdapter(List<Entrevista> entrevistaList, Context context, Entrevistado entrevistado, Map<String, Integer> params) {
        this.entrevistaList = entrevistaList;
        this.context = context;
        this.entrevistado = entrevistado;
        this.params = params;
    }

    @NonNull
    @Override
    public QuakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_entrevista, parent, false);
        return new QuakeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final QuakeViewHolder holder, int position) {

        final Entrevista entrevista = entrevistaList.get(position);

        holder.tv_entrevista_nombre.setText(String.format(Locale.US, "Entrevista %d", position + 1));

        holder.tv_tipo_entrevista.setText(entrevista.getTipoEntrevista().getNombre());

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        final String fechaEntrevista = simpleDateFormat.format(entrevista.getFecha_entrevista());

        holder.tv_fecha_registro.setText(fechaEntrevista);

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventosActivity.class);

                Bundle bundle = new Bundle();
                bundle.putInt("id_entrevista", entrevista.getId());
                bundle.putInt("id_entrevistado", entrevista.getId_entrevistado());

                bundle.putString("fecha_entrevista", fechaEntrevista);

                bundle.putString("nombre_entrevistado", entrevistado.getNombre());
                bundle.putString("apellido_entrevistado", entrevistado.getApellido());

                bundle.putInt("n_entrevistas", Objects.requireNonNull(params.get("n_entrevistas")));
                bundle.putString("n_normales", String.valueOf(params.get("n_normales")));
                bundle.putString("n_extraodrinarias", String.valueOf(params.get("n_extraodrinarias")));

                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        holder.iv_menu_entrevista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context, holder.iv_menu_entrevista);
                popupMenu.inflate(R.menu.menu_holder_entrevista);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == R.id.menu_ver_eventos) {
                            Intent intent = new Intent(context, EventosActivity.class);

                            Bundle bundle = new Bundle();
                            bundle.putInt("id_entrevista", entrevista.getId());
                            bundle.putInt("id_entrevistado", entrevista.getId_entrevistado());

                            bundle.putString("fecha_entrevista", fechaEntrevista);

                            bundle.putString("nombre_entrevistado", entrevistado.getNombre());
                            bundle.putString("apellido_entrevistado", entrevistado.getApellido());

                            bundle.putInt("n_entrevistas", Objects.requireNonNull(params.get("n_entrevistas")));
                            bundle.putString("n_normales", String.valueOf(params.get("n_normales")));
                            bundle.putString("n_extraodrinarias", String.valueOf(params.get("n_extraodrinarias")));

                            intent.putExtras(bundle);
                            context.startActivity(intent);
                            return true;
                        } else if (item.getItemId() == R.id.menu_editar_entrevista) {

                            Intent intent = new Intent(context, EditarEntrevistaActivity.class);

                            Bundle bundle = new Bundle();
                            bundle.putInt("id_entrevista", entrevista.getId());
                            bundle.putInt("id_entrevistado", entrevista.getId_entrevistado());
                            intent.putExtras(bundle);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

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

    static class QuakeViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_entrevista_nombre;
        private final TextView tv_fecha_registro;
        private final TextView tv_tipo_entrevista;
        private final View card_view;
        private final ImageView iv_menu_entrevista;

        QuakeViewHolder(@NonNull View itemView) {
            super(itemView);

            card_view = itemView.findViewById(R.id.card_view);
            tv_entrevista_nombre = itemView.findViewById(R.id.tv_entrevista_nombre);
            tv_fecha_registro = itemView.findViewById(R.id.tv_entrevista_fecha_registro_value);
            tv_tipo_entrevista = itemView.findViewById(R.id.tv_entrevista_tipo);
            iv_menu_entrevista = itemView.findViewById(R.id.iv_menu_entrevista);
        }
    }
}
