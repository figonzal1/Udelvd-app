package cl.udelvd.adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import cl.udelvd.R;
import cl.udelvd.views.fragments.IntervieweeListFragment;
import cl.udelvd.views.fragments.StatsFragment;

/**
 * Page Adapter used when paging is fixed (Main activity)
 */
public class FragmentPageAdapter extends FragmentPagerAdapter {

    private final String[] mTabs = new String[2];
    private final String msgLogin;
    private final Context context;

    public FragmentPageAdapter(@NonNull FragmentManager fm, Context context, String msgLogin) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mTabs[0] = context.getString(R.string.TAB_NAME_ENTREVISTADOS);
        mTabs[1] = context.getString(R.string.TAB_NAME_ESTADISTICAS);
        this.msgLogin = msgLogin;
        this.context = context;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment f = new Fragment();

        switch (position) {

            case 0:

                f = IntervieweeListFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString(context.getString(R.string.INTENT_KEY_MSG_LOGIN), msgLogin);
                f.setArguments(bundle);

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
