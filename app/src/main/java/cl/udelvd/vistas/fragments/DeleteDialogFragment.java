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
import cl.udelvd.modelo.Entrevista;

public class DeleteDialogFragment extends DialogFragment {

    private Entrevista entrevista;
    private DeleteDialogListener listener;

    public DeleteDialogFragment(Entrevista entrevista) {
        this.entrevista = entrevista;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setMessage(getString(R.string.DIALOG_MESSAGE))
                .setPositiveButton(getString(R.string.DIALOG_POSITIVE_BTN), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogPositiveClick(DeleteDialogFragment.this, entrevista);
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

    public interface DeleteDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, Entrevista entrevista);
    }
}
