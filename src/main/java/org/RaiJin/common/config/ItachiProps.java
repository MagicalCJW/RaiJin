package org.RaiJin.common.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "itachi.common")
public class ItachiProps {
    @NotBlank
    private String sentryDsn;
    //DeployEnv is set by Kubernetes during a new deployment, so we can identify the code version.
    @NotBlank
    private String deployEnv;
}
