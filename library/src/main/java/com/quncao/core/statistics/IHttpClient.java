package com.quncao.core.statistics;

import java.util.List;

/**
 * Created by zehong.tang on 2016/11/29.
 * 网络请求接口
 */

interface IHttpClient <T> extends ISetHttpRequestConverter<T> {
    /**
     * 发起一次网络请求
     * @param url url
     * @param content 请求参数
     */
    void request(String url, T content);

    /**
     * 发起一次网络请求
     * @param url url
     * @param content 批量参数
     */
    void request(String url, List<T> content);

    /**
     * 网络结果回调
     * @param callback callback
     */
    void setCallback(IHttpCallback<T> callback);

    /**
     * 使能重试
     * @param enabled enabled
     */
    void enableRetry(boolean enabled);

    /**
     * 设置重试次数
     * @param retryTimes retryTimes
     */
    void setRetryTimes(int retryTimes);

    /**
     * 标示当前是否正在处理网络请求
     * @return true 正在处理
     */
    boolean isBusy();

    /**
     * 取消网络任务
     */
    void cancel();
}
