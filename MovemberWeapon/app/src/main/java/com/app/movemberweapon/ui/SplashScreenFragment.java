package com.app.movemberweapon.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.app.movemberweapon.R;
import com.app.movemberweapon.util.Constants;


public class SplashScreenFragment extends Fragment {
    private View mRootView;

    public SplashScreenFragment() {
        // Required empty public constructor
    }


    public static SplashScreenFragment newInstance() {
        SplashScreenFragment fragment = new SplashScreenFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.splash_screen_layout, container, false);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction lTransaction=getFragmentManager().beginTransaction();
                lTransaction.replace(R.id.container,OpenCameraFragment.newInstance());
                lTransaction.commitAllowingStateLoss();
            }
        }, Constants.SPLASH_TIME);
    }
}
