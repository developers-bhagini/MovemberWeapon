package com.app.movemberweapon.ui;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.movemberweapon.R;


public class ShareFragment extends Fragment implements View.OnClickListener {
    private View mRootView;
    private ImageView mFbShareButton;
    private Bitmap mThumbnail;
    private ImageView mPhotoView;

    public ShareFragment() {
        // Required empty public constructor
    }


    public static ShareFragment newInstance() {
        ShareFragment fragment = new ShareFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.share_fragment, container, false);
        mFbShareButton = (ImageView) mRootView.findViewById(R.id.fb_share);
        mFbShareButton.setOnClickListener(this);
        mPhotoView = (ImageView) mRootView.findViewById(R.id.white_box);
        Bundle lBundle = this.getArguments();
        if (null != lBundle) {
            mThumbnail = lBundle.getParcelable("Photo");
            mPhotoView.setImageBitmap(mThumbnail);
        }
        return mRootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fb_share:
                //todo:
                break;
        }
    }
}
