package test.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DemoService1 {
    String getPermissions(Long id);

    String asyncInvoke(int id);

    CompletableFuture<String> sayHelloAsync(String name);

    String helloOnEvent(String name);
}