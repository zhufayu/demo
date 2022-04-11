package test.dmall.httpproxy.servlet;

public class BeanManager {
    private static final BeanManager instance = new BeanManager();

    private BeanManager() {
    }

    public static BeanManager getInstance() {
        return instance;
    }

    private String initialization;

    public void setInitialization(String initialization) {
        this.initialization = initialization;
    }

    public String getInitialization() {
        return this.initialization;
    }
}
