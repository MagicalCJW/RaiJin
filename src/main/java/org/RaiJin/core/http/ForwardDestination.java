package org.RaiJin.core.http;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.net.URI;
@Data
@AllArgsConstructor
public class ForwardDestination {
    @Setter(AccessLevel.NONE)
    protected final URI uri;
    @Setter(AccessLevel.NONE)
    protected final String mappingName;
    @Setter(AccessLevel.NONE)
    protected final String mappingMetricsName;
}
