package org.lzmhc.github.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.lzmhc.api.UserResponse;

public class GithubUserResponse implements UserResponse {
    @JsonProperty("login")
    private String login;
    @JsonProperty("id")
    private String id;
    @JsonProperty("avatar_url")
    private String avatarUrl;
    @JsonProperty("name")
    private String name;
    @JsonProperty("email")
    private String email;
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getAvatarUrl() {
        return avatarUrl;
    }
}
