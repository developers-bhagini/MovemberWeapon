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
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.app.movemberweapon.util.Constants;

import java.util.ArrayList;


public class WeaponSelectFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnTouchListener {
    private static final String TAG = WeaponSelectFragment.class.getSimpleName();
    // we can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int SCALE = 2;
    private View mRootView;
    private ImageView mOkButtonImageView;
    private GridView mGridView;
    private GridViewAdaptor mGridViewAdaptor;
    private ImageView mMustacheView;
    private FrameLayout mImageContainer;
    private Bitmap thumbnail;
    private Button mMenuButton;
    private PopupWindow mPopup_window;
    private Matrix matrix = new Matrix();
    private float scale = 1f;
    // these matrices will be used to move and zoom image
    private Matrix savedMatrix = new Matrix();
    private int mode = NONE;
    // remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    private float[] lastEvent = null;
    private Bundle mBundle;

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
        mBundle=new Bundle();
        if (bundle != null) {
            thumbnail = (Bitmap) bundle.getParcelable("Photo");
            mBundle.putString(Constants.DOCTOR_NAME, bundle.getString(Constants.DOCTOR_NAME));
            mBundle.putString(Constants.DOCTOR_SPECIALITY, bundle.getString(Constants.DOCTOR_SPECIALITY));
            mBundle.putString(Constants.DOCTOR_LOCATION, bundle.getString(Constants.DOCTOR_LOCATION));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        mMustacheView.setOnTouchListener(this);
        mImageContainer = (FrameLayout) mRootView.findViewById(R.id.image_containter);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mImageContainer) {
            mImageContainer.setBackground(new BitmapDrawable(getActivity().getResources(), thumbnail));
        }

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

                mImageContainer.setDrawingCacheEnabled(true);
                mImageContainer.buildDrawingCache();
                Bitmap bitmap = mImageContainer.getDrawingCache();
                mBundle.putParcelable("Photo", bitmap.copy(bitmap.getConfig(), true));
                lShareFragment.setArguments(mBundle);
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
                lTranscation.replace(R.id.container, DoctorDetailsFragment.newInstance()).commit();
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
        ImageItem lSelectedItem = (ImageItem) parent.getAdapter().getItem(position);
        mMustacheView.setImageBitmap(lSelectedItem.getImage());
        mMustacheView.setVisibility(View.VISIBLE);
    }

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

    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = SCALE;
                }
                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                d = rotation(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;
                    matrix.postTranslate(dx, dy);
                } else if (mode == SCALE) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = (newDist / oldDist);
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                    if (lastEvent != null && event.getPointerCount() == 3) {
                        newRot = rotation(event);
                        float r = newRot - d;
                        float[] values = new float[9];
                        matrix.getValues(values);
                        float tx = values[2];
                        float ty = values[5];
                        float sx = values[0];
                        float xc = (view.getWidth() / 2) * sx;
                        float yc = (view.getHeight() / 2) * sx;
                        matrix.postRotate(r, tx + xc, ty + yc);
                    }
                }
                break;
        }
        view.setImageMatrix(matrix);
        return true;
    }


    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Calculate the degree to be rotated by.
     *
     * @param event
     * @return Degrees
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }
}
