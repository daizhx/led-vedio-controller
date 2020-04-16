package demo.com.ledvediocontroller.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import demo.com.ledvediocontroller.Constants;
import demo.com.ledvediocontroller.R;
import demo.com.ledvediocontroller.SendCommandResult;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignalSourceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignalSourceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignalSourceFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;
    private SendCommandResult sendCommandResult;
    View layerAView;
    View layerBView;
    View layerCView;
    View layerDView;
    private Button layerAbtn1;
    private Button layerAbtn2;
    private Button layerBbtn1;
    private Button layerBbtn2;
    private Button layerCbtn1;
    private Button layerCbtn2;
    private Button layerDbtn1;
    private Button layerDbtn2;

    private int layer;
    private final int LAYERA = 1;
    private final int LAYERB = 2;
    private final int LAYERC = 3;
    private final int LAYERD = 4;

    public SignalSourceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SignalSourceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignalSourceFragment newInstance() {
        SignalSourceFragment fragment = new SignalSourceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_signal_source, container, false);
        v.findViewById(R.id.btn_cv).setOnClickListener(SignalSourceFragment.this);
        v.findViewById(R.id.btn_vga).setOnClickListener(SignalSourceFragment.this);
        v.findViewById(R.id.btn_dp1).setOnClickListener(SignalSourceFragment.this);
        v.findViewById(R.id.btn_dp2).setOnClickListener(SignalSourceFragment.this);
        v.findViewById(R.id.btn_hdm1_1).setOnClickListener(SignalSourceFragment.this);
        v.findViewById(R.id.btn_hdm1_2).setOnClickListener(SignalSourceFragment.this);
        v.findViewById(R.id.btn_dvi).setOnClickListener(SignalSourceFragment.this);
        v.findViewById(R.id.btn_sdi).setOnClickListener(SignalSourceFragment.this);
        v.findViewById(R.id.btn_ext).setOnClickListener(SignalSourceFragment.this);

        layerAView = v.findViewById(R.id.layer_a);
        TextView tvLayerA = layerAView.findViewById(R.id.tv_layer);
        tvLayerA.setText("LayerA");
        layerAbtn1 = layerAView.findViewById(R.id.btn_1);
        layerAbtn2 = layerAView.findViewById(R.id.btn_2);

        tvLayerA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableLayerA();
            }
        });
        layerAbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(Constants.AImage);
            }
        });
        layerAbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(Constants.ATOGGLE);
            }
        });


        layerBView = v.findViewById(R.id.layer_b);
        TextView tvLayerB = layerBView.findViewById(R.id.tv_layer);
        tvLayerB.setText("LayerB");
        layerBbtn1 = layerBView.findViewById(R.id.btn_1);
        layerBbtn2 = layerBView.findViewById(R.id.btn_2);

        tvLayerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableLayerB();
            }
        });
        layerBbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(Constants.BImage);
            }
        });
        layerBbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(Constants.BTOGGLE);
            }
        });

        layerCView = v.findViewById(R.id.layer_c);
        TextView tvLayerC = layerCView.findViewById(R.id.tv_layer);
        tvLayerC.setText("LayerC");
        layerCbtn1 = layerCView.findViewById(R.id.btn_1);
        layerCbtn2 = layerCView.findViewById(R.id.btn_2);

        tvLayerC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableLayerC();
            }
        });

        layerCbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(Constants.CImage);
            }
        });

        layerCbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(Constants.CTOGGLE);
            }
        });



        layerDView = v.findViewById(R.id.layer_d);
        TextView tvLayerD = layerDView.findViewById(R.id.tv_layer);
        tvLayerD.setText("LayerD");
        layerDbtn1 = layerDView.findViewById(R.id.btn_1);
        layerDbtn2 = layerDView.findViewById(R.id.btn_2);

        tvLayerD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableLayerD();
            }
        });

        layerDbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(Constants.DImage);
            }
        });

        layerDbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(Constants.DTOGGLE);
            }
        });

        layerSelected();

        return v;
    }

    private void layerSelected() {
        switch (layer){
            case LAYERA:
                setSelectedLayerA(true);
                setSelectedLayerB(false);
                setSelectedLayerC(false);
                setSelectedLayerD(false);
                break;
            case LAYERB:
                setSelectedLayerA(false);
                setSelectedLayerB(true);
                setSelectedLayerC(false);
                setSelectedLayerD(false);
                break;

            case LAYERC:
                setSelectedLayerA(false);
                setSelectedLayerB(false);
                setSelectedLayerC(true);
                setSelectedLayerD(false);
                break;
            case LAYERD:
                setSelectedLayerA(false);
                setSelectedLayerB(false);
                setSelectedLayerC(true);
                setSelectedLayerD(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnSetLightListener");
        }

    }

    private void enableLayerA(){
        sendCommand(Constants.LayerA, new SendCommandResult() {
            @Override
            public void sendCommandSuccess() {
                setSelectedLayerA(true);
                setSelectedLayerB(false);
                setSelectedLayerC(false);
                setSelectedLayerD(false);

                layer = LAYERA;
            }

            @Override
            public void sendCommandFail() {

            }
        });

    }

    private void enableLayerB(){
        sendCommand(Constants.LayerB, new SendCommandResult() {
            @Override
            public void sendCommandSuccess() {
                setSelectedLayerA(false);
                setSelectedLayerB(true);
                setSelectedLayerC(false);
                setSelectedLayerD(false);

                layer = LAYERB;
            }

            @Override
            public void sendCommandFail() {

            }
        });


    }

    private void enableLayerC(){
        sendCommand(Constants.LayerC, new SendCommandResult() {
            @Override
            public void sendCommandSuccess() {
                setSelectedLayerA(false);
                setSelectedLayerB(false);
                setSelectedLayerC(true);
                setSelectedLayerD(false);

                layer = LAYERC;
            }

            @Override
            public void sendCommandFail() {

            }
        });


    }

    private void enableLayerD(){
        sendCommand(Constants.LayerD, new SendCommandResult() {
            @Override
            public void sendCommandSuccess() {
                setSelectedLayerA(false);
                setSelectedLayerB(false);
                setSelectedLayerC(false);
                setSelectedLayerD(true);

                layer = LAYERD;
            }

            @Override
            public void sendCommandFail() {

            }
        });


    }

    private void setSelectedLayerA(boolean b){
        layerAView.setSelected(b);
        layerAbtn1.setEnabled(b);
        layerAbtn2.setEnabled(b);
    }

    private void setSelectedLayerB(boolean b){
        layerBView.setSelected(b);
        layerBbtn1.setEnabled(b);
        layerBbtn2.setEnabled(b);
    }

    private void setSelectedLayerC(boolean b){
        layerCView.setSelected(b);
        layerCbtn1.setEnabled(b);
        layerCbtn2.setEnabled(b);
    }

    private void setSelectedLayerD(boolean b){
        layerDView.setSelected(b);
        layerDbtn1.setEnabled(b);
        layerDbtn2.setEnabled(b);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_cv:
                sendCommand(Constants.CV);
                break;
            case R.id.btn_vga:
                sendCommand(Constants.VGA);
                break;
            case R.id.btn_dp1:
                sendCommand(Constants.DP1);
                break;
            case R.id.btn_dp2:
                sendCommand(Constants.DP2);
                break;
            case R.id.btn_hdm1_1:
                sendCommand(Constants.HDM1);
                break;
            case R.id.btn_hdm1_2:
                sendCommand(Constants.HDM2);
                break;
            case R.id.btn_dvi:
                sendCommand(Constants.DVI);
                break;
            case R.id.btn_sdi:
                sendCommand(Constants.SDI);
                break;
            case R.id.btn_ext:
                sendCommand(Constants.EXT);
                break;


            default:
                break;
        }
    }

    private void sendCommand(byte[] c){
        if(mListener != null){
            mListener.sendCommand(c);
        }
    }

    private void sendCommand(byte[] c,SendCommandResult result){
        if(mListener != null){
            mListener.sendCommand(c,result);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void sendCommand(byte[] command);
        void sendCommand(byte[] command, SendCommandResult result);
    }
}
