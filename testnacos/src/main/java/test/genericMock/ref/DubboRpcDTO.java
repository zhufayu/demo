package test.genericMock.ref;

import java.util.List;

public class DubboRpcDTO {
    private String interfaceName;
    public void setInterfaceName(String interfaceName){
        this.interfaceName = interfaceName;
    }
    public String getInterfaceName(){
        return interfaceName;
    }

    /**
     * 分组
     */
    private String group;

    public void setGroup(String group){
        this.group = group;
    }
    public String getGroup(){
        return group;
    }

    /**
     * 版本
     */
    private String version;

    public void setVersion(String version){
        this.version = version;
    }
    public String getVersion(){
        return version;
    }

    /**
     * 方法列表
     */
    private List<DubboMethodDTO> methodList;
    public void setMethodList( List<DubboMethodDTO> methodList){
        this.methodList = methodList;
    }
    public List<DubboMethodDTO> getMethodList(){
        return methodList;
    }
}
