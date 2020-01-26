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
import cl.udelvd.modelo.Evento;

public class DeleteEventoDialogFragment extends DialogFragment {

    private Evento evento;
    private DeleteDialogListener listener;

    public DeleteEventoDialogFragment(DeleteDialogListener listener, Evento evento) {
        this.evento = evento;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setMessage("¿Estas seguro que quieres eliminar este evento?")
                .setPositiveButton(getString(R.string.DIALOG_POSITIVE_BTN), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogPositiveClick(DeleteEventoDialogFragment.this, evento);
                    }
                })
                .setNegativeButton(getString(R.string.DIALOG_NEGATIVE_BTN), new DialogInterface.OnClickListener() {
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
