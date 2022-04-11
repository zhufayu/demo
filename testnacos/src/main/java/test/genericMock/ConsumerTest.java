package test.genericMock;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;
import test.helper.Constant;

public class ConsumerTest {
    public static void main(String[] args) throws Exception {
        ApplicationConfig application = new ApplicationConfig();
        application.setName("test-consumer-xxx");

        RegistryConfig registry = new RegistryConfig();
        registry.setAddress(Constant.zkAdd);

        for (int j = 0; j < 5; j++) {
            ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
            reference.setApplication(application);
            reference.setRegistry(registry);

            reference.setInterface("test.service.gen.DemoService" + j);
            reference.setGeneric("true"); // 声明为泛化接口

            GenericService genericService = reference.get();
            Object invokeBack = genericService.$invoke("getPermissions0", new String[]{}, new Object[]{1});

            System.out.println(invokeBack);
            reference.destroy();
        }

    }
}
