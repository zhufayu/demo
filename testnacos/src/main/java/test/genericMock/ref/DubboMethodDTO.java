package test.genericMock.ref;

public class DubboMethodDTO {

    private String methodName;
    public void setMethodName(String methodName){
        this.methodName = methodName;
    }
    public String getMethodName(){
        return methodName;
    }

    private String[] arguments;
    public void setArguments(String[] arguments){
        this.arguments = arguments;
    }
    public String[] getArguments(){
        return arguments;
    }


    private String returnType;
    public void setReturnType(String returnType){
        this.returnType = returnType;
    }
    public String getReturnType(){
        return returnType;
    }
}
