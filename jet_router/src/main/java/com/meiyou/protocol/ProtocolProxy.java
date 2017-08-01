package com.meiyou.protocol;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import com.com.meiyou.protocol.ProtocolBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 协议调用 入口
 *
 * @author zhengxiaobin@xiaoyouzi.com
 * @since 17/7/27
 */

public class ProtocolProxy {
    private static ProtocolProxy instance;
    private static final String TAG = "ProtocolProxy";
    /**
     * 缓存调用实例
     */
    private Map<Class<?>, Object> cacheBeanMap = new HashMap<>();
    /**
     * 存所有的interface-implement 对应关系
     */
    private HashMap<String, ProtocolBean> table = new HashMap<>();
    private Context context;

    public static ProtocolProxy getInstance() {
        if (instance == null) {
            instance = new ProtocolProxy(
            );
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();

        try {
            register();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void register() throws Exception {
        //临时空间存储
        HashMap<String, String> mapInterface = new HashMap<>();
        HashMap<String, String> mapImplement = new HashMap<>();

        AssetManager assetManager = context.getResources().getAssets();
        String rootPath = "protocol";
        String[] list = assetManager.list(rootPath);

        for (String path : list) {
            InputStream inputStream = assetManager.open(rootPath +"/"+ path);
            String s = InputStream2String(inputStream);
            new TypeToken<Map<String, String>>() {
            }.getType();
            Type type = new TypeToken<ArrayList<ProtocolBean>>() {
            }.getType();

            ArrayList<ProtocolBean> beanList = new Gson().fromJson(s, type);

            for (ProtocolBean bean : beanList) {
                if (!TextUtils.isEmpty(bean.key_interface)) {
                    mapInterface.put(bean.key, bean.key_interface);
                }

                if (!TextUtils.isEmpty(bean.key_implement)) {
                    mapImplement.put(bean.key, bean.key_implement);
                }
            }
        }

        for (Map.Entry<String, String> entry : mapInterface.entrySet()) {
            ProtocolBean bean = new ProtocolBean();
            bean.key = entry.getKey();
            bean.key_interface = entry.getValue();
            bean.key_implement = mapImplement.get(bean.key);
            table.put(bean.key_interface, bean);
        }

    }

    /**
     * 调用方法；
     *
     * @param stub
     * @param <T>
     * @return
     */
    // TODO: 17/7/28  return Null is bad, may cause npe，should fix
    public <T> T create(Class<T> stub) throws Exception {
        if (cacheBeanMap.get(stub) != null) {
            return (T) cacheBeanMap.get(stub);
        }
        String simpleName = stub.getCanonicalName();
        ProtocolBean bean = table.get(simpleName);
        if (bean == null) {
            Log.e(TAG, "can't find Implement; Interface Name: " + simpleName);
            return null;
        }
        if (!TextUtils.isEmpty(bean.key_implement)) {
            ProxyMethodHandler handler = new ProxyMethodHandler(bean.key_implement);
            T result = (T) Proxy.newProxyInstance(stub.getClassLoader(), new Class[]{stub}, handler);
            cacheBeanMap.put(stub, result);
            return result;
        }

        return null;
    }


    /********* Private *************/

    private static String InputStream2String(InputStream is) {
        String result = "";
        try {
            byte[] buffer = new byte[is.available()];
            is.read(buffer);//输出流
            result = new String(buffer, "utf-8");
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


}
