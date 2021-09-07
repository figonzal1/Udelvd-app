package cl.udelvd.views.fragments.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import cl.udelvd.R;
import cl.udelvd.models.Interviewee;

public class DeleteIntervieweeDialogFragment extends DialogFragment {

    private final Interviewee interviewee;
    private DeleteDialogListener listener;

    public DeleteIntervieweeDialogFragment(Interviewee interviewee) {
        this.interviewee = interviewee;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(getString(R.string.DIALOG_MESSAGE_ENTREVISTADO))
                .setPositiveButton(getString(R.string.DIALOG_POSITIVE_BTN), (dialog, id) -> listener.onDialogPositiveClick(interviewee))
                .setNegativeButton(getString(R.string.DIALOG_NEGATIVE_BTN), (dialog, id) -> dismiss());
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
