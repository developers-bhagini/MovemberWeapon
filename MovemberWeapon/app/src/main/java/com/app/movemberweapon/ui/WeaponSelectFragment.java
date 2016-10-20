package com.app.movemberweapon.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.app.movemberweapon.R;
import com.app.movemberweapon.object.ImageItem;

import java.util.ArrayList;


public class WeaponSelectFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnTouchListener {
    private static final String TAG = WeaponSelectFragment.class.getSimpleName();
    private View mRootView;
    private ImageView mOkButtonImageView;
    private GridView mGridView;
    private GridViewAdaptor mGridViewAdaptor;
    private ImageView mMustacheView;
    private FrameLayout mImageContainer;
    private Bitmap thumbnail;
    private int _xDelta;
    private int _yDelta;

    public WeaponSelectFragment() {
        // Required empty public constructor
    }


    public static WeaponSelectFragment newInstance() {
        WeaponSelectFragment fragment = new WeaponSelectFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            thumbnail = (Bitmap) bundle.getParcelable("Photo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.weapon_select_fragment, container, false);
        mMustacheView = (ImageView) mRootView.findViewById(R.id.mustache_view);
        mGridView = (GridView) mRootView.findViewById(R.id.gridView);
        mGridViewAdaptor = new GridViewAdaptor(getActivity(), R.layout.grid_item_layout, getData());
        mGridView.setAdapter(mGridViewAdaptor);
        mGridView.setOnItemClickListener(this);
        mOkButtonImageView = (ImageView) mRootView.findViewById(R.id.ok_button_id);
        mOkButtonImageView.setOnClickListener(this);
        mMustacheView.setOnTouchListener(this);
        mImageContainer = (FrameLayout) mRootView.findViewById(R.id.image_containter);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageContainer.setBackground(new BitmapDrawable(getActivity().getResources(), thumbnail));

    }

    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        TypedArray lImages = getResources().obtainTypedArray(R.array.image_array_list);
        String[] lNames = getResources().getStringArray(R.array.name_array_list);
        for (int i = 0; i < lImages.length(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), lImages.getResourceId(i, -1));
            imageItems.add(new ImageItem(bitmap, lNames[i]));
        }
        return imageItems;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_button_id:
                FragmentTransaction lTransaction = getFragmentManager().beginTransaction();
                lTransaction.setCustomAnimations(R.animator.slide_in_from_right, R.animator.slide_out_towards_left, R.animator.slide_in_from_leftt, R.animator.slide_out_towards_right);
                lTransaction.addToBackStack(null);
                ShareFragment lShareFragment = ShareFragment.newInstance();
                Bundle lBundle = new Bundle();
                mImageContainer.setDrawingCacheEnabled(true);
                mImageContainer.buildDrawingCache();
                Bitmap bitmap = mImageContainer.getDrawingCache();
                lBundle.putParcelable("Photo", bitmap.copy(bitmap.getConfig(),true));
                lShareFragment.setArguments(lBundle);
                lTransaction.replace(R.id.container, lShareFragment);
                lTransaction.commitAllowingStateLoss();
                break;

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick :: " + position);
        ImageItem lSelectedItem = (ImageItem) parent.getAdapter().getItem(position);
        mMustacheView.setImageBitmap(lSelectedItem.getImage());
        mMustacheView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) v.getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) v
                        .getLayoutParams();
                layoutParams.leftMargin = X - _xDelta;
                layoutParams.topMargin = Y - _yDelta;
                mMustacheView.setLayoutParams(layoutParams);
                break;
        }
        mMustacheView.invalidate();
        return true;
    }
}
