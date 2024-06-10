package com.jmormar.opentasker;

import android.app.Application;

import com.jmormar.opentasker.logging.OpentaskerExceptionHandler;
import com.jmormar.opentasker.logging.ReleaseTree;
import com.yariksoffice.lingver.Lingver;

import timber.log.Timber;

public class OpentaskerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Lingver.init(this, "es");

        Timber.plant(new ReleaseTree(this));
        Thread.setDefaultUncaughtExceptionHandler(new OpentaskerExceptionHandler(this));
    }
}
