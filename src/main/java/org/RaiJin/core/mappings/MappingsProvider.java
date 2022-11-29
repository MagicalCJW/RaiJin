package org.RaiJin.core.mappings;

import org.RaiJin.config.MappingProperties;
import org.RaiJin.config.RaiJinProperties;
import org.RaiJin.core.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;

import java.util.List;

public abstract class MappingsProvider {
    private static final Logger log = LoggerFactory.getLogger(MappingsProvider.class);

    protected final ServerProperties serverProperties;
    protected final RaiJinProperties raiJinProperties;
    protected final HttpClient httpClientProvider;
    protected List<MappingProperties> mappings;

    public MappingsProvider(ServerProperties serverProperties, RaiJinProperties raiJinProperties, ) {

    }
}
