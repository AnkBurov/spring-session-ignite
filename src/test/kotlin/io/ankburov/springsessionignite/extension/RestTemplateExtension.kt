package io.ankburov.springsessionignite.extension

import org.junit.Assert
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

inline fun <reified T> ResponseEntity<T?>.isRedirected(errorMessage: String? = null): ResponseEntity<T?> {
    Assert.assertTrue(errorMessage, this.statusCode.is3xxRedirection())
    return this
}

inline fun <reified T> ResponseEntity<T?>.redirectedTo(target: String, errorMessage: String? = null): ResponseEntity<T?> {
    Assert.assertTrue(errorMessage, this.headers.get("Location")?.get(0)?.contains(target) == true)
    return this
}

inline fun <reified T> ResponseEntity<T?>.ok(message: String? = null): ResponseEntity<T?> {
    Assert.assertEquals(message, HttpStatus.OK, this.statusCode)
    return this
}

inline fun <reified T> ResponseEntity<T?>.isBadRequest(message: String? = null): ResponseEntity<T?> {
    Assert.assertEquals(message, HttpStatus.BAD_REQUEST, this.statusCode)
    return this
}

inline fun <reified T> ResponseEntity<T?>.bodyNotNull(message: String? = null) = this.body ?: throw AssertionError(message)

inline fun <reified R> TestRestTemplate.send(url: String,
                                                        method: HttpMethod,
                                                        requestEntity: HttpEntity<Any>? = null): ResponseEntity<R?> {
    return this.exchange(url, method, requestEntity, object : ParameterizedTypeReference<R>() {})
}

inline fun <reified T> ResponseEntity<T?>.getCookiedHeaders(): HttpHeaders {
    return HttpHeaders().apply {
        val cookies = this@getCookiedHeaders.headers.get(HttpHeaders.SET_COOKIE)
        add(HttpHeaders.COOKIE, cookies?.joinToString(";"))
    }
}