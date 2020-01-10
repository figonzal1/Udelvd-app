package cl.udelvd.adaptadores;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import cl.udelvd.modelo.Accion;

public class AccionAdapter extends ArrayAdapter<Accion> {
    public AccionAdapter(@NonNull Context context, int resource, @NonNull List<Accion> accionList) {
        super(context, resource, accionList);
    }
}


