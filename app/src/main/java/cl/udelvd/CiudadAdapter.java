package cl.udelvd;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import cl.udelvd.modelo.Ciudad;

public class CiudadAdapter extends ArrayAdapter<Ciudad> {
    public CiudadAdapter(@NonNull Context context, int resource, List<Ciudad> ciudads) {
        super(context, resource, ciudads);
    }
}
