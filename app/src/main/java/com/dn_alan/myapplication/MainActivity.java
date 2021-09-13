package com.dn_alan.myapplication;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        try {
//            setInstrumentation();
//        } catch (Exception e) {
//            Toast.makeText(this,"setInstrumentation:"+e.getMessage(),Toast.LENGTH_SHORT).show();
//            Log.e("setInstrumentation", "setInstrumentation: "+e.getMessage(),e );
//            e.printStackTrace();
//        }
    }
    void setInstrumentation() throws NoSuchFieldException, IllegalAccessException {
//        mInstrumentation
        Field mInstrumentationField = null;
        mInstrumentationField = Activity.class.getDeclaredField("mInstrumentation");
        mInstrumentationField.setAccessible(true);
        Object mInstrumentation = mInstrumentationField.get(this);
        Instrumentation instrumentation=new Instrumentation(){
            public ActivityResult execStartActivity(
                    Context who, IBinder contextThread, IBinder token, Activity target,
                    Intent intent, int requestCode, Bundle options) {
                try {
                    Method method=Instrumentation.class.getMethod("execStartActivity"
                            ,Context.class,IBinder.class,IBinder.class,Activity.class,Intent.class,int.class,Bundle.class);
                    method.setAccessible(true);
                    return (ActivityResult) method.invoke(mInstrumentation,who,contextThread,token,target,intent,requestCode,options);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this,"execStartActivity:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    Log.e("execStartActivity", "execStartActivity: "+e.getMessage(),e );
                    return null;
                }

            }
        } ;
        mInstrumentationField.set(this,instrumentation);

    }

    @Override
    public void startActivity(Intent intent) {
        try {
            super.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this,"startActivity 失败："+e.getMessage(),Toast.LENGTH_SHORT).show();
            Log.e("startActivity",e.getMessage(),e);
            e.printStackTrace();
        }
    }

    public void jump2(View view) {
        Intent intent = new Intent(this, SceondActivity.class);
//        系统里面做了手脚   --》newIntent   msg--->obj-->intent
        startActivity(intent);
    }
    public void jump3(View view) {
        Intent intent = new Intent(this, ThreeActivity.class);
        startActivity(intent);
    }
    public void jump4(View view) {
        Intent intent = new Intent(this,ThirdActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        SharedPreferences share = this.getSharedPreferences("alan", MODE_PRIVATE);//实例化
        SharedPreferences.Editor editor = share.edit(); //使处于可编辑状态
        editor.putBoolean("login",false);   //设置保存的数据
        Toast.makeText(this, "退出登录成功",Toast.LENGTH_SHORT).show();
        editor.commit();    //提交数据保存
    }

}
