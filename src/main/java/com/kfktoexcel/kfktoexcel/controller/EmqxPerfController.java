package com.kfktoexcel.kfktoexcel.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.google.common.collect.Lists;
import com.kfktoexcel.kfktoexcel.endpoint.MqttProducer;
import com.kfktoexcel.kfktoexcel.entity.SendMsg;
import com.kfktoexcel.kfktoexcel.runnable.MqttSendRunnable;
import com.kfktoexcel.kfktoexcel.utils.LogCount;
import com.kfktoexcel.kfktoexcel.utils.RestTemplateUtil;
import com.kfktoexcel.kfktoexcel.utils.YmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @Author ChengKui
 * @Date 2022/7/9 14:18
 * @Version 1.0
 */
@RestController
@RequestMapping("/emqx")
@EnableAsync
public class EmqxPerfController {

    private static final Logger logger = LoggerFactory.getLogger(EmqxPerfController.class);


    @Value("${payload.count}")
    private Integer count;

    @Value("${payload.jsonarraysize}")
    private Integer jsonarraysize;

    @Value("${package.cutSwitch}")
    private Boolean cutSwitch;

    @Value("${package.cutSize}")
    private Integer cutSize;

    @Value("${http.serverAddr}")
    private String serverAddr;

    @Value("${payload.async}")
    private Boolean async;

    @Value("${payload.mqttswitch:false}")
    private Boolean mqttSwitch ;

    @Autowired
    private ThreadPoolTaskExecutor asyncServiceExecutor;

    @Autowired
    RestTemplateUtil restTemplateUtil;

    @Autowired
    private MqttProducer mqttProducer;


    List<String> esnList = new ArrayList<>();
    JSONObject mainJsonObj = new JSONObject();
    String mainStr = "";
    MqttSendRunnable mqttSendRunnable =null;
    @PostConstruct
    public void init() {
        Map<String,Map<String, Object>> map = YmlUtils.getMap();
        Integer count = (Integer) map.get("payload").get("count");
        //String path = EmqxPerfController.class.getClassLoader().getResource("mapinfo.txt").getPath();
        String mainpath = EmqxPerfController.class.getClassLoader().getResource("main.json").getPath();
        String subpath = EmqxPerfController.class.getClassLoader().getResource("sub.json").getPath();
        logger.info("=======================mainpath:{},subpath:{}", mainpath,subpath);
        String decodemainPath = null;
        String decodesubPath = null;
        try {
            decodemainPath = URLDecoder.decode(mainpath, "utf-8");
            decodesubPath = URLDecoder.decode(subpath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        logger.info("=======================decodePath:{},decodesubPath:{}", decodemainPath, decodesubPath);
        // 方法1（保持文件原状）：
        File file = new File(decodemainPath);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        File subfile = new File(decodesubPath);
        Long subfilelength = subfile.length();
        byte[] subfilecontent = new byte[subfilelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(subfile);
            in.read(subfilecontent);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String mainjson = new String(filecontent);
        String subjson = new String(subfilecontent);
         mainJsonObj = new JSONObject(mainjson);
        JSONObject subjsonObj = new JSONObject(subjson);
        JSONArray participant_list = (JSONArray) ((JSONObject) mainJsonObj.get("content")).get("participant_list");

        for (int i = 0; i < count; i++) {
            String esn = UUID.randomUUID().toString().replace("-", "");
            esnList.add(esn);
        }
        for (int i = 0; i < jsonarraysize; i++) {
            participant_list.add(subjsonObj);
        }
        mainStr=mainJsonObj.toString();
        mqttSendRunnable = new MqttSendRunnable(mainStr, "v2x/v1/edge/123/rsm/up", mqttProducer);
    }

    private static ThreadLocal<Runnable> local = new ThreadLocal<>();
    @PostMapping("/perf")
    public void post() {
        String batchid = UUID.randomUUID().toString().replace("-","");
        if (mqttSwitch) {
            logger.info("start batchid:{},{}，tasksize:{}", batchid, System.currentTimeMillis(),asyncServiceExecutor.getThreadPoolExecutor().getQueue().size());
            for (int i = 0; i < count; i++) {
                asyncServiceExecutor.execute(mqttSendRunnable);
            }
        } else {
            HttpHeaders headers = createHeaders();
            ArrayList<SendMsg> msgList = new ArrayList<>() ;
            for (int i = 0; i < count; i++) {
                SendMsg sendMsg = new SendMsg();
                sendMsg.setClientid("v2x_job_1657245066929");
                sendMsg.setQos(0);
                sendMsg.setRetain(false);
                //sendMsg.setTime(0);
                //sendMsg.setTopic("v2x/v1/edge/"+esnList.get(i)+"/rsm/up");
                sendMsg.setTopic("v2x/v1/edge/123/rsm/up");
                msgList.add(sendMsg);
            }
            cutSize = cutSwitch ? cutSize : msgList.size();
            List<List<SendMsg>> partitions = Lists.partition(msgList, cutSize);
            for (List<SendMsg> partition : partitions) {
                if (async) {
                    asyncServiceExecutor.execute(() -> sendMsg(partition, mainJsonObj, batchid, headers));
                } else {
                    sendMsg(partition, mainJsonObj, batchid, headers);
                }
            }
        }
    }

    private void sendMsg(List<SendMsg> partition, JSONObject jsonContent, String batchid, HttpHeaders headers) {
        long start = System.currentTimeMillis();
        partition.stream().forEach(msg -> {
            msg.setPayload(String.valueOf(jsonContent.set("batchid", batchid)));
            msg.setPayload(String.valueOf(jsonContent.set("starttime", start)));
        });
        logger.info("start batchid:{},{}", batchid, System.currentTimeMillis());
        restTemplateUtil.post(serverAddr, headers, partition, Object.class);
    }


    @RequestMapping("/logcount")
    @PostMapping
    public void logCount(String filePath){
        LogCount logCount = new LogCount();
        logCount.staticCount(filePath);
    }


    /**
     * Authorization Basic认证
     * @return
     */
    private HttpHeaders createHeaders() {
        return new HttpHeaders() {
            {
                String auth =  "admin:public" ;
                String authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
                set("authorization", authHeader);
                set("accept","application/json");
                set("Content-Type","application/json");
                //set("authorization","Basic YWRtaW46SGFyYm9yMTIzNDU=");
                //set("authorization", "Basic "+Base64.getUrlEncoder().encodeToString((HARBOR_USERNAME + ":" + HARBOR_PASSWORD).getBytes()));
                set("WWW-Authenticate","Basic realm=\"Access to staging site\"");
            }
        };
    }
}
