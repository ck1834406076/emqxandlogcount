package com.kfktoexcel.kfktoexcel.endpoint;

import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONObject;
import com.kfktoexcel.kfktoexcel.entity.SendMapMsg;
import com.kfktoexcel.kfktoexcel.service.ISendMapMsgService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.management.PlatformLoggingMXBean;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 */
@Service
@Slf4j
public class MqttHandler implements MessageHandler {
//public class MqttHandler {

    private static final Logger logger = LoggerFactory.getLogger(MqttHandler.class);

    @Autowired
    ISendMapMsgService sendMapMsgService;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private ThreadPoolTaskExecutor asyncServiceExecutor;
    @ServiceActivator(inputChannel = "mqttInboundChannel")
    @Async("asyncServiceExecutor")
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
//        String payload = (String) message.getPayload();
//        JSONObject jsonObject = new JSONObject(payload);
//        String batchid = (String) jsonObject.get("batchid");
        String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC, String.class);
        Object payload = message.getPayload();
        String regex = "^v2x/v1/([a-zA-Z0-9]+)/([a-zA-Z0-9_]+)/(.*)$";
        String esn = ReUtil.extractMulti(regex,topic, "$2");
        logger.info(esn + " ----- " + System.currentTimeMillis() + "");
        if(ReUtil.isMatch(regex, topic)){
            JSONObject jsonObject = new JSONObject();
            jsonObject.putOpt("payload", String.valueOf(payload));
            jsonObject.putOpt("topic",topic);
            kafkaTemplate.send("edge_rsm_up_server",jsonObject.toString());
//            int length = payload.toString().length();
//            String compress = compress(payload.toString());
//            int length1 = compress(payload.toString()).length();
//            String s = unCompress(compress);
//            int length2 = s.length();


            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.putOpt("payload", String.valueOf(payload));
            jsonObject2.putOpt("topic",topic);
            kafkaTemplate.send("rsm_up_large_screen",jsonObject2.toString());
        }



        //logger.info("accept,batchid,esn:{},{},{}", batchid, esn, System.currentTimeMillis());
//        long l = System.currentTimeMillis();
//        String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC, String.class);
//        Object payload = message.getPayload();
//        String regex = "^v2x/v1/([a-zA-Z0-9]+)/([a-zA-Z0-9_]+)/(.*)$";
//        String esn = ReUtil.extractMulti(regex,topic, "$2");
//       // if(esn.equals("2022524RsuWJL4000024")){
//            log.info(esn+" ----- "+System.currentTimeMillis()+"");
//        //}
//       // log.info(System.currentTimeMillis()+"");
//        SendMapMsg sendMapMsg = new SendMapMsg();
//        sendMapMsg.setEsn(esn);
//        sendMapMsg.setTime(String.valueOf(System.currentTimeMillis()));
//        //sendMapMsgService.save(sendMapMsg);
//        //log.info("time>>>>>>>>>>>>>>>>>："+(System.currentTimeMillis()-l));
    }

    public static String compress(String str) throws IOException {
        long time1  = System.currentTimeMillis();
        if (null == str || str.length() <= 0) {
            return str;
        }
        // 创建一个新的输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 使用默认缓冲区大小创建新的输出流
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        // 将字节写入此输出流
        gzip.write(str.getBytes("utf-8")); // 因为后台默认字符集有可能是GBK字符集，所以此处需指定一个字符集
        gzip.close();
        long time2 = System.currentTimeMillis();
        // System.out.println("compress time:"+(time2 - time1)/1000.0);
        // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
        return out.toString("ISO-8859-1");
    }

    public static String unCompress(String str) throws IOException {
        if (null == str || str.length() <= 0) {
            return str;
        }
        // 创建一个新的输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 创建一个 ByteArrayInputStream，使用 buf 作为其缓冲 区数组
        ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
        // 使用默认缓冲区大小创建新的输入流
        GZIPInputStream gzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n = 0;
        // 将未压缩数据读入字节数组
        while ((n = gzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
        return out.toString("utf-8");
    }
}