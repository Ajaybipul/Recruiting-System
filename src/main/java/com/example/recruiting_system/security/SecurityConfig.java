package com.example.recruiting_system.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(authz -> authz
                .requestMatchers(
                        "/login",
                        "/register",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/",
                        "/access-denied",
                        "/uploads/**"
                ).permitAll()

                // Job listing and details remain public
                .requestMatchers("/jobs/**", "/apply/**", "/application-status/**").permitAll()

                // Applicant pages (session-based auth check in controller)
                .requestMatchers("/applicant/**").permitAll()

                // Other role-based areas: keep permissive for now (adjust later)
                .requestMatchers("/recruiter/**").permitAll()
                .requestMatchers("/hiring-manager/**").permitAll()
                .requestMatchers("/hr/**").permitAll()
                .requestMatchers("/onboarding/**").permitAll()
                .requestMatchers("/onboard/**").permitAll()
                .requestMatchers("/admin/**").permitAll()

                // everything else
                .anyRequest().permitAll()
            )

            // Always create session (required for your custom login)
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            
            // Restore SecurityContext from session
            .securityContext(ctx -> ctx.requireExplicitSave(false))

            .exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"));

        return http.build();
    }
}
