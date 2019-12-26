package com.quncao.core.statistics;

import java.util.List;

/**
 * Created by zehong.tang on 2016/11/25.
 * 消息队列接口
 */

interface IMessageQueue <T>{
    /**
     * 获取 queue 能存储的最大值
     * @return int
     */
    int getMaxMessageSize();

    /**
     * 获取当前存储的 size
     * @return int
     */
    int getCurrentMessageSize();

    /**
     * 添加一条 message
     * @param msg msg
     */
    void addMessage(T msg);

    /**
     * 添加多条 message
     * @param list list
     */
    void addMessage(List<T> list);

    /**
     * 从 queue 中获取第一条 message 并从 queue 中移除
     * @return t
     */
    T pollMessage();

    /**
     * 从 queue 中第一条 message 开始，获取 size 条 message 并移除
     * @param size size
     * @return list
     */
    List<T> pollMessage(int size);

    /**
     * 从 queue 中获取所有 message，并清空
     * @return all message
     */
    List<T> pollAllMessage();

    /**
     * 添加数据变化监听器
     * @param l listener
     */
    void setOnMessageSizeChangedListener(IOnMessageSizeChangedListener l);

    /**
     * 释放资源
     */
    void shutdown();
}
