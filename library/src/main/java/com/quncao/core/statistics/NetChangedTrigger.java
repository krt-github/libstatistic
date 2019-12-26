package com.quncao.core.statistics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by zehong.tang on 2016/12/5.
 * 网络改变触发器
 */

public class NetChangedTrigger extends BroadcastReceiver implements ITrigger {
    private int lastNetState = ConnectivityManager.TYPE_WIFI;
    private OnTriggerListener mOnTriggerListener;
    private Context mContext;

    public NetChangedTrigger(Context context){
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.i(intent.toString());
        try {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

                if (null != activeNetworkInfo) {
                    if(ConnectivityManager.TYPE_WIFI == activeNetworkInfo.getType() && ConnectivityManager.TYPE_WIFI != lastNetState){
                        if(null != mOnTriggerListener){
                            mOnTriggerListener.onTrigger(this);
                        }
                    }
                    lastNetState = activeNetworkInfo.getType();
                } else {
                    lastNetState = -1; //NET_NONE;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        if(null != mContext){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            mContext.registerReceiver(this, intentFilter);
        }else{
            Logger.e("---NetChangedTrigger start failed---");
        }
    }

    @Override
    public void shutdown() {
        if(null != mContext){
            mContext.unregisterReceiver(this);
        }else{
            Logger.e("---Context is null, NetChangedTrigger skip shutdown");
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public void setOnTriggerListener(OnTriggerListener listener) {
        mOnTriggerListener = listener;
    }

    @Override
    public String getName() {
        return "[Net trigger]";
    }

}
