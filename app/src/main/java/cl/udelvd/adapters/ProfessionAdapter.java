package cl.udelvd.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import cl.udelvd.models.Profession;

public class ProfessionAdapter extends ArrayAdapter<Profession> {
    public ProfessionAdapter(@NonNull Context context, int resource, @NonNull List<Profession> professionList) {
        super(context, resource, professionList);
    }
}
