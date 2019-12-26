package com.quncao.core.statistics;

import java.io.Serializable;

/**
 * Created by zehong.tang on 2016/11/29.
 */

public class HttpResultBean implements Serializable {
    int errcode;
    String errmsg;
    String version;
    String logid;

    @Override
    public String toString() {
        return "HttpResultBean{" +
                "errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                ", version='" + version + '\'' +
                ", logid='" + logid + '\'' +
                '}';
    }
}
