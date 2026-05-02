package com.fhk.security;

import com.fhk.common.exception.GlobalExceptionHandler;
import com.fhk.common.logging.config.ApiLoggingFilterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // BaseEntity
@Import({GlobalExceptionHandler.class, ApiLoggingFilterConfig.class}) // 임포트 모듈
public class FhkSecurityServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FhkSecurityServerApplication.class, args);
    }

}
