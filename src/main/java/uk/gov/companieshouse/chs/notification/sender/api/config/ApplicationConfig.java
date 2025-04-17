package uk.gov.companieshouse.chs.notification.sender.api.config;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static uk.gov.companieshouse.chs.notification.sender.api.ChsNotificationSenderApiApplication.APPLICATION_NAMESPACE;

import consumer.serialization.AvroSerializer;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.filter.CustomCorsFilter;
import uk.gov.companieshouse.api.interceptor.InternalUserInterceptor;
import uk.gov.companieshouse.chs.notification.sender.api.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.kafka.producer.Acks;


@Configuration
@EnableWebSecurity
public class ApplicationConfig implements WebMvcConfigurer {

    private final KafkaProperties kafkaProperties;
    private final SecurityProperties securityProperties;
    private final LoggingInterceptor loggingInterceptor;
    //public static String APPLICATION_NAMESPACE;
    private String emailTopic;
    private String letterTopic;

    public ApplicationConfig(
        //@Value("${spring.application.name}") final String applicationNamespace,
        final KafkaProperties kafkaProperties,
        final SecurityProperties securityProperties,
        final LoggingInterceptor loggingInterceptor
    ) {
        // ApplicationConfig.APPLICATION_NAMESPACE = applicationNamespace;
        emailTopic = kafkaProperties.getEmailTopic();
        letterTopic = kafkaProperties.getLetterTopic();
        this.kafkaProperties = kafkaProperties;
        this.securityProperties = securityProperties;
        this.loggingInterceptor = loggingInterceptor;
    }

    @Override
    public void addInterceptors(@NonNull final InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor);
        registry.addInterceptor(new InternalUserInterceptor(APPLICATION_NAMESPACE));
    }

    @Bean
    public KafkaTemplate<String, byte[]> kafkaTemplate() {
        Acks ack = Acks.valueOf(kafkaProperties.getAcknowledgementsName());
        Map<String, Object> configProps = Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBrokerAddress(),
            ProducerConfig.ACKS_CONFIG, ack.getCode(),
            ProducerConfig.RETRIES_CONFIG, kafkaProperties.getRetries(),
            ProducerConfig.MAX_BLOCK_MS_CONFIG, kafkaProperties.getMaxBlockMilliseconds(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, AvroSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializer.class
        );
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        return http.cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(new CustomCorsFilter(List.of(GET.name())), CsrfFilter.class)
            .authorizeHttpRequests(request -> request
                .requestMatchers(POST, securityProperties.getApiSecurityPath()).permitAll()
                .requestMatchers(GET, securityProperties.getHealthcheckPath()).permitAll()
                .anyRequest().denyAll()
            ).build();
    }

    public String getEmailTopic() {
        return emailTopic;
    }

    public String getLetterTopic() {
        return letterTopic;
    }
}
