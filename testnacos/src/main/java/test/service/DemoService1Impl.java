package test.service;

import org.apache.dubbo.rpc.RpcContext;

import java.util.concurrent.CompletableFuture;

public class DemoService1Impl implements DemoService1 {
    @Override
    public String getPermissions(Long id) {
        return "getPermissions " + id;
    }

    @Override
    public String asyncInvoke(int id) {
        return "asyncInvoke " + id;
    }

    @Override
    public CompletableFuture<String> sayHelloAsync(String name) {
        return CompletableFuture.supplyAsync(() -> {
            return "sayHelloAsync: " + name;
        });
    }

    @Override
    public String helloOnEvent(String name) {
        String index = RpcContext.getContext().getAttachment("AttachmentIndex");
        return "helloOnEvent: " + name + " index:" + index;
    }
}