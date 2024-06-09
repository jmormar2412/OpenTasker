package com.jmormar.opentasker;

import android.app.Application;

import com.yariksoffice.lingver.Lingver;

public class OpentaskerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Lingver.init(this, "es");
    }
}
