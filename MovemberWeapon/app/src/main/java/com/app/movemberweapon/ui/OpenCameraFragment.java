package com.app.movemberweapon.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.movemberweapon.R;


public class OpenCameraFragment extends Fragment implements View.OnClickListener {
    private View mRootView;
    private ImageView mOpenCamera;
    public OpenCameraFragment() {
        // Required empty public constructor
    }


    public static OpenCameraFragment newInstance() {
        OpenCameraFragment fragment = new OpenCameraFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.open_camera_fragment, container,false);
        mOpenCamera= (ImageView) mRootView.findViewById(R.id.open_camera_id);
        mOpenCamera.setOnClickListener(this);
        return mRootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.open_camera_id:
                FragmentTransaction lTransaction=getFragmentManager().beginTransaction();
                lTransaction.replace(R.id.container,CaptureCameraFragment.newInstance());
                lTransaction.commitAllowingStateLoss();
                break;
        }
    }
}
