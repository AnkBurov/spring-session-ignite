package io.ankburov.springsessionignite

import io.ankburov.springsessionignite.extension.bodyNotNull
import io.ankburov.springsessionignite.extension.getCookiedHeaders
import io.ankburov.springsessionignite.extension.isRedirected
import io.ankburov.springsessionignite.extension.ok
import org.apache.ignite.Ignite
import org.apache.ignite.configuration.CacheConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.session.ExpiringSession
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.LinkedMultiValueMap

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [TestConfiguration::class],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class SpringSessionIgniteIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var ignite: Ignite

    @Autowired
    private lateinit var cacheConfiguration: CacheConfiguration<String, ExpiringSession>

    @Value("\${spring.session.ignite.defaultMaxInactiveInterval:#{null}}")
    private var maxInactiveInterval: Int? = null

    @Test
    fun testIgniteSession() {
        val cookiedHeaders = login()
                .isRedirected()
                .getCookiedHeaders()

        val (sessionNameCookie, sessionId) = getSessionCookie(cookiedHeaders)

        val sessionIgniteCache = ignite.getOrCreateCache(cacheConfiguration)

        val igniteSession = sessionIgniteCache.get(sessionId)
        assertEquals(sessionId, igniteSession?.id)
        assertFalse(igniteSession.isExpired)
        assertEquals(maxInactiveInterval!!, igniteSession?.maxInactiveIntervalInSeconds)

        restTemplate.getForEntity("/rest", String::class.java)
                .ok()
                .bodyNotNull()
    }

    /**
     * Login to website
     */
    private fun login(username: String = getUsername(), password: String = getPassword()): ResponseEntity<String?> {
        val form = LinkedMultiValueMap<String, String>()
        form.add("username", username)
        form.add("password", password)
        return restTemplate.postForEntity("/login", form, String::class.java)
    }

    private fun getSessionCookie(cookiedHeaders: HttpHeaders): Pair<String, String> {
        val cookies = cookiedHeaders.get("Cookie")!![0]

        val splittedSessionCookie = cookies.split(";")
                .first { it.contains("session", ignoreCase = true) }
                .split("=")
        return splittedSessionCookie[0] to splittedSessionCookie[1]
    }

    private fun getUsername() = "test"
    private fun getPassword() = "test"
}