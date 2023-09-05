package org.apereo.cas.support.wsfederation.web;

import org.apereo.cas.CasProtocolConstants;
import org.apereo.cas.services.DefaultRegisteredServiceProperty;
import org.apereo.cas.services.RegisteredServiceProperty;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.UnauthorizedServiceException;
import org.apereo.cas.support.wsfederation.AbstractWsFederationTests;
import org.apereo.cas.util.MockRequestContext;
import org.apereo.cas.util.http.HttpRequestUtils;
import lombok.val;
import org.apereo.inspektr.common.web.ClientInfo;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.view.RedirectView;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link WsFederationNavigationControllerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.3.0
 */
@Tag("WSFederation")
class WsFederationNavigationControllerTests extends AbstractWsFederationTests {

    @Autowired
    @Qualifier("wsFederationNavigationController")
    private WsFederationNavigationController wsFederationNavigationController;

    @Test
    void verifyOperation() throws Throwable {
        val context = MockRequestContext.create();

        context.getHttpServletRequest().setRemoteAddr("185.86.151.11");
        context.getHttpServletRequest().setLocalAddr("185.88.151.11");
        context.getHttpServletRequest().addHeader(HttpRequestUtils.USER_AGENT_HEADER, "Mozilla/5.0 (Windows NT 10.0; WOW64)");
        ClientInfoHolder.setClientInfo(ClientInfo.from(context.getHttpServletRequest()));

        val config = wsFederationConfigurations.toList().get(0);
        val registeredService = RegisteredServiceTestUtils.getRegisteredService("https://wsfedservice");
        registeredService.setProperties(Map.of(RegisteredServiceProperty.RegisteredServiceProperties.WSFED_RELYING_PARTY_ID.getPropertyName(),
            new DefaultRegisteredServiceProperty(config.getRelyingPartyIdentifier())));
        val service = RegisteredServiceTestUtils.getService(registeredService.getServiceId());
        servicesManager.save(registeredService);

        context.setParameter(CasProtocolConstants.PARAMETER_SERVICE, service.getId());
        val id = config.getId();
        context.setParameter(WsFederationNavigationController.PARAMETER_NAME, id);
        val view = wsFederationNavigationController.redirectToProvider(context.getHttpServletRequest(), context.getHttpServletResponse());
        assertTrue(view instanceof RedirectView);
    }

    @Test
    void verifyMissingId() throws Throwable {
        val context = MockRequestContext.create();

        context.setParameter(WsFederationNavigationController.PARAMETER_NAME, UUID.randomUUID().toString());
        assertThrows(UnauthorizedServiceException.class,
            () -> wsFederationNavigationController.redirectToProvider(context.getHttpServletRequest(), context.getHttpServletResponse()));
    }
}
