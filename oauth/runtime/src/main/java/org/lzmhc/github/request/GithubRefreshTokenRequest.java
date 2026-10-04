package org.lzmhc.github.request;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.ResponseHeader;
import org.lzmhc.api.RefreshTokenRequest;
import org.lzmhc.github.response.GithubAccessTokenResponse;

@RegisterRestClient(baseUri = "https://github.com")
public interface GithubRefreshTokenRequest extends RefreshTokenRequest {
    @POST
    @Path("/oauth/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseHeader(name = "Accept", value = "application/json")
    GithubAccessTokenResponse exchangeCodeForToken(
            @FormParam("grant_type") String grantType,
            @FormParam("refresh_token") String refreshToken
    );
}
