package org.RaiJin.core.trace;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.RaiJin.util.BodyConverter;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = false)
public class ReceivedResponse extends HttpEntity{
    @Setter(AccessLevel.PROTECTED)
    protected HttpStatus status;
    @Setter(AccessLevel.PROTECTED)
    protected byte[] body;

    public String getBodyAsString() {
        return BodyConverter.convertBodyToString(body);
    }
}
