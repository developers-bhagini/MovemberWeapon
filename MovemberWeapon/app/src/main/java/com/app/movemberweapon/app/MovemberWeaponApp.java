package com.app.movemberweapon.app;

import android.app.Application;
import android.graphics.Bitmap;

/**
 * Created by Aron on 08-11-2016.
 */

public class MovemberWeaponApp extends Application {

    private static Bitmap mThumbnail;

    public static Bitmap getThumbnail() {
        return mThumbnail;
    }

    public static void setThumbnail(Bitmap mThumbnail) {
        MovemberWeaponApp.mThumbnail = mThumbnail;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
