package org.lzmhc.github;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.lzmhc.api.OAuthProvider;
import org.lzmhc.api.UserRequest;
import org.lzmhc.github.auth.GitHubAuthorize;
import org.lzmhc.github.request.GithubUserRequest;
import org.lzmhc.github.request.GithubRefreshTokenRequest;
import org.lzmhc.github.request.GithubTokenRequest;
import org.lzmhc.security.OauthSingle;
import org.lzmhc.SnowflakeIdUtil;

@ApplicationScoped
public class GithubOAuthProvider implements OAuthProvider {
    @Inject
    GitHubAuthorize authorize;
    @Inject
    @RestClient
    GithubTokenRequest tokenRequest;
    @Inject
    @RestClient
    GithubUserRequest userRequest;
    @Inject
    @RestClient
    GithubRefreshTokenRequest refreshTokenRequest;
    @Override
    public String getAuthorizeUri(){
        String state = SnowflakeIdUtil.nextId() + "";
        OauthSingle.getInstance().put(state, "github");
        return authorize.url() +
                "client_id="+authorize.clientId()+
                "&redirect_uri="+authorize.redirectUri()+
                "&response_type=code&state="+state;
    }

    public GithubTokenRequest getTokenRequest() {
        return tokenRequest;
    }

    public UserRequest getUserRequest() {
        return userRequest;
    }

    public GithubRefreshTokenRequest getRefreshTokenRequest() {
        return refreshTokenRequest;
    }

    public GitHubAuthorize getAuthorize(){
        return authorize;
    }
}
