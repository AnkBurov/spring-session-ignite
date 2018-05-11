package io.ankburov.springsessionignite

import io.ankburov.springsessionignite.extension.bodyNotNull
import io.ankburov.springsessionignite.extension.getCookiedHeaders
import io.ankburov.springsessionignite.extension.isRedirected
import io.ankburov.springsessionignite.extension.ok
import org.apache.ignite.Ignite
import org.apache.ignite.configuration.CacheConfiguration
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.session.MapSession
import org.springframework.session.Session
import org.springframework.session.SessionRepository
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.LinkedMultiValueMap

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [TestConfiguration::class],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class SpringSessionIgniteIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @SpyBean
    private lateinit var sessionRepository: SessionRepository<MapSession>

    @Autowired
    private lateinit var ignite: Ignite

    @Autowired
    private lateinit var cacheConfiguration: CacheConfiguration<String, Session>

    @Value("\${spring.session.ignite.defaultMaxInactiveInterval:#{null}}")
    private var maxInactiveInterval: Int? = null

    @Test
    @DirtiesContext
    fun testIgniteSession() {
        val cookiedHeaders = login()
                .isRedirected()
                .getCookiedHeaders()
        restTemplate.exchange("/rest", HttpMethod.GET, HttpEntity<Any>(cookiedHeaders), String::class.java)
                .ok()
                .bodyNotNull() == "pong"
        Mockito.verify(sessionRepository, Mockito.times(1)).createSession()
    }

    @Test
    @DirtiesContext
    fun testNotAuthenticated() {
        val body =  restTemplate.exchange("/rest", HttpMethod.GET, HttpEntity.EMPTY, String::class.java)
                .ok()
                .bodyNotNull()
        assertNotNull(body)
        assertTrue(body.contains("login"))
        assertFalse(body.contains("pong"))
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
    private fun getPassword() = "{noop}test"
}