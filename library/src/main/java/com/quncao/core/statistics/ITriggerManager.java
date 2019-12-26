package com.quncao.core.statistics;

/**
 * Created by zehong.tang on 2016/11/25.
 * 触发器管理者接口
 */

interface ITriggerManager {
    /**
     * 添加触发器
     * @param trigger trigger
     */
    void addTrigger(ITrigger trigger);

    /**
     * 移出指定触发器，需 shutdown 该触发器
     * @param trigger trigger
     */
    void removeTrigger(ITrigger trigger);

    /**
     * 设置触发回调
     * @param listener 回调
     */
    void setOnTriggerListener(OnTriggerListener listener);

    /**
     * 启动触发管理器，并进入工作状态
     */
    void start();

    /**
     * 关闭触发管理器，并释放资源
     */
    void shutdown();
}
