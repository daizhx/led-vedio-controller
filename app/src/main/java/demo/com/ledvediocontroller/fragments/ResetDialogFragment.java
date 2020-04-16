package demo.com.ledvediocontroller.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import demo.com.ledvediocontroller.Constants;
import demo.com.ledvediocontroller.R;

public class ResetDialogFragment extends DialogFragment {

    private OnResetListener onResetListener;

    public interface OnResetListener {
        void sendCommand(byte[] command);
    }

    public OnResetListener getOnResetListener() {
        return onResetListener;
    }

    public void setOnResetListener(OnResetListener onResetListener) {
        this.onResetListener = onResetListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.reset_alert);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(onResetListener != null){
                    onResetListener.sendCommand(Constants.RESET);
                }
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ResetDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }
}
