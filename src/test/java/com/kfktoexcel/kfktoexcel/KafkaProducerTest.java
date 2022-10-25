package com.kfktoexcel.kfktoexcel;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.admin.ListConsumerGroupsResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
public class KafkaProducerTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    @Test
    public void testProducer() {
        for (int i = 0; i < 100000; i++) {
            String msg = "======testfunction========" + i + "==========";
            kafkaTemplate.send("rsu_info_up", msg);
        }
    }

    public static void main(String[] args) throws TimeoutException {
//        List<String> topicList = Arrays.asList("edge_rsm_up_server");
//        lagOf("V2X_KAFKA", "122.112.138.244:9094,119.3.9.172:9094,122.112.164.242:9094",topicList);
        List<String> topicList = Arrays.asList("edge_rsm_up_server");
        lagOf("data-test", "122.112.179.121:9092",topicList);
    }


    public static void lagOf(String groupID, String bootstrapServers, List<String> topicList) throws TimeoutException {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true); // 禁止自动提交位移
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        while (true) {
            try (AdminClient client = AdminClient.create(props)) {
                ListConsumerGroupsResult listConsumerGroupsResult = client.listConsumerGroups();
                ListConsumerGroupOffsetsResult result = client.listConsumerGroupOffsets(groupID);
                Map<TopicPartition, OffsetAndMetadata> consumedOffsets = result.partitionsToOffsetAndMetadata().get();

                try (final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {

                    Map<TopicPartition, Long> endOffsets = consumer.endOffsets(consumedOffsets.keySet());
                    Map<TopicPartition, Long> longMap = endOffsets.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(),
                            entry -> entry.getValue() - consumedOffsets.get(entry.getKey()).offset()));
                    System.out.println("====================================================");
                    //System.out.println(longMap);
                    Set<Map.Entry<TopicPartition, Long>> entries = longMap.entrySet();
                    for (Map.Entry<TopicPartition, Long> entry : entries) {
                        String topic = entry.getKey().topic();
                        int partition = entry.getKey().partition();
                        Long value = entry.getValue();
                        if (topicList.contains(topic)){
                            System.out.println(topic+"-"+partition+"======="+value);
                        }
                    }
                    Thread.sleep(10000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 处理中断异常
                // ...

            } catch (ExecutionException e) {
                // 处理ExecutionException
                // ...
                System.out.println(e);
            }
        }
    }
}
