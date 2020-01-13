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

import cl.udelvd.R;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.vistas.activities.EditarEntrevistadoActivity;
import cl.udelvd.vistas.activities.EntrevistasListaActivity;

public class EntrevistadoAdapter extends RecyclerView.Adapter<EntrevistadoAdapter.EntrevistadoViewHolder> {

    private final List<Entrevistado> entrevistadoList;
    private Context context;

    public EntrevistadoAdapter(List<Entrevistado> entrevistadoList, Context context) {
        this.entrevistadoList = entrevistadoList;
        this.context = context;
    }

    @NonNull
    @Override
    public EntrevistadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_entrevistado, parent, false);
        return new EntrevistadoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final EntrevistadoViewHolder holder, int position) {

        final Entrevistado entrevistado = entrevistadoList.get(position);

        holder.tv_nombre_apellido.setText(String.format("%s %s", entrevistado.getNombre(), entrevistado.getApellido()));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string.FORMATO_FECHA_2), Locale.US);

        int annos = Utils.calculateYearsOld(entrevistado.getFechaNacimiento());
        holder.tv_fecha_nacimiento.setText(String.format("%s - %s años", simpleDateFormat.format(entrevistado.getFechaNacimiento()), annos));

        if (entrevistado.getN_entrevistas() == 1) {
            holder.tv_n_entrevistas.setText(String.format(Locale.US, "%d entrevista", entrevistado.getN_entrevistas()));
        } else {
            holder.tv_n_entrevistas.setText(String.format(Locale.US, "%d entrevistas", entrevistado.getN_entrevistas()));
        }

        holder.card_view_entrevistado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EntrevistasListaActivity.class);

                Bundle bundle = new Bundle();
                bundle.putInt("id_entrevistado", entrevistado.getId());
                bundle.putString("nombre_entrevistado", entrevistado.getNombre());
                bundle.putString("apellido_entrevistado", entrevistado.getApellido());
                intent.putExtras(bundle);
                context.startActivity(intent, bundle);
            }
        });

        holder.iv_menu_entrevistado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context, holder.iv_menu_entrevistado);
                popupMenu.inflate(R.menu.menu_holder_entrevistado);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == R.id.menu_editar_entrevistado) {

                            Intent intent = new Intent(context, EditarEntrevistadoActivity.class);
                            intent.putExtra("id_entrevistado", entrevistado.getId());
                            context.startActivity(intent);

                        } else if (item.getItemId() == R.id.menu_ver_entrevistas) {

                            Intent intent = new Intent(context, EntrevistasListaActivity.class);

                            Bundle bundle = new Bundle();
                            bundle.putInt("id_entrevistado", entrevistado.getId());
                            bundle.putString("nombre_entrevistado", entrevistado.getNombre());
                            bundle.putString("apellido_entrevistado", entrevistado.getApellido());
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        }

                        //TODO: Ver posibilidad de tener perfil de entrevistado

                        return true;
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

    static class EntrevistadoViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_nombre_apellido;
        private final TextView tv_fecha_nacimiento;
        private final TextView tv_n_entrevistas;
        private final ImageView iv_menu_entrevistado;

        private final View card_view_entrevistado;


        EntrevistadoViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_nombre_apellido = itemView.findViewById(R.id.cv_tv_nombre);
            tv_fecha_nacimiento = itemView.findViewById(R.id.cv_tv_fecha_nac);
            tv_n_entrevistas = itemView.findViewById(R.id.tv_n_entrevistas);
            iv_menu_entrevistado = itemView.findViewById(R.id.iv_menu_entrevistado);

            card_view_entrevistado = itemView.findViewById(R.id.card_view_entrevistado);
        }
    }
}
