# Spring session ignite #
[![Build Status](https://travis-ci.org/AnkBurov/spring-session-ignite.svg?branch=master)](https://travis-ci.org/AnkBurov/spring-session-ignite)

Spring Session Ignite is a Spring Session extension which uses Apache Ignite as a session storage.

### Usage

* Add `@EnableIgniteHttpSession` annotation to your Spring Boot application class.
* Add property **spring.session.ignite.defaultMaxInactiveInterval** to your property configuration. 
It will set maximum inactive time in seconds for each session. Note that this property only sets DefaultMaxInactiveInterval
for session repository and doesn't set TTL of using Apache Ignite cache. It's best to have same Cache TTL 
in Apache Ignite cache configuration. 

* Register Ignite and CacheConfiguration<String, ExpiringSession> beans in Spring Context (see any 
Apache Ignite Spring configuration guides). CacheConfiguration<String, ExpiringSession> bean will be used to
create Apache Ignite cache that will store Spring sessions.

That's all. All Spring sessions will be stored in created cache.

### Examples

See test `io.ankburov.springsessionignite.SpringSessionIgniteIntegrationTest` and configuration 
`io.ankburov.springsessionignite.TestConfiguration`