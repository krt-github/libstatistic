package com.quncao.core.statistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zehong.tang on 2016/11/25.
 * 触发器管理器，方便统一执行
 */

class TriggerManager implements ITriggerManager {
    private List<ITrigger> mTriggerList = new ArrayList<>();
    private OnTriggerListener mOnTriggerListener;
    private static TriggerManager mTriggerManager;
    private boolean mIsStart = false;

    private TriggerManager(){}

    static TriggerManager getInstance(){
        if(null == mTriggerManager){
            synchronized (TriggerManager.class){
                if(null == mTriggerManager){
                    mTriggerManager = new TriggerManager();
                }
            }
        }
        return mTriggerManager;
    }

    /**
     * 添加触发器，trigger == null 将添加不成功，不能重复添加同一个触发器
     * @param trigger trigger
     */
    @Override
    public void addTrigger(ITrigger trigger) {
        if(null == trigger || mTriggerList.contains(trigger))
            return;

        mTriggerList.add(trigger);
    }

    /**
     * 移出指定触发器，并 shutdown 该触发器
     * @param trigger 要移出的 trigger
     */
    @Override
    public void removeTrigger(ITrigger trigger) {
        if(mTriggerList.remove(trigger)){
            trigger.shutdown();
        }
    }

    @Override
    public void setOnTriggerListener(OnTriggerListener listener) {
        mOnTriggerListener = listener;
    }

    /**
     * 初始化被管理的触发器并启动之
     */
    @Override
    public void start() {
        if(mIsStart)
            return;

        if(null == mTriggerList){
            Logger.e("---Can not restart a released Manager---");
            return;
        }

        for(ITrigger trigger : mTriggerList){
            trigger.setOnTriggerListener(new OnTriggerListener() {
                public void onTrigger(ITrigger sourceTrigger) {
                    onTriggerOccur(sourceTrigger);
                }
            });
            trigger.start();
        }
        mIsStart = true;
        Logger.d("---TriggerManager start---");
    }

    /**
     * 停止所有触发器并释放资源
     */
    @Override
    public void shutdown() {
        if(null != mTriggerList) {
            for (ITrigger trigger : mTriggerList) {
                trigger.setOnTriggerListener(null);
                trigger.shutdown();
            }
            mTriggerList.clear();
        }
        mTriggerList = null;
        mOnTriggerListener = null;
        mTriggerManager = null;
        mIsStart = false;
        Logger.d("---TriggerManager shutdown---");
    }

    public boolean isStart(){
        return mIsStart;
    }

    /**
     * 触发监听器 mOnTriggerListener 只有一个，所有触发器共享，避免并发访问，设置到每个触发器的监听器是不同实例，
     * 也就是说，每个触发器触发时调用的是不同的监听器，但该监听器中执行的业务都是同一份 onTriggerOccur
     */
    private synchronized void onTriggerOccur(ITrigger sourceTrigger){
        if(null != mOnTriggerListener){
            mOnTriggerListener.onTrigger(sourceTrigger);
        }
    }
}
