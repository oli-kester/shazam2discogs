package net.olikester.shazam2discogs.service;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import net.olikester.shazam2discogs.model.JpaOAuthConsumerToken;
import net.olikester.shazam2discogs.model.MediaFormats;
import net.olikester.shazam2discogs.model.Release;
import net.olikester.shazam2discogs.model.Tag;

@SpringBootTest
@TestPropertySource(locations="classpath:apiSecret.properties") // API keys are hidden in second properties file.
public class DiscogsServiceTest {

    @Value("${shazam2discogs.test-access-token}")
    private String OAUTH_TOKEN;
    @Value("${shazam2discogs.test-access-secret}")
    private String OAUTH_SECRET;

    private final JpaOAuthConsumerToken accessToken = new JpaOAuthConsumerToken();
    private final Tag testTag1 = new Tag(0, "si00", "Autechre", "SIGN", "Warp Records", 2020);

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

    @DisplayName("Test sending a search request")
    @Test
    public void searchRequest1() {
	Release result = discogsService.getRelease(testTag1, accessToken, MediaFormats.DIGITAL_HI_RES);
    }

}
