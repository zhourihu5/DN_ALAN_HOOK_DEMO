package com.dn_alan.myapplication.os;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dn_alan.myapplication.ProxyActivity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 专门处理绕过AMS检测，让LoginActivity可以正常通过
 */
public class DnAMSCheckEngine {

    /**
     * TODO 此方法 适用于 21以下的版本 以及 21_22_23_24_25  26_27_28 等系统版本
     * @param mContext
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public static void mHookAMS(final Context mContext) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
//        android.app.ActivityManager activityManager=null;
//        activityManager.getService()
//        activityManager.IActivityManagerSingleton;
//        android.app.IActivityManager iActivityManager;
        // 公共区域
        Object mIActivityManagerSingleton = null; // TODO 公共区域 适用于 21以下的版本 以及 21_22_23_24_25  26_27_28 等系统版本
        Object mIActivityManager = null; // TODO 公共区域 适用于 21以下的版本 以及 21_22_23_24_25  26_27_28 等系统版本
        Class mSingletonClass = Class.forName("android.util.Singleton");
        Class mIActivityManagerClass = Class.forName("android.app.IActivityManager");
        if (AndroidSdkVersion.isAndroidOS_26_27_28()) {
//            android.app.ActivityManager activityManager;
            // 获取系统的 IActivityManager.aidl
            Class mActivityManagerClass = Class.forName("android.app.ActivityManager");
            mIActivityManager = mActivityManagerClass.getMethod("getService").invoke(null);


            // 获取IActivityManagerSingleton
            Field mIActivityManagerSingletonField = mActivityManagerClass.getDeclaredField("IActivityManagerSingleton");
            mIActivityManagerSingletonField.setAccessible(true);
            mIActivityManagerSingleton = mIActivityManagerSingletonField.get(null);
            mIActivityManagerClass = Class.forName("android.app.IActivityManager");
        }
        else if(AndroidSdkVersion.isAndroidOS_30()){
            //小米9 se 上是这个类
//            android.app.ActivityTaskManager.getService()

            mIActivityManagerClass = Class.forName("android.app.IActivityTaskManager");
            Class mActivityManagerClass = Class.forName("android.app.ActivityTaskManager");
            /** @hide  的方法反射调用不到 */
            try {
                mIActivityManager = mActivityManagerClass.getMethod("getService").invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }


            // 获取IActivityManagerSingleton
            Field mIActivityManagerSingletonField = mActivityManagerClass.getDeclaredField("IActivityTaskManagerSingleton");
            mIActivityManagerSingletonField.setAccessible(true);
            mIActivityManagerSingleton = mIActivityManagerSingletonField.get(null);
            if(mIActivityManager==null){
                Method  getService=mSingletonClass.getDeclaredMethod("get");
                getService.setAccessible(true);
                mIActivityManager=getService.invoke(mIActivityManagerSingleton);
            }
        }
        else if (AndroidSdkVersion.isAndroidOS_21_22_23_24_25()) {
            Class mActivityManagerClass = Class.forName("android.app.ActivityManagerNative");
            Method getDefaultMethod = mActivityManagerClass.getDeclaredMethod("getDefault");
            getDefaultMethod.setAccessible(true);
            mIActivityManager = getDefaultMethod.invoke(null);

            //gDefault
            Field gDefaultField = mActivityManagerClass.getDeclaredField("gDefault");
            gDefaultField.setAccessible(true);
            mIActivityManagerSingleton = gDefaultField.get(null);
        }
//        android.app.IActivityManager
        //获取动态代理

        final Object finalMIActivityManager = mIActivityManager;
        Object mIActivityManagerProxy =  Proxy.newProxyInstance(mContext.getClassLoader(),
                new Class[]{mIActivityManagerClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Log.e("mIActivityManagerProxy", "invoke: method:"+method.getName());
                        if ("startActivity".equals(method.getName())) {
                            // 把LoginActivity 换成 ProxyActivity
                            // TODO 把不能经过检测的LoginActivity 替换 成能够经过检测的ProxyActivity
                            Intent proxyIntent = new Intent(mContext, ProxyActivity.class);
//                            printArgs(args);
                            printArgs(method.getParameterTypes());
                            // 把目标的LoginActivity 取出来 携带过去
                            int argIndexIntent=findArgIndexIntent(method.getParameterTypes());
                            Intent target = (Intent) args[argIndexIntent];
                            proxyIntent.putExtra(Parameter.TARGET_INTENT, target);
                            args[argIndexIntent] = proxyIntent;
                        }

                        return method.invoke(finalMIActivityManager, args);
                    }

                    private int findArgIndexIntent(Class<?>[] parameterTypes) {
                        for(int i=0;i<parameterTypes.length;i++){
                            if(parameterTypes[i]==Intent.class){
                                return i;
                            }
                        }
                        return -1;
                    }

                    private void printArgs(Object[] args) {
                        if(args!=null){
                            for(Object o:args){
                                Log.e("printArgs", "printArgs: "+o);
                            }
                        }
                    }
                });

        if (mIActivityManagerSingleton == null || mIActivityManagerProxy == null) {
            throw new IllegalStateException("实在是没有检测到这种系统，需要对这种系统单独处理...");
        }



        Field mInstanceField = mSingletonClass.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);

        // 把系统里面的 IActivityManager 换成 我们自己写的动态代理
        mInstanceField.set(mIActivityManagerSingleton, mIActivityManagerProxy);
    }

}
