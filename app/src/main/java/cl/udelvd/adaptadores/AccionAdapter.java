package cl.udelvd.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import cl.udelvd.R;
import cl.udelvd.modelo.Accion;

/**
 * Adaptador para listado de acciones para ADMIN
 */
public class AccionAdapter extends RecyclerView.Adapter<AccionAdapter.EntrevistaViewHolder> {

    private List<Accion> accionList;
    private Context context;

    public AccionAdapter(List<Accion> accionList, Context context) {
        this.accionList = accionList;
        this.context = context;
    }

    @NonNull
    @Override
    public EntrevistaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_accion, parent, false);
        return new EntrevistaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrevistaViewHolder holder, int position) {
        Accion accion = accionList.get(position);

        holder.tv_accion.setText(String.format(Locale.getDefault(), context.getString(R.string.FORMATO_ACCION), (position + 1)));

        holder.tv_espanol.setText(accion.getNombreEs());
        holder.tv_ingles.setText(accion.getNombreEn());

        //TODO Generar menu de acciones
        holder.iv_menu_accion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return accionList.size();
    }

    public void actualizarLista(List<Accion> accionesList) {
        this.accionList = accionesList;
        notifyDataSetChanged();
    }

    static class EntrevistaViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iv_menu_accion;
        private TextView tv_accion;
        private TextView tv_ingles;
        private TextView tv_espanol;

        EntrevistaViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_accion = itemView.findViewById(R.id.cv_tv_accion);
            tv_ingles = itemView.findViewById(R.id.tv_ingles);
            tv_espanol = itemView.findViewById(R.id.cv_tv_espanol);
            iv_menu_accion = itemView.findViewById(R.id.iv_menu_accion);
        }
    }
}
