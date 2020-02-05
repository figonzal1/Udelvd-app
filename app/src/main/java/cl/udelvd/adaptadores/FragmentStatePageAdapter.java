package cl.udelvd.adaptadores;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

import cl.udelvd.modelo.Evento;
import cl.udelvd.vistas.fragments.DeleteDialogListener;
import cl.udelvd.vistas.fragments.EventsSwipeFragment;

public class FragmentStatePageAdapter extends FragmentStatePagerAdapter {

    private final List<Evento> eventoList;
    private final String fecha_entrevista;
    private final Activity activity;
    private final FragmentManager fragmentManager;
    private final DeleteDialogListener listener;

    public FragmentStatePageAdapter(@NonNull FragmentManager fm, int behavior, List<Evento> eventoList, String fecha_entrevista, Activity activity, DeleteDialogListener listener) {
        super(fm, behavior);
        this.eventoList = eventoList;
        this.fecha_entrevista = fecha_entrevista;
        this.activity = activity;
        this.fragmentManager = fm;
        this.listener = listener;
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
        fragment.setActivity(activity);
        fragment.setFragmentManager(fragmentManager);
        fragment.setListener(listener);

        return fragment;
    }

    @Override
    public int getCount() {
        return eventoList.size();
    }
}
