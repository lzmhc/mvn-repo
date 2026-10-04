package org.lzmhc.gitea.request;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.lzmhc.gitea.response.AccessTokenResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.ResponseHeader;

@RegisterRestClient(baseUri = "oauth-api")
public interface TokenRequest {
    @POST
    @Path("/login/oauth/access_token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseHeader(name = "Accept", value = "application/json")
    AccessTokenResponse exchangeCodeForToken(
            @FormParam("grant_type") String grantType,
            @FormParam("client_id") String clientId,
            @FormParam("client_secret") String clientSecret,
            @FormParam("code") String code,
            @FormParam("redirect_uri") String redirectUri
    );
}
