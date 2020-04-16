package demo.com.ledvediocontroller.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import demo.com.ledvediocontroller.Constants;
import demo.com.ledvediocontroller.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PreSettingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PreSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreSettingFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;

    public PreSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment PreSettingFragment.
     */
    public static PreSettingFragment newInstance() {
        PreSettingFragment fragment = new PreSettingFragment();
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
        View v = inflater.inflate(R.layout.fragment_pre_setting, container, false);
        v.findViewById(R.id.btn_m1).setOnClickListener(this);
        v.findViewById(R.id.btn_m2).setOnClickListener(this);
        v.findViewById(R.id.btn_m3).setOnClickListener(this);
        v.findViewById(R.id.btn_m4).setOnClickListener(this);
        v.findViewById(R.id.btn_m5).setOnClickListener(this);
        v.findViewById(R.id.btn_m6).setOnClickListener(this);
        v.findViewById(R.id.btn_m7).setOnClickListener(this);
        v.findViewById(R.id.btn_m8).setOnClickListener(this);
        v.findViewById(R.id.btn_m9).setOnClickListener(this);
        v.findViewById(R.id.btn_m10).setOnClickListener(this);
        return v;
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(mListener == null){
            return;
        }
        switch (id){
            case R.id.btn_m1:
                mListener.sendCommand(Constants.M1);
                break;
            case R.id.btn_m2:
                mListener.sendCommand(Constants.M2);
                break;
            case R.id.btn_m3:
                mListener.sendCommand(Constants.M3);
                break;
            case R.id.btn_m4:
                mListener.sendCommand(Constants.M4);
                break;
            case R.id.btn_m5:
                mListener.sendCommand(Constants.M5);
                break;
            case R.id.btn_m6:
                mListener.sendCommand(Constants.M6);
                break;
            case R.id.btn_m7:
                mListener.sendCommand(Constants.M7);
                break;
            case R.id.btn_m8:
                mListener.sendCommand(Constants.M8);
                break;
            case R.id.btn_m9:
                mListener.sendCommand(Constants.M9);
                break;
            case R.id.btn_m10:
                mListener.sendCommand(Constants.M10);
                break;
                default:
                    break;

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
    }
}
