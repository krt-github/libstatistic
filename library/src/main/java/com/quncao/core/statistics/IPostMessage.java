package com.quncao.core.statistics;

import java.util.List;

/**
 * Created by zehong.tang on 2016/11/25.
 * 提交 message 接口
 */

interface IPostMessage <T> extends ISetHttpRequestConverter<T> {
    /**
     * 发起一次网络请求
     * @param url url
     * @param content content
     */
    void post(String url, T content);

    /**
     * 发起一次网络请求
     * @param url url
     * @param contentList list
     */
    void post(String url, List<T> contentList);

    /**
     * 异步提交回调
     * @param callback callback
     */
    void setPostMessageCallback(IHttpCallback<T> callback);

    /**
     * start
     */
    void start();

    /**
     * 释放资源
     */
    void shutdown();
}
