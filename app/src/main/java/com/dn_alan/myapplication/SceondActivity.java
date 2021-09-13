package com.dn_alan.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class SceondActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
