package test.dmall.httpproxy.porxy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DemoContext {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;

    public DemoContext(HttpServletRequest request, HttpServletResponse response) {
        req=request;
        resp = response;
    }
    public HttpServletRequest getServletRequest() {
        return req;
    }
    public HttpServletResponse getServletResponse() {
        return resp;
    }



}
