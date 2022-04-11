package test.helper;


import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.text.SimpleDateFormat;
import java.util.concurrent.*;

public class Constant {
    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");


    public static ExecutorService executorService = new ThreadPoolExecutor(1000, 300 * 1000,
            100L, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<Runnable>(500),// 线程任务队列最小数
            new BasicThreadFactory.Builder().namingPattern("dsf-schedule-pool-%d").daemon(true).build(), new ThreadPoolExecutor.AbortPolicy());

    public static String zkAdd = "zookeeper://10.248.224.74:2181?backup=10.248.224.66:2181,10.248.224.72:2181";
    public static String zkhost = "10.248.224.74:2181,10.248.224.66:2181,10.248.224.72:2181";
    public static String nacosAdd = "nacos://10.248.224.74:8848/?backup=10.248.224.66:8848,10.248.224.72:8848";
    public static String nacosPort = "10.248.224.74:8848,10.248.224.66:8848,10.248.224.72:8848";

//    public static String zkAdd = "zookeeper://10.16.245.21:2181?backup=10.16.245.37:2181,10.16.245.19:2181";
//    public static String zkhost = "10.16.245.21:2181,10.16.245.37:2181,10.16.245.19:2181";
//    public static String nacosAdd = "nacos://10.16.245.68:8848/?backup=10.16.245.79:8848,10.16.245.71:8848";
//    public static String nacosPort = "10.16.245.68:8848,10.16.245.79:8848,10.16.245.71:8848";

    public static String packageName = "test.service.gen";
    public static String classShortName ="DemoService";
    public static String classLongName ="SamplingTaskRequestDemoServiceSamplingTaskRequestDemoServiceSamplingTaskRequestDemoServiceSamplingTaskRequestDemoService";

    public static String interfaceLongName = packageName + "." + classLongName;
    public static String interfaceShortName = packageName + "." + classShortName;

    public static String namespace = "public";

   }
