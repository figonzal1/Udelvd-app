package cl.udelvd.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import cl.udelvd.R;
import cl.udelvd.modelo.Entrevistado;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.viewmodel.EntrevistadoListaViewModel;
import cl.udelvd.vistas.activities.EditarEntrevistadoActivity;
import cl.udelvd.vistas.activities.EntrevistasListaActivity;
import cl.udelvd.vistas.fragments.DeleteEntrevistadoDialogFragment;
import cl.udelvd.vistas.fragments.EntrevistadoListaFragment;

public class EntrevistadoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG_DELETE_DIALOG_NAME = "EliminarEntrevistado";
    private static final int REQUEST_CODE_EDITAR_ENTREVISTADO = 300;

    private static final int ENTREVISTADO = 0;
    private static final int PROGRESS_PAGINACION = 1;

    private final List<Entrevistado> entrevistadoList;
    private final Context context;

    private final EntrevistadoListaFragment entrevistadoListaFragment;
    private final FragmentManager fragmentManager;

    private final EntrevistadoListaViewModel entrevistadoListaViewModel;

    private int pagina = 1;

    private Button btn_cargar_mas;

    public EntrevistadoAdapter(List<Entrevistado> entrevistadoList, Context context, EntrevistadoListaFragment entrevistadoListaFragment, FragmentManager fragmentManager, EntrevistadoListaViewModel entrevistadoListaViewModel) {
        this.entrevistadoList = entrevistadoList;
        this.context = context;
        this.entrevistadoListaFragment = entrevistadoListaFragment;
        this.fragmentManager = fragmentManager;
        this.entrevistadoListaViewModel = entrevistadoListaViewModel;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;

        View v;
        if (viewType == ENTREVISTADO) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_entrevistado, parent, false);
            viewHolder = new EntrevistadoViewHolder(v);
        } else if (viewType == PROGRESS_PAGINACION) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paginacion, parent, false);
            viewHolder = new LoadingViewHolder(v);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {

        if (getItemViewType(position) == ENTREVISTADO) {

            final EntrevistadoViewHolder holder = (EntrevistadoViewHolder) viewHolder;

            final Entrevistado entrevistado = entrevistadoList.get(position);

            holder.tv_nombre_apellido.setText(String.format("%s %s", entrevistado.getNombre(), entrevistado.getApellido()));

            int annos = Utils.calculateYearsOld(entrevistado.getFechaNacimiento());
            holder.tv_fecha_nacimiento.setText(String.format("%s - %s años", Utils.dateToString(context.getApplicationContext(), false, entrevistado.getFechaNacimiento()), annos));

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
                    bundle.putInt(context.getString(R.string.KEY_ENTREVISTADO_ID_LARGO), entrevistado.getId());
                    bundle.putString(context.getString(R.string.KEY_ENTREVISTADO_NOMBRE_LARGO), entrevistado.getNombre());
                    bundle.putString(context.getString(R.string.KEY_ENTREVISTADO_APELLIDO_LARGO), entrevistado.getApellido());
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
                                intent.putExtra(context.getString(R.string.KEY_ENTREVISTADO_ID_LARGO), entrevistado.getId());
                                entrevistadoListaFragment.startActivityForResult(intent, REQUEST_CODE_EDITAR_ENTREVISTADO);

                                return true;

                            } else if (item.getItemId() == R.id.menu_ver_entrevistas) {

                                Intent intent = new Intent(context, EntrevistasListaActivity.class);

                                Bundle bundle = new Bundle();
                                bundle.putInt(context.getString(R.string.KEY_ENTREVISTADO_ID_LARGO), entrevistado.getId());
                                bundle.putString(context.getString(R.string.KEY_ENTREVISTADO_NOMBRE_LARGO), entrevistado.getNombre());
                                bundle.putString(context.getString(R.string.KEY_ENTREVISTADO_APELLIDO_LARGO), entrevistado.getApellido());
                                intent.putExtras(bundle);
                                context.startActivity(intent);

                                return true;
                            } else if (item.getItemId() == R.id.menu_eliminar_entrevistado) {
                                DeleteEntrevistadoDialogFragment dialog = new DeleteEntrevistadoDialogFragment(entrevistado);
                                dialog.show(fragmentManager, TAG_DELETE_DIALOG_NAME);

                                return true;
                            }

                            //TODO: Ver posibilidad de tener perfil de entrevistado

                            return false;
                        }
                    });

                    popupMenu.show();
                }
            });
        } else if (getItemViewType(position) == PROGRESS_PAGINACION) {
            final LoadingViewHolder holder = (LoadingViewHolder) viewHolder;

            btn_cargar_mas = holder.btn_cargar_mas;
            btn_cargar_mas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pagina += 1;
                    entrevistadoListaViewModel.cargarSiguientePagina(pagina);
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return entrevistadoList == null ? 0 : entrevistadoList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        return (position == entrevistadoList.size()) ? PROGRESS_PAGINACION : ENTREVISTADO;
    }

    public void agregarEntrevistados(List<Entrevistado> entrevistados) {
        if (entrevistados.size() == 0) {
            btn_cargar_mas.setVisibility(View.GONE);
        } else {
            btn_cargar_mas.setVisibility(View.VISIBLE);
        }
        entrevistadoList.addAll(entrevistados);
        notifyDataSetChanged();
    }

    public List<Entrevistado> getEntrevistadoList() {
        return entrevistadoList;
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

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {

        final Button btn_cargar_mas;

        LoadingViewHolder(View itemView) {
            super(itemView);

            btn_cargar_mas = itemView.findViewById(R.id.btn_cargar_mas);
        }

    }
}
