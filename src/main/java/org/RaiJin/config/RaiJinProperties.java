package org.RaiJin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("RaiJin")
@Data
public class RaiJinProperties {
    /**
     * servlet filter order
     */
    private int filterOrder = Ordered.HIGHEST_PRECEDENCE+100;
    /**
     * Enable programmatic mapping or not,
     * false only in dev env, in dev we use mapping via configuration file.
     */
    private boolean enableProgrammaticMapping = true;
    /**
     * Properties responsible for collecting metrics during HTTP requests forwarding.
     */
    @NestedConfigurationProperty
    private MetricsProperties metrics = new MetricsProperties();
    /**
     * Properties responsible for tracing HTTP requests proxying processes.
     */
    @NestedConfigurationProperty
    private TracingProperties tracing = new TracingProperties();
    /**
     * List of proxy mappings
     */
    private List<MappingProperties> mappings =new ArrayList<>();
}
