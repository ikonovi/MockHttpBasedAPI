package ik.mock.admin.mappings;

import com.google.gson.Gson;
import ik.mock.admin.client.CloseableHttpClientFactory;
import ik.mock.admin.mappings.entity.Mapping;
import ik.mock.config.TestsConfigReader;
import ik.mock.config.entity.MockProps;
import ik.mock.exceptions.StubMappingExceptionNot;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

@Log4j2
public class StubMappings {
    private final String mockEndpointUrl;
    private final String createStubMappingPath;

    public StubMappings() {
        MockProps mockProps = TestsConfigReader.getTestsConfig().getMockProps();
        this.mockEndpointUrl = mockProps.getAdminEndpointProtocol() + "://" +
                mockProps.getAdminEndpointHost() + ":" + mockProps.getAdminEndpointPort();
        this.createStubMappingPath = mockProps.getCreateStubMappingPath();
    }

    public Mapping createStubMapping(String mappingJson) throws StubMappingExceptionNot {
        CloseableHttpClient httpClient = CloseableHttpClientFactory.getInstance().createHttpClient();
        HttpPost httpPost = new HttpPost(mockEndpointUrl + createStubMappingPath);
        httpPost.setHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType());
        StringEntity stringEntity = new StringEntity(mappingJson, StandardCharsets.UTF_8);
        httpPost.setEntity(stringEntity);
        try(CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            log.debug("New Mapping: statusCode={} \n {}", statusCode, responseBody);
            Mapping mapping = new Gson().fromJson(responseBody, Mapping.class);
            log.debug("{}", mapping);
            return mapping;
        } catch (IOException e) {
            throw new StubMappingExceptionNot("Could not create mapping with json \n" + mappingJson, e);
        }
    }

}
