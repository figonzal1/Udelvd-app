package cl.udelvd.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

import cl.udelvd.R;
import cl.udelvd.modelo.Investigador;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.vistas.fragments.ActivarInvestigadorDialogFragment;

public class InvestigadorAdapter extends RecyclerView.Adapter<InvestigadorAdapter.InvestigadorViewHolder> {

    private final List<Investigador> investigadorList;
    private final Context context;
    private final FragmentManager fragmentManager;
    private final String TAG_ACTIVAR_INVESTIGADOR = "ActivarInvestigador";

    public InvestigadorAdapter(List<Investigador> investigadorList, Context context, FragmentManager fragmentManager) {
        this.investigadorList = investigadorList;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public InvestigadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_investigador, parent, false);
        return new InvestigadorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final InvestigadorViewHolder holder, int position) {
        final Investigador investigador = investigadorList.get(position);


        holder.tv_email.setText(investigador.getEmail());
        holder.tv_nombre.setText(String.format("%s %s", investigador.getNombre(), investigador.getApellido()));
        holder.tv_fecha_registro.setText(Utils.dateToString(context, false, Utils.stringToDate(context, false, investigador.getCreateTime())));

        if (investigador.isActivado()) {
            holder.switch_activate.setChecked(true);
            holder.switch_activate.setText(context.getString(R.string.PERFIL_ACTIVADO));
        } else {
            holder.switch_activate.setChecked(false);
            holder.switch_activate.setText(context.getString(R.string.PERFIL_NO_ACTIVADO));
        }


        holder.switch_activate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.switch_activate.setChecked(true);
                    holder.switch_activate.setText(context.getString(R.string.PERFIL_ACTIVADO));

                    ActivarInvestigadorDialogFragment dialogFragment = new ActivarInvestigadorDialogFragment(investigador, true);
                    dialogFragment.setCancelable(false);
                    dialogFragment.show(fragmentManager, TAG_ACTIVAR_INVESTIGADOR);
                } else {
                    holder.switch_activate.setChecked(false);
                    holder.switch_activate.setText(context.getString(R.string.PERFIL_NO_ACTIVADO));

                    ActivarInvestigadorDialogFragment dialogFragment = new ActivarInvestigadorDialogFragment(investigador, false);
                    dialogFragment.setCancelable(false);
                    dialogFragment.show(fragmentManager, TAG_ACTIVAR_INVESTIGADOR);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return investigadorList.size();
    }

    static class InvestigadorViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_nombre;
        private final TextView tv_email;
        private final TextView tv_fecha_registro;
        private final SwitchMaterial switch_activate;

        InvestigadorViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_nombre = itemView.findViewById(R.id.cv_tv_nombre);
            tv_email = itemView.findViewById(R.id.cv_tv_email);
            tv_fecha_registro = itemView.findViewById(R.id.cv_tv_fecha_registro);
            switch_activate = itemView.findViewById(R.id.cv_switch_activate);
        }
    }
}
