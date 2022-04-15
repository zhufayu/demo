package origin;

//import com.dmall.mqsync.utils.PropertiesUtil;
import org.apache.rocketmq.client.impl.producer.TopicPublishInfo;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.route.BrokerData;
import org.apache.rocketmq.common.protocol.route.QueueData;
import org.apache.rocketmq.common.protocol.route.TopicRouteData;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RktSendMain {
    private static Logger logger = LoggerFactory.getLogger(RktSendMain.class);

    public static void main(String[] args) {
        // namesrv地址
        String namesrv = "10.248.225.10:9876";
        // 需要发消息的broker列表，逗号隔开，示例：broker-A,broker-B
        String brokers = "broker-a";
        // topic名
        String topic = "rktest_zfy_test_mid";
        // group名
        String group = "group_mid_cloud_test_zfy";
        // 消息大小
        String msgSizeStr = "3000000";
        // 消息条数
        String msgNumStr = "500000";

        DefaultMQProducer producer = new DefaultMQProducer(group);
        producer.setNamesrvAddr(namesrv);
        try {
            producer.start();
            List<MessageQueue> messageQueues = null;
            TopicRouteData topicRouteInfoFromNameServer = producer.getDefaultMQProducerImpl().getmQClientFactory().getMQClientAPIImpl().getTopicRouteInfoFromNameServer(topic, 5000);
            if (topicRouteInfoFromNameServer != null) {
                TopicPublishInfo topicPublishInfo = topicRouteData2TopicPublishInfo(topic, topicRouteInfoFromNameServer);
                if (topicPublishInfo != null && topicPublishInfo.ok()) {
                    messageQueues = topicPublishInfo.getMessageQueueList();
                }
            }
            List<MessageQueue> useMessageQueues = new ArrayList<>();
            if (null != messageQueues && messageQueues.size() > 0) {
                messageQueues.forEach(mq -> {
                    if (brokers.contains(mq.getBrokerName())) {
                        useMessageQueues.add(mq);
                    }
                });
            }
            if (useMessageQueues.size() <= 0) {
                logger.error("rkt send error, info: no use message queue");
                return;
            }
            Integer msgSize = Integer.valueOf(msgSizeStr);
            Integer msgNum = Integer.valueOf(msgNumStr);
            for (int i = 0; i < msgNum; i++) {
                Message message = buildMessage(msgSize, topic);
                MessageQueue messageQueue = useMessageQueues.get(i % useMessageQueues.size());
                SendResult send = producer.send(message, messageQueue);
                logger.info("send msg success, info: msgid-{} queue-{}", send.getMsgId(), messageQueue.toString());
            }
        } catch (Exception e) {
            logger.error("rkt send error, info:{}", e.getMessage());
        } finally {
            producer.shutdown();
        }
    }

    private static Message buildMessage(final int messageSize, final String topic) throws UnsupportedEncodingException {
        Message msg = new Message();
        msg.setTopic(topic);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < messageSize; i += 10) {
            sb.append("hello  boy");
        }
        msg.setBody(sb.toString().getBytes(RemotingHelper.DEFAULT_CHARSET));
        return msg;
    }

    public static TopicPublishInfo topicRouteData2TopicPublishInfo(final String topic, final TopicRouteData route) {
        TopicPublishInfo info = new TopicPublishInfo();
        info.setTopicRouteData(route);
        if (route.getOrderTopicConf() != null && route.getOrderTopicConf().length() > 0) {
            String[] brokers = route.getOrderTopicConf().split(";");
            for (String broker : brokers) {
                String[] item = broker.split(":");
                int nums = Integer.parseInt(item[1]);
                for (int i = 0; i < nums; i++) {
                    MessageQueue mq = new MessageQueue(topic, item[0], i);
                    info.getMessageQueueList().add(mq);
                }
            }

            info.setOrderTopic(true);
        } else {
            List<QueueData> qds = route.getQueueDatas();
            Collections.sort(qds);
            for (QueueData qd : qds) {
                BrokerData brokerData = null;
                for (BrokerData bd : route.getBrokerDatas()) {
                    if (bd.getBrokerName().equals(qd.getBrokerName())) {
                        brokerData = bd;
                        break;
                    }
                }

                if (null == brokerData) {
                    continue;
                }

                if (!brokerData.getBrokerAddrs().containsKey(MixAll.MASTER_ID)) {
                    continue;
                }

                for (int i = 0; i < qd.getWriteQueueNums(); i++) {
                    MessageQueue mq = new MessageQueue(topic, qd.getBrokerName(), i);
                    info.getMessageQueueList().add(mq);
                }
            }
            info.setOrderTopic(false);
        }
        return info;
    }
}
