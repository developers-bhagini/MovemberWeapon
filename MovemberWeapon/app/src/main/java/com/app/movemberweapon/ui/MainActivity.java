package com.app.movemberweapon.ui;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.app.movemberweapon.R;
import com.app.movemberweapon.util.Constants;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar lActionBar = getSupportActionBar();
        if (null != lActionBar) {
            lActionBar.hide();
        }
        
        FragmentTransaction lTransaction=getFragmentManager().beginTransaction();
        lTransaction.replace(R.layout.splash_screen_layout,)
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, Constants.SPLASH_TIME);
    }
}
