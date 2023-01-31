package com.kfktoexcel.kfktoexcel.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class YmlUtils {

    public static Map<String,Map<String, Object>> getMap() {
        try {
            Yaml yaml = new Yaml();
            String url = URLDecoder.decode(YmlUtils.class.getClassLoader().getResource("application.yml").getPath(),"utf-8");;
            if (url != null) {
                //获取application.yml文件中的配置数据，然后转换为obj，
                Object obj = yaml.load(new FileInputStream(url));
                System.out.println(obj);
                //也可以将值转换为Map
                //这里具体看你的配置文件有几层，我只有两层，所以两个Map套娃就够了
                Map<String,Map<String, Object>> map = (Map) yaml.load(new FileInputStream(url));
                return map;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
        //ArrayList中存放城市集合
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("北京");
        arrayList.add("上海");
        arrayList.add("天津");
        arrayList.add("广州");
        arrayList.add("深圳");
        arrayList.add("成都");
        arrayList.add("杭州");
        arrayList.add("大连");


        HashSet<String> area1 = new HashSet<>();
        area1.add("北京");
        area1.add("上海");
        area1.add("天津");

        HashSet<String> area2 = new HashSet<>();
        area2.add("广州");
        area2.add("北京");
        area2.add("深圳");

        HashSet<String> area3 = new HashSet<>();
        area3.add("成都");
        area3.add("上海");
        area3.add("杭州");

        HashSet<String> area4 = new HashSet<>();
        area4.add("上海");
        area4.add("天津");

        HashSet<String> area5 = new HashSet<>();
        area5.add("杭州");
        area5.add("大连");
        //电台所覆盖城市的范围
        HashMap<String, HashSet<String>> broadcasts = new HashMap<>();
        broadcasts.put("K1", area1);
        broadcasts.put("K2", area2);
        broadcasts.put("K3", area3);
        broadcasts.put("K4", area4);
        broadcasts.put("K5", area5);


        //用于存储符合要求的电台
        ArrayList<String> keyList = new ArrayList<>();
        //用于存储交际最多的电台
        String key = null;
        //临时存储所覆盖的城市
        HashSet<String> temp = new HashSet<>();
        while (!arrayList.isEmpty()) {

            for (String broadcastsKey : broadcasts.keySet()) {
                //先清空
                temp.clear();
                temp.addAll(broadcasts.get(broadcastsKey));
                temp.retainAll(arrayList);
                if (temp.size() > 0 && (key == null || temp.size() > broadcasts.get(key).size())) {
                    key = broadcastsKey;
                }
            }
            if (key != null) {
                keyList.add(key);
                arrayList.removeAll(broadcasts.get(key));
                broadcasts.remove(key);
                key = null;
            }

        }
        System.out.print(keyList);

    }
}
