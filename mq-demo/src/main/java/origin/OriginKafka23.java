package origin;


//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.Producer;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.apache.kafka.clients.producer.RecordMetadata;
//import org.apache.kafka.common.PartitionInfo;
//import org.apache.kafka.common.TopicPartition;
//import org.apache.kafka.common.header.Header;
//import org.apache.kafka.common.header.Headers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.Future;

//import org.apache.kafka.common.header.Headers;

public class OriginKafka23 {
//    private static String server = "10.248.224.33:8092";
//    private static String server = "10.248.224.108:8092,10.248.224.32:8092,10.248.224.33:8092";
//    private static String topic = "zfy_test_topic";
//
//    private static String headerKey = Constant.headerKey;
//
//    public static void testProducer() throws Exception {
//
//        Properties props = new Properties();
//        props.put("bootstrap.servers", server);
//        props.put("acks", "all");
//        props.put("retries", 0);
//        props.put("batch.size", 16384);
//        props.put("linger.ms", 1);
//        props.put("buffer.memory", 33554432);
//
//        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//
//        Producer<String, String> producer = new KafkaProducer<>(props);
//        for (int i = 0; i < 10; i++) {
//
//            ProducerRecord<String, String> record = new ProducerRecord<>(topic, Integer.toString(i) + "test_key", Integer.toString(i) + "test_value");
//            Headers header = record.headers();
//            header.add(headerKey + 1, ("testValue1" + headerKey).getBytes("UTF-8"));
//            header.add(headerKey + 2, ("testValue2" + headerKey).getBytes("UTF-8"));
//            Future<RecordMetadata> metadataFuture = producer.send(record);
//            System.out.println(metadataFuture.get());
//        }
//
//        producer.close();
//        System.out.printf("send 100 message");
//    }
//
//    public static void testProducerTransaction() throws Exception {
//        int j = 0;
//        while (true) {
//            j++;
//            Properties props = new Properties();
//            props.put("bootstrap.servers", server);
//            props.put("acks", "all");
//            props.put("retries", 3);
//            props.put("batch.size", 16384);
//            props.put("linger.ms", 1);
//            props.put("buffer.memory", 33554432);
//
//            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//            props.put("transactional.id", "transactionId" + j);
//
//            Producer<String, String> producer = new KafkaProducer<>(props);
//            producer.initTransactions();
//            producer.beginTransaction();
//            for (int i = 0; i < 10; i++) {
//
//                ProducerRecord<String, String> record = new ProducerRecord<>(topic, Integer.toString(i) + "test_key", Integer.toString(i) + "test_value");
//                Headers header = record.headers();
//                header.add(headerKey + 1, ("testValue1" + headerKey).getBytes("UTF-8"));
//                header.add(headerKey + 2, ("testValue2" + headerKey).getBytes("UTF-8"));
//                RecordMetadata metadata = producer.send(record).get();
//                System.out.println(Constant.df.format(new Date()) + "send: " + metadata);
//            }
//
//            Thread.sleep(1000 * 30);
//            producer.commitTransaction();
//            System.out.println(Constant.df.format(new Date()) + "send commit: ");
//        }
//    }
//
//    public static void testResetOffset() throws ParseException {
//        Properties props = new Properties();
//        // ?????????????????????
//        props.put("bootstrap.servers", server);
//        props.put("group.id", "group1");
//
//        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//
//        // ????????????offset
//        props.put("enable.auto.commit", "false");
//
//        props.put("auto.offset.reset", "earliest");
//        props.put("client.id", "zy_client_id");
//
//
//        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
//
//        consumer.subscribe(Collections.singletonList(topic));
//
//        //??????assignment???????????????????????????poll?????????????????????assignment
//        consumer.poll(100);
//
//        //??????consumer????????????????????????,?????????????????????????????????????????????????????????
//        Set<TopicPartition> assignments = consumer.assignment();
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//        long timestamp = sdf.parse("202106010000000").getTime();
//        //????????????????????????????????????
//        Map<TopicPartition, Long> query = new HashMap<>();
//        for (TopicPartition topicPartition : assignments) {
//            query.put(topicPartition, timestamp);
//        }
//
//        //????????????????????????????????????????????????
//        Map<TopicPartition, OffsetAndTimestamp> result = consumer.offsetsForTimes(query);
//
//
//        //????????????????????????????????????
////        Map<TopicPartition, Long> offsets = consumer.endOffsets(assignments);
//
//
//        for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry : result.entrySet()) {
//            TopicPartition partition = entry.getKey();
//            long targetOffset = entry.getValue().offset();
//            System.out.println(partition + "=============" + targetOffset);
//
//            //????????????seek????????????offset
//            consumer.seek(partition, targetOffset);
//        }
//
//        //seek???????????????????????????
//        consumer.commitSync();
//    }
//
//    public static void testConsumer() {
//        Properties props = new Properties();
//
//        // ?????????????????????
//        props.put("bootstrap.servers", server);
//        props.put("group.id", "group1");
//
//        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//
//        // ????????????offset
//        props.put("enable.auto.commit", "true");
//        // ????????????offset,???1s????????????
//        props.put("auto.commit.interval.ms", "1000");
//
//        props.put("client.id", "zy_client_id");
//
//        props.put("auto.offset.reset", "earliest");
//
//        //????????????????????????????????????
//        props.put("isolation.level", "read_committed");
//
//        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
//
//        final int[] counter = {0};
//        //??????test1 topic
//        consumer.subscribe(Collections.singletonList(topic));
//        while (true) {
//
//            //  ??????????????????????????????
//            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
//
//            //??????Reblance???assigment?????????
//            Set<TopicPartition> assignments = consumer.assignment();
//            System.out.println("assignments size: " + assignments);
//
//            records.forEach(record -> {
//                counter[0]++;
//
//                Headers headers = record.headers();
//                for (Header header : headers) {
//                    System.out.println(header.key() + header.value());
//                }
//
//                System.out.printf(Constant.df.format(new Date()) + "topic = %s ,partition = %d,offset = %d, key = %s, value = %s%n", record.topic(), record.partition(),
//                        record.offset(), record.key(), record.value());
//            });
//
//            try {
//                Thread.sleep(1000);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//    public static void testConsumerCommitManually() {
//        Properties props = new Properties();
//
//        // ?????????????????????
//        props.put("bootstrap.servers", server);
//        props.put("group.id", "group1");
//
//        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//
//        // ???????????????offset
//        props.put("enable.auto.commit", "false");
//        //????????????
//        props.put("session.timeout.ms", "30000");
//        //???????????????????????????
//        props.put("max.poll.records", 10);
//
//        props.put("auto.offset.reset", "earliest");
//        props.put("client.id", "zy_client_id");
//
//        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
//
//        //??????test1 topic
//        consumer.subscribe(Collections.singletonList(topic));
//
//        int messageNo = 1;
//        List<String> list = new ArrayList<String>();
//        long offset = 0;
//
//        while (true) {
//            ConsumerRecords<String, String> msgList = consumer.poll(100);
//            if (null != msgList && msgList.count() > 0) {
//                for (ConsumerRecord<String, String> record : msgList) {
//                    if (messageNo % 10 == 0) {
//                        System.out.println(messageNo + "=======receive: key = " + record.key() + ", value = " + record.value() + " offset===" + record.offset());
//                    }
//                    messageNo++;
//                    offset = record.offset();
//                    list.add(record.key());
//                }
//
//                // ????????????
//                consumer.commitSync();
//                System.out.println("????????????" + list.size() + "???,?????????offset???:" + offset);
//            }
//        }
//    }

}
