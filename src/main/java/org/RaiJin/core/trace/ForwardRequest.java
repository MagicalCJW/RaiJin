package org.RaiJin.core.trace;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.RaiJin.util.BodyConverter;

@Data
@EqualsAndHashCode(callSuper = false)
public class ForwardRequest extends IncomingRequest{
    @Setter(AccessLevel.PROTECTED)
    protected String mappingName;
    @Setter(AccessLevel.PROTECTED)
    protected byte[] body;

    public String getBodyAsString() {
        return BodyConverter.convertBodyToString(body);
    }
}
