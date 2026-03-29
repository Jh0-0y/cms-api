package com.malgn.configure.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI openAPI() {
        String securitySchemeName = "Cookie Authentication";

        String description = """
                간단한 CMS 콘텐츠 관리 REST API

                ---

                ## 공통 응답 포맷

                모든 API 응답은 `CustomResponse<T>` 형태로 감싸서 반환됩니다.

                `null` 필드는 응답에서 생략됩니다.

                ### 성공 응답 예시

                ```json
                {
                  "success": true,
                  "data": { ... }
                }
                ```

                ### 실패 응답 예시

                ```json
                {
                  "success": false,
                  "message": "에러 메시지"
                }
                ```

                ---

                ## 인증

                로그인 후 발급되는 `access_token` HttpOnly 쿠키로 인증합니다.
                각 API 옆의 자물쇠 아이콘을 클릭해 쿠키 값을 직접 입력할 수 있습니다.
                """;

        return new OpenAPI()
                .info(new Info()
                        .title("Simple CMS REST API")
                        .description(description)
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name("access_token")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .description("로그인 시 발급되는 Access Token (HttpOnly 쿠키)")));
    }

}
