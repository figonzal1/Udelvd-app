package cl.udelvd.adaptadores;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import cl.udelvd.modelo.TipoConvivencia;

public class TipoConvivenciaAdapter extends ArrayAdapter<TipoConvivencia> {
    public TipoConvivenciaAdapter(@NonNull Context context, int resource, @NonNull List<TipoConvivencia> tipoConvivenciaList) {
        super(context, resource, tipoConvivenciaList);
    }
}
