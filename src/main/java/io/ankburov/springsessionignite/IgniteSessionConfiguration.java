package io.ankburov.springsessionignite;

import lombok.NonNull;
import lombok.val;
import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.ExpiringSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

import java.util.Optional;

@Configuration
@EnableSpringHttpSession
public class IgniteSessionConfiguration {

    /**
     * Max inactive interval in seconds
     */
    @Value("${spring.session.ignite.defaultMaxInactiveInterval:#{null}}")
    private Integer maxInactiveInterval;

    @Bean
    public SessionRepository<ExpiringSession> sessionRepository(@NonNull Ignite ignite,
                                                                @NonNull CacheConfiguration<String, ExpiringSession> sessionCacheConfiguration) {
        val sessionCache = ignite.getOrCreateCache(sessionCacheConfiguration);
        val mappedSessionCache = new CacheMapAdapter<String, ExpiringSession>(sessionCache);
        val sessionRepository = new MapSessionRepository(mappedSessionCache);
        Optional.ofNullable(maxInactiveInterval)
                .ifPresent(sessionRepository::setDefaultMaxInactiveInterval);
        return sessionRepository;
    }
}
