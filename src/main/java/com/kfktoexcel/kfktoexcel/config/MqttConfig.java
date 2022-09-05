package com.kfktoexcel.kfktoexcel.config;

import cn.hutool.core.util.ReUtil;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mapping.BytesMessageMapper;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@EnableIntegration
@IntegrationComponentScan(basePackages = "com.kfktoexcel")
public class MqttConfig {
    public static void main(String[] args) {
        String logData = "2022-07-12 17:02:56.963  INFO 17526 --- [d_1657615829598] c.k.kfktoexcel.endpoint.MqttHandler      : 2022524RSUwjl4000011_1657616576949 ----- 1657616576963";
        String regex = "^([a-zA-Z0-9_-]+) +([0-9.:]+)  INFO\\s[0-9]+ --- \\[[a-zA-Z0-9_]+\\] c.k.kfktoexcel.endpoint.MqttHandler      : +([a-zA-Z0-9_]+) +----- +([a-zA-Z0-9_]+)$";
        String esn = ReUtil.extractMulti(regex, logData, "$3:$4");
        System.out.println(esn);
    }

    //
    @Value("${mqtt.server-uris}")
    private String[] serverURIs;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private char[] password;

    @Value("${mqtt.keep-alive-interval}")
    private int keepAliveInterval;

    @Value("${mqtt.sub-topics}")
    private String[] subTopics;

    @Value("${mqtt.client-id-prefix}")
    private String clientIdPrefix;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(serverURIs);
        options.setUserName(username);
        options.setPassword(password);
        options.setKeepAliveInterval(keepAliveInterval);
        factory.setConnectionOptions(options);
        return factory;
    }

    /*@Bean
    public MqttMessageConverter bytesMessageConverter(@Value("${mqtt.model-packages}") String modelPackages) throws NoSuchMethodException {
        BytesMessageMapper bytesMessageMapper = BeanUtils.instantiateClass(mapperClass.getConstructor(String.class), modelPackages);
        return new BytesMessageConverter(bytesMessageMapper);
    }*/

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler
                (clientIdPrefix + "_outbound_"+System.currentTimeMillis(), mqttClientFactory());
        messageHandler.setCompletionTimeout(50000);
        messageHandler.setAsync(true);
        return messageHandler;
    }

    @Bean
    public MessageProducer mqttInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter
                        (clientIdPrefix + "_inbound_"+System.currentTimeMillis(), mqttClientFactory(), subTopics);
        adapter.setOutputChannel(mqttInboundChannel());
        adapter.setCompletionTimeout(50000);
        adapter.setQos(1);
        return adapter;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }
}