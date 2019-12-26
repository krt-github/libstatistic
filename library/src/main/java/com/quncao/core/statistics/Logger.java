package com.quncao.core.statistics;

import android.util.Log;

/**
 * Created by zehong.tang on 2016/11/29.
 * 统一日志
 */

class Logger {
    private static boolean mEnableDebug = false;

    static void setEnableDebug(boolean enabled){
        mEnableDebug = enabled;
    }

    static void d(String s){
        if(mEnableDebug){
            Log.d("LarkStatistics", s);
        }
    }

    static void i(String s){
        if(mEnableDebug) {
            Log.i("LarkStatistics", s);
        }
    }

    static void e(String s){
        if(mEnableDebug) {
            Log.e("LarkStatistics", s);
        }
    }
}
