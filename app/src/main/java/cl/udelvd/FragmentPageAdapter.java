package cl.udelvd;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import cl.udelvd.views.fragments.UsuarioListaFragment;

/**
 * Page Adapter usado cuando la paginacion es fija
 */
public class FragmentPageAdapter extends FragmentPagerAdapter {

    private final String[] mTabs = new String[2];


    public FragmentPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
        mTabs[0] = "Usuarios";
        mTabs[1] = "Estadísticas";
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment f = new Fragment();
        switch (position) {

            case 0:
                f = UsuarioListaFragment.newInstance();
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
