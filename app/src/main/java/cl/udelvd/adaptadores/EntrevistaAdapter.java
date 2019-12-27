package cl.udelvd.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cl.udelvd.R;
import cl.udelvd.modelo.Entrevista;

public class EntrevistaAdapter extends RecyclerView.Adapter<EntrevistaAdapter.QuakeViewHolder> {

    private final List<Entrevista> entrevistaList;
    private Context context;

    public EntrevistaAdapter(List<Entrevista> entrevistaList, Context context) {
        this.entrevistaList = entrevistaList;
        this.context = context;
    }

    @NonNull
    @Override
    public QuakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_entrevista, parent, false);
        return new QuakeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QuakeViewHolder holder, int position) {

        final Entrevista entrevista = entrevistaList.get(position);

        holder.tv_entrevista_nombre.setText("Entrevista " + (position + 1));

        holder.tv_tipo_entrevista.setText(entrevista.getTipoEntrevista().getNombre());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String fechaEntrevista = simpleDateFormat.format(entrevista.getFecha_entrevista());

        holder.tv_fecha_registro.setText(fechaEntrevista);


        //TODO: Setear in click listener para abrir actividad con eventos
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Item click listener", Toast.LENGTH_LONG).show();
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

        QuakeViewHolder(@NonNull View itemView) {
            super(itemView);

            card_view = itemView.findViewById(R.id.card_view);
            tv_entrevista_nombre = itemView.findViewById(R.id.tv_entrevista_nombre);
            tv_fecha_registro = itemView.findViewById(R.id.tv_entrevista_fecha_registro_value);
            tv_tipo_entrevista = itemView.findViewById(R.id.tv_entrevista_tipo);
        }
    }
}
