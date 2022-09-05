package com.kfktoexcel.kfktoexcel.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.kfktoexcel.kfktoexcel.entity.db.DbRsmData;
import com.kfktoexcel.kfktoexcel.entity.rsm.Content;
import com.kfktoexcel.kfktoexcel.entity.rsm.Participant_list;
import com.kfktoexcel.kfktoexcel.entity.rsm.RsmData;
import com.kfktoexcel.kfktoexcel.entity.rsm.RsmInfo;
import com.kfktoexcel.kfktoexcel.service.IDbRsmDataService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * kafka控制器
 *
 * @author 154594742@qq.com
 * @date 2021/3/2 15:01
 */

@RestController
@Api(tags = "Kafka控制器")
@Slf4j
public class KafkaController {

    List<Participant_list> allList = new ArrayList<>();

    ArrayList<Map<String, Object>> rows = new ArrayList<>();

    int i=0;

    //@KafkaListener(topics = {"edge_rsi_up_server"})
    public void saveRsmUp(String message, Acknowledgment ack) {
        //log.info(message);
        String mes ="{\"payload\":\"{\\\"name\\\":\\\"rsm\\\",\\\"content\\\":{\\\"rsm_source\\\":{\\\"rsm_source_type\\\":\\\"detection\\\",\\\"rsm_source_id\\\":\\\"test\\\"},\\\"id\\\":\\\"test\\\",\\\"time_stamp\\\":1652355019700,\\\"send_time\\\":1652355019972,\\\"ref_pos\\\":{\\\"lon\\\":0.0,\\\"lat\\\":0.0},\\\"participant_list\\\":[{\\\"ptc_type\\\":\\\"motor\\\",\\\"ptcId\\\":51470,\\\"sec_mark\\\":19700,\\\"cross_id\\\":\\\"\\\",\\\"global_track_id\\\":51470,\\\"source\\\":\\\"integrated\\\",\\\"pos\\\":{\\\"lon\\\":116.1969829,\\\"lat\\\":39.4216601},\\\"pos_confidence\\\":{\\\"position_confidence\\\":0},\\\"nez\\\":{\\\"n\\\":4363877.956,\\\"e\\\":430879.503,\\\"z\\\":0.0,\\\"v\\\":72.82657644678898,\\\"vX\\\":14.4036,\\\"vY\\\":71.38799999999999},\\\"speed\\\":1011,\\\"heading\\\":990,\\\"vehicle_color\\\":\\\"99\\\",\\\"vehicle_model\\\":\\\"\\\",\\\"vehicle_brand\\\":\\\"\\\",\\\"vehicle_style\\\":\\\"\\\",\\\"size\\\":{\\\"width\\\":120,\\\"length\\\":509,\\\"height\\\":0},\\\"vehicle_class\\\":{\\\"basic_vehicle_class\\\":10}}]}}\",\"topic\":\"v2x/v1/edge/9de2410c1e6f1255005f043f486bc9d6_2102313AGUP0MC100081/rsm/up\"}";
        RsmData rsmData = JSONUtil.toBean(mes, RsmData.class);
        String payload = rsmData.getPayload();
        RsmInfo rsmInfo = JSONUtil.toBean(payload, RsmInfo.class);
        Content content = rsmInfo.getContent();
        List<Participant_list> participant_list = content.getParticipant_list();
        for (Participant_list participantList : participant_list) {
            String s = JSONUtil.toJsonStr(participantList);
            //participantList.setPos_confidence(null);
            //participantList.setVehicle_class(null);
            //log.info(s);
            allList.add(participantList);

        }
        Map<String, Object> row1 = new LinkedHashMap<>();
        row1.put("姓名", "张三");
        row1.put("年龄", 23);
        row1.put("成绩", 88.32);
        row1.put("是否合格", true);
        row1.put("考试日期", DateUtil.date());

        Map<String, Object> row2 = new LinkedHashMap<>();
        row2.put("姓名", "李四");
        row2.put("年龄", 33);
        row2.put("成绩", 59.50);
        row2.put("是否合格", false);
        row2.put("考试日期", DateUtil.date());
        rows.add(row1);
        rows.add(row2);

        if(i==1){
            // 通过工具类创建writer
            //BigExcelWriter writer = ExcelUtil.getBigWriter("d:/writeBeanTest"+System.currentTimeMillis()+".xlsx");
            //ExcelWriter writer = ExcelUtil.getWriter("d:\\rsm"+System.currentTimeMillis()+".xlsx");
            // 合并单元格后的标题行，使用默认标题样式
            //writer.merge(25, "一班成绩单");
            // 一次性写出内容，使用默认样式，强制输出标题
           //writer.write(rows, true);
            // 关闭writer，释放内存
            //writer.close();
            // 通过工具类创建writer，默认创建xls格式
            ExcelWriter writer = ExcelUtil.getWriter();
        //创建xlsx格式的
        //ExcelWriter writer = ExcelUtil.getWriter(true);
        // 一次性写出内容，使用默认样式，强制输出标题
            //writer.write(rows, true);
        //out为OutputStream，需要写出到的目标流
            //writer.flush(out);
        // 关闭writer，释放内存
            writer.close();
            allList = new ArrayList<>();
            i= 0;
        }

    }

