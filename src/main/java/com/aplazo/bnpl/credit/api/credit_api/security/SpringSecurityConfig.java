package com.aplazo.bnpl.credit.api.credit_api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.aplazo.bnpl.credit.api.credit_api.security.filter.JwtAuthenticationFilter;
import com.aplazo.bnpl.credit.api.credit_api.security.filter.JwtValidationFilter;

@Configuration
public class SpringSecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests((authz) -> authz
                // .requestMatchers("/v1/users").permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/users").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/users/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/customers").hasRole("USER")
                // .requestMatchers(HttpMethod.GET, "/api/products",
                // "/api/products/{id}").hasAnyRole("ADMIN", "USER")
                // .requestMatchers(HttpMethod.POST, "/api/products").hasRole("ADMIN")
                // .requestMatchers(HttpMethod.PUT, "/api/products/{id}").hasRole("ADMIN")
                // .requestMatchers(HttpMethod.DELETE, "/api/products/{id}").hasRole("ADMIN")
                .anyRequest().authenticated())
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtValidationFilter(authenticationManager()))
                .csrf(config -> config.disable())
                // .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

}
