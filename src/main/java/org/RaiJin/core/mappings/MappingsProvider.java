package org.RaiJin.core.mappings;

import org.RaiJin.config.MappingProperties;
import org.RaiJin.config.RaiJinProperties;
import org.RaiJin.core.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MappingsProvider {
    private static final Logger log = LoggerFactory.getLogger(MappingsProvider.class);

    protected final ServerProperties serverProperties;
    protected final RaiJinProperties raiJinProperties;
    protected final HttpClient httpClientProvider;
    protected final MappingValidator mappingValidator;
    protected List<MappingProperties> mappings;

    public MappingsProvider(ServerProperties serverProperties, RaiJinProperties raiJinProperties, MappingValidator mappingValidator, HttpClient httpClient) {
        this.serverProperties = serverProperties;
        this.raiJinProperties = raiJinProperties;
        this.mappingValidator = mappingValidator;
        this.httpClientProvider = httpClient;
    }

    public MappingProperties resolveMapping(String originHost, HttpServletRequest request) {
        if(shouldUpdateMappings(request)){
            updateMappings();
        }
        List<MappingProperties> resolvedMappings = mappings.stream().filter(mapping-> originHost.equalsIgnoreCase(mapping.getHost())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(resolvedMappings))
            return null;
        return resolvedMappings.get(0);
    }

    @PostConstruct
    protected synchronized void updateMappings() {
        List<MappingProperties> newMappings = retrieveMappings();
        mappingValidator.validate(mappings);
        mappings = newMappings;
        httpClientProvider.updateHttpClients(mappings);
        log.info("Destination Mappings Updated {}", mappings);
    }

    protected abstract boolean shouldUpdateMappings(HttpServletRequest request);

    protected abstract List<MappingProperties> retrieveMappings();
}
