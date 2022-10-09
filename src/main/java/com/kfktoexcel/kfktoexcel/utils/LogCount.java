package com.kfktoexcel.kfktoexcel.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author ChengKui
 * @Date 2022/7/9 20:20
 * @Version 1.0
 */
public class LogCount {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCount.class);

    public void staticCount(String directionPath) {
//        String FilePath = "C:\\Users\\ck\\Desktop\\log";
        File file = new File(directionPath);
        HashMap<String, List<Integer>> perMap = new HashMap<>();
        HashMap<String, List<Integer>> startMap = new HashMap<>();
        HashMap<String, List<Integer>> esnMap = new HashMap<>();
        ArrayList<Integer> startList = new ArrayList<>();
        readlog(file, perMap,startMap,esnMap,startList);


    }


    private void readlog(File file, HashMap<String, List<Integer>> perMap,HashMap<String, List<Integer>> startMap,HashMap<String, List<Integer>> esnMap,ArrayList<Integer> startList) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                if (file1.isFile()) {
                    FileReader fr = null;
                    BufferedReader br = null;
                    try {
                        try {
                            fr = new FileReader(file1);// 建立FileReader对象，并实例化为fr
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        br = new BufferedReader(fr);// 建立BufferedReader对象，并实例化为br
                        String Line = br.readLine();// 从文件读取一行字符串
// 判断读取到的字符串是否不为空
                        while (Line != null) {
                            if (Line.contains(" start batchid:")) {
                                String start = Line.substring(Line.lastIndexOf(" start batchid:") + " start batchid:".length());
                                String[] strs = start.split(",");
                                String batchid = strs[0];
                                Integer starttime = Integer.valueOf(strs[1].substring(6));
                                startList.add(starttime);
                                if (startMap.containsKey(batchid)) {
                                    startMap.get(batchid).add(starttime);
                                } else {
                                    ArrayList<Integer> timeList = new ArrayList<>();
                                    timeList.add(starttime);
                                    startMap.put(batchid, timeList);
                                }
                            }
                            if (Line.contains("accept,batchid,esn:")) {
                                String accept = Line.substring(Line.lastIndexOf("accept,batchid,esn:") + "accept,batchid,esn:".length());
                                String[] strs = accept.split(",");
                                String batchid = strs[0];
                                String esn = strs[1];
                                Integer accepttime = Integer.valueOf(strs[2].substring(6));
                                if (perMap.containsKey(batchid)) {
                                    perMap.get(batchid).add(accepttime);
                                } else {
                                    ArrayList<Integer> timeList = new ArrayList<>();
                                    timeList.add(accepttime);
                                    perMap.put(batchid, timeList);
                                }
                                if (esnMap.containsKey(esn)) {
                                    esnMap.get(esn).add(accepttime);
                                } else {
                                    ArrayList<Integer> timeList = new ArrayList<>();
                                    timeList.add(accepttime);
                                    esnMap.put(esn, timeList);
                                }
                            }
                            Line = br.readLine();//从文件中继续读取一行数据
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (br != null) {
                                br.close();
                            }// 关闭BufferedReader对象
                            if (fr != null) {
                                fr.close();
                            }// 关闭文件
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String FilePath = "C:\\Users\\chengkui\\Desktop\\log";
        File file = new File(FilePath);
       // HashMap<String, List<Integer>> perMap = new HashMap<>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                HashMap<String, List<Integer>> perMap = new HashMap<>();
                FileReader fr = null;
                BufferedReader br = null;
                StringBuffer sBuffer = new StringBuffer();
                try {
                    try {
                        // 建立FileReader对象，并实例化为fr
                        fr = new FileReader(file1);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    // 建立BufferedReader对象，并实例化为br
                    br = new BufferedReader(fr);
                    // 从文件读取一行字符串
                    String Line = br.readLine();
// 判断读取到的字符串是否不为空
                    while (Line != null) {
                        if (Line.contains("logClass")) {
                            String logStr = Line.substring(Line.indexOf("logClass"));
                            String[] strs = logStr.split("\\|");
                            String[] logClass = strs[0].split(":");
                            String topic=logClass[1];
                            String[] timeContent = strs[1].split(":");
                            Integer time = Integer.valueOf(timeContent[1]);
                            if (perMap.containsKey(topic)) {
                                perMap.get(topic).add(time);
                            } else {
                                ArrayList<Integer> timeList = new ArrayList<>();
                                timeList.add(time);
                                perMap.put(topic, timeList);
                            }
                        }
                        //从文件中继续读取一行数据
                        Line = br.readLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (br != null){
                            // 关闭BufferedReader对象
                            br.close();}
                        if (fr != null){
                            fr.close();
                        }// 关闭文件
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                stableCount( perMap,file1);
            }

        }

    }

    private static void stableCount(HashMap<String, List<Integer>> perMap, File file) {
        Set<Map.Entry<String, List<Integer>>> entries = perMap.entrySet();
        for (Map.Entry<String, List<Integer>> entry : entries) {

            //区间分割
            LinkedList<Integer> linkedList = new LinkedList<>(Arrays.asList(3,20,30,40,50,100,200));
            linkedList.add(0,0);
            linkedList.add(linkedList.size(),999);

            Map<String, List<Integer>> groupMap = new LinkedHashMap<>();
            for (int i = 0; i < linkedList.size()-1; i++) {
                ArrayList<Integer> list = new ArrayList<>();
                String key = linkedList.get(i).intValue() + "_" + linkedList.get(i + 1).intValue();
                groupMap.put(key,list);
            }
            //日志分类
            String key = entry.getKey();
            //所有时间
            List<Integer> timeList = entry.getValue();
            for (Integer time : timeList) {
                for (int i = 0; i < linkedList.size() - 1; i++) {
                    //按区间分组
                    if (time.intValue() >= linkedList.get(i) && time.intValue() < linkedList.get(i + 1)) {
                        groupMap.get(linkedList.get(i).intValue() + "_" + linkedList.get(i + 1).intValue()).add(time);
                    }
                }
            }
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(4);

            Set<Map.Entry<String, List<Integer>>> groupEntries = groupMap.entrySet();
            List<Integer> endOffsetsToList =  groupEntries.stream().map(x -> x.getValue().size()).collect(Collectors.toList());
            float total =  endOffsetsToList.stream().mapToLong(x->x).sum();
            LOGGER.info("文件名:"+file.getName()+",稳定性统计时间间隔:"+key);
            LOGGER.info("共统计 " + total + " 个数据");
            for (Map.Entry<String, List<Integer>> groupEntry : groupEntries) {
                String timepharse = groupEntry.getKey();
                List<Integer> gropuList = groupEntry.getValue();
                LOGGER.info("处理时间"+timepharse+"ms的有:" + gropuList.size() + " 个," + "占比:" + numberFormat.format(gropuList.size() / total * 100));
            }
            LOGGER.info("===================================================================");
        }
    }
}

