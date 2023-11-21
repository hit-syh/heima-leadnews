package com.heima.kafka.smaple;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerQuickStart2 {
    public static void main(String[] args) {
        //
        Properties properties = new Properties();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");

        properties.put(ConsumerConfig.GROUP_ID_CONFIG,"group1");
        KafkaConsumer<String,String> consumer=new KafkaConsumer<>(properties);

        consumer.subscribe(Collections.singletonList("topic-first"));

        while(true)
        {
            ConsumerRecords<String, String> record = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> consumerRecord : record) {
                System.out.println(consumerRecord.key()+consumerRecord.value() +" : "+"***"+consumerRecord.partition()+"---"+consumerRecord.offset());
            }
        }



    }
}
