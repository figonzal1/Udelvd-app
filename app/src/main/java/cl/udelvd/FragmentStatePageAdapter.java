package cl.udelvd;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

import cl.udelvd.modelo.Evento;

public class FragmentStatePageAdapter extends FragmentStatePagerAdapter {

    private List<Evento> eventoList;
    private String fecha_entrevista;

    FragmentStatePageAdapter(@NonNull FragmentManager fm, int behavior, List<Evento> eventoList, String fecha_entrevista) {
        super(fm, behavior);
        this.eventoList = eventoList;
        this.fecha_entrevista = fecha_entrevista;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        EventsSwipeFragment fragment;
        fragment = EventsSwipeFragment.newInstance();
        fragment.setEvento(eventoList.get(position));
        fragment.setFechaEntrevista(fecha_entrevista);
        fragment.setPosition(position);

        return fragment;
    }

    @Override
    public int getCount() {
        Log.d("SIZE", String.valueOf(eventoList.size()));
        return eventoList.size();
    }
}
