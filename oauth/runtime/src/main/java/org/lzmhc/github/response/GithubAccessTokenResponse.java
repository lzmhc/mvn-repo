package org.lzmhc.github.response;

import org.lzmhc.api.AccessTokenResponse;

public class GithubAccessTokenResponse implements AccessTokenResponse {
    private String access_token;
    private String expires_in;
    private String refresh_token;
    private String refresh_token_expires_in;
    private String scope;
    private String token_type;
    @Override
    public String getAccess_token() {
        return access_token;
    }
}
