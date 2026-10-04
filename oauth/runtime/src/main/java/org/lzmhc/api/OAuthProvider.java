package org.lzmhc.api;

public interface OAuthProvider {
    String getAuthorizeUri();
    Authorize getAuthorize();
    TokenRequest getTokenRequest();
    UserRequest getUserRequest();
    RefreshTokenRequest getRefreshTokenRequest();
}
