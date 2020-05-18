package cl.udelvd.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import cl.udelvd.models.CoexistanceType;

public class CoexistenceTypeAdapter extends ArrayAdapter<CoexistanceType> {
    public CoexistenceTypeAdapter(@NonNull Context context, int resource, @NonNull List<CoexistanceType> coexistenceTypeList) {
        super(context, resource, coexistenceTypeList);
    }
}
