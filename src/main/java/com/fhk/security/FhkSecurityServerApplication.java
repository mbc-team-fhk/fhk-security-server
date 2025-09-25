package com.fhk.security;

import com.fhk.common.exception.GlobalExceptionHandler;
import com.fhk.core.config.jwt.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // BaseEntity
@EnableConfigurationProperties(JwtProperties.class) // JwtProperties
@Import(GlobalExceptionHandler.class) // 임포트
public class FhkSecurityServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FhkSecurityServerApplication.class, args);
    }

}
