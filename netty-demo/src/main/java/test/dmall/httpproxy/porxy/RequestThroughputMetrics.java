package test.dmall.httpproxy.porxy;

import java.util.concurrent.atomic.AtomicLong;

public class RequestThroughputMetrics {

    //入口总流量
    private static AtomicLong incomingFlows = new AtomicLong();
    //出口总流量
    private static AtomicLong outcomingFlows = new AtomicLong();
    //网关积压response body大小
    private static AtomicLong backlogSize = new AtomicLong();

    public static long getIncomingFlows() {
        return incomingFlows.get();
    }

    public static long getOutcomingFlows() {
        return outcomingFlows.get();
    }

    public static long getBacklogSize() {
        return backlogSize.get();
    }

    public static long incrementAndGetIncomingFlows() {
        return incomingFlows.incrementAndGet();
    }

    public static long incrementAndGetOutcomingFlows() {
        return outcomingFlows.incrementAndGet();
    }

    public static void calculateAndGetBacklogSize(long size) {
        backlogSize.addAndGet(size);
    }

    public static void resetOutcomingFlows() {
        outcomingFlows.set(incomingFlows.get());
    }
}
