package com.meiyou.protocol;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 方法代理
 *
 * @author zhengxiaobin@xiaoyouzi.com
 * @since 17/7/27 下午2:05
 */
public class ProxyMethodHandler implements InvocationHandler {
    private static final String TAG = "JetProxy";
    private final String targetClazzName;

    public ProxyMethodHandler(String targetClazzName) {
        this.targetClazzName = targetClazzName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //异常类，异常方法，出错等会有Log， Throw 会被不捕获住
        Class clazz = Class.forName(targetClazzName);
        Object newInstance = clazz.newInstance();
        String methodName = method.getName();
        Method realMethod = clazz.getMethod(methodName, method.getParameterTypes());
//        if (realMethod == null) {
//            Log.e(TAG, String.format("can't find Method:%s  in  class : %s", methodName, targetClazzName));
//            return null;
//        }
        realMethod.setAccessible(true);
        return realMethod.invoke(newInstance, args);
    }
}