package test.helper;

import java.io.File;


public class ClassHelper {
    public static int interfaceCount = 5;
    public static int methondCount = 3;

    public static String packageName = Constant.packageName;

    private static String javaLoc = "C:\\code\\testnacos\\src\\main\\java\\test\\service\\gen";
    private static String headerInterface = "package " + packageName + ";\n" +
            "\n" +
            "import java.util.List; \n" +
            //"import com.dmall.leadwarehouse.sdk.request.sampling.TakeSamplingTaskRequest; \n" +
            "public interface ";

    private static String shortMethodname = "public String getPermissionsGetPermissionsGetPermissions";
    private static String shortMethodPara = "(org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter id1" +
            ", org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter id2" +
            ", org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter id3" +
            ", org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter id4" +
            ", org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter id5" +
            ", org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter id6" +
            ", org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter id7" +
            ", org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter id8" +
            ", org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter id9" +
            ", org.apache.dubbo.remoting.zookeeper.support.AbstractZookeeperTransporter id10) ";

    private static String headerImp = "package test.service.gen;\n" +
            "\n" +
            "import java.util.ArrayList;\n" +
            "import java.util.List;\n" +
            "\n" +
            "public class ";


    public static void outImpl(String interfaceName, String implName) {
        for (int i = 0; i < interfaceCount; i++) {
            String file = javaLoc + "\\" + implName + i + ".java";
            FileHelper.write(file, headerImp + implName + i + " implements " + interfaceName + i + "\n{", false);
            for (int j = 0; j < methondCount; j++) {
                String methond = "public String getPermissions" + j + "(Long id) { return \"getPermissions" + j + " \" + id;    }\n";
                FileHelper.write(file, methond, true);
            }
            FileHelper.write(file, " }", true);
        }
    }


    public static void outInterface(String interfaceName) {
        for (int i = 0; i < interfaceCount; i++) {
            String file = javaLoc + "\\" + interfaceName + i + ".java";
            FileHelper.write(file, headerInterface + interfaceName + i + "\n{", false);
            for (int j = 0; j < methondCount; j++) {
                String methond = shortMethodname + j
                        + shortMethodPara + "; \n";
                FileHelper.write(file, methond, true);
            }
            FileHelper.write(file, " }", true);
        }
    }


    public static void createClass(String interfaceName) {
        File file = new File(javaLoc);
        FileHelper.dealDir(file);
        outInterface(interfaceName);
//        outImpl(interfaceName, interfaceName + "Impl");
    }

    public static void main(String[] args) {
        ClassHelper.interfaceCount = 10;
        ClassHelper.methondCount = 10;

        ClassHelper.createClass(Constant.classLongName);
//        ClassHelper.createClass(Constant.classShortName);
    }
}
