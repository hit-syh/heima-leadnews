package com.heima.kafka.smaple;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class ProducerQuickStart {
    public static void main(String[] args) throws InterruptedException {
        //1kafka链接配置信息
        Properties prop=new Properties();
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        prop.put(ProducerConfig.ACKS_CONFIG,"all");
        //2.创建kafka生产者对象
        KafkaProducer<String,String> producer=new KafkaProducer<>(prop);


        //参数 topic key value
        for (int i = 0; i < 1; i++) {

            ProducerRecord<String,String> record =new ProducerRecord<>("topic-first","key-00"+i,"hello kafka"+i);
            //3发送消息
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if(e!=null)
                    {
                        System.out.println(e);
                    }
                    System.out.println(recordMetadata.offset());
                }
            });
            Thread.sleep(1000);
        }


        //关闭
        producer.close();
    }
}
