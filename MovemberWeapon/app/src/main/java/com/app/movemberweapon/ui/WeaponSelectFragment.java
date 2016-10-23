package com.app.movemberweapon.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.app.movemberweapon.R;
import com.app.movemberweapon.object.ImageItem;
import com.app.movemberweapon.util.ScaleGestureDetectorCompat;

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
    private Button mMenuButton;
    private PopupWindow mPopup_window;

    private Matrix matrix = new Matrix();
    private float scale = 1f;
    private ScaleGestureDetector SGD;

    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetectorCompat mGestureDetector;

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
        //SGD = new ScaleGestureDetector(getActivity(),new ScaleListener());
        mScaleGestureDetector = new ScaleGestureDetector(getActivity(), mScaleGestureListener);
        mGestureDetector = new GestureDetectorCompat(getActivity(), mGestureListener);
        mRootView = inflater.inflate(R.layout.weapon_select_fragment, container, false);
        mMenuButton = (Button) mRootView.findViewById(R.id.menu_button);
        mMenuButton.setOnClickListener(this);
        mMustacheView = (ImageView) mRootView.findViewById(R.id.mustache_view);
        mGridView = (GridView) mRootView.findViewById(R.id.gridView);
        mGridViewAdaptor = new GridViewAdaptor(getActivity(), R.layout.grid_item_layout, getData());
        mGridView.setAdapter(mGridViewAdaptor);
        mGridView.setOnItemClickListener(this);
        mOkButtonImageView = (ImageView) mRootView.findViewById(R.id.ok_button_id);
        mOkButtonImageView.setOnClickListener(this);
        mMustacheView.setOnTouchListener(new PrivateOnTouchListener());
        mImageContainer = (FrameLayout) mRootView.findViewById(R.id.image_containter);
        mImageContainer.setOnTouchListener(this);
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
            case R.id.menu_button:
                mPopup_window = popupDisplay();
                mPopup_window.showAsDropDown(v, -40, 18);
                break;
            case R.id.home_button:
                if (null != mPopup_window) {
                    mPopup_window.dismiss();
                }
                FragmentTransaction lTranscation = getFragmentManager().beginTransaction();
                lTranscation.setCustomAnimations(R.animator.slide_in_from_right, R.animator.slide_out_towards_left, R.animator.slide_in_from_leftt, R.animator.slide_out_towards_right);
                lTranscation.replace(R.id.container, OpenCameraFragment.newInstance()).commit();
                break;
            case R.id.help_button:
                if (null != mPopup_window) {
                    mPopup_window.dismiss();
                }
                Toast.makeText(getActivity(), getString(R.string.in_progress_text), Toast.LENGTH_SHORT).show();
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
        boolean retVal = mScaleGestureDetector.onTouchEvent(event);
       // retVal = mGestureDetector.onTouchEvent(event) || retVal;

        /*final int X = (int) event.getRawX();
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
        mMustacheView.invalidate();*/
        return retVal;
    }

    /*public boolean onTouchEvent(MotionEvent ev) {
        SGD.onTouchEvent(ev);
        return true;
    }*/

    public PopupWindow popupDisplay() {

        final PopupWindow popupWindow = new PopupWindow(getActivity());

        // inflate your layout or dynamically add view
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.custom_popup_layout, null);

        Button item_home = (Button) view.findViewById(R.id.home_button);
        Button item_help = (Button) view.findViewById(R.id.help_button);
        item_home.setOnClickListener(this);
        item_help.setOnClickListener(this);
        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);

        return popupWindow;
    }

    /*private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.max(0.1f, Math.min(scale, 5.0f));

            matrix.setScale(scale, scale);
            mMustacheView.setImageMatrix(matrix);
            return true;
        }
    }*/


    /**
     * The scale listener, used for handling multi-finger scale gestures.
     */
    private final ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener
            = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        private float lastSpanX;
        private float lastSpanY;
        float newWidth;
        float newHeight;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            Log.d("mtion","onScaleBegin");
            lastSpanX = ScaleGestureDetectorCompat.getCurrentSpanX(detector);
            lastSpanY = ScaleGestureDetectorCompat.getCurrentSpanY(detector);
            return super.onScaleBegin(detector);
        }

        /**
         * This is the active focal point in terms of the viewport. Could be a local
         * variable but kept here to minimize per-frame allocations.
         *
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.max(0.1f, Math.min(scale, 5.0f));
            matrix.setScale(scale, scale);
            float spanX = ScaleGestureDetectorCompat.getCurrentSpanX(detector);
            float spanY = ScaleGestureDetectorCompat.getCurrentSpanY(detector);
            newWidth = lastSpanX / spanX * mMustacheView.getWidth();
            newHeight = lastSpanY / spanY * mMustacheView.getHeight();
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            FrameLayout.LayoutParams parms = new FrameLayout.LayoutParams(Math.round(newWidth),Math.round(newHeight));
           // mMustacheView.setLayoutParams(parms);
            mMustacheView.setImageMatrix(matrix);
            //mMustacheView.setZoom(scale);
          //  mMustacheView.invalidate();
        }
    };

    /**
     * The gesture listener, used for handling simple gestures such as double touches, scrolls,
     * and flings.
     */
    private final GestureDetector.SimpleOnGestureListener mGestureListener
            = new GestureDetector.SimpleOnGestureListener() {
        private int X;
        private int Y;
        @Override
        public boolean onDown(MotionEvent e) {
            Log.d("Motion","onDown");
             /*X = (int) e.getRawX();
             Y = (int) e.getRawY();
            FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) mMustacheView.getLayoutParams();
            _xDelta = X - lParams.leftMargin;
            _yDelta = Y - lParams.topMargin;*/
            return true;
        }


        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d("Motion","onDoubleTap");
            return true;
        }


        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d("Motion","onScroll");
            /*FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mMustacheView.getLayoutParams();
            layoutParams.leftMargin = X - _xDelta;
            layoutParams.topMargin = Y - _yDelta;
            mMustacheView.setLayoutParams(layoutParams);*/
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("Motion","onFling");
            return true;
        }
    };


    private class PrivateOnTouchListener implements View.OnTouchListener {

        //
        // Remember last point position for dragging
        //
        private PointF last = new PointF();

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            PointF curr = new PointF(event.getX(), event.getY());

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        last.set(curr);
                        break;

                    case MotionEvent.ACTION_MOVE:
                            float deltaX = curr.x - last.x;
                            float deltaY = curr.y - last.y;

                        // mMustacheView.setLayoutParams(parms);
                            //float fixTransX = getFixDragTrans(deltaX, viewWidth, getImageWidth());
                            //float fixTransY = getFixDragTrans(deltaY, viewHeight, getImageHeight());
                          //  matrix.postTranslate(curr.x, curr.y);
                        matrix.setScale(deltaX, deltaY);
                            last.set(curr.x, curr.y);
                        break;

                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                }

            mMustacheView.setImageMatrix(matrix);

            return true;
        }
    }
}
