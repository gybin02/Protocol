package com.seeker.tony.myapplication.protocol;

import android.util.Log;

import com.meiyou.annotation.Implement;

/**
 * @author zhengxiaobin@xiaoyouzi.com
 * @since 17/7/27
 */
@Implement("protocol")
public class TestImplement {
    private static final String TAG = "TestImplement";

    public void test() {
        Log.e(TAG, "test: ");
    }
}
