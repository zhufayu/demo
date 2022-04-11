package test.nacosreg;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import test.helper.Constant;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NacosRegisterTest {

    public static void main(String [] ages) throws Exception{
        ExecutorService EXECUTOR = Executors.newFixedThreadPool(3);
        Properties properties = new Properties();
        properties.setProperty("serverAddr", Constant.nacosPort);
        properties.setProperty("namespace", "public");
        final NamingService naming = NamingFactory.createNamingService(properties);
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread-1 start....");
                Date start = new Date();
                for(int i=0;i<200;i++){
                    for(int j=0;j<200;j++) {
                        //String path = "/dubbo/majitest.service.gen.DemoService" + i + "/providers/" + "10.248.224.73:" + j + "test.service.gen.DemoService19?side=provider&syncSource=NACOS&methods=getPermissions2,getPermissions0,getPermissions1&release=0.0.3-SNAPSHOT&deprecated=false&dubbo=2.0.2&threads=200&pid=14454&destClusterId=c0d4be04dc32daab3775a51e36c1f562&interface=test.service.gen.DemoService19&generic=false&sourceClusterId=f25df9683702cbc8fb629eb4eb74860a&revision=1.0-SNAPSHOT&path=test.service.gen.DemoService19&protocol=dubbo&application=test-provider-xxx&sdk_version=2.7.2&dynamic=true&category=providers&anyhost=true&timestamp=1609833422714";
                        try {
                            naming.registerInstance("majitest.service.gen.DemoService" + i, "10.1.1." + j, 9090);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        // naming.registerInstance("nacos.test.3", "aaaa", "2.2.2.2", 9999, "DEFAULT");
                    }
                }
                Date end = new Date();
                long cost = end.getTime() - start.getTime();
                System.out.println("thread-1 this proicess cost:" + cost/1000/60);
                System.out.println("thread-1 end....");
            }
        });


        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread-2 start....");
                Date start = new Date();
                for(int i=100;i<200;i++){
                    for(int j=0;j<200;j++) {
                        //String path = "/dubbo/majitest.service.gen.DemoService" + i + "/providers/" + "10.248.224.73:" + j + "test.service.gen.DemoService19?side=provider&syncSource=NACOS&methods=getPermissions2,getPermissions0,getPermissions1&release=0.0.3-SNAPSHOT&deprecated=false&dubbo=2.0.2&threads=200&pid=14454&destClusterId=c0d4be04dc32daab3775a51e36c1f562&interface=test.service.gen.DemoService19&generic=false&sourceClusterId=f25df9683702cbc8fb629eb4eb74860a&revision=1.0-SNAPSHOT&path=test.service.gen.DemoService19&protocol=dubbo&application=test-provider-xxx&sdk_version=2.7.2&dynamic=true&category=providers&anyhost=true&timestamp=1609833422714";
                        try {
                            naming.registerInstance("majitest.service.gen.DemoService" + i, "10.1.1." + j, 9090);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        // naming.registerInstance("nacos.test.3", "aaaa", "2.2.2.2", 9999, "DEFAULT");
                    }
                }
                Date end = new Date();
                long cost = end.getTime() - start.getTime();
                System.out.println("thread-2 this proicess cost:" + cost/1000/60);
                System.out.println("thread-2 end....");
            }
        });

        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread-3 start....");
                Date start = new Date();
                for(int i=200;i<300;i++){
                    for(int j=0;j<200;j++) {
                        //String path = "/dubbo/majitest.service.gen.DemoService" + i + "/providers/" + "10.248.224.73:" + j + "test.service.gen.DemoService19?side=provider&syncSource=NACOS&methods=getPermissions2,getPermissions0,getPermissions1&release=0.0.3-SNAPSHOT&deprecated=false&dubbo=2.0.2&threads=200&pid=14454&destClusterId=c0d4be04dc32daab3775a51e36c1f562&interface=test.service.gen.DemoService19&generic=false&sourceClusterId=f25df9683702cbc8fb629eb4eb74860a&revision=1.0-SNAPSHOT&path=test.service.gen.DemoService19&protocol=dubbo&application=test-provider-xxx&sdk_version=2.7.2&dynamic=true&category=providers&anyhost=true&timestamp=1609833422714";
                        try {
                            naming.registerInstance("majitest.service.gen.DemoService" + i, "10.1.1." + j, 9090);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        // naming.registerInstance("nacos.test.3", "aaaa", "2.2.2.2", 9999, "DEFAULT");
                    }
                }
                Date end = new Date();
                long cost = end.getTime() - start.getTime();
                System.out.println("thread-3 this proicess cost:" + cost/1000/60);
                System.out.println("thread-3 end....");
            }
        });


    }
}
