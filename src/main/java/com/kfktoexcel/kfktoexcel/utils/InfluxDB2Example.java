package com.kfktoexcel.kfktoexcel.utils;

import cn.hutool.core.util.RandomUtil;
import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.*;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.exceptions.InfluxException;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.kfktoexcel.kfktoexcel.entity.RSMInflux;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.Random;

public class InfluxDB2Example {

    private static char[] token = "mWJeDA-NuFy5X7gFVfNvE6K67TL_aATfKrbiUrW-TuWHkEZMZ8wpr4-wtqEopClNSp8O0DnM0NFxSpvJXQM1Mg==".toCharArray();
    private static String org = "ck";
    private static String bucket = "test";

    public static void main(final String[] args) {

        InfluxDBClient influxDBClient = InfluxDBClientFactory.create("http://122.112.153.221:8086", token, org, bucket);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        try {
            //
            // Write by LineProtocol
            //
//            String record = "temperature,location=north value=60.0";
//
//            writeApi.writeRecord(WritePrecision.NS, record);
//
//            //
//            // Write by Data Point
//            //
//            Point point = Point.measurement("temperature")
//                    .addTag("location", "west")
//                    .addField("value", 57D)
//                    .time(Instant.now().toEpochMilli(), WritePrecision.MS);
//
//            writeApi.writePoint(point);
//
//            //
//            // Write by POJO
//            //
//            Temperature temperature = new Temperature();
//            temperature.location = "south";
//            temperature.value = 69D;
//            temperature.time = Instant.now();
//
//            writeApi.writeMeasurement(WritePrecision.NS, temperature);
            while (true) {
                RSMInflux rsmInflux = new RSMInflux();
                rsmInflux.setPlate_no("苏A66666");
                rsmInflux.setPtcid("12345678");
                rsmInflux.setLat(RandomUtil.randomDouble(40D,110D));
                rsmInflux.setLon(RandomUtil.randomDouble(40D,90D));
                rsmInflux.setTime(Instant.now());
                writeApi.writeMeasurement(WritePrecision.NS, rsmInflux);
            }

//            QueryApi queryApi = influxDBClient.getQueryApi();
//            String flux = "from(bucket:\"test\")\n" +
//                    "    |> range(start: 2023-01-16T00:00:00Z, stop: 2023-01-16T12:00:00Z)";
//
//            List<FluxTable> tables = queryApi.query(flux);
//            System.out.println("查询结果：======"+tables.size());
//            for (FluxTable fluxTable : tables) {
//                List<FluxRecord> records = fluxTable.getRecords();
//                for (FluxRecord fluxRecord : records) {
//                    System.out.println(fluxRecord.getTime() + ": " + fluxRecord.getValueByKey("_value"));
//                }
//            }


        } catch (InfluxException ie) {
            System.out.println("InfluxException: " + ie);
        }
        influxDBClient.close();
    }

    @Measurement(name = "temperature")
    @Setter
    @Getter
    public static class Temperature {

        @Column(tag = true)
        public String location;

        @Column
        public Double value;

        @Column(timestamp = true)
        public Instant time;
    }
}