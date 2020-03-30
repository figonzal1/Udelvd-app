package cl.udelvd.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.modelo.Investigador;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.InvestigadorListaViewModel;
import cl.udelvd.vistas.fragments.ActivarInvestigadorDialogFragment;

public class InvestigadorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int INVESTIGADOR = 0;
    private final Context context;
    private final FragmentManager fragmentManager;
    private final String TAG_ACTIVAR_INVESTIGADOR = "ActivarInvestigador";
    private static final int PROGRESS_PAGINACION = 1;
    private final InvestigadorListaViewModel investigadorListaViewModel;
    private List<Investigador> investigadorList;
    private Button btn_cargar_mas;
    private ProgressBar progressBar;
    private Investigador investigador;
    private int pagina = 1;
    private int investigadores_totales;

    public InvestigadorAdapter(List<Investigador> investigadorList, Context context, FragmentManager fragmentManager, InvestigadorListaViewModel investigadorListaViewModel, Investigador investigador) {
        this.investigadorList = investigadorList;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.investigador = investigador;
        this.investigadorListaViewModel = investigadorListaViewModel;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;

        View v;
        if (viewType == INVESTIGADOR) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_investigador, parent, false);
            viewHolder = new InvestigadorViewHolder(v);
        } else if (viewType == PROGRESS_PAGINACION) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paginacion, parent, false);
            viewHolder = new LoadingViewHolder(v);
        }
        return Objects.requireNonNull(viewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {

        if (getItemViewType(position) == INVESTIGADOR) {

            final InvestigadorViewHolder holder = (InvestigadorViewHolder) viewHolder;
            final Investigador investigador = investigadorList.get(position);

            holder.tv_email.setText(investigador.getEmail());
            holder.tv_nombre.setText(String.format("%s %s", investigador.getNombre(), investigador.getApellido()));
            holder.tv_fecha_registro.setText(String.format(context.getString(R.string.FORMATO_FECHA_REGISTRO), Utils.dateToString(context, false, Utils.stringToDate(context, false, investigador.getCreateTime()))));

            if (investigador.isActivado()) {
                holder.switch_activate.setChecked(true);
                holder.switch_activate.setText(context.getString(R.string.PERFIL_ACTIVADO));
            } else {
                holder.switch_activate.setChecked(false);
                holder.switch_activate.setText(context.getString(R.string.PERFIL_NO_ACTIVADO));
            }

            holder.switch_activate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.switch_activate.isChecked()) {
                        ActivarInvestigadorDialogFragment dialogFragment = new ActivarInvestigadorDialogFragment(investigador, true, holder.switch_activate);
                        dialogFragment.setCancelable(false);
                        dialogFragment.show(fragmentManager, TAG_ACTIVAR_INVESTIGADOR);
                    } else {
                        ActivarInvestigadorDialogFragment dialogFragment = new ActivarInvestigadorDialogFragment(investigador, false, holder.switch_activate);
                        dialogFragment.setCancelable(false);
                        dialogFragment.show(fragmentManager, TAG_ACTIVAR_INVESTIGADOR);
                    }
                }
            });

        } else if (getItemViewType(position) == PROGRESS_PAGINACION) {
            final LoadingViewHolder holder = (LoadingViewHolder) viewHolder;

            btn_cargar_mas = holder.btn_cargar_mas;
            progressBar = holder.progressBar;

            if (investigadorList.size() == 0) {
                btn_cargar_mas.setVisibility(View.GONE);
                pagina = 1;
            }

            if (investigadores_totales == investigadorList.size()) {
                btn_cargar_mas.setVisibility(View.GONE);
            }

            btn_cargar_mas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pagina += 1;

                    if (investigadorListaViewModel != null) {
                        investigadorListaViewModel.cargarSiguientePagina(pagina, investigador);
                    }

                    progressBar.setVisibility(View.VISIBLE);

                    btn_cargar_mas.setVisibility(View.GONE);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return investigadorList == null ? 0 : investigadorList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == investigadorList.size()) ? PROGRESS_PAGINACION : INVESTIGADOR;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void resetPages() {
        pagina = 1;
    }

    public void actualizarLista(List<Investigador> investigadorList) {
        this.investigadorList = investigadorList;
        notifyDataSetChanged();
    }

    public void agregarEntrevistados(List<Investigador> invesSgt) {
        if (btn_cargar_mas != null) {
            if (invesSgt.size() == 0) {
                btn_cargar_mas.setVisibility(View.GONE);
            } else {
                btn_cargar_mas.setVisibility(View.VISIBLE);
            }
        }
        investigadorList.addAll(invesSgt);
        notifyDataSetChanged();
    }

    public void ocultarProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    public List<Investigador> getInvestigadorList() {
        return investigadorList;
    }

    public void setInvestigadoresTotales(int investigadores_totales) {
        this.investigadores_totales = investigadores_totales;
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

    static class LoadingViewHolder extends RecyclerView.ViewHolder {

        final MaterialButton btn_cargar_mas;
        final ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            btn_cargar_mas = itemView.findViewById(R.id.btn_cargar_mas);
            progressBar = itemView.findViewById(R.id.progress_bar_paginacion);
        }

    }
}
