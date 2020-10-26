package net.olikester.shazam2discogs.service;

import java.text.ParseException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SuppressWarnings("unused")
@SpringBootTest
public class OauthRequestTokenTest {

    private final String COMPLETE_RESPONSE_BODY_1 = "oauth_token=bedcjjsFbIXZYxBvKMXwVWaWaxFicOEkHwFFQBom&oauth_token_secret=JBETVRkXoqgVqorHHDSnuiPRAXrhqUMUKPkAoIci";
    private final String VALID_TOKEN = "bedcjjsFbIXZYxBvKMXwVWaWaxFicOEkHwFFQBom";
    private final String VALID_SECRET = "JBETVRkXoqgVqorHHDSnuiPRAXrhqUMUKPkAoIci";

    @DisplayName("Basic check to see if the class works for simple get/set")
    @Test
    public void basicTokenTest() {
	OauthRequestToken token = new OauthRequestToken(VALID_TOKEN, VALID_SECRET);

	Assertions.assertEquals(VALID_TOKEN, token.getToken(), "Check token saved properly");
	Assertions.assertEquals(VALID_SECRET, token.getSecret(), "Check secret saved properly");
    }

    @DisplayName("Testing if we can pass in a single request body and get back separated token / secret")
    @Test
    public void tokenParseTest1() throws ParseException {
	OauthRequestToken token = new OauthRequestToken(COMPLETE_RESPONSE_BODY_1);

	Assertions.assertEquals(VALID_TOKEN, token.getToken(), "Check token saved properly");
	Assertions.assertEquals(VALID_SECRET, token.getSecret(), "Check secret saved properly");
    }

    @DisplayName("Testing if an exception is thrown with an empty request body")
    @Test
    public void tokenParseEmptyFailingTest1() {
	try {
	    OauthRequestToken token = new OauthRequestToken("");
	    Assertions.fail("Should have thrown an exception - we provided an empty string request");
	} catch (ParseException e) {
	}
    }

}
