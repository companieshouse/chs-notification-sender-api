package uk.gov.companieshouse.chs.notification.sender.api.config;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.converter.DateToOffsetDateTimeConverter;
import uk.gov.companieshouse.chs.notification.sender.api.mongo.converter.OffsetDateTimeToDateConverter;

@Configuration
@EnableMongoRepositories("uk.gov.companieshouse.chs.notification.sender.api.mongo.repository")
@EnableMongoAuditing(dateTimeProviderRef = "mongodbDatetimeProvider")
public class MongoConfig {

    @Bean( name = "mongodbDatetimeProvider" )
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of( LocalDateTime.now() );
    }

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(
                new DateToOffsetDateTimeConverter(),
                new OffsetDateTimeToDateConverter()
        ));
    }

    @EventListener(ContextRefreshedEvent.class)
    public void initIndicesAfterStartup(ContextRefreshedEvent event) {
        MongoTemplate mongoTemplate = event.getApplicationContext().getBean(MongoTemplate.class);

        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoTemplate.
                getConverter().getMappingContext();

        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);

        // consider only entities that are annotated with @Document
        mappingContext.getPersistentEntities()
                .stream()
                .filter(it -> it.isAnnotationPresent(Document.class))
                .forEach(it -> {

                    IndexOperations indexOps = mongoTemplate.indexOps(it.getType());
                    resolver.resolveIndexFor(it.getType()).forEach(indexOps::createIndex);
                });
    }

}
