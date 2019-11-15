package cl.udelvd;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewUserDialog extends DialogFragment {

    private Spinner genreSpinner;
    private View v;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_user_dialog, container, false);
        setCancelable(false);

        //Icono Cerrar dialog
        Toolbar toolbar = v.findViewById(R.id.toolbar_dialog);
        toolbar.setTitle("Crear Persona");
        toolbar.inflateMenu(R.menu.new_user_dialog_menu);

        //Logica de cierre de dialog
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        //Logica de menu de item
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_guardar_usuario) {
                    Toast.makeText(getContext(), "Usuario guardado", Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });

        //Poblar spinner
        genreSpinner = v.findViewById(R.id.spinner_genre);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(Objects.requireNonNull(getActivity()),
                        R.array.genre,
                        android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(adapter);

        genreSpinner.setSelection(0, true);


        return v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.new_user_dialog, null);
        builder.setView(v);

        return builder.create();
    }


}
