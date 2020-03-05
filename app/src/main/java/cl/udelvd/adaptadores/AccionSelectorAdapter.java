package cl.udelvd.adaptadores;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import cl.udelvd.modelo.Accion;

/**
 * Adaptador para llenar selector de acciones
 */
public class AccionSelectorAdapter extends ArrayAdapter<Accion> {
    public AccionSelectorAdapter(@NonNull Context context, int resource, @NonNull List<Accion> accionList) {
        super(context, resource, accionList);
    }
}


