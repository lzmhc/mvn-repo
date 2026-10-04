package org.lzmhc.github.auth;

import io.smallrye.config.ConfigMapping;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.lzmhc.api.Authorize;

@ConfigMapping(prefix = "oauth.github")
public interface GitHubAuthorize extends Authorize {
    @ConfigProperty(name = "url")
    String url();
    @ConfigProperty(name = "client-id")
    String clientId();
    @ConfigProperty(name = "redirect-uri")
    String redirectUri();
    @ConfigProperty(name = "index-uri")
    String indexUri();
    @ConfigProperty(name = "secret")
    String secret();
}
