package uk.gov.companieshouse.chs.notification.sender.api.config;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
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

    private final String apiSecurityPath;
    private final String healthcheckPath;

    public SecurityConfig(
        @Value("${management.endpoints.security.path-mapping.api}") String apiSecurityPath,
        @Value("${management.endpoints.web.path-mapping.health}") String healthcheckPath) {
        this.apiSecurityPath = apiSecurityPath;
        this.healthcheckPath = healthcheckPath;
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http
            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(new CustomCorsFilter(List.of(GET.name())), CsrfFilter.class)
            .authorizeHttpRequests(request -> request
                .requestMatchers(POST, apiSecurityPath).permitAll()
                .requestMatchers(GET, healthcheckPath).permitAll()
                .anyRequest().denyAll()
            );
        return http.build();
    }

}
