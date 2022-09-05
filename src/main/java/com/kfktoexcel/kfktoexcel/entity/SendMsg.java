package com.kfktoexcel.kfktoexcel.entity;

import lombok.Data;

@Data
public class SendMsg {

    //private long time;
    private String topic;
    private String payload;
    private int qos;
    private boolean retain;
    private String clientid;
    //private UserProperties user_properties;

}