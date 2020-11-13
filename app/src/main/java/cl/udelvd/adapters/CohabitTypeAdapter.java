package cl.udelvd.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import cl.udelvd.R;
import cl.udelvd.models.CohabitType;

public class CohabitTypeAdapter extends ArrayAdapter<CohabitType> {

    public CohabitTypeAdapter(@NonNull Context context, int resource, @NonNull List<CohabitType> coexistenceTypeList) {
        super(context, resource, coexistenceTypeList);

        CohabitType c = new CohabitType();
        c.setName(context.getString(R.string.sin_seleccionar));
        coexistenceTypeList.add(0, c);
    }
}
