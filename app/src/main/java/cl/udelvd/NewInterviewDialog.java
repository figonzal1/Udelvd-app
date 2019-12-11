package cl.udelvd;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

public class NewInterviewDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.new_interview_dialog,container,false);
        setCancelable(false);

        //Icono Cerrar dialog
        Toolbar toolbar = v.findViewById(R.id.toolbar_dialog_interview);
        toolbar.setTitle("Crear Entrevista");
        toolbar.inflateMenu(R.menu.menu_guardar_datos);

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
                if (item.getItemId() == R.id.menu_guardar) {
                    Toast.makeText(getContext(), "Entrevista guardada", Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });

        Button btn = v.findViewById(R.id.btn_date_picker);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(v);
            }
        });


        return v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.new_interview_dialog, null);
        builder.setView(v);

        return builder.create();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerDialogFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
}
