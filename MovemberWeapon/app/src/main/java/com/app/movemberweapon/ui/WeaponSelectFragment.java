package com.app.movemberweapon.ui;

import android.app.Fragment;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.movemberweapon.R;
import com.app.movemberweapon.object.ImageItem;

import java.util.ArrayList;


public class WeaponSelectFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = WeaponSelectFragment.class.getSimpleName();
    private View mRootView;
    private ImageView mOkButtonImageView;
    private GridView mGridView;
    private GridViewAdaptor mGridViewAdaptor;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.weapon_select_fragment, container, false);
        mGridView = (GridView) mRootView.findViewById(R.id.gridView);
        mGridViewAdaptor = new GridViewAdaptor(getActivity(), R.layout.grid_item_layout, getData());
        mGridView.setAdapter(mGridViewAdaptor);
        mGridView.setOnItemClickListener(this);
        mOkButtonImageView = (ImageView) mRootView.findViewById(R.id.ok_button_id);
        mOkButtonImageView.setOnClickListener(this);
        return mRootView;
    }


    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        TypedArray lImages = getResources().obtainTypedArray(R.array.image_array_list);
        String[] lNames = getResources().getStringArray(R.array.name_array_list);
        for (int i = 0; i < lImages.length(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), lImages.getResourceId(i, -1));
            imageItems.add(new ImageItem(bitmap,lNames[i]));
        }
        return imageItems;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_button_id:
                break;

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick :: " + position);
        Toast.makeText(getActivity(),"onItemClick "+position,Toast.LENGTH_SHORT).show();
    }
}
