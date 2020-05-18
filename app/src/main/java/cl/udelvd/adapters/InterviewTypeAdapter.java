package cl.udelvd.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import cl.udelvd.models.InterviewType;

public class InterviewTypeAdapter extends ArrayAdapter<InterviewType> {

    public InterviewTypeAdapter(@NonNull Context context, int resource, @NonNull List<InterviewType> interviewTypeList) {
        super(context, resource, interviewTypeList);
    }
}
