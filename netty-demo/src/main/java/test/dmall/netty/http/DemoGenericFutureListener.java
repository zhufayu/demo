package test.dmall.netty.http;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class DemoGenericFutureListener implements GenericFutureListener<Future<? super Void>> {
    @Override
    public void operationComplete(Future<? super Void> future) throws Exception {
        System.out.println("DemoGenericFutureListener");
    }
}
