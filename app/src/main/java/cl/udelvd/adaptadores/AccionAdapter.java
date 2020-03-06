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
public class AccionAdapter extends RecyclerView.Adapter<AccionAdapter.AccionViewHolder> {

    private List<Accion> accionList;
    private Context context;

    public AccionAdapter(List<Accion> accionList, Context context) {
        this.accionList = accionList;
        this.context = context;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public AccionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_accion, parent, false);
        return new AccionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AccionViewHolder holder, int position) {
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
