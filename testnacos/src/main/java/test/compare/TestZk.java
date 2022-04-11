package test.compare;

import com.alibaba.dubbo.common.URL;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import test.helper.Constant;


import java.util.ArrayList;
import java.util.List;

public class TestZk {

    public static int servicesCount(ZooKeeper zk, ArrayList<String> nameList, String name) throws KeeperException, InterruptedException {
        int count = 0;
        String zpath = "/dubbo";
        List<String> zooChildren = zk.getChildren(zpath, false);

        for (String child : zooChildren) {
            if(name == null){
                if (child.contains(Constant.interfaceShortName) || child.contains("test.service.DemoService")) {
                    nameList.add(child);
                    count++;
                }
            }else {
                if (child.contains(name) ) {
                    nameList.add(child);
                    count++;
                }
            }

        }
        return count;
    }

    public static int providersInstanceCount(ZooKeeper zk, ArrayList<String> nameList) throws KeeperException, InterruptedException {
        int count = 0;
        for (String name : nameList) {
            String zpath = "/dubbo/" + name + "/providers";
            List<String> zooChildren = zk.getChildren(zpath, false);
            count = count + zooChildren.size();
        }
        return count;
    }

    public static int consumersInstanceCount(ZooKeeper zk, ArrayList<String> nameList) throws KeeperException, InterruptedException {
        int count = 0;
        for (String name : nameList) {
            String zpath = "/dubbo/" + name + "/consumers";
            List<String> zooChildren2 = zk.getChildren(zpath, false);
            count = count + zooChildren2.size();
        }
        return count;
    }

    public static ArrayList<String> getChildNode(ZooKeeper zk, String zpath) throws KeeperException, InterruptedException {
        ArrayList<String> list = new ArrayList<>();
        List<String> zooChildren = zk.getChildren(zpath, false);
        for (String child : zooChildren) {
            list.add(URL.decode(child));
        }
        return list;
    }

    public static void outChildNode(ZooKeeper zk, String zpath) throws KeeperException, InterruptedException {
        List<String> zooChildren = zk.getChildren(zpath, false);
        for (String child : zooChildren) {
            System.out.println(URL.decode(child));
        }

    }

