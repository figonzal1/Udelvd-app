package cl.udelvd.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import cl.udelvd.models.CivilState;

public class CivilStateAdapter extends ArrayAdapter<CivilState> {

    public CivilStateAdapter(@NonNull Context context, int resource, @NonNull List<CivilState> civilStateList) {
        super(context, resource, civilStateList);
    }
}
