package test.service.notice;

public class NotifyService implements Notify {
    @Override
    public void onInvoke(String name) {
        System.out.println("======oninvoke======, param: " + name);
    }

    @Override
    public void onReturnWithoutParam(String result) {
        System.out.println("======onreturnWithoutParam====== result: " + result);
    }

    @Override
    public void onReturn(String result, String name) {
        System.out.println("======onreturn======, param: " + name + ", result: " + result);
    }

    @Override
    public void onThrow(Throwable ex, String name) {
        System.out.println("======onthrow======, param: " + name + ", exception: " + ex.getMessage());
    }
}
