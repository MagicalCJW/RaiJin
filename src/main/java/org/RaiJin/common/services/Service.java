package org.RaiJin.common.services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Service is an app on Itachi that runs on a subdomain.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Service {
    //Public ,Auth or Admin
    private int security;
    //if true, service is suppressed in stage and prod
    private boolean restrictDev;
    //backend service to qry
    private String backendDomain;
    //if true, injects a header for HTML responses telling the browser not to cache HTML.
    private boolean noCacheHtml;
}
