package origin;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.common.RemotingHelper;


import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;


public class OriginRocketMQ {
    private static String server = "10.248.224.57:9876";
    private static String topic = "rkt_new_consumer_mid_123";
    private static String headerKey = Constant.headerKey;

    public static void testProducerTransaction() throws Exception {
        ExecutorService executor = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("client-transaction-msg-check-thread");
                return thread;
            }
        });

        TransactionListener transactionListener = new MyTransactionListenerImpl();

        TransactionMQProducer producer = new TransactionMQProducer("please_rename_unique_group_name");
        producer.setNamesrvAddr(server);
        producer.setSendMsgTimeout(Integer.MAX_VALUE);
        producer.setExecutorService(executor);
        producer.setTransactionListener(transactionListener);
        producer.start();

        String[] tags = new String[]{"TagA", "TagB", "TagC", "TagD", "TagE"};
        for (int i = 0; i < 10; i++) {
            try {
                Message msg = new Message(topic, tags[i % tags.length], "KEY" + i, ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                SendResult sendResult = producer.sendMessageInTransaction(msg, null);
                System.out.printf("%s%n", sendResult);

            } catch (MQClientException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        Thread.sleep(1000 * 100000);

        producer.shutdown();
    }

    public static void testProducer() throws Exception {
        //Instantiate with a producer group name.
        String group = "group_testmqapp_3";

        DefaultMQProducer producer = new DefaultMQProducer(group);
        // Specify name server addresses.
        producer.setNamesrvAddr(server);

        //Launch the instance.
        producer.start();
        for (int i = 0; i < 100; i++) {
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message(topic,
                    "TagA" /* Tag */,
                    ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            // /u0001 /u0002为rocketmq里properties的分割符
            msg.putUserProperty(headerKey + 1, "testValue1" + headerKey);
            msg.putUserProperty(headerKey + 2, "testValue2" + headerKey);

//            msg.setWaitStoreMsgOK(false);
            //是否等落盘
            msg.setWaitStoreMsgOK(true);

            //Call send message to deliver message to one of brokers.
            SendResult sendResult = producer.send(msg);

            System.out.printf("%s%n", sendResult);
        }
        //Wait for sending to complete
        Thread.sleep(5000);
        producer.shutdown();
        System.out.printf("send 100 message");
    }

    public static void testConsumer() throws MQClientException {
        String group = "group_testmqapp_3";

        //使用 DefaultMQPushConsumer 设置好各种传人参数和处理消息的函数 。 自动保存 Offset，加入新的 DefaultMQPushConsumer后会自动做负载均衡
        //Push的方式是 Server端接收到消息后，主动把消息推送给 Client端，主动权在Server端
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();

        /**
         * CONSUME_FROM_LAST_OFFSET will ignore the historical messages, and consume anything produced after that.
         * CONSUME_FROM_FIRST_OFFSET will consume every message existed in the Broker.
         * You can also use CONSUME_FROM_TIMESTAMP to consume messages produced after the specified timestamp
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        //消费模式：MessageModel.CLUSTERING和MessageModel.BROADCASTING
        //集群模式：消息只能被消费一次，位置点保存在服务端
        //广播模式：消息被所有消费端
        consumer.setMessageModel(MessageModel.CLUSTERING);

        consumer.setNamesrvAddr(server);
        consumer.setInstanceName(group + "manually");

        consumer.subscribe(topic, "*");
        consumer.setConsumerGroup(group);
        consumer.setConsumeThreadMin(5);
        consumer.setConsumeThreadMax(5);

        final int[] counter = {0};
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                for (MessageExt msg : list) {
                    counter[0]++;
                    Map<String, String> map = msg.getProperties();


                    System.out.println(Thread.currentThread().getName() + "  " + counter[0] + " Property : " + map.get(headerKey + 1) + " Messages : " + msg.toString());
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
        System.out.printf("Clustering Consumer Started.%n");

//        while (true) {
//            Map<String, String> assignQueue = consumer.get;
//            System.out.println("assignments size: " + assignQueue);
//
//            try {
//                Thread.sleep(1000);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    public static void testBroad() throws MQClientException {
        String group = "group_testmqapp_3";


        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();

        /**
         * CONSUME_FROM_LAST_OFFSET will ignore the historical messages, and consume anything produced after that.
         * CONSUME_FROM_FIRST_OFFSET will consume every message existed in the Broker.
         * You can also use CONSUME_FROM_TIMESTAMP to consume messages produced after the specified timestamp
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        //set to broadcast mode
        consumer.setMessageModel(MessageModel.BROADCASTING);

        consumer.setNamesrvAddr(server);
        consumer.setInstanceName(group + "manually");

        consumer.subscribe(topic, "*");
        consumer.setConsumerGroup(group);
        consumer.setConsumeThreadMin(1);
        consumer.setConsumeThreadMax(1);

        final int[] counter = {0};
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                for (MessageExt e : list) {
                    counter[0]++;
                    System.out.println(counter[0]);
                    System.out.printf(Constant.df.format(new Date()) + Thread.currentThread().getName() + " Receive New Messages: " + e.toString() + "%n");
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
        System.out.printf("Broadcast Consumer Started.%n");
    }

    public static void resetOffset() throws Exception {
        String group = "group_testmqapp_3";

        //获取Message Queue,维护 Offsetstore
        //Pull方式是 Client端循环地从 Server端拉取消息，主动权在 Client手里
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer(group);

        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.setNamesrvAddr(server);
        consumer.setInstanceName(group + "manually");
        consumer.setConsumerGroup(group);
        consumer.start();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        long timestamp = sdf.parse("202106010000000").getTime();

        Set<MessageQueue> mqs = null;
        try {
            mqs = consumer.fetchSubscribeMessageQueues(topic);
            if (mqs != null && !mqs.isEmpty()) {
                TreeSet<MessageQueue> mqsNew = new TreeSet<MessageQueue>(mqs);
                for (MessageQueue mq : mqsNew) {
                    long offset = consumer.searchOffset(mq, timestamp);
                    if (offset >= 0) {
                        consumer.updateConsumeOffset(mq, offset);
                        System.out.printf("resetOffsetByTimestamp success, %s %d %s \n", group, offset, mq);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mqs != null) {
                consumer.getDefaultMQPullConsumerImpl().getOffsetStore().persistAll(mqs);
            }
            consumer.shutdown();
        }
    }
}
