package org.lzmhc.api;

public interface UserRequest {
    UserResponse getUser(String accessToken);
}
