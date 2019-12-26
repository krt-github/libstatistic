package com.quncao.core.statistics;

/**
 * Created by zehong.tang on 2016/12/6.
 * 参考 http://192.168.30.7:8090/pages/viewpage.action?pageId=13992501
 */

public class BaseParams {
    private static final String UNKNOWN = "-unInit-";
    public String larkid = UNKNOWN; //百灵鸟号
    public String ver = UNKNOWN; //app版本号，ios和android必须填
    public String dev_id = UNKNOWN; //设备ID
    private final String protocol_ver = "V1.0"; //协议版本号

    @Override
    public String toString() {
        return "BaseParams{" +
                "larkid='" + larkid + '\'' +
                ", ver='" + ver + '\'' +
                ", dev_id='" + dev_id + '\'' +
                ", protocol_ver='" + protocol_ver + '\'' +
                '}';
    }
}
