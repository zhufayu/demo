package test.genericMock;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.rpc.service.GenericService;
import test.helper.Constant;

import java.util.Date;

public class MockProviderMain {


    public static void main(String[] args) throws Exception {
        ApplicationConfig application = new ApplicationConfig();
        application.setDefault(true);
        application.setName("luckymock");

        String prefix = Constant.interfaceLongName;

        for (int i = 0; i < 5; i++) {
            ProtocolConfig protocol = new ProtocolConfig();
            protocol.setName("dubbo");
            protocol.setPort(20880 + i);
            for (int j = 0; j < 5; j++) {
                ServiceConfig<GenericService> service = new ServiceConfig<GenericService>();
                service.setRegistry(new RegistryConfig(Constant.zkAdd));
                service.setProtocol(protocol);
                service.setApplication(application);
                service.setGeneric("true");

                GenericService genericService = new DemoGenericServiceImpl();
                service.setRef(genericService);

                service.setInterface(prefix + j);
                service.export();
                System.out.println("export: " + Constant.df.format(new Date()) + " id: " + service.getId());
            }
        }

        Thread.sleep(1000 * 30000);
    }
}
