package com.tig.libraysociallogins.base;

import java.math.BigInteger;
import java.security.SecureRandom;

public class BaseLoginManager {
    public static final int SOCIAL_PROVIDER_GOOGLE=0;
    public static final int SOCIAL_PROVIDER_AMAZON=1;
    public static final int SOCIAL_PROVIDER_FACEBOOK=2;
    /**
     * OAuth 2.0 suggests we generate a token state and use it as a param to request user
     * authentication.Later a token state will be returned by authenticate service, if the
     * state be the same with this one, we can be assured that the response comes from an authentic
     * source rather than a malicious source.
     * <a href="https://developers.google.com/identity/protocols/OpenIDConnect">Click to know more...</a>
     */
    public String genAntiForgeryTokenState() {
        return new BigInteger(130, new SecureRandom()).toString(32);
    }
}
