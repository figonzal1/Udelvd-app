package cl.udelvd;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class FragmentStatePageAdapter extends FragmentStatePagerAdapter {


    public FragmentStatePageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        fragment = EventsSwipeFragment.newInstance();

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
