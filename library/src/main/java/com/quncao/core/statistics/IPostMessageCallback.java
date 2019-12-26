package com.quncao.core.statistics;

/**
 * Created by zehong.tang on 2016/11/25.
 * 提交回调接口
 */

interface IPostMessageCallback {
    /**
     * 提交成功回调
     */
    void onPostSuccess();

    /**
     * 提交失败回调
     * @param errString 错误信息
     */
    void onPostFailed(String errString);
}
