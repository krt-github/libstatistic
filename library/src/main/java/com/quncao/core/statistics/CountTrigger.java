package com.quncao.core.statistics;

/**
 * Created by zehong.tang on 2016/11/25.
 * 计数触发器，达到设定数值触发回调
 */

public class CountTrigger implements ITrigger {
    private int mTriggerCount = 0;
    private int mCurrentCount = 0;
    private boolean mRepeat = true;
    private boolean mInvalid = false;
    private boolean mStart = false;

    private OnTriggerListener mOnTriggerListener;

    /**
     * 计数触发器
     * @param triggerCount 触发回调的数值
     * @param repeat 是否重复计数
     */
    public CountTrigger(int triggerCount, boolean repeat){
        if(triggerCount <= 0){
            throw new IllegalArgumentException("---Illegal argument -> triggerCount: " + triggerCount);
        }
        mTriggerCount = triggerCount;
        mRepeat = repeat;
    }

    /**
     * 当外部产生变化，需要计数时调用
     * @param count 本次记录需增加的次数
     */
    void onCountChanged(int count){
        if(mStart) {
            mCurrentCount += count;
            checkCount();
        }
        Logger.d("---count changed : " + count + "  mCurrentCount: " + mCurrentCount);
    }

    /**
     * 当外部产生变化，需要计数时调用，计数一次
     */
    public void onCountChangedOnce(){
        if(mStart) {
            mCurrentCount++;
            checkCount();
        }
    }

    @Override
    public void start() {
        mStart = true;
        Logger.d("---CountTrigger start---");
    }

    @Override
    public void shutdown() {
        mStart = false;
        mOnTriggerListener = null;
        Logger.d("---CountTrigger shutdown---");
    }

    @Override
    public void reset() {
        mCurrentCount = 0;
        Logger.d("---CountTrigger reset---");
    }

    @Override
    public void setOnTriggerListener(OnTriggerListener listener) {
        mOnTriggerListener = listener;
    }

    @Override
    public String getName() {
        return "[Count trigger]";
    }

    /**
     * 检查是否达到设定数值，是：回调，并判断是否需重复计数；否：otherwise
     */
    private void checkCount(){
        if(mInvalid || mCurrentCount < mTriggerCount)
            return;

        if(null != mOnTriggerListener) {
            mOnTriggerListener.onTrigger(this);
        }

        if(mRepeat)
            mCurrentCount = 0;
        else
            mInvalid = true;
    }
}
