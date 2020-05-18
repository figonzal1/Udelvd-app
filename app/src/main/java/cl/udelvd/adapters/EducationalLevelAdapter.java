package cl.udelvd.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import cl.udelvd.models.EducationalLevel;

public class EducationalLevelAdapter extends ArrayAdapter<EducationalLevel> {

    public EducationalLevelAdapter(@NonNull Context context, int resource, @NonNull List<EducationalLevel> educationaLevelList) {
        super(context, resource, educationaLevelList);
    }
}
