package org.RaiJin.core.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.RaiJin.core.util.BodyConverter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBody {

    protected HttpStatus status;

    protected HttpHeaders headers;

    protected byte[] respBody;

    protected OriginReqBody reqBody;

    public byte[] getBody() {
        return respBody;
    }

    public String getBodyAsString() {
        return BodyConverter.convertBodyToString(respBody);
    }

    public void setBody(String body) {
        this.respBody = BodyConverter.convertStringToBody(body);
    }
}
