package com.quncao.core.statistics;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zehong.tang on 2016/11/25.
 * 基于时间的触发器，可设置是否重复
 */

public class TimeTrigger implements ITrigger {
    private boolean mStart = false;
    private boolean mRepeat = true;
    private long mIntervalTime = 0;

    private Timer mTimer;

    /**
     * 基于时间的触发器
     * @param intervalTime 触发间隔
     * @param repeat 是否重复
     */
    public TimeTrigger(long intervalTime, boolean repeat){
        mIntervalTime = intervalTime;
        mRepeat = repeat;
    }

    private OnTriggerListener mOnTriggerListener;

    @Override
    public void start() {
        mStart = true;
        if(null != mTimer)
            mTimer.cancel();

        mTimer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                trigger();
            }
        };
        if(mRepeat) {
            mTimer.schedule(task, mIntervalTime, mIntervalTime);
        }else{
            mTimer.schedule(task, mIntervalTime);
        }

        Logger.d("---TimeTrigger start---");
    }

    /**
     * 清除本次计时，开始新一轮
     */
    public void reset(){
        // TODO: 2016/11/25 清除本次计时，从新开始新一轮
    }

    @Override
    public void shutdown() {
        mStart = false;
        if(null != mTimer) {
            mTimer.cancel();
        }
        mTimer = null;
        Logger.d("---TimeTrigger shutdown---");
    }

    @Override
    public void setOnTriggerListener(OnTriggerListener listener) {
        mOnTriggerListener = listener;
    }

    @Override
    public String getName() {
        return "[Time trigger]";
    }

    private void trigger(){
        if(null != mOnTriggerListener){
            mOnTriggerListener.onTrigger(this);
        }
    }
}
