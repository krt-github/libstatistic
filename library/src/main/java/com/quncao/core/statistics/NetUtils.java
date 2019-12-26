package com.quncao.core.statistics;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by zehong.tang on 2016/11/25.
 * 网络连接工具类
 */
class NetUtils {
    private static Context mContext;

    private NetUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void init(Context context){
        mContext = context;
    }

    public static void shutdown(){
        mContext = null;
    }

    /**
     * 判断网络是否连接
     * @return return
     */
    static boolean isConnected() {
        if(null == mContext)
            return false;

        try {
            ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null != connectivity) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (null != info && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
            return false;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * 判断是否是wifi连接
     */
    static boolean isWiFi() {
        if(null == mContext)
            return false;

        try{
            return ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                        .getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent = new Intent("/");
        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.WirelessSettings"));
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }

}
