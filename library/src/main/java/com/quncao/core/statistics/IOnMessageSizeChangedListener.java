package com.quncao.core.statistics;

/**
 * Created by zehong.tang on 2016/11/25.
 * queue size 变化监听器
 */

interface IOnMessageSizeChangedListener {
    /**
     * queue size 变化回调
     * @param size 变化后 size
     */
    void onMessageSizeChanged(int size);

    /**
     * 当 queue 存满时回调
     * @param max max
     */
    void onMessageReachMax(int max);
}
