package com.quncao.core.statistics;

import java.util.List;

/**
 * Created by zehong.tang on 2016/11/29.
 * http 回调接口
 */

interface IHttpCallback <T> {
    void onSuccess();
    void onFailed(String msg, List<T> requestData);
}
