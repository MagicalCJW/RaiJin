package org.RaiJin.core.mappings;

import org.RaiJin.config.MappingProperties;
import org.RaiJin.config.RaiJinProperties;
import org.RaiJin.core.http.HttpClient;
import org.springframework.boot.autoconfigure.web.ServerProperties;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class ProgrammaticMappingsProvider extends MappingsProvider{

    public ProgrammaticMappingsProvider(ServerProperties serverProperties, RaiJinProperties raiJinProperties, MappingValidator mappingValidator, HttpClient httpClient) {
        super(serverProperties, raiJinProperties, mappingValidator, httpClient);
    }

    @Override
    protected boolean shouldUpdateMappings(HttpServletRequest request) {
        return false;
    }

    @Override
    protected List<MappingProperties> retrieveMappings() {
        List<MappingProperties> mappings = new ArrayList<>();
//        Map<String, ?> serviceMap =
        return mappings;
    }
}
