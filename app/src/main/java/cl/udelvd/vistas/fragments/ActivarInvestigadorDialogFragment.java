package cl.udelvd.vistas.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.modelo.Investigador;

public class ActivarInvestigadorDialogFragment extends DialogFragment {

    private Investigador investigador;
    private DeleteDialogListener listener;
    private boolean activar;

    public ActivarInvestigadorDialogFragment(Investigador investigador, boolean activar) {
        this.investigador = investigador;
        this.activar = activar;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        if (activar) {
            builder.setMessage("La activación de cuenta concederá al investigador acceso al sistema , ¿Está seguro?");
            builder.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    listener.onDialogPositiveClick(ActivarInvestigadorDialogFragment.this, investigador);
                }
            });
        } else {
            builder.setMessage("La desactivación de cuenta prohibirá al investigador acceso al sistema , ¿Está seguro?");
            builder.setPositiveButton("Desactivar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    listener.onDialogPositiveClick(ActivarInvestigadorDialogFragment.this, investigador);
                }
            });
        }

        builder.setNegativeButton(getString(R.string.DIALOG_NEGATIVE_BTN), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dismiss();
            }
        });
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (DeleteDialogListener) context;
    }
}
