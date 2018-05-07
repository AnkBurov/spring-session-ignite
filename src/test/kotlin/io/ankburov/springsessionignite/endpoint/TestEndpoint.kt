package io.ankburov.springsessionignite.endpoint

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest")
class TestEndpoint {

    @GetMapping
    fun authenticatedPing() = "pong"
}