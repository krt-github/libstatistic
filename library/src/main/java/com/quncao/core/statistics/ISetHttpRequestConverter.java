package com.quncao.core.statistics;

import java.util.List;

/**
 * Created by zehong.tang on 2016/12/5.
 * 网络请求参数转换器
 */

interface ISetHttpRequestConverter<T> {
    /**
     * 设置转换器，将请求参数转换为 http 请求需要的 String 类型
     * @param converter converter
     */
    void setConverter(IConverter<List<T>, String> converter);
}
