package com.dn_alan.myapplication;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
        //系统实例化了
        ActivityManagerNative activityManagerNative=new ActivityManagerNative();

        //我们调用
        try {
            //还原
            Class activityManagerNativeClass=Class.forName("com.dn_alan.myapplication.ActivityManagerNative");
            Field sigletoneField=activityManagerNativeClass.getDeclaredField("sigletone");
            sigletoneField.setAccessible(true);
            Object sigletoneObj=sigletoneField.get(null);
            Sigletone sigletone=(Sigletone) sigletoneObj;
            System.out.println("---   "+sigletone.name);

            //替换
            Sigletone sigletone2=new Sigletone();
            sigletone2.setName("jett");
            sigletoneField.set(activityManagerNative,sigletone2);



            //系统自己调用
            System.out.println("-->"+ActivityManagerNative.sigletone.name);

        } catch ( Exception e) {
            e.printStackTrace();
        }

    }
}