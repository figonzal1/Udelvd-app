package cl.udelvd.adaptadores;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

import cl.udelvd.modelo.Evento;
import cl.udelvd.vistas.fragments.EventsSwipeFragment;

public class FragmentStatePageAdapter extends FragmentStatePagerAdapter {

    private List<Evento> eventoList;
    private String fecha_entrevista;

    public FragmentStatePageAdapter(@NonNull FragmentManager fm, int behavior, List<Evento> eventoList, String fecha_entrevista) {
        super(fm, behavior);
        this.eventoList = eventoList;
        this.fecha_entrevista = fecha_entrevista;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        EventsSwipeFragment fragment;
        fragment = EventsSwipeFragment.newInstance();

        //Enviar datos a fragments
        fragment.setEvento(eventoList.get(position));
        fragment.setFechaEntrevista(fecha_entrevista);
        fragment.setPosition(position);

        return fragment;
    }

    @Override
    public int getCount() {
        return eventoList.size();
    }
}
