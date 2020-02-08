package cl.udelvd.adaptadores;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import cl.udelvd.EstadisticasFragment;
import cl.udelvd.R;
import cl.udelvd.vistas.fragments.EntrevistadoListaFragment;

/**
 * Page Adapter usado cuando la paginacion es fija
 */
public class FragmentPageAdapter extends FragmentPagerAdapter {

    private final String[] mTabs = new String[2];
    private final String msg_login;
    private final Context context;

    public FragmentPageAdapter(@NonNull FragmentManager fm, Context context, String msg_login) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mTabs[0] = context.getString(R.string.TAB_NAME_ENTREVISTADOS);
        mTabs[1] = context.getString(R.string.TAB_NAME_ESTADISTICAS);
        this.msg_login = msg_login;
        this.context = context;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment f = new Fragment();

        switch (position) {

            case 0:

                f = EntrevistadoListaFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString(context.getString(R.string.INTENT_KEY_MSG_LOGIN), msg_login);
                f.setArguments(bundle);

                break;
            case 1:
                f = EstadisticasFragment.newInstance();
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
