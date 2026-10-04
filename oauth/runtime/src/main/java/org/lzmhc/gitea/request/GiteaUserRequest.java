package org.lzmhc.gitea.request;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.lzmhc.gitea.response.GiteaUserResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "oauth-api")
public interface GiteaUserRequest {
    @GET
    @Path("/api/v1/user")
    GiteaUserResponse getUser(@QueryParam("access_token") String accessToken);
    @GET
    @Path("/api/v1/user/emails")
    String getEmails(@QueryParam("access_token") String accessToken);
}
