package com.dn_alan.myapplication;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.dn_alan.myapplication.os.DnAMSCheckEngine;
import com.dn_alan.myapplication.os.DnActivityThread;

import java.lang.reflect.InvocationTargetException;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            DnAMSCheckEngine.mHookAMS(this);
        } catch (Exception e) {
            Toast.makeText(this,"mHookAMS 失败:"+e.getMessage(),Toast.LENGTH_SHORT).show();
            Log.e("mHookAMS", "mHookAMS 失败:"+e.getMessage(),e );
            e.printStackTrace();
        }

        try {
            DnActivityThread dnActivityThread = new DnActivityThread(this);
            dnActivityThread.mActivityThreadmHAction(this);
        } catch (Exception e) {
            Toast.makeText(this,"mActivityThreadmHAction 失败:"+e.getMessage(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Log.e("mActivityThreadmHAction", "mActivityThreadmHAction 失败:"+e.getMessage(),e );
        }
    }
}
