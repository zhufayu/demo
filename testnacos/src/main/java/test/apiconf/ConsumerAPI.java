package test.apiconf;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;
import test.helper.Constant;
import test.service.DemoService1;
import com.alibaba.dubbo.config.ReferenceConfig;

import java.lang.reflect.Method;


public class ConsumerAPI {
    public static void testone() {
        ApplicationConfig application = new ApplicationConfig();
        application.setName("test-consumer-xxx");

        RegistryConfig registry = new RegistryConfig();
        registry.setAddress(Constant.zkAdd);

        ReferenceConfig<DemoService1> reference = new ReferenceConfig<DemoService1>();
        // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
        reference.setApplication(application);
        reference.setRegistry(registry);
        // 多个注册中心可以用setRegistries()
        reference.setInterface(DemoService1.class);
        reference.setVersion("1.0.0");

        DemoService1 demoService = reference.get();
        System.out.println(demoService.getPermissions(1L));
    }

    //接口个数
    public static void testMutilService(int count) throws Exception {
        ApplicationConfig application = new ApplicationConfig();
        application.setName("test-consumer-xxx");

        RegistryConfig registry = new RegistryConfig();
        registry.setAddress(Constant.zkAdd);

        for (int i = 0; i < count; i++) {
            String interName = "test.service.gen.DemoService" + i;
            Class cInter = Class.forName(interName);

            Method method = cInter.getMethod("getPermissions0", Long.class);

            ReferenceConfig<Object> reference = new ReferenceConfig<>();
            reference.setApplication(application);
            reference.setRegistry(registry);
            reference.setInterface(cInter);
            //reference.setVersion("1.0.0");
            Object serviceBean = reference.get();

            System.out.println(interName + ": " + method.invoke(serviceBean, 1L));
        }
    }

    //接口编号
    public static void testInvoke(int num) throws Exception {
        ApplicationConfig application = new ApplicationConfig();
        application.setName("test-consumer-xxx");

        RegistryConfig registry = new RegistryConfig();
        registry.setAddress(Constant.zkAdd);

        String interName = "test.service.gen.DemoService" + num;
        Class cInter = Class.forName(interName);

        Method method = cInter.getMethod("getPermissions0", Long.class);

        ReferenceConfig<Object> reference = new ReferenceConfig<>();
        reference.setApplication(application);
        reference.setRegistry(registry);
        reference.setInterface(cInter);
        //reference.setVersion("1.0.0");
        Object serviceBean = reference.get();

        System.out.println(interName + ": " + method.invoke(serviceBean, 1L));
    }

    public static void main(String[] args) throws Exception {

//        testMutilService(5);
//        testInvoke(64);
        testone();
    }
}
