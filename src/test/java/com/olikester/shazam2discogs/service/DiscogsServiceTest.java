package com.olikester.shazam2discogs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.olikester.shazam2discogs.model.JpaOAuthConsumerToken;
import com.olikester.shazam2discogs.model.Release;
import com.olikester.shazam2discogs.model.Tag;
import com.olikester.shazam2discogs.service.DiscogsService;

@SpringBootTest
@TestPropertySource(locations = "classpath:apiSecret.properties") // API keys are hidden in second properties file.
public class DiscogsServiceTest {

    @Value("${shazam2discogs.test-username}")
    private String TEST_USERNAME;
    @Value("${shazam2discogs.test-access-token}")
    private String OAUTH_TOKEN;
    @Value("${shazam2discogs.test-access-secret}")
    private String OAUTH_SECRET;

    private final JpaOAuthConsumerToken accessToken = new JpaOAuthConsumerToken();
    private final Tag testTag1 = new Tag("0", "si00", "Autechre", "SIGN", "Warp Records", 2020, "");

    @Autowired
    DiscogsService discogsService;

    @BeforeEach
    public void setup() {
	accessToken.setHttpSessionId("0");
	accessToken.setAdditionalParameters(new HashMap<String, String>());
	accessToken.setOauthResourceId(DiscogsService.APP_ID);
	accessToken.setAccessToken(true);
	accessToken.setOauthToken(OAUTH_TOKEN);
	accessToken.setOauthSecret(OAUTH_SECRET);
    }

    @DisplayName("Should get no exceptions from a simple login token request.")
    @Test
    public void simpleRestLoginTokenTest() {
	discogsService.fetchRequestToken("/error");
    }

    @SuppressWarnings("unused")
    @DisplayName("Check there's no exceptions from a simple search request. ")
    @Test
    public void searchRequest1() {
	ArrayList<Release> results = discogsService.getReleaseList(testTag1, accessToken);
    }
    
    @DisplayName("Check there's no exceptions from a simple search request. ")
    @Test
    public void searchRequest2() {
	ArrayList<Release> results = discogsService.getReleaseList("si00 Autechre", accessToken);
	results.stream().forEach(result -> System.out.println(result.toString()));
    }

    @DisplayName("Check we're escaping question marks from query parameters")
    @Test
    public void queryStringRemoveQuestionMarksTest() {
	String input = "https://api.discogs.com/database/search?type=release&release_title=Are%20'Friends'%20Electric?%20-%20Single&artist=Tubeway%20Army&label=Beggars%20Banquet&year=1979";
	String expected = "https://api.discogs.com/database/search?type=release&release_title=Are%20'Friends'%20Electric%3F%20-%20Single&artist=Tubeway%20Army&label=Beggars%20Banquet&year=1979";
	assertEquals(expected, DiscogsService.stripIllegalQueryChars(input));
    }
    
    @DisplayName("Check we're escaping semicolons from query parameters")
    @Test
    public void queryStringRemoveSemicolonsTest() {
	String input = "https://api.discogs.com/database/search?query=Red%20(Original%20Mix;Asot%20391)%20Rafael%20Frost";
	String expected = "https://api.discogs.com/database/search?query=Red%20(Original%20Mix%3BAsot%20391)%20Rafael%20Frost";
	assertEquals(expected, DiscogsService.stripIllegalQueryChars(input));
    }
    
    @DisplayName("Test that we can get a valid username back")
    @Test
    public void testUsernameRetrieval() {
	assertEquals(TEST_USERNAME, discogsService.getUserName(accessToken));
    }
    
}
