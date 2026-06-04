package com.msa4meerkatgram.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "cors")
public record CorsConfig(
        // 허용할 Origin:
        // [Protocol] + [Host(Domain)] + [Port] 설정
        //     [http] + [localhost] + [5173]
        List<String> allowedOrigins,
        Long maxAge
) {
}
