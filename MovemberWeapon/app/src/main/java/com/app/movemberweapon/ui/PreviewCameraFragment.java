package com.app.movemberweapon.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.movemberweapon.R;


public class PreviewCameraFragment extends Fragment implements View.OnClickListener {
    private View mRootView;
    private ImageView mManImage;
    private ImageView mPhotoPreview;
    private ImageView mRetakeButton;
    private ImageView mProceedButton;
    Bitmap thumbnail;
    private Bundle mBundle;

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
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            thumbnail = (Bitmap)bundle.getParcelable("Photo");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBundle = new Bundle();
        mRootView = inflater.inflate(R.layout.preview_camera_fragment, container, false);
        mManImage =(ImageView)mRootView.findViewById(R.id.man_image);
        mPhotoPreview=(ImageView)mRootView.findViewById(R.id.capture_demo_screen);
        mRetakeButton = (ImageView) mRootView.findViewById(R.id.retake_id);
        mProceedButton = (ImageView) mRootView.findViewById(R.id.proceed_id);
        mRetakeButton.setOnClickListener(this);
        mProceedButton.setOnClickListener(this);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPhotoPreview.setImageBitmap(thumbnail);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retake_id:
                //todo:
                break;
            case R.id.proceed_id:
                mBundle.putParcelable("Photo", thumbnail);
                WeaponSelectFragment weaponFragment=new WeaponSelectFragment();
                weaponFragment.setArguments(mBundle);
                FragmentTransaction lTransaction = getFragmentManager().beginTransaction();
                lTransaction.replace(R.id.container, WeaponSelectFragment.newInstance());
                lTransaction.commitAllowingStateLoss();
                break;
        }
    }
}
