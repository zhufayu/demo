package com.dmall.testlog;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LogScheduledService {
    private static ThreadPoolExecutor executor = null;
    public static ExecutorService taskPolThreadFactory() {
        if (executor == null) {
            executor = new ThreadPoolExecutor(5, 50,
                    60L, TimeUnit.SECONDS, new ArrayBlockingQueue(10000));
        }
        return executor;
    }
}
