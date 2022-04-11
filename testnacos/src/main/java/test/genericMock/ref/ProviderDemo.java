package test.genericMock.ref;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class ProviderDemo {
    private static void test() {
        DubboRpcDTO dubboRpcDTO = new DubboRpcDTO();
//        dubboRpcDTO.setGroup("dubbo");
//        dubboRpcDTO.setVersion("1.0.0");
        dubboRpcDTO.setInterfaceName("test.service.gen.DemoService" + 0);
        List<DubboMethodDTO> methodList = new LinkedList<>();
        DubboMethodDTO dubboMethodDTO = new DubboMethodDTO();
        dubboMethodDTO.setMethodName("getPermissions");
        dubboMethodDTO.setReturnType("java.lang.String");
        dubboMethodDTO.setArguments(new String[]{"java.lang.String"});
        methodList.add(dubboMethodDTO);
        dubboRpcDTO.setMethodList(methodList);
        try {
            new DubboProvideService().rpcMockProvide(dubboRpcDTO);
            System.out.println("dubbo service started,enter any keys stop");
            Scanner scanner = new Scanner(System.in);
            scanner.next();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws Exception {
        test();
    }
}
