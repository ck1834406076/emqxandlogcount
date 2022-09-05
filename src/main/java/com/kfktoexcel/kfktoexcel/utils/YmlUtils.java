package com.kfktoexcel.kfktoexcel.utils;

import org.yaml.snakeyaml.Yaml;

import javax.print.DocFlavor;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
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
}
