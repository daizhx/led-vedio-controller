package demo.com.ledvediocontroller.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import demo.com.ledvediocontroller.Constants;
import demo.com.ledvediocontroller.R;

public class SetLightDialogFragment extends DialogFragment {
    AppCompatSeekBar lightSeekBar;

    private OnSetLightListener onSetLightListener;

    public interface OnSetLightListener {
        void sendCommand(byte[] command);
    }

    public OnSetLightListener getOnSetLightListener() {
        return onSetLightListener;
    }

    public void setOnSetLightListener(OnSetLightListener onSetLightListener) {
        this.onSetLightListener = onSetLightListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.set_light_bar,null);
        lightSeekBar = view.findViewById(R.id.light_seek_bar);
        lightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                getDialog().setTitle(getResources().getString(R.string.light)+i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

//        builder.setView(R.layout.set_light_bar);
        builder.setView(view);
        builder.setTitle(getResources().getString(R.string.light) + lightSeekBar.getProgress());

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //确定
                int v = lightSeekBar.getProgress();
                if(onSetLightListener != null){
                    onSetLightListener.sendCommand(Constants.getLightCommand(v));
                }


            }
        });

        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //取消
                SetLightDialogFragment.this.getDialog().cancel();
            }
        });


        return builder.create();
    }
}
