package origin;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Kafka08Producer {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String topic ="zfy_test_topic";
//        String topic ="kb_test111";

        Properties kafkaPropertie = new Properties();
        //配置broker地址，配置多个容错
        kafkaPropertie.put("bootstrap.servers", "10.248.224.73:9093,10.248.224.73:9094");
//        kafkaPropertie.put("bootstrap.servers", "10.248.224.30:9092");

        //配置key-value允许使用参数化类型
        kafkaPropertie.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaPropertie.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        //0：producer不等待来自broker同步完成的确认继续发送下一条（批）消息
        //1：producer在leader已成功收到的数据并得到确认后发送下一条message
        //-1：producer在follower副本确认接收到数据后才算一次发送完成
        kafkaPropertie.put("request.required.acks","1");

        KafkaProducer kafkaProducer = new KafkaProducer(kafkaPropertie);
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, "key1", "hello world");
        Future<RecordMetadata> metata = kafkaProducer.send(record);
        System.out.println("send message: " + metata.get().toString());
    }
}
