package io.ankburov.springsessionignite;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.CacheConfiguration;
import org.jetbrains.annotations.NotNull;
import org.springframework.session.Session;

import java.util.function.Function;

@UtilityClass
class SessionRepositoryConfigurationUtils {

    @NotNull
    static <R> R getSessionRepository(@NonNull Ignite ignite,
                                      @NonNull CacheConfiguration<String, Session> sessionCacheConfiguration,
                                      @NonNull Function<CacheMapAdapter<String, Session>, R> repositorySupplier) {
        val sessionCache = ignite.getOrCreateCache(sessionCacheConfiguration);
        val mappedSessionCache = new CacheMapAdapter<String, Session>(sessionCache);
        return repositorySupplier.apply(mappedSessionCache);
    }
}
