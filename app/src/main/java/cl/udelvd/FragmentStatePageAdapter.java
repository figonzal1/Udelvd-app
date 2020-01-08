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

    FragmentStatePageAdapter(@NonNull FragmentManager fm, int behavior, List<Evento> eventoList) {
        super(fm, behavior);
        this.eventoList = eventoList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        fragment = EventsSwipeFragment.newInstance();

        return fragment;
    }

    @Override
    public int getCount() {
        Log.d("SIZE", String.valueOf(eventoList.size()));
        return eventoList.size();
    }
}
