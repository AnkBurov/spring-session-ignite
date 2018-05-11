package io.ankburov.springsessionignite;

import lombok.val;
import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSession;
import org.springframework.session.ReactiveMapSessionRepository;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.session.Session;

import java.util.Optional;

import static io.ankburov.springsessionignite.SessionRepositoryConfigurationUtils.getSessionRepository;

@Configuration
@ConditionalOnClass(name = "reactor.core.publisher.Mono")
public class ReactiveIgniteSessionConfiguration {

    /**
     * Max inactive interval in seconds
     */
    @Value("${spring.session.ignite.defaultMaxInactiveInterval:#{null}}")
    private Integer maxInactiveInterval;

    @Bean
    public ReactiveSessionRepository<MapSession> reactiveSessionRepository(Ignite ignite,
                                                                           CacheConfiguration<String, Session> sessionCacheConfiguration) {
        val sessionRepository = getSessionRepository(ignite, sessionCacheConfiguration, ReactiveMapSessionRepository::new);
        Optional.ofNullable(maxInactiveInterval)
                .ifPresent(sessionRepository::setDefaultMaxInactiveInterval);

        return sessionRepository;
    }
}
