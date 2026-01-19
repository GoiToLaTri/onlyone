package com.zwind.identityservice.configurations;

import com.zwind.identityservice.components.SessionAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {
    private final SessionAuthFilter sessionAuthFilter;

    private static final String[] PUBLIC_ENDPOINT = {
            "/accounts",
            "/authentication/token",
            "/authentication/refresh",
            "/authentication/logout",
    };

    private static final String[] SWAGGER_ENDPOINT = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**"
    };

    SecurityConfig(SessionAuthFilter sessionAuthFilter){
        this.sessionAuthFilter = sessionAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        httpSecurity.authorizeHttpRequests(request
                -> request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINT).permitAll()
                        .requestMatchers(HttpMethod.GET, SWAGGER_ENDPOINT).permitAll()
                .anyRequest().authenticated())
                .exceptionHandling(ex
                        -> ex.authenticationEntryPoint(new SessionAuthenticationEntryPoint()))
                .addFilterBefore(sessionAuthFilter, UsernamePasswordAuthenticationFilter.class);

        httpSecurity.headers(headers -> headers
                .addHeaderWriter(new StaticHeadersWriter("Accept-CH",
                        "Sec-CH-UA-Platform-Version"))
                .addHeaderWriter(new StaticHeadersWriter("Critical-CH",
                        "Sec-CH-UA-Platform-Version"))
                .addHeaderWriter(new StaticHeadersWriter("Permissions-Policy",
                                "ch-ua-platform-version=(self \"http://localhost:5500\"),"))
        );

        httpSecurity.cors(Customizer.withDefaults());
        httpSecurity.csrf(CsrfConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}
