package com.app.movemberweapon.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.movemberweapon.R;


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
        mRootView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                FragmentTransaction lTransaction = getFragmentManager().beginTransaction();
                lTransaction.setCustomAnimations(R.animator.slide_in_from_right, R.animator.slide_out_towards_left, R.animator.slide_in_from_leftt, R.animator.slide_out_towards_right);
                lTransaction.replace(R.id.container, DoctorDetailsFragment.newInstance());
                //lTransaction.addToBackStack(null);
                lTransaction.commitAllowingStateLoss();
            }
        });
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getActivity(), R.string.swipe_right_information_text, Toast.LENGTH_LONG).show();
/*        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction lTransaction = getFragmentManager().beginTransaction();
                lTransaction.setCustomAnimations(R.animator.slide_in_from_right, R.animator.slide_out_towards_left, R.animator.slide_in_from_leftt, R.animator.slide_out_towards_right);
                lTransaction.replace(R.id.container, DoctorDetailsFragment.newInstance());
                //lTransaction.addToBackStack(null);
                lTransaction.commitAllowingStateLoss();
            }
        }, Constants.SPLASH_TIME);*/
    }
}
