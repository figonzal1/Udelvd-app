package cl.udelvd.adaptadores;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import cl.udelvd.modelo.Profesion;

public class ProfesionAdapter extends ArrayAdapter<Profesion> {
    public ProfesionAdapter(@NonNull Context context, int resource, @NonNull List<Profesion> profesionList) {
        super(context, resource, profesionList);
    }
}
