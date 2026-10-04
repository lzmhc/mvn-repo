package org.lzmhc.api;

public interface Authorize {
    String url();
    String clientId();
    String redirectUri();
    String indexUri();
    String secret();
}
