package cl.udelvd.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import cl.udelvd.models.City;

public class CityAdapter extends ArrayAdapter<City> {

    public CityAdapter(@NonNull Context context, int resource, List<City> cityList) {
        super(context, resource, cityList);
    }
}
