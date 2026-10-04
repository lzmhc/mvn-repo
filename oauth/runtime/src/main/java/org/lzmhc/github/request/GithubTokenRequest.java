package org.lzmhc.github.request;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.ResponseHeader;
import org.lzmhc.api.TokenRequest;
import org.lzmhc.github.response.GithubAccessTokenResponse;

@RegisterRestClient(baseUri = "https://github.com")
public interface GithubTokenRequest extends TokenRequest {
    @POST
    @Path("/login/oauth/access_token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseHeader(name = "Accept", value = "application/json")
    @Override
    GithubAccessTokenResponse exchangeCodeForToken(
            @FormParam("grant_type") String grantType,
            @FormParam("client_id") String clientId,
            @FormParam("client_secret") String clientSecret,
            @FormParam("code") String code,
            @FormParam("redirect_uri") String redirectUri,
            @FormParam("scope") String scope
    );
}
