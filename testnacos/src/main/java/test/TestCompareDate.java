package test;

import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.apache.zookeeper.ZooKeeper;
import test.compare.TestNacos;
import test.compare.TestZk;
import test.helper.Constant;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestCompareDate {


    private static void testCreateZK() throws Exception {
        ZooKeeper zk = TestZk.getZK();
//        String name = "test.service.DemoService";
        String name = "test.service.MockService";

        TestZk.createService(name, zk);
        TestZk.outService(name, zk);

        Thread.sleep(60000);
        TestZk.deleteService(name, zk);

        TestZk.closeZK(zk);
    }


    private static void testCompareDate(String compareType, String direction, String openOutPut, String zpath, String nacosName) throws Exception {
        ZooKeeper zk = TestZk.getZK();
        NamingService naming = TestNacos.getNamingService();

        String begin = "";
        String end = "";

        for (int i = 0; i < 1000000; i++) {
            List<Instance> instances = naming.getAllInstances(nacosName);
            List<String> zooChildren = zk.getChildren(zpath, false);

            int nacosCount = instances.size();
            int zkCount = zooChildren.size();

            if ("open".equals(openOutPut)) {
                System.out.println("nacosCount ========" + nacosCount + " zkCount========" + zkCount);
            }

            if ("zk-nacos".equals(direction)) {
                if ("create".equals(compareType)) {
                    if (zkCount == 1 && nacosCount == 0 && "".equals(begin)) {
                        begin = Constant.df.format(new Date());
                    }

                    if (zkCount == 1 && nacosCount == 1 && "".equals(end)) {
                        end = Constant.df.format(new Date());
                        break;
                    }
                } else {
                    if (zkCount == 0 && nacosCount == 1 && "".equals(begin)) {
                        begin = Constant.df.format(new Date());
                    }

                    if (zkCount == 0 && nacosCount == 0 && "".equals(end)) {
                        end = Constant.df.format(new Date());
                        break;
                    }
                }
            } else {
                if ("create".equals(compareType)) {
                    if (zkCount == 0 && nacosCount == 1 && "".equals(begin)) {
                        begin = Constant.df.format(new Date());
                    }

                    if (zkCount == 1 && nacosCount == 1 && "".equals(end)) {
                        end = Constant.df.format(new Date());
                        break;
                    }
                } else {
                    if (zkCount == 1 && nacosCount == 0 && "".equals(begin)) {
                        begin = Constant.df.format(new Date());
                    }
                    if (zkCount == 0 && nacosCount == 0 && "".equals(end)) {
                        end = Constant.df.format(new Date());
                        break;
                    }
                }
            }
        }

        if ("".equals(begin) || "".equals(end)) {
            System.out.println(compareType + " begin:" + begin + " end:" + end);
        } else {
            Date a = Constant.df.parse(begin);
            Date b = Constant.df.parse(end);
            long mm = b.getTime() - a.getTime();
            System.out.println(compareType + " begin:" + begin + " end:" + end + " lag: " + mm);
        }

        zk.close();
    }

    private static void testCompareProviderDate(String compareType, String direction, String openOutPut) throws Exception {

        String serviceName = "test.service.gen.DemoService2";
        String type = "providers";

        String zpath = "/dubbo/" + serviceName + "/" + type;
        String nacosName = type + ":" + serviceName + "::";

        testCompareDate(compareType, direction, openOutPut, zpath, nacosName);
    }


    private static void outPutIntanceCount(int from, int to, String create, int total, boolean outPut) throws Exception {
        ZooKeeper zk = TestZk.getZK();
        NamingService naming = TestNacos.getNamingService();

        String begin = "";
        String end = "";

        for (int i = 0; i < 1000000; i++) {
            int counter = 0;
            boolean breakFlag = false;

            for (int j = from; j < to; j++) {
                try {

                    String serviceName = "test.service.gen.DemoService" + j;
                    String type = "providers";

                    String zpath = "/dubbo/" + serviceName + "/" + type;
                    String nacosName = type + ":" + serviceName + "::";

                    List<Instance> instances = naming.getAllInstances(nacosName);
                    List<String> zooChildren = zk.getChildren(zpath, false);

                    int nacosCount = instances.size();
                    int zkCount = zooChildren.size();

                    if (outPut) {
                        System.out.println(serviceName + " nacosCount: " + nacosCount + " zkCount: " + zkCount + "        " + Constant.df.format(new Date()));
                    }

                    if ("create".equals(create)) {
                        if ((nacosCount > 0 || zkCount > 0) && begin.length() == 0) {
                            begin = Constant.df.format(new Date());
                            System.out.println(" begin:" + begin);
                        }
                        if (nacosCount == total && zkCount == total) {
                            counter++;
                        }
                        if (counter == to - from && end.length() == 0) {
                            end = Constant.df.format(new Date());
                            System.out.println(" end:" + end);
                            breakFlag = true;
                            break;
                        }
                    } else {
                        if ((nacosCount < total || zkCount < total) && begin.length() == 0) {
                            begin = Constant.df.format(new Date());
                            System.out.println(" begin:" + begin);
                        }
                        if (nacosCount == 0 && 0 == zkCount) {
                            counter++;
                        }
                        if (counter == to - from && end.length() == 0) {
                            end = Constant.df.format(new Date());
                            System.out.println(" end:" + end);
                            breakFlag = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (breakFlag) break;
        }

        if ("".equals(begin) || "".equals(end)) {
            System.out.println(create + " begin:" + begin + " end:" + end);
        } else {
            Date a = Constant.df.parse(begin);
            Date b = Constant.df.parse(end);
            long mm = b.getTime() - a.getTime();
            System.out.println(create + " begin:" + begin + " end:" + end + " lag: " + mm);
        }

        zk.close();
    }

    private static void outPutIntanceCount(int from, int to, int count) throws Exception {
        NamingService naming = TestNacos.getNamingService();
        for (int i = 0; i < 10000000; i++) {
            for (int j = from; j < to; j++) {
                String serviceName = "test.service.gen.DemoService" + j;
                String type = "providers";

                //String zpath = "/dubbo/" + serviceName + "/" + type;
                String nacosName = type + ":" + serviceName + "::";
                List<Instance> instances = naming.getAllInstances(nacosName);

                long nacosCount = instances.size();
                if (nacosCount < count) {
                    System.out.println(serviceName + " nacosCount: " + nacosCount);
                }

//                long healthyCount = instances.stream().filter(Instance::isHealthy).count();
//                if (nacosCount < count || nacosCount != healthyCount) {
//                    System.out.println(serviceName + " nacosCount: " + nacosCount + " healthyCount: " + healthyCount);
//                }


            }
        }
    }

    private static void testNacosCount(int to, int count) {
        ExecutorService EXECUTOR = Executors.newFixedThreadPool(20);
        for (int i = 0; i < to / 100; i++) {
            int finalI = i;
            EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        outPutIntanceCount(finalI * 100, finalI * 100 + 100, count);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    private static void allServiceCount() throws Exception {
        ZooKeeper zk = TestZk.getZK();
        NamingService naming = TestNacos.getNamingService();

        for (int i = 0; i < 1000000; i++) {

            for (int k = 0; k < 100; k++) {
                int start = k * 50;
                int end = start + 2;

                for (int j = start; j < end; j++) {
                    try {
                        String serviceName = "test.service.gen.DemoService" + j;
                        String type = "providers";

                        String zpath = "/dubbo/" + serviceName + "/" + type;
                        String nacosName = type + ":" + serviceName + "::";

                        List<Instance> instances = naming.getAllInstances(nacosName);
                        List<String> zooChildren = zk.getChildren(zpath, false);

                        int nacosCount = instances.size();
                        int zkCount = zooChildren.size();

//                        if(nacosCount != zkCount){
                        System.out.println(serviceName + " nacosCount: " + nacosCount + " zkCount: " + zkCount + "        " + Constant.df.format(new Date()));
//                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    private static void count(String prefixService, int from, int to, int tarCount) throws Exception {
        ZooKeeper zk = TestZk.getZK();
        NamingService naming = TestNacos.getNamingService();
        for (int i = 0; i < 1000000; i++) {

            for (int j = from; j < to; j++) {
                String serviceName = prefixService + j;

                String type = "providers";

                String zpath = "/dubbo/" + serviceName + "/" + type;
                String nacosName = type + ":" + serviceName + "::";

                try {
                    List<Instance> instances = naming.getAllInstances(nacosName);
                    List<String> zooChildren = zk.getChildren(zpath, false);

                    int nacosCount = instances.size();
                    int zkCount = zooChildren.size();

                    if (tarCount != zkCount || tarCount != nacosCount) {
                        System.out.println(Constant.df.format(new Date()) + " " + serviceName
                                + " nacosCount: " + nacosCount + " zkCount: " + zkCount);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String from = System.getProperty("from");
        if (from == null || "".equals(from)) {
            from = "0";
        }
        String end = System.getProperty("end");
        if (end == null || "".equals(end)) {
            end = "10";
        }

        //服务个数
        int fromC = Integer.parseInt(from);
        int endC = Integer.parseInt(end);

        String total = System.getProperty("total");
        if (total == null || "".equals(total)) {
            total = "100";
        }

        String prefix = System.getProperty("prefix");
        if (prefix == null || "".equals(prefix)) {
            prefix = Constant.interfaceLongName;
        }

        int totalC = Integer.parseInt(total);

        System.out.println(" from: " + from + " end: " + end + " totalC: " + totalC + " prefix: " + prefix);

        count(prefix, fromC, endC, totalC);
    }
}
