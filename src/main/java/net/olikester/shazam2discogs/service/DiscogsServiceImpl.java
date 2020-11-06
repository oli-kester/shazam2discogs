package net.olikester.shazam2discogs.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.InMemoryProtectedResourceDetailsService;
import org.springframework.security.oauth.consumer.OAuthConsumerToken;
import org.springframework.security.oauth.consumer.OAuthRequestFailedException;
import org.springframework.security.oauth.consumer.client.CoreOAuthConsumerSupport;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.util.concurrent.RateLimiter;

import net.olikester.shazam2discogs.json.DiscogsReleaseSearchResultsDeserializer;
import net.olikester.shazam2discogs.model.JpaOAuthConsumerToken;
import net.olikester.shazam2discogs.model.Release;
import net.olikester.shazam2discogs.model.Tag;

@Service
@SuppressWarnings("deprecation")
@PropertySource("classpath:apiSecret.properties") // API keys are hidden in second properties file.
public class DiscogsServiceImpl implements DiscogsService {

    @Value("${shazam2discogs.api-key}")
    private String API_KEY;
    @Value("${shazam2discogs.api-secret}")
    private String API_SECRET;

    @Value("${shazam2discogs.test-access-token}")
    private String DEV_OAUTH_TOKEN;
    @Value("${shazam2discogs.test-access-secret}")
    private String DEV_OAUTH_SECRET;

    private BaseProtectedResourceDetails resource;
    private HashMap<String, String> extraHeaderParams;
    private CoreOAuthConsumerSupport consumerSupport;
    private InMemoryProtectedResourceDetailsService protectedResourceDetailsService;
    private HashMap<String, BaseProtectedResourceDetails> resourceDetailsStore;

    private static final RateLimiter rateLimiter = RateLimiter.create(1);

    @PostConstruct
    private void init() {
	resource = new BaseProtectedResourceDetails();
	extraHeaderParams = new HashMap<>();
	consumerSupport = new CoreOAuthConsumerSupport();
	protectedResourceDetailsService = new InMemoryProtectedResourceDetailsService();
	resourceDetailsStore = new HashMap<String, BaseProtectedResourceDetails>();

	resource.setId(APP_ID);
	resource.setConsumerKey(API_KEY);
	resource.setSharedSecret(new SharedConsumerSecretImpl(API_SECRET));
	resource.setRequestTokenURL(REQUEST_TOKEN_URL);
	resource.setUserAuthorizationURL(AUTHORIZATION_URL);
	resource.setAccessTokenURL(ACCESS_TOKEN_URL);

	extraHeaderParams.put("User-Agent", USER_AGENT);
	resource.setAdditionalParameters(extraHeaderParams);

	resourceDetailsStore.put(APP_ID, resource);
	protectedResourceDetailsService.setResourceDetailsStore(resourceDetailsStore);
	consumerSupport.setProtectedResourceDetailsService(protectedResourceDetailsService);
    }

    @Override
    public OAuthConsumerToken fetchRequestToken(String callbackURL) {
	rateLimiter.acquire();
	OAuthConsumerToken token = consumerSupport.getUnauthorizedRequestToken(resource, callbackURL);
	if (token.getAdditionalParameters().get("oauth_callback_confirmed").equals("true")) {
	    return token;
	} else {
	    throw new OAuthRequestFailedException("Callback URL not confirmed by OAuth provider. ");
	}
    }

    @Override
    public OAuthConsumerToken fetchAccessToken(OAuthConsumerToken oAuthConsumerToken, String oAuthVerifier) {
	rateLimiter.acquire();
	OAuthConsumerToken token = consumerSupport.getAccessToken(resource, oAuthConsumerToken, oAuthVerifier);
	return token;
    }

    @Override
    public ArrayList<Release> getReleaseList(Tag currTag, JpaOAuthConsumerToken accessToken) {

	UriComponentsBuilder uriComponents = UriComponentsBuilder.fromHttpUrl(DISCOGS_SEARCH_URL)
		.queryParam("type", "release").queryParam("release_title", currTag.getAlbum())
		.queryParam("artist", currTag.getArtist());

	//only add these search parameters if they're not null
	if (currTag.getLabel() != null) {
	    uriComponents.queryParam("label", currTag.getLabel());
	}
	if (currTag.getReleaseYear() != 0) {
	    uriComponents.queryParam("year", currTag.getReleaseYear());
	}
	
	String query = DiscogsService.stripIllegalQueryChars(uriComponents.toUriString());

	ArrayList<Release> releases = new ArrayList<>();

	try {
	    double sleepValue =  rateLimiter.acquire();
	    System.out.println("Slept for - " + sleepValue); //TODO remove
	    InputStream inputStream = consumerSupport.readProtectedResource(new URL(query),
		    accessToken.toOAuthConsumerToken(), "GET");

	    Scanner s = new Scanner(inputStream).useDelimiter("\\A");
	    String json = s.hasNext() ? s.next() : "";
	    s.close();
	    System.out.println("Request succeeded - " + query); // TODO remove

	    ObjectMapper mapper = new ObjectMapper();
	    SimpleModule module = new SimpleModule("DiscogsReleaseSearchResultsDeserializer",
		    new Version(1, 0, 0, null, null, null));
	    module.addDeserializer(ArrayList.class, new DiscogsReleaseSearchResultsDeserializer());
	    mapper.registerModule(module);

	    try {
		releases = mapper.readValue(json, new TypeReference<ArrayList<Release>>() {
		});
	    } catch (JsonProcessingException e) {
		// TODO Discogs JSON not mapped properly
		e.printStackTrace();
	    }

	} catch (OAuthRequestFailedException | IOException e) {
	    // TODO URL was malformed.
	    System.err.println("Request failed - " + query);
//	    e.printStackTrace();
	}

	return releases;
    }

    @Override
    public JpaOAuthConsumerToken createTestAccessToken(String sessionId) {
	JpaOAuthConsumerToken oauthToken = new JpaOAuthConsumerToken();
	oauthToken.setHttpSessionId(sessionId);
	oauthToken.setAdditionalParameters(new HashMap<String, String>());
	oauthToken.setOauthResourceId(DiscogsService.APP_ID);
	oauthToken.setAccessToken(true);
	oauthToken.setOauthToken(DEV_OAUTH_TOKEN);
	oauthToken.setOauthSecret(DEV_OAUTH_SECRET);
	return oauthToken;
    }
}