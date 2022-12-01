package org.RaiJin.core.mappings;

import org.RaiJin.common.env.EnvConfig;
import org.RaiJin.common.services.Service;
import org.RaiJin.common.services.ServiceDirectory;
import org.RaiJin.config.MappingProperties;
import org.RaiJin.config.RaiJinProperties;
import org.RaiJin.core.http.HttpClient;
import org.springframework.boot.autoconfigure.web.ServerProperties;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class ProgrammaticMappingsProvider extends MappingsProvider{

    protected final EnvConfig envConfig;

    public ProgrammaticMappingsProvider(EnvConfig envConfig,ServerProperties serverProperties, RaiJinProperties raiJinProperties, MappingValidator mappingValidator, HttpClient httpClient) {
        super(serverProperties, raiJinProperties, mappingValidator, httpClient);
        this.envConfig = envConfig;
    }

    @Override
    protected boolean shouldUpdateMappings(HttpServletRequest request) {
        return false;
    }

    @Override
    protected List<MappingProperties> retrieveMappings() {
        List<MappingProperties> mappings = new ArrayList<>();
        Map<String, Service> serviceMap = ServiceDirectory.getMapping();
        for(String key: serviceMap.keySet()) {
            String subDomain = key.toLowerCase();
            Service service = serviceMap.get(key);
            MappingProperties mapping = new MappingProperties();
            mapping.setName(subDomain+"_route");
            mapping.setHost(subDomain+"."+envConfig.getExternalApex());
            // No security on backend right now :-(
            String dest = "http://"+service.getBackendDomain();
            mapping.setDestinations(List.of(dest));
            mappings.add(mapping);
        }
        return mappings;
    }
}
