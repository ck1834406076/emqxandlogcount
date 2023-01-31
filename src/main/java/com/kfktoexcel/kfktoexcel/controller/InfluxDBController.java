package com.kfktoexcel.kfktoexcel.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.influxdb.client.*;
import com.influxdb.client.domain.WritePrecision;
import com.kfktoexcel.kfktoexcel.entity.RSMInflux;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/influx")
public class InfluxDBController {

    private static final Logger logger = LoggerFactory.getLogger(InfluxDBController.class);

    private static char[] token = "mWJeDA-NuFy5X7gFVfNvE6K67TL_aATfKrbiUrW-TuWHkEZMZ8wpr4-wtqEopClNSp8O0DnM0NFxSpvJXQM1Mg==".toCharArray();
    private static String org = "ck";
    private static String bucket = "test";

    private InfluxDBClient influxDBClient;
    private WriteApi asyncWriteApi;
    private List<String> plateNums=new ArrayList<>(2048);

    @PostConstruct
    public void initAdminClient() {
        influxDBClient = InfluxDBClientFactory.create("http://192.168.2.85:8086", token, org, bucket);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        WriteOptions writeOptions = new WriteOptions.Builder().bufferLimit(100000000).flushInterval(10000).batchSize(100000).build();
        asyncWriteApi = influxDBClient.makeWriteApi(writeOptions);
        for (int i = 0; i < 1000; i++) {
            String plateNum="苏A"+ UUID.randomUUID().toString().substring(0,5).toUpperCase();
            plateNums.add(plateNum);
        }
    }

    @GetMapping("/save")
    public void saveData() {
        logger.info("save data to influxdb");
        ArrayList<RSMInflux> rsmInfluxes = new ArrayList<>();
        for (int i = 0; i < 512; i++) {
            RSMInflux rsmInflux = new RSMInflux();
            String plateNum = plateNums.get(i);
            rsmInflux.setPlate_no(plateNum);
            rsmInflux.setPtcid(plateNum);
            rsmInflux.setLat(RandomUtil.randomDouble(40D,110D));
            rsmInflux.setLon(RandomUtil.randomDouble(40D,110D));
            rsmInflux.setTime(Instant.now());
            rsmInfluxes.add(rsmInflux);
        }
        asyncWriteApi.writeMeasurements(WritePrecision.MS, rsmInfluxes);
        logger.info("end save data to influxdb");
    }


}
