package com.quncao.core.statistics;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by zehong.tang on 2016/11/25.
 * 消息 bean
 */

class MessageBean implements Serializable{
    String event_id;
    String time;
    String platform = "Android";
    Map<String, String> content;

    @Override
    public String toString() {
        return "MessageBean{" +
                "event_id='" + event_id + '\'' +
                ", time='" + time + '\'' +
                ", platform='" + platform + '\'' +
                ", content=" + content +
                '}';
    }
}
