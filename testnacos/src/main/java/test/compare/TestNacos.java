package test.compare;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import test.helper.Constant;

import java.util.*;

public class TestNacos {
    public static int servicesCount(NamingService naming, ArrayList<String> nameList, String name) throws Exception {
        int count = 0;
        ListView<String> listView = naming.getServicesOfServer(1, 100000);
        for (String child : listView.getData()) {

            if (name == null) {
                if ((child.contains(Constant.interfaceShortName) || child.contains("test.service.DemoService")) && naming.getAllInstances(child).size() > 0) {
                    nameList.add(child.split(":")[1]);
                    count++;
                    //System.out.println(child);
                }
            } else {
                if (child.contains(name) && naming.getAllInstances(child).size() > 0) {
                    nameList.add(child.split(":")[1]);
                    count++;
                }
            }
        }
        //System.out.println(count);
        return count;
    }

    public static ArrayList<String> getAllInstances(NamingService naming, String name) throws NacosException {
        ArrayList<String> list = new ArrayList<>();
        List<Instance> instances = naming.getAllInstances(name);
        for (Instance instance : instances) {
            list.add(instance.getIp() + ":" + instance.getPort() + "@" + instance.getMetadata());
        }


        return list;
    }

    public static void getInstance(NamingService naming, String name) throws NacosException {
//        List<Instance> instances = naming.getAllInstances("providers:test.service.gen.DemoService0::");
//        List<Instance> instances2 = naming.getAllInstances("consumers:test.service.gen.DemoService0::");
        List<Instance> instances = naming.getAllInstances(name);
        for (Instance instance : instances) {
            System.out.println(instance.getIp() + ":" + instance.getPort() + instance.getMetadata());
        }
    }

    public static void outPutInfo(NamingService naming, String name, String type) throws NacosException {
        getInstance(naming, type + ":" + name + "::");
    }

    public static void registerInstance() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("serverAddr", Constant.nacosPort);
        properties.setProperty("namespace", "public");

        NamingService naming = NamingFactory.createNamingService(properties);

        naming.registerInstance("nacos.test.3", "11.11.11.11", 8888, "TEST1");

        Instance instance = new Instance();
        instance.setIp("55.55.55.55");
        instance.setPort(9999);
        instance.setHealthy(true);
        instance.setWeight(2.0);
        Map<String, String> instanceMeta = new HashMap<>();
        instanceMeta.put("site", "et2");
        instance.setMetadata(instanceMeta);
        //instance.setServiceName("nacos.test.3");
        naming.registerInstance("nacos.test.4", instance);
    }


    public static NamingService getNamingService() throws NacosException {
        Properties properties = new Properties();
        properties.setProperty("serverAddr", Constant.nacosPort);
        properties.setProperty("namespace", Constant.namespace);
        NamingService naming = NamingFactory.createNamingService(properties);
        return naming;
    }
}
