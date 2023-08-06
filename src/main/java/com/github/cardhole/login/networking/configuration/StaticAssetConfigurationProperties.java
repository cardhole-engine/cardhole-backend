package com.github.cardhole.login.networking.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("asset")
public record StaticAssetConfigurationProperties(

        String location
) {
}
