package org.RaiJin.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TracingProperties {
    /**
     * Flag for enabling and disabling tracing HTTP requests proxying processes.
     */
    private boolean enableTrace;
}
