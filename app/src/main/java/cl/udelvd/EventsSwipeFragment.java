package cl.udelvd;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Locale;

import cl.udelvd.modelo.Evento;


public class EventsSwipeFragment extends Fragment {

    private Evento evento;
    private String fecha_entrevista;
    private int position;

    private TextView tv_fecha_entrevista;
    private TextView tv_evento;
    private TextView tv_accion;
    private ImageView iv_emoticon;
    private TextView tv_hora_evento;
    private TextView tv_justificacion;


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
        View v = inflater.inflate(R.layout.fragment_swipe_events, container, false);

        instanciaRecursosInterfaz(v);

        setearInformacionEvento();

        return v;
    }

    private void setearInformacionEvento() {
        tv_fecha_entrevista.setText(String.format("Entrevista día %s", fecha_entrevista));
        tv_evento.setText(String.format(Locale.US, "Evento %d", position + 1));

        tv_accion.setText(String.valueOf(evento.getAccion().getNombre()));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        tv_hora_evento.setText(simpleDateFormat.format(evento.getHora_evento()));

        tv_justificacion.setText(evento.getJustificacion());

        //GLIDE PARA CARGAR IMAGEN DE foto
    }

    private void instanciaRecursosInterfaz(View v) {
        tv_fecha_entrevista = v.findViewById(R.id.tv_fecha_entrevista);
        tv_evento = v.findViewById(R.id.tv_detalle_evento);
        tv_hora_evento = v.findViewById(R.id.tv_hora_evento);
        tv_accion = v.findViewById(R.id.tv_accion_evento);
        tv_justificacion = v.findViewById(R.id.tv_justificacion_evento);
        iv_emoticon = v.findViewById(R.id.iv_emoticon_evento);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
}