    public static ZooKeeper getZK() {
        ZooKeeper zk = null;
        Watcher wh = new Watcher() {
            public void process(WatchedEvent event) {
                //System.out.println("WatchedEvent >>> " + event.toString());
            }
        };
        try {
            zk = new ZooKeeper(Constant.zkhost, 1000000, wh);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return zk;
    }

    public static void closeZK(ZooKeeper zk) {
        if (zk != null) {
            try {
                zk.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getNode(ZooKeeper zk, String path) throws KeeperException, InterruptedException {
        Stat stat = new Stat();
        return new String(zk.getData("/dubbo", null, stat));
    }

    public static void outPutInfo(ZooKeeper zk, String name, String type) throws KeeperException, InterruptedException {
        //ZooKeeper zk = getZK();
        String zpath = "/dubbo/" + name + "/" + type;
        outChildNode(zk, zpath);
        //closeZK(zk);
    }

    public static void outService(String name, ZooKeeper zk) throws KeeperException, InterruptedException {
        String type = "providers";
        String zpath = "/dubbo/" + name + "/" + type;
        outChildNode(zk, zpath);

        type = "consumers";
        zpath = "/dubbo/" + name + "/" + type;
        outChildNode(zk, zpath);
    }

    public static void createService(String name, ZooKeeper zk) throws Exception {
        String spath = "/dubbo/" + name;
        String proPath = spath + "/providers";
        String conPath = spath + "/consumers";

        String proIns = "dubbo://12.143.150.127:20881/" + name +
                "?anyhost=true&app_code=comdmall&application=" + name +
                "&flag=blue&interface=" + name + "&ip=10.12.197.40&pid=7616&pro_code=demoseq_99&registry.role=3&timestamp=20201203152402";
        String proIns2 = "dubbo://12.143.150.127:20882/" + name +
                "?anyhost=true&app_code=comdmall&application=test-provier-mock" + name +
                "&flag=blue&interface=" + name + "&ip=10.12.197.10&pid=7616&pro_code=demoseq_99&registry.role=3&timestamp=20201203152402";

        String conIns = "consumer://10.12.197.40:0/" + name +
                "?pro_code=noProject&side=consumer&syncSource=NACOS&lazy=false&methods=getPermissions&release=0.0.3-SNAPSHOT&dubbo=2.0.2&interface="
                + name + "&path="
                + name + "&protocol=consumer&application=test-consumer-mock&sticky=false&sdk_version=2.7.2&category=consumers&app_code=noApp&timestamp=1606981982794";
        String conIns2 = "consumer://10.12.197.40:1/" + name +
                "?pro_code=noProject&side=consumer&syncSource=NACOS&lazy=false&methods=getPermissions&release=0.0.3-SNAPSHOT&dubbo=2.0.2&interface="
                + name + "&path="
                + name + "&protocol=consumer&application=test-consumer-mock&sticky=false&sdk_version=2.7.2&category=consumers&app_code=noApp&timestamp=1606981982794";

        zk.create(spath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zk.create(proPath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zk.create(conPath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        zk.create(proPath + "/" + URL.encode(proIns), "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        zk.create(proPath + "/" + URL.encode(proIns2), "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        zk.create(conPath + "/" + URL.encode(conIns), "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        zk.create(conPath + "/" + URL.encode(conIns2), "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    }

    public static void deleteService(String name, ZooKeeper zk) throws Exception {
        if (name.startsWith("test.service.MockService")) {
            String spath = "/dubbo/" + name;
            String proPath = spath + "/providers";
            String conPath = spath + "/consumers";

            String proIns = "dubbo://12.143.150.127:20881/" + name +
                    "?anyhost=true&app_code=comdmall&application=" + name +
                    "&flag=blue&interface=" + name + "&ip=10.12.197.40&pid=7616&pro_code=demoseq_99&registry.role=3&timestamp=20201203152402";
            String proIns2 = "dubbo://12.143.150.127:20882/" + name +
                    "?anyhost=true&app_code=comdmall&application=test-provier-mock" + name +
                    "&flag=blue&interface=" + name + "&ip=10.12.197.10&pid=7616&pro_code=demoseq_99&registry.role=3&timestamp=20201203152402";

            String conIns = "consumer://10.12.197.40:0/" + name +
                    "?pro_code=noProject&side=consumer&syncSource=NACOS&lazy=false&methods=getPermissions&release=0.0.3-SNAPSHOT&dubbo=2.0.2&interface="
                    + name + "&path="
                    + name + "&protocol=consumer&application=test-consumer-mock&sticky=false&sdk_version=2.7.2&category=consumers&app_code=noApp&timestamp=1606981982794";
            String conIns2 = "consumer://10.12.197.40:1/" + name +
                    "?pro_code=noProject&side=consumer&syncSource=NACOS&lazy=false&methods=getPermissions&release=0.0.3-SNAPSHOT&dubbo=2.0.2&interface="
                    + name + "&path="
                    + name + "&protocol=consumer&application=test-consumer-mock&sticky=false&sdk_version=2.7.2&category=consumers&app_code=noApp&timestamp=1606981982794";

            zk.delete(proPath + "/" + URL.encode(proIns), -1);
            zk.delete(proPath + "/" + URL.encode(proIns2), -1);
            zk.delete(conPath + "/" + URL.encode(conIns), -1);
            zk.delete(conPath + "/" + URL.encode(conIns2), -1);

            zk.delete(conPath, -1);
            zk.delete(proPath, -1);
            zk.delete(spath, -1);
        }
    }

    public static void deleteNode(String path, ZooKeeper zk) throws Exception {
        List<String> children = zk.getChildren(path, false);
        for (String pathCd : children) {
            String newPath = path + "/" + pathCd;
            zk.delete(newPath, -1);
        }
        zk.delete(path, -1);
    }
}