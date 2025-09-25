package com.fhk.security.jwt.config;

import com.fhk.core.config.jwt.JwtProperties;
import com.fhk.core.config.jwt.RefreshTokenHasher;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
@SpringBootApplication(scanBasePackages = {"com.fhk.core"})
public class JwtConfig {

	@Bean
	public RefreshTokenHasher refreshTokenHasher(JwtProperties props) throws Exception {
		return new RefreshTokenHasher(props);
	}
}
