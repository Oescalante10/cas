const puppeteer = require('puppeteer');
const assert = require('assert');
const cas = require('../../cas.js');

(async () => {
    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await cas.newPage(browser);
    await cas.gotoLogin(page, "https://github.com");

    await cas.loginWith(page);

    await cas.gotoLogin(page);
    await page.waitForTimeout(1000);

    await cas.assertCookie(page);

    await cas.gotoLogout(page);

    await cas.assertInnerText(page, "#content h2", "Do you, casuser, want to log out completely?");
    await cas.assertVisibility(page, '#logoutButton');
    await cas.assertVisibility(page, '#divServices');
    await cas.assertVisibility(page, '#servicesTable');
    await cas.submitForm(page, "#fm1");

    await cas.logPage(page);
    let url = await page.url();
    assert(url === "https://localhost:8443/cas/logout");

    await page.waitForTimeout(1000);
    await cas.assertCookie(page, false);

    await cas.log("Logout with redirect...");
    await cas.goto(page, "https://localhost:8443/cas/logout?url=https://github.com/apereo/cas");
    await cas.submitForm(page, "#fm1");
    url = await page.url();
    await cas.logPage(page);
    assert(url === "https://github.com/apereo/cas");

    await cas.log("Logout with unauthorized redirect...");
    await cas.goto(page, "https://localhost:8443/cas/logout?url=https://google.com");
    await cas.submitForm(page, "#fm1");
    url = await page.url();
    await page.waitForTimeout(1000);
    await cas.logPage(page);
    assert(url.toString().startsWith("https://localhost:8443/cas/logout"));

    await browser.close();
})();
