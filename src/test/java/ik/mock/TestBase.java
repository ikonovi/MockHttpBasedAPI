package ik.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

@Log4j2
public abstract class TestBase {
    protected WireMockServer mock;

    @BeforeTest
    public void setupMockServer() {
        WireMockConfiguration wireMockConfiguration = new WireMockConfiguration();
        mock = new WireMockServer(wireMockConfiguration);
        mock.start();
    }

    @AfterTest
    public void tearDownMockServer() {
        mock.stop();
    }
}