    /**
     * 监听topic5,批量消费
     */
    //@KafkaListener(topics =  {"rsm_up"}, containerFactory = "batchFactory")
    public void listen2(List<ConsumerRecord<String, String>> records) {
        for (ConsumerRecord<String, String> record : records) {
            BufferedWriter bufferedWriter = null;
            try {
                String value = record.value();
                RsmData rsmData = JSONUtil.toBean(value, RsmData.class);
                String payload = rsmData.getPayload();
                RsmInfo rsmInfo = JSONUtil.toBean(payload, RsmInfo.class);
                Content content = rsmInfo.getContent();
                long send_time = content.getSend_time();
                System.out.println(send_time);
                //log.info(String.valueOf(send_time));
                //if(send_time>=1652320800000L){
                    //  List<Participant_list> participant_list = content.getParticipant_list();
                    //是否追加
                    Boolean isAppend = true;
                    //创建流
                    bufferedWriter = FileUtil.getWriter("D:\\RSM.txt", CharsetUtil.CHARSET_UTF_8,isAppend);
               /* for (Participant_list participantList : participant_list) {
                    String s = JSONUtil.toJsonStr(participantList);
                    //participantList.setPos_confidence(null);
                    //participantList.setVehicle_class(null);
                    //log.info(s);
                    //allList.add(participantList);
                     String str = s+"\n";
                    bufferedWriter.write(str);
                }*/
                    String str = payload+",\n";
                    bufferedWriter.write(str);
                    bufferedWriter.flush();
                //}
            } catch (IOException e) {
                //抛出一个运行时异常(直接停止掉程序)
                throw new RuntimeException("运行时异常",e);
            }finally {
                IoUtil.close(bufferedWriter);
            }
        }

    }

    @Resource
    private IDbRsmDataService dbRsmDataService;

    /**
     * 监听topic5,批量消费
     */
    //@KafkaListener(topics =  {"rsm_up"}, containerFactory = "batchFactory")
    public void listen3(List<ConsumerRecord<String, String>> records) {
        List<DbRsmData> list  = new ArrayList<>();
        for (ConsumerRecord<String, String> record : records) {
            BufferedWriter bufferedWriter = null;
            String value = record.value();
            log.info(value);
            /*RsmData rsmData = JSONUtil.toBean(value, RsmData.class);
            String payload = rsmData.getPayload();
            RsmInfo rsmInfo = JSONUtil.toBean(payload, RsmInfo.class);
            Content content = rsmInfo.getContent();
            long send_time = content.getSend_time();
            List<Participant_list> participant_list = content.getParticipant_list();
            for (Participant_list pa : participant_list) {
                DbRsmData dbRsmData = new DbRsmData();
                dbRsmData.setTimeStamp(content.getTime_stamp()+"");
                dbRsmData.setSendTime(send_time+"");
                dbRsmData.setPtcType(pa.getPtc_type());
                dbRsmData.setPtcId(pa.getPtcId()+"");
                dbRsmData.setSecMark(String.valueOf(pa.getSec_mark()));
                dbRsmData.setCrossId(pa.getCross_id());
                dbRsmData.setGlobalTrackId(pa.getGlobal_track_id()+"");
                dbRsmData.setSource(pa.getSource());
                dbRsmData.setSpeed(pa.getSpeed()+"");
                dbRsmData.setHeading(pa.getHeading()+"");
                dbRsmData.setPlateNo(pa.getPlate_no());
                dbRsmData.setVehicleModel(pa.getVehicle_model());
                dbRsmData.setVehicleBrand(pa.getVehicle_brand());
                dbRsmData.setVehicleColor(pa.getVehicle_color());
                dbRsmData.setVehicleStyle(pa.getVehicle_style());
                list.add(dbRsmData);
                //dbRsmDataService.save(dbRsmData);
            }*/

        }
       // log.info(String.valueOf(list.size()));
        //dbRsmDataService.saveBatch(list);


    }

    //@KafkaListener(topics =  {"rsu_info_up"}, containerFactory = "batchFactory")
   /* public void listeninfoUp(List<ConsumerRecord<String, String>> records) {
        List<DbRsmData> list  = new ArrayList<>();
        for (ConsumerRecord<String, String> record : records) {
            BufferedWriter bufferedWriter = null;
            String value = record.value();
            JSONObject jsonObject = new JSONObject(value);
            JSONObject payload = new JSONObject(jsonObject.getStr("payload"));
            String rsuEsn = payload.getStr("rsuEsn");
            log.info(value);
        }
        System.out.println(1);
    }*/

    /*@KafkaListener(topics =  {"rsi_up"}, containerFactory = "batchFactory")
    public void listenrsiUp(List<ConsumerRecord<String, String>> records) {
        List<DbRsmData> list  = new ArrayList<>();
        for (ConsumerRecord<String, String> record : records) {
            BufferedWriter bufferedWriter = null;
            String value = record.value();
            log.info(value);
        }
        System.out.println(1);
    }*/
}
