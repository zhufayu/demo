package test.service.notice;

public interface Notify {
    public void onInvoke(String name);

    public void onReturnWithoutParam(String result);

    public void onReturn(String result, String name);

    public void onThrow(Throwable ex, String name);
}
