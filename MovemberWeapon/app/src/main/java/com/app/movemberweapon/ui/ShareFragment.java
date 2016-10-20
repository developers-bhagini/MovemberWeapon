package com.app.movemberweapon.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.movemberweapon.R;
import com.app.movemberweapon.util.Constants;
import com.app.movemberweapon.util.NetworkUtil;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


public class ShareFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ShareFragment.class.getSimpleName();

    private View mRootView;
    private ImageView mFbShareButton;
    private Bitmap mThumbnail;
    private ImageView mPhotoView;

    private CallbackManager mCallbackManager;

    private AlertDialog mAlertDialog;
    private ProgressDialog pd;
    private LoginButton mLoginButton;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private String[] mPermissions = new String[]{"publish_actions"};
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            postToPage();
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "User cancelled ");
        }

        @Override
        public void onError(FacebookException e) {
            Toast.makeText(getActivity(), "Failed to login :: user not authorized", Toast.LENGTH_SHORT).show();

        }
    };

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != mCallbackManager) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initializeFacebookSdk();
        pd = new ProgressDialog(getActivity());
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void initView() {
        mLoginButton = (LoginButton) mRootView.findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(this);
        mLoginButton.setPublishPermissions(mPermissions);
        mLoginButton.setFragment(new NativeFragmentWrapper(this));
        mLoginButton.registerCallback(mCallbackManager, callback);
        if (null != accessTokenTracker) {
            accessTokenTracker.startTracking();
        }
        if (null != profileTracker) {
            profileTracker.startTracking();
        }
    }

    /**
     * Method to initialize the facebook sdk
     */
    private void initializeFacebookSdk() {
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        if (Constants.FACEBOOK_DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }
        mCallbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }


        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                Log.d(TAG, "profile " + newProfile);
            }
        };
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fb_share:
                if (NetworkUtil.areWeConnectedTonetwork(getActivity())) {
                    showFacebookPostConfirmation();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network_error_text), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void postToPage() {
        Bundle params = new Bundle();
        Log.d(TAG, "postToPage :: " + mThumbnail);
        params.putParcelable("source", mThumbnail);

        Log.d(TAG, "AccessToken.getCurrentAccessToken()" + AccessToken.getCurrentAccessToken());
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + Constants.PAGE_ID + "/photos",
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                        Log.d(TAG, "response :" + response);
                        if (response.getError() == null) {
                            Toast.makeText(getActivity(), "Posted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to post " + response.getError().getErrorMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        ).executeAsync();
        pd.setTitle("Please wait");
        pd.setMessage("Facebook posting is in progress..");
        pd.show();
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
    }

    private void showFacebookPostConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirmation_title_message_text);
        builder.setCancelable(false);
        builder.setMessage(R.string.confirmation_dialog_message_text)
                .setPositiveButton(R.string.yes_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (isLoggedIn()) {
                            postToPage();
                        } else {
                            mLoginButton = (LoginButton) mRootView.findViewById(R.id.login_button);
                            mLoginButton.performClick();
                        }
                    }
                })
                .setNegativeButton(R.string.no_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (null != mAlertDialog) {
                            mAlertDialog.dismiss();
                        }
                    }
                });
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    public static class NativeFragmentWrapper extends android.support.v4.app.Fragment {
        private Fragment nativeFragment = null;

        public NativeFragmentWrapper(Fragment nativeFragment) {
            this.nativeFragment = nativeFragment;
        }

        public NativeFragmentWrapper() {
        }

        @Override
        public void startActivityForResult(Intent intent, int requestCode) {
            nativeFragment.startActivityForResult(intent, requestCode);
        }

        @Override
        public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
            nativeFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
