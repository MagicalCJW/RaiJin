package org.RaiJin.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricsProperties {
    /**
     * Global metrics name prefix
     */
    private String namePrefix = "RAIJIN";
}
