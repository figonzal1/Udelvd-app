package cl.udelvd.vistas.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.modelo.Investigador;

public class ActivarInvestigadorDialogFragment extends DialogFragment {

    private final Investigador investigador;
    private DeleteDialogListener listener;
    private final boolean activar;
    private SwitchMaterial switchMaterial;

    public ActivarInvestigadorDialogFragment(Investigador investigador, boolean activar, SwitchMaterial switchMaterial) {
        this.investigador = investigador;
        this.activar = activar;
        this.switchMaterial = switchMaterial;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        if (activar) {
            builder.setMessage(getString(R.string.DIALOG_ACTIVACION_MENSAJE));
            builder.setPositiveButton(getString(R.string.DIALOG_ACTIVAR), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    investigador.setActivado(true);
                    switchMaterial.setChecked(true);
                    switchMaterial.setText(getString(R.string.PERFIL_ACTIVADO));

                    listener.onDialogPositiveClick(investigador);

                    dialog.dismiss();
                }
            });

            builder.setNegativeButton(getString(R.string.DIALOG_NEGATIVE_BTN), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    switchMaterial.setChecked(false);
                    switchMaterial.setText(getString(R.string.PERFIL_NO_ACTIVADO));
                    dismiss();
                }
            });

        } else {
            builder.setMessage(getString(R.string.DIALOG_DESACTIVACION_MENSAJE));
            builder.setPositiveButton(getString(R.string.DIALOG_DESACTIVAR), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    investigador.setActivado(false);
                    switchMaterial.setChecked(false);
                    switchMaterial.setText(getString(R.string.PERFIL_NO_ACTIVADO));
                    listener.onDialogPositiveClick(investigador);

                    dialog.dismiss();
                }
            });

            builder.setNegativeButton(getString(R.string.DIALOG_NEGATIVE_BTN), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    switchMaterial.setChecked(true);
                    switchMaterial.setText(getString(R.string.PERFIL_ACTIVADO));
                    dismiss();
                }
            });
        }
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (DeleteDialogListener) context;
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }
}
