package cl.udelvd.adaptadores;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import cl.udelvd.modelo.NivelEducacional;

public class NivelEducacionalAdapter extends ArrayAdapter<NivelEducacional> {

    public NivelEducacionalAdapter(@NonNull Context context, int resource, @NonNull List<NivelEducacional> objects) {
        super(context, resource, objects);
    }
}
