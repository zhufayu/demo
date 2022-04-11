package test.xmlconf;

import org.apache.dubbo.remoting.exchange.ResponseCallback;
import org.apache.dubbo.remoting.exchange.ResponseFuture;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcResult;
import org.apache.dubbo.rpc.protocol.dubbo.FutureAdapter;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import test.service.DemoService1;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Consumer {

    public static void testzk1() {
        try {
            ClassPathXmlApplicationContext context =
                    new ClassPathXmlApplicationContext("consumer.xml");
            context.start();
            System.out.println("Consumer start");

            //同步调用
            DemoService1 demoService = (DemoService1) context.getBean("demoService");
            System.out.println("demoService =================" + demoService.getPermissions(1L));

            //通知调用，调用时设置上下文
            for (int i = 0; i < 5; i++) {
                RpcContext.getContext().setAttachment("AttachmentIndex", "attachment_" + i);
                demoService.helloOnEvent("invoke hello 123");
            }

            //异步调用1
            demoService.asyncInvoke(123);
            Future<DemoService1> demoFuture = RpcContext.getContext().getFuture();
            String result = String.valueOf(demoFuture.get());
            System.out.println("demoFuture =================" + result);

            //异步调用2
            demoService.asyncInvoke(456);
            ResponseFuture responseFuture = ((FutureAdapter) RpcContext.getContext().getFuture()).getFuture();
            responseFuture.setCallback(new ResponseCallback() {
                @Override
                public void done(Object response) {
                    System.out.println("ResponseCallback done");
                }

                @Override
                public void caught(Throwable exception) {
                    System.out.println("caught");
                }
            });

            RpcResult rpcResult = (RpcResult) responseFuture.get();
            System.out.println("ResponseCallback  =================" + rpcResult.getResult());

            //异步调用3
            CompletableFuture<String> completableFuture = demoService.sayHelloAsync("asyncName");
            String resultComple = completableFuture.get();
            System.out.println("completableFuture  =================" + resultComple);

            //泛华调用
            GenericService genericService = (GenericService) context.getBean("genericServiceDemo");
            Object genericServiceResult = genericService.$invoke("getPermissions", new String[]{"java.lang.Long"}, new Object[]{1L});
            System.out.println("genericServiceResult  =================" + genericServiceResult);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void testzk2() {
        try {
            ClassPathXmlApplicationContext context =
                    new ClassPathXmlApplicationContext("consumer.xml");
            context.start();
            System.out.println("Consumer start");

            String className = "test.service.DemoService";
            Class c = Class.forName(className);

            Object bean = context.getBean("proxyService");
            Method method = c.getMethod("getPermissions", Long.class);

            System.out.println("return =================" + method.invoke(bean, 1L));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        testzk1();
    }
}
