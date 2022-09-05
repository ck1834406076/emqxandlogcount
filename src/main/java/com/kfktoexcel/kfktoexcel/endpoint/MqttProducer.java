package com.kfktoexcel.kfktoexcel.endpoint;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 */
@Component
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttProducer {

    @Gateway(replyTimeout = 2, requestTimeout = 200)
    void sendTo(@Header(MqttHeaders.TOPIC) String topic, Object payload);

    @Gateway(replyTimeout = 2, requestTimeout = 200)
    void sendTo(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, Object payload);
}