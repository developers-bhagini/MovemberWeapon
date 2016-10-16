package com.app.movemberweapon.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.movemberweapon.R;


public class PreviewCameraFragment extends Fragment implements View.OnClickListener {
    private View mRootView;
    private ImageView mRetakeButton;
    private ImageView mProceedButton;

    public PreviewCameraFragment() {
        // Required empty public constructor
    }


    public static PreviewCameraFragment newInstance() {
        PreviewCameraFragment fragment = new PreviewCameraFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.preview_camera_fragment, container, false);
        mRetakeButton = (ImageView) mRootView.findViewById(R.id.retake_id);
        mProceedButton = (ImageView) mRootView.findViewById(R.id.proceed_id);
        mRetakeButton.setOnClickListener(this);
        mProceedButton.setOnClickListener(this);
        return mRootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retake_id:
                //todo:
                break;
            case R.id.proceed_id:
                FragmentTransaction lTransaction = getFragmentManager().beginTransaction();
                lTransaction.replace(R.id.container, WeaponSelectFragment.newInstance());
                lTransaction.commitAllowingStateLoss();
                break;
        }
    }
}
