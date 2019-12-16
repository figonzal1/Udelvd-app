package cl.udelvd;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import cl.udelvd.vistas.fragments.EntrevistadoListaFragment;

/**
 * Page Adapter usado cuando la paginacion es fija
 */
public class FragmentPageAdapter extends FragmentPagerAdapter {

    private final String[] mTabs = new String[2];


    public FragmentPageAdapter(@NonNull FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mTabs[0] = "Usuarios";
        mTabs[1] = "Estadísticas";
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment f = new Fragment();
        switch (position) {

            case 0:
                f = EntrevistadoListaFragment.newInstance();
                break;
            case 1:
                f = StatsFragment.newInstance();
                break;

        }
        return f;
    }

    @Override
    public int getCount() {
        return mTabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs[position];
    }
}
