package org.RaiJin.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "itachi")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItachiProperties {
    @NotNull
    private String signingSecret;
}
