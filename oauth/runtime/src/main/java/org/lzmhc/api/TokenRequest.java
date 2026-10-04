package org.lzmhc.api;

public interface TokenRequest {
    AccessTokenResponse exchangeCodeForToken(String grantType,
                                             String clientId,
                                             String clientSecret,
                                             String code,
                                             String redirectUri,
                                             String scope);
}
