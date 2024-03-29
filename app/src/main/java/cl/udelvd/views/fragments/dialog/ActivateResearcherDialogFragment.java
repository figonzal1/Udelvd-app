package cl.udelvd.views.fragments.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

import cl.udelvd.R;
import cl.udelvd.models.Researcher;

public class ActivateResearcherDialogFragment extends DialogFragment {

    private final Researcher researcher;
    private final boolean activated;
    private ActivateAccountDialogListener listener;
    private final SwitchMaterial switchMaterial;

    public ActivateResearcherDialogFragment(Researcher researcher, boolean activated, SwitchMaterial switchMaterial) {

        this.researcher = researcher;
        this.activated = activated;
        this.switchMaterial = switchMaterial;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        if (activated) {

            builder.setMessage(getString(R.string.DIALOG_ACTIVACION_MENSAJE));
            builder.setPositiveButton(getString(R.string.DIALOG_ACTIVAR), (dialog, id) -> {

                researcher.setActivated(true);
                switchMaterial.setChecked(true);
                switchMaterial.setText(getString(R.string.PERFIL_ACTIVADO));

                listener.onDialogPositiveClick(researcher, researcher.isActivated());

                dialog.dismiss();
            });

            builder.setNegativeButton(getString(R.string.DIALOG_NEGATIVE_BTN), (dialog, id) -> {

                switchMaterial.setChecked(false);
                switchMaterial.setText(getString(R.string.PERFIL_NO_ACTIVADO));
                dismiss();
            });

        } else {

            builder.setMessage(getString(R.string.DIALOG_DESACTIVACION_MENSAJE));
            builder.setPositiveButton(getString(R.string.DIALOG_DESACTIVAR), (dialog, id) -> {

                researcher.setActivated(false);
                switchMaterial.setChecked(false);
                switchMaterial.setText(getString(R.string.PERFIL_NO_ACTIVADO));
                listener.onDialogPositiveClick(researcher, researcher.isActivated());

                dialog.dismiss();
            });

            builder.setNegativeButton(getString(R.string.DIALOG_NEGATIVE_BTN), (dialog, id) -> {

                switchMaterial.setChecked(true);
                switchMaterial.setText(getString(R.string.PERFIL_ACTIVADO));
                dismiss();
            });
        }
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (ActivateAccountDialogListener) context;
    }

    @Override
    public void onDestroyView() {

        if (getDialog() != null && getRetainInstance()) {

            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }
}
