package cl.udelvd.adaptadores;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import cl.udelvd.modelo.EstadoCivil;

public class EstadoCivilAdapter extends ArrayAdapter<EstadoCivil> {

    public EstadoCivilAdapter(@NonNull Context context, int resource, @NonNull List<EstadoCivil> objects) {
        super(context, resource, objects);
    }
}
