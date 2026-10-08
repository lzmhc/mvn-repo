package org.lzmhc.gitcode;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.lzmhc.api.OAuthProvider;
import org.lzmhc.api.TokenRequest;
import org.lzmhc.gitcode.auth.GitCodeAuthorize;
import org.lzmhc.gitcode.request.GitCodeUserRequest;
import org.lzmhc.gitcode.request.GitcodeRefreshTokenRequest;
import org.lzmhc.gitcode.request.GitcodeTokenRequest;
import org.lzmhc.security.OauthSingle;
import org.lzmhc.SnowflakeIdUtil;

@ApplicationScoped
public class GitcodeOAuthProvider implements OAuthProvider {
    @Inject
    GitCodeAuthorize authorize;
    @Inject
    @RestClient
    GitcodeTokenRequest tokenRequest;
    @Inject
    @RestClient
    GitCodeUserRequest userRequest;
    @Inject
    @RestClient
    GitcodeRefreshTokenRequest refreshTokenRequest;
    @Override
    public String getAuthorizeUri() {
        String state = SnowflakeIdUtil.nextId() + "";
        OauthSingle.getInstance().put(state, "gitcode");
        return authorize.url() +
                "client_id="+authorize.clientId()+
                "&redirect_uri="+authorize.redirectUri()+
                "&response_type=code&state="+state;
    }

    public TokenRequest getTokenRequest() {
        return tokenRequest;
    }

    public GitCodeUserRequest getUserRequest() {
        return userRequest;
    }

    public GitcodeRefreshTokenRequest getRefreshTokenRequest() {
        return refreshTokenRequest;
    }

    public GitCodeAuthorize getAuthorize(){
        return authorize;
    }
}
