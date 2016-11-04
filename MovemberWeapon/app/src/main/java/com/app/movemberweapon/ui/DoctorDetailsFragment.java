package com.app.movemberweapon.ui;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.app.movemberweapon.R;
import com.app.movemberweapon.util.Constants;


public class DoctorDetailsFragment extends Fragment implements View.OnClickListener {


    private Bundle mBundle;
    private View rootView;
    private EditText doctorNameText;
    private EditText doctorSpecialityText;
    private EditText doctorLocationText;
    private Button submitButton;
    private PopupWindow mPopup_window;
    private Button mMenuButton;


    public DoctorDetailsFragment() {
        // Required empty public constructor
    }

    public static DoctorDetailsFragment newInstance() {
        DoctorDetailsFragment fragment = new DoctorDetailsFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBundle = getArguments();
        rootView = inflater.inflate(R.layout.fragment_doctor_details, container, false);
        mMenuButton = (Button) rootView.findViewById(R.id.menu_button);
        mMenuButton.setOnClickListener(this);
        doctorNameText = (EditText) rootView.findViewById(R.id.doctor_name);
        doctorSpecialityText = (EditText) rootView.findViewById(R.id.speciality);
        doctorLocationText = (EditText) rootView.findViewById(R.id.location);
        submitButton = (Button) rootView.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_button:
                String doctorName = doctorNameText.getText().toString().trim();
                if (TextUtils.isEmpty(doctorName)) {
                    doctorNameText.setError("Name cannot be blank");
                    return;
                }
                String doctorSpeciality = doctorSpecialityText.getText().toString().trim();
                if (TextUtils.isEmpty(doctorSpeciality)) {
                    doctorSpecialityText.setError("Speciality cannot be blank");
                    return;
                }
                String doctorLocation = doctorLocationText.getText().toString().trim();
                if (TextUtils.isEmpty(doctorLocation)) {
                    doctorLocationText.setError("Location cannot be blank");
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString(Constants.DOCTOR_NAME, doctorName);
                bundle.putString(Constants.DOCTOR_SPECIALITY, doctorSpeciality);
                bundle.putString(Constants.DOCTOR_LOCATION, doctorLocation);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.slide_in_from_right, R.animator.slide_out_towards_left, R.animator.slide_in_from_leftt, R.animator.slide_out_towards_right);
                OpenCameraFragment openCameraFragment = OpenCameraFragment.newInstance();
                openCameraFragment.setArguments(bundle);
                ft.addToBackStack("DoctorPledgeFragment");
                ft.replace(R.id.container, openCameraFragment).commit();
                break;
            case R.id.menu_button:
                mPopup_window = popupDisplay();
                mPopup_window.showAsDropDown(view, -40, 18);
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
    public void onResume() {
        super.onResume();
        doctorNameText.setText("");
        doctorLocationText.setText("");
        doctorSpecialityText.setText("");
    }

    @Override
    public void onDestroyView() {
        //mContainer.removeAllViews();
        ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.container);
        mContainer.removeAllViews();
        super.onDestroyView();
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


}
