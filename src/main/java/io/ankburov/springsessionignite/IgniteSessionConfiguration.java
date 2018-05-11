package io.ankburov.springsessionignite;

import lombok.val;
import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.session.MapSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

import java.util.Optional;

import static io.ankburov.springsessionignite.SessionRepositoryConfigurationUtils.getSessionRepository;

@Configuration
@EnableSpringHttpSession
@Import(ReactiveIgniteSessionConfiguration.class)
public class IgniteSessionConfiguration {

    /**
     * Max inactive interval in seconds
     */
    @Value("${spring.session.ignite.defaultMaxInactiveInterval:#{null}}")
    private Integer maxInactiveInterval;

    @Bean
    public SessionRepository<MapSession> sessionRepository(Ignite ignite,
                                                           CacheConfiguration<String, Session> sessionCacheConfiguration) {
        val sessionRepository = getSessionRepository(ignite, sessionCacheConfiguration, MapSessionRepository::new);
        Optional.ofNullable(maxInactiveInterval)
                .ifPresent(sessionRepository::setDefaultMaxInactiveInterval);

        return sessionRepository;
    }
}
