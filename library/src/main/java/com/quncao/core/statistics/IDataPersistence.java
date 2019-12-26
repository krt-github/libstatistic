package com.quncao.core.statistics;

import java.util.List;

/**
 * Created by zehong.tang on 2016/11/25.
 * 数据持久化接口
 */

interface IDataPersistence <T>{
    /**
     * 持久化指定数据
     * @param data data
     */
    boolean persistenceData(T data);

    /**
     * 获取所有持久化的数据
     * @return
     */
    List<T> getAllData();

    /**
     * 清除所有数据
     */
    void clearAll();

    /**
     * 释放资源
     */
    void release();
}
