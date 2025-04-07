package uk.gov.companieshouse.chs.notification.sender.api.config;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import uk.gov.companieshouse.api.filter.CustomCorsFilter;

@Configuration
@EnableWebSecurity

public class SecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http
            .cors(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(new CustomCorsFilter(List.of(GET.name())), CsrfFilter.class)
            .authorizeHttpRequests(request -> request
                .requestMatchers(GET, "/chs-notification-sender-api/**")
                .permitAll()
                .requestMatchers(POST, "/notification-sender/**")
                .permitAll()
                .anyRequest()
                .denyAll())
        ;
        return http.build();
    }

}