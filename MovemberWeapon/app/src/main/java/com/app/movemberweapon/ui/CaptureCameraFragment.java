package com.app.movemberweapon.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.movemberweapon.R;


public class CaptureCameraFragment extends Fragment implements View.OnClickListener {
    private View mRootView;
    private ImageView mCaptureView;

    public CaptureCameraFragment() {
        // Required empty public constructor
    }


    public static CaptureCameraFragment newInstance() {
        CaptureCameraFragment fragment = new CaptureCameraFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.capture_camera_fragment, container, false);
        mCaptureView = (ImageView) mRootView.findViewById(R.id.capture_image_id);
        mCaptureView.setOnClickListener(this);
        return mRootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.capture_image_id:
                FragmentTransaction lTransaction = getFragmentManager().beginTransaction();
                lTransaction.replace(R.id.container, WeaponSelectFragment.newInstance());
                lTransaction.commitAllowingStateLoss();
                break;
        }
    }
}
