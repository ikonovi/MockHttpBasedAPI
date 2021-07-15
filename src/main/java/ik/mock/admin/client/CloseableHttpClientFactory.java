package ik.mock.admin.client;

import ik.mock.config.TestsConfigReader;
import ik.mock.config.entity.HttpClientProps;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class CloseableHttpClientFactory implements HttpClientFactory {

    private static final CloseableHttpClientFactory instance = new CloseableHttpClientFactory();
    private static final int connectionRequestTimeoutMillis;
    private static final int connectTimeoutMillis;

    static {
        HttpClientProps httpClientProps = TestsConfigReader.getTestsConfig().getHttpClientProps();
        connectionRequestTimeoutMillis = httpClientProps.getConnectionRequestTimeoutMillis();
        connectTimeoutMillis = httpClientProps.getConnectTimeoutMillis();
    }

    private CloseableHttpClientFactory() {
    }

    public static CloseableHttpClientFactory getInstance() {
        return instance;
    }

    @Override
    public CloseableHttpClient createHttpClient() {
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeoutMillis)
                .setConnectTimeout(connectTimeoutMillis)
                .build();
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .build();
    }
}
