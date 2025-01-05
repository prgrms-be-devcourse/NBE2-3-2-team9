package com.team9.anicare.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    /**
     * OpenAPI Bean을 생성하여 애플리케이션의 OpenAPI 문서 구성을 제공합니다.
     *
     * @return OpenAPI OpenAPI 설정 객체
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    /**
     * API 문서의 기본 정보를 설정합니다.
     *
     * @return Info OpenAPI의 기본 정보 객체
     */
    private Info apiInfo() {
        return new Info()
                .title("Anicare")
                .description("Anicare 프로젝트의 API 문서입니다.")
                .version("1.0");
    }
}