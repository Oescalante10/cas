package org.apereo.cas.services;

import org.apereo.cas.util.serialization.JacksonObjectMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is test cases for
 * {@link GroovyRegisteredServiceAccessStrategyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("GroovyServices")
class GroovyRegisteredServiceAccessStrategyTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "GroovyRegisteredServiceAccessStrategyTests.json");

    private static final ObjectMapper MAPPER = JacksonObjectMapperFactory.builder()
        .defaultTypingEnabled(true).build().toObjectMapper();

    @Test
    void checkDefaultAuthzStrategyConfig() throws Throwable {
        val authz = new GroovyRegisteredServiceAccessStrategy();
        authz.setGroovyScript("classpath:accessstrategy.groovy");
        assertTrue(authz.isServiceAccessAllowed());
        assertTrue(authz.isServiceAccessAllowedForSso());
        assertTrue(authz.doPrincipalAttributesAllowServiceAccess(RegisteredServiceAccessStrategyRequest.builder().principalId("test").build()));
        assertNull(authz.getUnauthorizedRedirectUrl());
        assertNull(authz.getDelegatedAuthenticationPolicy());
        assertNotNull(authz.getRequiredAttributes());
    }

    @Test
    void verifySerializationToJson() throws IOException {
        val authz = new GroovyRegisteredServiceAccessStrategy();
        authz.setGroovyScript("classpath:accessstrategy.groovy");
        MAPPER.writeValue(JSON_FILE, authz);

        val strategyRead = MAPPER.readValue(JSON_FILE, GroovyRegisteredServiceAccessStrategy.class);
        assertEquals(authz, strategyRead);
    }
}
