package com.example.syworksboardassignment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/h2-console/**")  // H2 콘솔만 예외
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/boards", "/boards/*", "/login", "/register", "/css/**", "/h2-console/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/boards")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/boards")
                        .permitAll()
                )
                .headers(h -> h.frameOptions(f -> f.disable()));

        return http.build();
    }
}