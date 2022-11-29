package org.RaiJin.core.http;

import org.RaiJin.config.MappingProperties;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */

public class HttpClient {

    protected Map<String, RestTemplate> httpClients = new HashMap<>();

    public RestTemplate getHttpClient(String name) {
        return httpClients.get(name);
    }

    public void updateHttpClients(List<MappingProperties> mappingPropertiesList) {
        this.httpClients = mappingPropertiesList.stream().collect(Collectors.toMap(MappingProperties::getName, this::createRestTemplate));
    }

    protected RestTemplate createRestTemplate(MappingProperties mappingProperties) {
        CloseableHttpClient client = createHttpClient().build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
        requestFactory.setConnectTimeout(mappingProperties.getTimeout().getConnect());
        requestFactory.setReadTimeout(mappingProperties.getTimeout().getRead());

        return new RestTemplate(requestFactory);
    }

    protected HttpClientBuilder createHttpClient() {
        return HttpClientBuilder.create().useSystemProperties().disableRedirectHandling().disableCookieManagement();
    }
}
