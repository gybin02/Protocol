package com.seeker.tony.myapplication.protocol;

/**
 * @author zhengxiaobin
 * @since 17/7/27
 */

import android.util.Log;

import com.meiyou.annotation.Implement;
import com.meiyou.jet.module_b.protocol.ModuleTestInterface;
@Implement("module_b")
public class TestModuleImplement implements ModuleTestInterface {
    private static final String TAG = "TestModuleImplement";

    public void function() {
        Log.d(TAG, "function: ");
    }


}
