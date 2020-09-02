package cl.udelvd.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import cl.udelvd.models.CohabitType;

public class CohabitTypeAdapter extends ArrayAdapter<CohabitType> {

    public CohabitTypeAdapter(@NonNull Context context, int resource, @NonNull List<CohabitType> coexistenceTypeList) {
        super(context, resource, coexistenceTypeList);
    }
}
