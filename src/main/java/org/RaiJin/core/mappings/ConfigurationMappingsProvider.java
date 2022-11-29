package org.RaiJin.core.mappings;

import org.RaiJin.config.MappingProperties;
import org.RaiJin.config.RaiJinProperties;
import org.RaiJin.core.http.HttpClient;
import org.springframework.boot.autoconfigure.web.ServerProperties;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigurationMappingsProvider extends MappingsProvider{

    public ConfigurationMappingsProvider(ServerProperties serverProperties, RaiJinProperties raiJinProperties, MappingValidator mappingValidator, HttpClient httpClient) {
        super(serverProperties,raiJinProperties,mappingValidator,httpClient);
    }
    @Override
    protected boolean shouldUpdateMappings(HttpServletRequest request) {
        return false;
    }

    @Override
    protected List<MappingProperties> retrieveMappings() {
        return raiJinProperties.getMappings().stream().map(MappingProperties::copy).collect(Collectors.toList());
    }
}
