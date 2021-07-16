package ik.mock.admin.mappings.service;

import com.google.gson.Gson;
import ik.mock.admin.client.CloseableHttpClientFactory;
import ik.mock.admin.mappings.entity.Mapping;
import ik.mock.config.TestsConfigReader;
import ik.mock.config.entity.MockProps;
import ik.mock.exceptions.MappingHttpServiceException;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

@Log4j2
public class MappingHttpService {
    private final String endPointContextUrl;
    private final String endPointAdminMappingsPath;
    CloseableHttpClient httpClient;

    public MappingHttpService() {
        MockProps mockProps = TestsConfigReader.getTestsConfig().getMockProps();
        this.endPointContextUrl = mockProps.getAdminEndpointProtocol() + "://" +
                mockProps.getAdminEndpointHost() + ":" + mockProps.getAdminEndpointPort();
        this.endPointAdminMappingsPath = mockProps.getEndPointAdminMappingsPath();
        this.httpClient = CloseableHttpClientFactory.getInstance().createHttpClient();
    }

    public Mapping createStubMapping(String mappingJson) throws MappingHttpServiceException {
        HttpPost httpPost = new HttpPost(endPointContextUrl + endPointAdminMappingsPath);
        httpPost.setHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType());
        StringEntity stringEntity = new StringEntity(mappingJson, StandardCharsets.UTF_8);
        httpPost.setEntity(stringEntity);
        try(CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            log.debug("Created new stub mapping: statusCode={} \n {}", statusCode, responseBody);
            Mapping mapping = new Gson().fromJson(responseBody, Mapping.class);
            log.debug("New stub mapping Object: \n {}", mapping);
            return mapping;
        } catch (IOException e) {
            throw new MappingHttpServiceException("Could not create mapping with json \n" + mappingJson, e);
        }
    }

    public void deleteAllStubMappings() throws MappingHttpServiceException {
        HttpDelete httpDelete = new HttpDelete(endPointContextUrl + endPointAdminMappingsPath);
        try(CloseableHttpResponse response = httpClient.execute(httpDelete)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new MappingHttpServiceException("Status code is not 200: " + statusCode);
            }
            log.debug("Deleted all stub mappings");
        } catch (IOException exception) {
            throw new MappingHttpServiceException("Could not delete mappings", exception);
        }
    }

}
