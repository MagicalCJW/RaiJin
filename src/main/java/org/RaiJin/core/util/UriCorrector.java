package org.RaiJin.core.util;

import org.apache.commons.lang3.StringUtils;

public class UriCorrector {
    public static String correctUri(String uri) {
        if(StringUtils.isBlank(uri))
            return StringUtils.EMPTY;
        return StringUtils.removeEnd(StringUtils.prependIfMissing(uri,"/"),"/");
    }
}
