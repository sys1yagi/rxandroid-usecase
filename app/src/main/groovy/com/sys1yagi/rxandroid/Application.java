package com.sys1yagi.rxandroid;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

public class Application extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        try {
            super.attachBaseContext(base);
        } catch (Exception e) {
            String vmName = System.getProperty("java.vm.name");
            if (!vmName.startsWith("Java")) {
                throw e;
            }
        }
    }
}
