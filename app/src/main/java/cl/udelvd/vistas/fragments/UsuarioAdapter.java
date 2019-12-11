package cl.udelvd.vistas.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cl.udelvd.R;
import cl.udelvd.modelo.Usuario;
import cl.udelvd.utilidades.Utils;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.QuakeViewHolder> {

    private final List<Usuario> usuarioList;

    public UsuarioAdapter(List<Usuario> usuarioList) {
        this.usuarioList = usuarioList;
    }

    @NonNull
    @Override
    public QuakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_entrevistado, parent, false);
        return new QuakeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QuakeViewHolder holder, int position) {

        Usuario usuario = usuarioList.get(position);

        holder.tv_nombre_apellido.setText(String.format("%s %s", usuario.getNombre(), usuario.getApellido()));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/mm/dd", Locale.US);

        int annos = Utils.calculateYearsOld(usuario.getFechaNacimiento());
        holder.tv_fecha_nacimiento.setText(String.format("%s - %s años", simpleDateFormat.format(usuario.getFechaNacimiento()), annos));
        holder.tv_n_entrevistas.setText("10 entrevistas");


    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    static class QuakeViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_nombre_apellido;
        private final TextView tv_fecha_nacimiento;
        private final TextView tv_n_entrevistas;


        QuakeViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_nombre_apellido = itemView.findViewById(R.id.cv_tv_nombre);
            tv_fecha_nacimiento = itemView.findViewById(R.id.cv_tv_fecha_nac);
            tv_n_entrevistas = itemView.findViewById(R.id.cv_tv_n_entrevistas);
        }
    }
}
