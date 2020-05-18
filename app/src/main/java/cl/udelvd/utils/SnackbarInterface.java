package cl.udelvd.utils;

import android.view.View;

public interface SnackbarInterface {

    void showSnackbar(View v, int duration, String title, String action);
}
