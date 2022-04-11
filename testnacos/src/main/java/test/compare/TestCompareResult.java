package test.compare;

import com.alibaba.nacos.api.naming.NamingService;
import com.google.common.collect.Sets;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TestCompareResult {
    private static HashMap<String, String> getZKMap(String instance) {
        HashMap<String, String> zkMap = new HashMap<>();

        String[] strs = instance.substring("dubbo://".length()).split("/");
        zkMap.put("host", strs[0].trim());

        String[] tmp = strs[1].trim().split("\\?");
        zkMap.put("name", tmp[0].trim());

        String[] params = tmp[1].trim().split("&");
        for (String par : params) {
            String[] s = par.split("=");
            String key = s[0].trim();
            String value = s[1].trim();

            if (!(key.equals("acl") || key.equals("name"))) {
                zkMap.put(key, value);
            }
        }
        return zkMap;
    }

    public static boolean compareMap(HashMap<String, String> map1, HashMap<String, String> map2) {
        boolean flag = true;
        Set<String> missing = Sets.difference(map1.keySet(), map2.keySet());
        if (missing.size() == 0) {
            for (String key : map1.keySet()) {
                if (!(map1.get(key)).equals(map2.get(key))) {
                    flag = false;
                    break;
                }
            }
        }

        return flag;
    }

    private static HashMap<String, String> getNacosMap(String instance) {
        HashMap<String, String> nacosMap = new HashMap<>();
        if (instance == null || "".equals(instance)) {
            return nacosMap;
        }
        String[] strs = instance.split("@");
        nacosMap.put("host", strs[0].trim());
        String data = strs[1].trim();
        String tmp = data.substring(1, data.length() - 1);

        String[] params = tmp.split(",");
        for (String par : params) {
            if (par.contains("=")) {
                String[] s = par.split("=");
                String key = s[0].trim();
                String value = s[1].trim();

                if (!(key.equals("syncSource") || key.equals("destClusterId") || key.equals("sourceClusterId"))) {
                    nacosMap.put(key, value);
                }
            } else {
                if (nacosMap.containsKey("methods")) {
                    nacosMap.put("methods", nacosMap.get("methods") + "," + par);
                } else {
                    String[] s = par.split("=");
                    String key = s[0].trim();
                    String value = s[1].trim();
                    nacosMap.put(key, value);
                }
            }

        }
        return nacosMap;
    }

    private static boolean compareIntance(ArrayList<String> listZk, ArrayList<String> listNacos) {
        if (listZk.size() != listNacos.size()) {
            return false;
        } else {
            for (String instance : listZk) {
                HashMap<String, String> zkMap = getZKMap(instance);

                boolean flag = false;
                for (String nacosStr : listNacos) {
                    HashMap<String, String> nacosMap = getNacosMap(nacosStr);
                    if (compareMap(zkMap, nacosMap)) {
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    return false;
                }
            }
            return true;
        }
    }

    private static void testCompareResult(ZooKeeper zk, NamingService naming, String serviceName) throws Exception {
        ArrayList<String> nameListZK = new ArrayList();
        TestZk.servicesCount(zk, nameListZK, null);

        ArrayList<String> nameListNacos = new ArrayList();
        TestNacos.servicesCount(naming, nameListNacos, null);

        System.out.println("zk count: " + nameListZK.size());
        System.out.println("nacos count: " + nameListNacos.size());

        Set<String> setZk = new HashSet<>(nameListZK);
        Set<String> setNacos = new HashSet<>(nameListNacos);

        Set<String> diff1 = Sets.difference(setNacos, setZk);
        Set<String> diff2 = Sets.difference(setZk, setNacos);
        System.out.println("nacos exist, zk not exist:" + diff1);
        System.out.println("zk exist, nacos not exist:" + diff2);

        for (String service : nameListZK) {
            if (serviceName != null && !service.contains(serviceName)) {
                continue;
            } else {
                String zpath = "/dubbo/" + service + "/providers";
                ArrayList<String> listZk = TestZk.getChildNode(zk, zpath);

                String name = "providers:" + service + "::";
                ArrayList<String> listNacos = TestNacos.getAllInstances(naming, name);

                boolean proFlag = compareIntance(listZk, listNacos);
                System.out.println(service + " providers: " + proFlag);

                if (!proFlag) {
                    System.out.println("listZk: " + listZk);
                    System.out.println("listNacos: " + listNacos);
                    continue;
                }
            }
        }
    }

    private static void testCompareResult(String serviceName) throws Exception {
        ZooKeeper zk = TestZk.getZK();

        NamingService naming = TestNacos.getNamingService();
        testCompareResult(zk, naming, serviceName);
        zk.close();
    }

    public static void main(String[] args) throws Exception {
        testCompareResult(null);
//        cleanZK();
    }
}
