package test.genericMock;

import org.apache.dubbo.rpc.service.GenericException;
import org.apache.dubbo.rpc.service.GenericService;

public class DemoGenericServiceImpl implements GenericService{
    @Override
    public Object $invoke(String method, String[] parameterTypes, Object[] args) throws GenericException {
        // 这里只有method方法名可以进行区别，所以该类可能需要动态编译
        if (method.equals("hi")) {
            return "hi, " + args[0];
        } else if (method.equals("hello")) {
            return "hello, " + args[0];
        } else if (method.equals("sayHello")) {
            return "say:hello, " + args[0];
        }else if (method.startsWith("getPermissions")) {
            return "say:getPermissions, " + args[0];
        }
        return "未找到该方法，无法mock";
    }
}
