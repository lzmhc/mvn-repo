package org.lzmhc.gitcode.auth;

import io.smallrye.config.ConfigMapping;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.lzmhc.api.Authorize;

@ConfigMapping(prefix = "oauth.gitcode")
public interface GitCodeAuthorize extends Authorize {
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
