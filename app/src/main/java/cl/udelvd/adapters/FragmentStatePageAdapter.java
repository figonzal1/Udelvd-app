package cl.udelvd.adapters;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

import cl.udelvd.models.Event;
import cl.udelvd.views.fragments.EventItemFragment;
import cl.udelvd.views.fragments.dialog.DeleteDialogListener;

/**
 * StatePageAdapter used for Swipe Views in Events
 */
public class FragmentStatePageAdapter extends FragmentStatePagerAdapter {

    private final String interviewDate;
    private List<Event> eventList;
    private final Activity activity;
    private final FragmentManager fragmentManager;
    private final DeleteDialogListener listener;

    public FragmentStatePageAdapter(@NonNull FragmentManager fm, int behavior, List<Event> eventList, String interviewDate, Activity activity, DeleteDialogListener listener) {

        super(fm, behavior);

        this.eventList = eventList;
        this.interviewDate = interviewDate;
        this.activity = activity;
        this.fragmentManager = fm;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        EventItemFragment fragment;
        fragment = EventItemFragment.newInstance();

        fragment.setEvent(eventList.get(position));
        fragment.setInterviewDate(interviewDate);
        fragment.setPosition(position);
        fragment.setActivity(activity);
        fragment.setFragmentManager(fragmentManager);
        fragment.setListener(listener);

        return fragment;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    public void updateList(List<Event> eventList) {

        this.eventList = eventList;
        notifyDataSetChanged();
    }
}
