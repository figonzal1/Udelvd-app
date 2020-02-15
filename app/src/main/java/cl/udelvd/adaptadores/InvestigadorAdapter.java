package cl.udelvd.adaptadores;

import android.app.Activity;
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
import cl.udelvd.vistas.fragments.ActivarInvestigadorDialogFragment;

public class InvestigadorAdapter extends RecyclerView.Adapter<InvestigadorAdapter.InvestigadorViewHolder> {

    private List<Investigador> investigadorList;
    private Context context;
    private Activity activity;
    private FragmentManager fragmentManager;
    private String TAG_ACTIVAR_INVESTIGADOR = "ActivarInvestigador";

    public InvestigadorAdapter(List<Investigador> investigadorList, Context context, Activity activity, FragmentManager fragmentManager) {
        this.investigadorList = investigadorList;
        this.context = context;
        this.activity = activity;
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

        if (investigador.isActivado()) {
            holder.switch_activate.setChecked(true);
            holder.switch_activate.setText("Activado");
        } else {
            holder.switch_activate.setChecked(false);
            holder.switch_activate.setText("Desactivado");
        }


        holder.switch_activate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.switch_activate.setChecked(true);
                    holder.switch_activate.setText("Activado");

                    ActivarInvestigadorDialogFragment dialogFragment = new ActivarInvestigadorDialogFragment(investigador, true);
                    dialogFragment.setCancelable(false);
                    dialogFragment.show(fragmentManager, TAG_ACTIVAR_INVESTIGADOR);
                } else {
                    holder.switch_activate.setChecked(false);
                    holder.switch_activate.setText("Desactivado");

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

    public void actualizarLista(List<Investigador> investigadorList) {
        this.investigadorList = investigadorList;
        notifyDataSetChanged();
    }

    public class InvestigadorViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_nombre;
        private TextView tv_email;
        private SwitchMaterial switch_activate;

        public InvestigadorViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_nombre = itemView.findViewById(R.id.cv_tv_nombre);
            tv_email = itemView.findViewById(R.id.cv_tv_email);
            switch_activate = itemView.findViewById(R.id.cv_switch_activate);
        }
    }
}
