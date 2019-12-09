package cl.udelvd.views.fragments;

import android.app.Activity;
import android.content.Context;
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
import cl.udelvd.model.Usuario;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.QuakeViewHolder> {

    private final List<Usuario> usuarioList;
    private final Context context;
    private final Activity activity;

    public UsuarioAdapter(List<Usuario> usuarioList, Context context, Activity activity) {
        this.activity = activity;
        this.usuarioList = usuarioList;
        this.context = context;
    }

    @NonNull
    @Override
    public QuakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_user, parent, false);
        return new QuakeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QuakeViewHolder holder, int position) {

        Usuario usuario = usuarioList.get(position);

        holder.tv_nombre_apellido.setText(usuario.getNombre() + " " + usuario.getApellido());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/mm/dd", Locale.US);
        holder.tv_fecha_nacimiento.setText(simpleDateFormat.format(usuario.getFechaNacimiento()));
        holder.tv_n_entrevistas.setText("10");


    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    public static class QuakeViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_nombre_apellido;
        private TextView tv_fecha_nacimiento;
        private TextView tv_n_entrevistas;


        public QuakeViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_nombre_apellido = itemView.findViewById(R.id.cv_tv_nombre);
            tv_fecha_nacimiento = itemView.findViewById(R.id.cv_tv_fecha_nac);
            tv_n_entrevistas = itemView.findViewById(R.id.cv_tv_n_entrevistas);
        }
    }
}
