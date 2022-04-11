package test.dmall.tcpproxy;

import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.*;


/**
 * @author xu.xiao
 */
public class SSLContextFactory {


    private static Map<String, SslContext> sslContextMap = new HashMap<String, SslContext>();

    public static SslContext getSslContext(String name) {

        SslContext sslCtx = sslContextMap.get(name);
        if (null == sslCtx) {
            try {
                String crtResourcePath = "ssl/"+name+"_ov.crt";
                String keyResourcePaht = "ssl/"+name+"_ov.pkcs8";


                File certChainFile = new File(SSLContextFactory.class.getClassLoader().getResource(crtResourcePath).getPath());
                File keyFile = new File(SSLContextFactory.class.getClassLoader().getResource(keyResourcePaht).getPath());

                List<String> ciphers = Arrays.asList("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256");
                if (certChainFile.exists() && keyFile.exists()) {
                    sslCtx = SslContextBuilder.forServer(certChainFile, keyFile)
                            .trustManager(certChainFile)
                            .protocols("SSLv3","TLSv1", "TLSv1.1", "TLSv1.2")
                            .clientAuth(ClientAuth.NONE)
                            .ciphers(ciphers)
                            .build();
                    sslContextMap.put(name, sslCtx);
                }
            } catch (SSLException e) {
                System.out.println("create sslContext failed: {}");
            }
        }
        return sslCtx;
    }


}
