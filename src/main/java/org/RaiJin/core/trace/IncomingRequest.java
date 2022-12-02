package org.RaiJin.core.trace;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.springframework.http.HttpMethod;

@Data
@EqualsAndHashCode(callSuper = false)
public class IncomingRequest extends HttpEntity{
    @Setter(AccessLevel.PROTECTED)
    protected HttpMethod method;
    @Setter(AccessLevel.PROTECTED)
    protected String uri;
    @Setter(AccessLevel.PROTECTED)
    protected String host;
}
