package cl.udelvd.adaptadores;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import cl.udelvd.modelo.TipoEntrevista;

public class TipoEntrevistaAdapter extends ArrayAdapter<TipoEntrevista> {

    public TipoEntrevistaAdapter(@NonNull Context context, int resource, @NonNull List<TipoEntrevista> objects) {
        super(context, resource, objects);
    }
}
