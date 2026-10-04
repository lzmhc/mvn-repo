package org.lzmhc.gitcode.request;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.lzmhc.api.RefreshTokenRequest;
import org.lzmhc.gitcode.response.GitcodeAccessTokenResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.ResponseHeader;

@RegisterRestClient(baseUri = "https://gitcode.com")
public interface GitcodeRefreshTokenRequest extends RefreshTokenRequest {
    @POST
    @Path("/oauth/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseHeader(name = "Accept", value = "application/json")
    GitcodeAccessTokenResponse exchangeCodeForToken(
            @FormParam("grant_type") String grantType,
            @FormParam("refresh_token") String refreshToken
    );
}
