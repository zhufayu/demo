package test.genericMock.ref;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.metadata.definition.TypeDefinitionBuilder;
import org.apache.dubbo.metadata.definition.model.FullServiceDefinition;
import org.apache.dubbo.metadata.definition.model.MethodDefinition;
import org.apache.dubbo.metadata.definition.model.TypeDefinition;
import org.apache.dubbo.metadata.definition.util.ClassUtils;
import org.apache.dubbo.metadata.identifier.MetadataIdentifier;
import org.apache.dubbo.metadata.store.MetadataReport;
import org.apache.dubbo.metadata.store.MetadataReportFactory;
import org.apache.dubbo.rpc.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.helper.Constant;
import test.genericMock.DemoGenericServiceImpl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class DubboProvideService {
    private static Logger logger = LoggerFactory.getLogger(DubboProvideService.class);
    private MetadataReportFactory metadataReportFactory = ExtensionLoader.getExtensionLoader(MetadataReportFactory.class).getAdaptiveExtension();

    /**
     * 该类很重 自行缓存
     */
    private static ServiceConfig<GenericService> service;

    /**
     * 提供rpc服务
     *
     * @return 是否完成
     */
    public Boolean rpcMockProvide(DubboRpcDTO rpcDTO) throws ClassNotFoundException {

        // 注册并对外暴露服务
        ServiceConfig<GenericService> service = getService();
        service.setGroup(rpcDTO.getGroup());
        service.setVersion(rpcDTO.getVersion());
        service.setInterface(rpcDTO.getInterfaceName());
        // 指向自己实现的通用类实例,需要动态化
        GenericService genericService = new DemoGenericServiceImpl();
        service.setGeneric("true");
        service.setRef(genericService);
        service.export();

        // 注册数据源
        FullServiceDefinition fullServiceDefinition = new FullServiceDefinition();
        TypeDefinitionBuilder builder = new TypeDefinitionBuilder();
        List<TypeDefinition> typeDefinitions = new LinkedList<>();
        List<MethodDefinition> methodDefinitions = new LinkedList<>();
        for (DubboMethodDTO method : rpcDTO.getMethodList()) {
            // 记录方法
            MethodDefinition methodDefinition = new MethodDefinition();
            methodDefinition.setName(method.getMethodName());
            methodDefinition.setParameterTypes(method.getArguments());
            methodDefinition.setReturnType(method.getReturnType());
            methodDefinitions.add(methodDefinition);
            // 记录所有入参的type
            for (String argument : method.getArguments()) {
                TypeDefinition td = builder.build(Class.forName(argument), Class.forName(argument));
                typeDefinitions.add(td);
            }
            // 返回值的type也需要记录
            typeDefinitions.add(builder.build(Class.forName(method.getReturnType()), Class.forName(method.getReturnType())));
        }
        // 拼接服务内容
        Map<String, String> parameters = new HashMap<>(16);
        parameters.put("side", "provider");
        parameters.put("release", "2.7.3");
        parameters.put("methods", "*");
        parameters.put("deprecated", "false");
        parameters.put("dubbo", "2.0.2");
        parameters.put("interface", rpcDTO.getInterfaceName());
        parameters.put("version", rpcDTO.getVersion());
        parameters.put("generic", "true");
        parameters.put("application", "luckymock");
        parameters.put("dynamic", "true");
        parameters.put("register", "true");
        parameters.put("group", rpcDTO.getGroup());
        parameters.put("anyhost", "true");
        fullServiceDefinition.setParameters(parameters);
        fullServiceDefinition.setCodeSource(ClassUtils.getCodeSource(this.getClass()));
        fullServiceDefinition.setCanonicalName(rpcDTO.getInterfaceName());
        fullServiceDefinition.setTypes(typeDefinitions);
        fullServiceDefinition.setMethods(methodDefinitions);
        // 拼接服务描述
        MetadataIdentifier metadataIdentifier = new MetadataIdentifier();
        metadataIdentifier.setServiceInterface(rpcDTO.getInterfaceName());
        metadataIdentifier.setVersion(rpcDTO.getVersion());
        metadataIdentifier.setGroup(rpcDTO.getGroup());
        metadataIdentifier.setSide("provider");
        // 应用名统一为luckymock
        metadataIdentifier.setApplication("luckyMock");
        // 写元数据中心
        MetadataReport metadataReport = metadataReportFactory.getMetadataReport(URL.valueOf(Constant.zkAdd));
        metadataReport.storeProviderMetadata(metadataIdentifier, fullServiceDefinition);
        logger.info("注册RPC服务成功：{}", rpcDTO.getInterfaceName());
        return true;

    }

    /**
     * 该类很重，缓存
     *
     * @return @Service的信息
     */
    private static ServiceConfig<GenericService> getService() {
        if (null == service) {
            service = new ServiceConfig<>();
            // 注册中心配置
            RegistryConfig registryConfig = new RegistryConfig();
            registryConfig.setAddress(Constant.zkAdd);
            service.setRegistry(registryConfig);
            // 应用配置
            ApplicationConfig application = new ApplicationConfig();
            application.setName("luckymock");
            service.setApplication(application);
            // 协议配置
            ProtocolConfig protocol = new ProtocolConfig();
            protocol.setName("dubbo");
            protocol.setPort(20880);
            service.setProtocol(protocol);
        }
        return service;
    }
}
