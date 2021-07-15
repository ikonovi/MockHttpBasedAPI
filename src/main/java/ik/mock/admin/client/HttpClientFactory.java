package ik.mock.admin.client;

import org.apache.http.client.HttpClient;

public interface HttpClientFactory {
    HttpClient createHttpClient();
}
