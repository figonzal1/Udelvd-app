package cl.udelvd.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import cl.udelvd.models.Action;

/**
 * Adapter to fill action selector
 */
public class ActionSelectorAdapter extends ArrayAdapter<Action> {
    public ActionSelectorAdapter(@NonNull Context context, int resource, @NonNull List<Action> actionsList) {
        super(context, resource, actionsList);
    }
}


