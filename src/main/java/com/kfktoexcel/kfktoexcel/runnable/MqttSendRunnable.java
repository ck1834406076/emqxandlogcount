package com.kfktoexcel.kfktoexcel.runnable;

import com.kfktoexcel.kfktoexcel.endpoint.MqttProducer;

public class MqttSendRunnable implements Runnable{

    private String jsonStr;

    private String topic;

    private MqttProducer mqttProducer;

    public MqttSendRunnable() {
    }

    public MqttSendRunnable(String jsonStr, String topic, MqttProducer mqttProducer) {
        this.jsonStr = jsonStr;
        this.topic = topic;
        this.mqttProducer = mqttProducer;
    }

    @Override
    public void run() {
        mqttProducer.sendTo(topic,jsonStr);
    }

}
