package org.lzmhc.github.request;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.lzmhc.api.UserRequest;
import org.lzmhc.github.response.GithubUserResponse;

@RegisterRestClient(baseUri = "https://api.github.com")
public interface GithubUserRequest extends UserRequest {
    default GithubUserResponse getUser(String accessToken){
        return this.getUserInternal("Bearer "+accessToken);
    }
    @GET
    @Path("/user")
    @ClientHeaderParam(name = "Accept", value = "application/vnd.github+json")
    GithubUserResponse getUserInternal(@HeaderParam("Authorization") String accessToken);
}
