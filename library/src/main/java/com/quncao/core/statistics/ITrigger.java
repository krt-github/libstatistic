package com.quncao.core.statistics;

/**
 * Created by zehong.tang on 2016/11/25.
 * 触发器接口
 */

public interface ITrigger {
    /**
     * 启动触发器，进入工作状态
     */
    public void start();

    /**
     * 关闭触发器，并释放资源
     */
    public void shutdown();

    /**
     * 重置触发器
     */
    public void reset();

    /**
     * 设置满足触发雕件是的回调
     * @param listener 回调
     */
    public void setOnTriggerListener(OnTriggerListener listener);

    /**
     * 获取触发器名字
     * @return name
     */
    public String getName();
}
