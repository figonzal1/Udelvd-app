package cl.udelvd.vistas.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

import cl.udelvd.R;
import cl.udelvd.modelo.Evento;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.vistas.activities.EditarEventoActivity;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class EventsSwipeFragment extends Fragment {

    private static final String TAG_DELETE_DIALOG_NAME = "EliminarEvento";
    private Evento evento;
    private String fecha_entrevista;
    private int position;

    private TextView tv_fecha_entrevista;
    private TextView tv_evento;
    private TextView tv_accion;
    private ImageView iv_emoticon;
    private TextView tv_hora_evento;
    private TextView tv_justificacion;
    private TextView tv_descripcion_emoticon;
    private FloatingActionButton fbEditarEvento;
    private FloatingActionButton fbEliminarEvento;

    private static final int REQUEST_CODE_EDITAR_EVENTO = 300;

    private Activity activity;
    private FragmentManager fragmentManager;
    private DeleteDialogListener listener;


    public EventsSwipeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EventsSwipeFragment newInstance() {
        return new EventsSwipeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_swipe_evento, container, false);

        instanciaRecursosInterfaz(v);

        setearInformacionEvento();

        return v;
    }

    /**
     * Inicializar recursos usados en interfaz
     *
     * @param v View de interfaz
     */
    private void instanciaRecursosInterfaz(View v) {
        tv_fecha_entrevista = v.findViewById(R.id.tv_fecha_entrevista);
        tv_evento = v.findViewById(R.id.tv_detalle_evento);
        tv_hora_evento = v.findViewById(R.id.tv_hora_evento);
        tv_accion = v.findViewById(R.id.tv_accion_evento);
        tv_justificacion = v.findViewById(R.id.tv_justificacion_evento);
        iv_emoticon = v.findViewById(R.id.iv_emoticon_evento);
        tv_descripcion_emoticon = v.findViewById(R.id.tv_descripcion_emoticon);

        fbEditarEvento = v.findViewById(R.id.fb_editar_evento);
        fbEliminarEvento = v.findViewById(R.id.fb_eliminar_evento);
    }

    /**
     * Configurar informacion del evento en cada cardview dentro del Swipe
     */
    private void setearInformacionEvento() {
        tv_fecha_entrevista.setText(String.format(getString(R.string.FORMATO_ENTREVISTA_FECHA), fecha_entrevista));
        tv_evento.setText(String.format(Locale.US, getString(R.string.FORMATO_EVENTO_N), position + 1));

        tv_accion.setText(String.valueOf(evento.getAccion().getNombre()));

        tv_hora_evento.setText(Utils.dateToString(getContext(), true, evento.getHora_evento()));

        tv_justificacion.setText(evento.getJustificacion());

        tv_descripcion_emoticon.setText(evento.getEmoticon().getDescripcion());

        //GLIDE PARA CARGAR IMAGEN DE foto
        Glide.with(this)
                .load(evento.getEmoticon().getUrl())
                .apply(new RequestOptions()
                        .error(R.drawable.not_found))
                .transition(withCrossFade())
                .into(iv_emoticon);


        fbEditarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), EditarEventoActivity.class);
                intent.putExtra(getString(R.string.KEY_EVENTO_ID_ENTREVISTA), evento.getEntrevista().getId());
                intent.putExtra(getString(R.string.KEY_EVENTO_ID_LARGO), evento.getId());
                activity.startActivityForResult(intent, REQUEST_CODE_EDITAR_EVENTO);
            }
        });

        fbEliminarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteEventoDialogFragment dialog = new DeleteEventoDialogFragment(listener, evento);
                dialog.show(fragmentManager, TAG_DELETE_DIALOG_NAME);
            }
        });
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public void setFechaEntrevista(String fecha_entrevista) {
        this.fecha_entrevista = fecha_entrevista;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void setListener(DeleteDialogListener listener) {
        this.listener = listener;
    }
}
