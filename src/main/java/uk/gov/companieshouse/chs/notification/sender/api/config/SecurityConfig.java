package uk.gov.companieshouse.chs.notification.sender.api.config;

import static uk.gov.companieshouse.csrf.config.ChsCsrfMitigationHttpSecurityBuilder.configureApiCsrfMitigations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Order(1)
    @Bean
    public SecurityFilterChain filterChainActuator(final HttpSecurity http) throws Exception {
        http
            .cors(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .securityMatcher("/actuator/**");
        return configureApiCsrfMitigations(http).build();
    }

    @Order(2)
    @Bean
    public SecurityFilterChain filterChainNotificationSender(final HttpSecurity http) throws Exception {
        http
            .cors(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .securityMatcher("/notification-sender/**")
            // some possible further granularity in control
            //.authorizeHttpRequests(req -> req.requestMatchers("/notification-sender/**").hasRole("NOTIFIER"))
            ;
        return configureApiCsrfMitigations(http).build();
    }
}