package org.lzmhc.api;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.lzmhc.gitcode.GitcodeOAuthProvider;
import org.lzmhc.github.GithubOAuthProvider;

import java.util.HashMap;
import java.util.Map;

@Startup
@Singleton
public class OAuthProviderRegistry {
    @Inject
    GitcodeOAuthProvider gitcodeOAuthProvider;
    @Inject
    GithubOAuthProvider githubOAuthProvider;

    private Map<String, OAuthProvider> oAuthProviderMap;

    @PostConstruct
    void init(){
        oAuthProviderMap = new HashMap<>();
        oAuthProviderMap.put("github", githubOAuthProvider);
        oAuthProviderMap.put("gitcode", gitcodeOAuthProvider);
    }
    public OAuthProvider getProvider(String name){
        return oAuthProviderMap.get(name);
    }
}
