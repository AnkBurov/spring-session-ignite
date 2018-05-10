package io.ankburov.springsessionignite

import io.ankburov.springsessionignite.endpoint.TestEndpoint
import org.apache.ignite.Ignite
import org.apache.ignite.Ignition
import org.apache.ignite.cache.CacheMode
import org.apache.ignite.configuration.CacheConfiguration
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.session.ExpiringSession
import java.util.concurrent.TimeUnit
import javax.cache.expiry.AccessedExpiryPolicy
import javax.cache.expiry.Duration

@SpringBootApplication
@EnableIgniteHttpSession
class TestConfiguration : WebSecurityConfigurerAdapter() {

    @Value("\${spring.session.ignite.defaultMaxInactiveInterval:#{null}}")
    private var maxInactiveInterval: Int? = null

    @Bean
    fun igniteConfiguration(): IgniteConfiguration {
        val configuration = IgniteConfiguration()

        val multicastIpFinder = TcpDiscoveryMulticastIpFinder()
        multicastIpFinder.multicastGroup = "228.10.10.250"

        val tcpDiscoverySpi = TcpDiscoverySpi()
        tcpDiscoverySpi.ipFinder = multicastIpFinder
        configuration.discoverySpi = tcpDiscoverySpi

        return configuration
    }

    @Bean
    fun ignite(igniteConfiguration: IgniteConfiguration): Ignite {
        return Ignition.getOrStart(igniteConfiguration)
    }

    @Bean
    fun sessionCacheConfiguration(): CacheConfiguration<String, ExpiringSession> {
        val cacheConfiguration = CacheConfiguration<String, ExpiringSession>()
        cacheConfiguration.name = "spring-session-ignite"
        cacheConfiguration.cacheMode = CacheMode.REPLICATED
        val expirationFactory = AccessedExpiryPolicy.factoryOf(Duration(TimeUnit.SECONDS, maxInactiveInterval!!.toLong()))
        cacheConfiguration.setExpiryPolicyFactory(expirationFactory)
        return cacheConfiguration
    }

    @Bean
    fun testEndpoint() = TestEndpoint()

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and().formLogin().loginProcessingUrl("/login")
                .and()
                .csrf().disable()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.inMemoryAuthentication()
                .withUser("test")
                .password("test")
                .roles("ADMIN")
    }
}