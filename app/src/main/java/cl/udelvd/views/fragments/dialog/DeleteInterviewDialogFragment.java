package cl.udelvd.views.fragments.dialog;

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

import cl.udelvd.R;
import cl.udelvd.models.Interview;

public class DeleteInterviewDialogFragment extends DialogFragment {

    private final Interview interview;
    private DeleteDialogListener listener;

    public DeleteInterviewDialogFragment(Interview interview) {
        this.interview = interview;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder.setMessage(getString(R.string.DIALOG_MESSAGE_ENTREVISTA))
                .setPositiveButton(getString(R.string.DIALOG_POSITIVE_BTN), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        listener.onDialogPositiveClick(interview);
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
