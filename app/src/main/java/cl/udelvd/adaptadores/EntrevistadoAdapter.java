package cl.udelvd.adaptadores;

import android.content.Context;
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

import cl.udelvd.R;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.utilidades.Utils;

public class EntrevistadoAdapter extends RecyclerView.Adapter<EntrevistadoAdapter.QuakeViewHolder> {

    private final List<Entrevistado> entrevistadoList;
    private Context context;

    public EntrevistadoAdapter(List<Entrevistado> entrevistadoList, Context context) {
        this.entrevistadoList = entrevistadoList;
        this.context = context;
    }

    @NonNull
    @Override
    public QuakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_entrevistado, parent, false);
        return new QuakeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final QuakeViewHolder holder, int position) {

        Entrevistado entrevistado = entrevistadoList.get(position);

        holder.tv_nombre_apellido.setText(String.format("%s %s", entrevistado.getNombre(), entrevistado.getApellido()));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/mm/dd", Locale.US);

        int annos = Utils.calculateYearsOld(entrevistado.getFechaNacimiento());
        holder.tv_fecha_nacimiento.setText(String.format("%s - %s años", simpleDateFormat.format(entrevistado.getFechaNacimiento()), annos));

        //TODO: Agregar soporte para conteo de entrevistas
        holder.tv_n_entrevistas.setText("10 entrevistas");


        holder.iv_menu_entrevistado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context, holder.iv_menu_entrevistado);
                popupMenu.inflate(R.menu.menu_holder_entrevistado);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return false;
                    }
                });
                popupMenu.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return entrevistadoList.size();
    }

    static class QuakeViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_nombre_apellido;
        private final TextView tv_fecha_nacimiento;
        private final TextView tv_n_entrevistas;
        private final ImageView iv_menu_entrevistado;


        QuakeViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_nombre_apellido = itemView.findViewById(R.id.cv_tv_nombre);
            tv_fecha_nacimiento = itemView.findViewById(R.id.cv_tv_fecha_nac);
            tv_n_entrevistas = itemView.findViewById(R.id.cv_tv_n_entrevistas);
            iv_menu_entrevistado = itemView.findViewById(R.id.iv_menu_entrevistado);
        }
    }
}
