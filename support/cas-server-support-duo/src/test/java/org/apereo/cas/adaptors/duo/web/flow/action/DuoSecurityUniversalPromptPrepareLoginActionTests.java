package org.apereo.cas.adaptors.duo.web.flow.action;

import org.apereo.cas.BaseCasWebflowMultifactorAuthenticationTests;
import org.apereo.cas.adaptors.duo.BaseDuoSecurityTests;
import org.apereo.cas.authentication.MultifactorAuthenticationPrincipalResolver;
import org.apereo.cas.authentication.mfa.TestMultifactorAuthenticationProvider;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.util.MockRequestContext;
import org.apereo.cas.util.spring.ApplicationContextProvider;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.support.WebUtils;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.webflow.execution.Action;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link DuoSecurityUniversalPromptPrepareLoginActionTests}.
 *
 * @author Misagh Moayyed
 * @since 6.3.0
 */
@SpringBootTest(classes = BaseDuoSecurityTests.SharedTestConfiguration.class,
    properties = {
        "cas.authn.mfa.duo[0].duo-secret-key=Q2IU2i8BFNd6VYflZT8Evl6lF7oPlj3PM15BmRU7",
        "cas.authn.mfa.duo[0].duo-integration-key=DIOXVRZD2UMZ8XXMNFQ5",
        "cas.authn.mfa.duo[0].trusted-device-enabled=true",
        "cas.authn.mfa.duo[0].duo-api-host=theapi.duosecurity.com"
    })
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Tag("DuoSecurity")
class DuoSecurityUniversalPromptPrepareLoginActionTests extends BaseCasWebflowMultifactorAuthenticationTests {
    @Autowired
    @Qualifier(CasWebflowConstants.ACTION_ID_DUO_UNIVERSAL_PROMPT_PREPARE_LOGIN)
    private Action duoUniversalPromptPrepareLoginAction;

    @Autowired
    private ConfigurableApplicationContext configurableApplicationContext;

    @Test
    void verifyOperation() throws Throwable {
        val context = MockRequestContext.create();

        val identifier = casProperties.getAuthn().getMfa().getDuo().get(0).getId();
        val provider = TestMultifactorAuthenticationProvider
            .registerProviderIntoApplicationContext(applicationContext, new TestMultifactorAuthenticationProvider(identifier));

        configurableApplicationContext.getBeansOfType(MultifactorAuthenticationPrincipalResolver.class)
            .forEach((key, value) -> ApplicationContextProvider.registerBeanIntoApplicationContext(applicationContext, value, key));

        WebUtils.putAuthentication(RegisteredServiceTestUtils.getAuthentication(), context);
        WebUtils.putRegisteredService(context, RegisteredServiceTestUtils.getRegisteredService());
        WebUtils.putMultifactorAuthenticationProvider(context, provider);
        val result = duoUniversalPromptPrepareLoginAction.execute(context);
        assertNotNull(result);
        assertNotNull(result.getAttributes().get("result"));
        assertTrue(context.getFlowScope().contains("duoUniversalPromptLoginUrl"));
    }

}
