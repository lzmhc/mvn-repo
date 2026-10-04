package org.lzmhc.gitcode.request;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.lzmhc.api.UserRequest;
import org.lzmhc.gitcode.response.GitCodeUserResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "https://api.gitcode.com")
public interface GitCodeUserRequest extends UserRequest {
    @GET
    @Path("/api/v5/user")
    GitCodeUserResponse getUser(@QueryParam("access_token") String accessToken);
  }
