package org.RaiJin.common.auth;

public class AuthConstant {
    public static final String COOKIE_NAME="itachi-RaiJin";
    // header set for internal user id
    public static final String CURRENT_USER_HEADER="RaiJin-current-user-id";
    // AUTHORIZATION_HEADER is the http request header
    // key used for accessing the internal authorization.
    public static final String AUTHORIZATION_HEADER="Authorization";
    // AUTHORIZATION_ANONYMOUS_WEB is set as the Authorization header to denote that
    // a request is being made by an unauthenticated web user
    public static final String AUTHORIZATION_ANONYMOUS_WEB = "RaiJinn-anonymous";
    // AUTHORIZATION_SUPPORT_USER is set as the Authorization header to denote that
    // a request is being made by a RaiJin team member
    public static final String AUTHORIZATION_SUPPORT_USER = "RaiJin-support";
    // AUTHORIZATION_AUTHENTICATED_USER is set as the Authorization header to denote that
    // a request is being made by an authenticated web user
    public static final String AUTHORIZATION_AUTHENTICATED_USER = "RaiJin-authenticated";
    // AUTH ERROR Messages
    public static final String ERROR_MSG_DO_NOT_HAVE_ACCESS = "You do not have access to this service";
    public static final String ERROR_MSG_MISSING_AUTH_HEADER = "Missing Authorization http header";
}
