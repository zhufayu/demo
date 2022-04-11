package test.dmall.netty.server_client.client;

import io.netty.util.internal.PlatformDependent;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 收集netty直接内存占用情况
 * @author xiaoxu
 */
public class NettyDirectMemCollector {
    private AtomicLong directMem = new AtomicLong();

    public NettyDirectMemCollector() {
        Field field = ReflectionUtils.findField(PlatformDependent.class, "DIRECT_MEMORY_COUNTER");
        field.setAccessible(true);
        try {
            directMem = (AtomicLong) field.get(PlatformDependent.class);
        } catch (IllegalAccessException e) {}
    }

    public void logInfo() {
        System.out.println("netty direct memory size: " + directMem.get() + ", max:" + PlatformDependent.maxDirectMemory());
    }
}
