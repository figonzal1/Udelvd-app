package cl.udelvd.utilidades;

import android.view.View;

public interface SnackbarInterface {

    void showSnackbar(View v, int duration, String titulo, String accion);
}
