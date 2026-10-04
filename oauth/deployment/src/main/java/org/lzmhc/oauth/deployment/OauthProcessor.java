package org.lzmhc.oauth.deployment;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import org.lzmhc.api.OAuthProviderRegistry;
import org.lzmhc.gitcode.GitcodeOAuthProvider;
import org.lzmhc.github.GithubOAuthProvider;
import org.lzmhc.security.OauthSingle;
import org.lzmhc.security.TokenUtils;

public class OauthProcessor {

    @BuildStep
    AdditionalBeanBuildItem registerBeans() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClasses(
                        OAuthProviderRegistry.class,
                        GithubOAuthProvider.class,
                        GitcodeOAuthProvider.class,
                        OauthSingle.class,
                        TokenUtils.class
                )
                .setUnremovable()
                .build();
    }
}
