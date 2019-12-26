package com.quncao.core.statistics;

/**
 * Created by zehong.tang on 2016/12/5.
 * 数据转换器
 */

interface IConverter<T, K> {
    /**
     * T 转换为 K
     * @param data data
     * @return K
     */
    K convert(T data);
}
