// KafkaConfiguration.java

package com.kfktoexcel.kfktoexcel.config;
 
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.kfktoexcel.kfktoexcel.entity.rsm.RsmInfo;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

import java.util.Map;
 
/**
 * @author tianshl
 * @version 2017/9/1 下午04:07
 */
@Configuration
@EnableKafka
public class KafkaConfiguration {
 
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
 
    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private Boolean autoCommit;
 
    @Value("${spring.kafka.consumer.auto-commit-interval}")
    private Integer autoCommitInterval;
 
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
 
    @Value("${spring.kafka.consumer.max-poll-records}")
    private Integer maxPollRecords;
 
    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;
 
    @Value("${spring.kafka.producer.retries}")
    private Integer retries;
 
    @Value("${spring.kafka.producer.batch-size}")
    private Integer batchSize;
 
    @Value("${spring.kafka.producer.buffer-memory:33554432}")
    private Integer bufferMemory;

    @Value("${spring.kafka.producer.compression-type:lz4}")
    private String compressionType;
 
    /**
     *  生产者配置信息
     */
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = Maps.newHashMap();
        props.put(ProducerConfig.ACKS_CONFIG, "0");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        //压缩消息
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,compressionType);
        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG,1024 * 1024*10);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }
 
    /**
     *  生产者工厂
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }
 
    /**
     *  生产者模板
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
 
    /**
     *  消费者配置信息
     */
    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = Maps.newHashMap();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 120000);
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 180000);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }
 
    /**
     *  消费者批量工程
     */
    @Bean
    public KafkaListenerContainerFactory<?> batchFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerConfigs()));
        //设置为批量消费，每个批次数量在Kafka配置参数中设置ConsumerConfig.MAX_POLL_RECORDS_CONFIG
        factory.setBatchListener(true);
        // 设置记录筛选策略
        /*factory.setRecordFilterStrategy(new RecordFilterStrategy() {
            @Override
            public boolean filter(ConsumerRecord consumerRecord) {
                String msg = consumerRecord.value().toString();
                JSONObject jsonObject = new JSONObject(msg);
                String payload1 = jsonObject.getStr("payload");
                RsmInfo rsmInfo = JSONUtil.toBean(payload1, RsmInfo.class);
               // RsmInfo payload = new JSONObject(msg).get("payload", RsmInfo.class);
                long send_time = rsmInfo.getContent().getSend_time();
                if(send_time>=1652320800000L){
                    return false;
                }
                // 返回true消息将会被丢弃
                return true;
            }
        });*/
        return factory;
    }
}