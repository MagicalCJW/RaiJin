package org.RaiJin.common.services;

public class SecurityConstant {
    //Public security means a user may be logged out or in
    public static final int SEC_PUBLIC = 0;
    //Authenticated security means a user must be logged in
    public static final int SEC_AUTHENTICATED=1;
    //Admin security means a user must log in and have sudo flag
    public static final int SEC_ADMIN=2;
}
