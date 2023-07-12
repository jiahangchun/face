package org.tool.http;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.tool.Digest;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author Administrator
 */
public class SkipHttpsUtil {

    /**
     * 绕过证书
     */
    public HttpClient wrapClient(String ipAddress,
                                 String password) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0,
                                               String arg1)
                        throws
                        CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0,
                                               String arg1)
                        throws
                        CertificateException {
                }
            };
            ctx.init(null,
                    new TrustManager[]{tm},
                    null);
            SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(ctx,
                    NoopHostnameVerifier.INSTANCE);
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setDefaultCredentialsProvider(this.getCred(ipAddress,
                            password))
                    .setSSLSocketFactory(ssf)
                    .build();
            return httpclient;
        } catch (Exception e) {
            return HttpClients.createDefault();
        }
    }

    private CredentialsProvider getCred(String ipAddress,
                                        String password) {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                //请求地址 + 端口号
                new AuthScope(ipAddress,
                        Digest.BASE_PORT),
                // 用户名 + 密码 （用于验证）
                new UsernamePasswordCredentials(Digest.MEG_NAME,
                        password));
        return credsProvider;
    }
}

