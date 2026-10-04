package org.lzmhc.security;

import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
@Singleton
public class OauthSingle {
    private static Map<String, String> oauthMap;
    private OauthSingle(){}
    public static Map<String, String> getInstance(){
        if(oauthMap ==null){
            oauthMap = new HashMap<>();
        }
        return oauthMap;
    }
}
