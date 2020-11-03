package net.olikester.shazam2discogs.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

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

import net.olikester.shazam2discogs.json.DiscogsReleaseSearchResultsDeserializer;
import net.olikester.shazam2discogs.model.JpaOAuthConsumerToken;
import net.olikester.shazam2discogs.model.MediaFormats;
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

    private BaseProtectedResourceDetails resource;
    private HashMap<String, String> extraHeaderParams;
    private CoreOAuthConsumerSupport consumerSupport;
    private InMemoryProtectedResourceDetailsService protectedResourceDetailsService;
    private HashMap<String, BaseProtectedResourceDetails> resourceDetailsStore;

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
	OAuthConsumerToken token = consumerSupport.getUnauthorizedRequestToken(resource, callbackURL);
	if (token.getAdditionalParameters().get("oauth_callback_confirmed").equals("true")) {
	    return token;
	} else {
	    throw new OAuthRequestFailedException("Callback URL not confirmed by OAuth provider. ");
	}
    }

    @Override
    public OAuthConsumerToken fetchAccessToken(OAuthConsumerToken oAuthConsumerToken, String oAuthVerifier) {
	OAuthConsumerToken token = consumerSupport.getAccessToken(resource, oAuthConsumerToken, oAuthVerifier);
	return token;
    }

    @Override
    public ArrayList<Release> getReleaseList(Tag currTag, JpaOAuthConsumerToken accessToken) {

	UriComponentsBuilder uriComponents = UriComponentsBuilder.fromHttpUrl(DISCOGS_SEARCH_URL)
		.queryParam("type", "release").queryParam("release_title", currTag.getAlbum())
		.queryParam("artist", currTag.getArtist()).queryParam("label", currTag.getLabel())
		.queryParam("year", currTag.getReleaseYear());

	String json = "";

	try {
	    InputStream inputStream = consumerSupport.readProtectedResource(new URL(uriComponents.toUriString()),
		    accessToken.toOAuthConsumerToken(), "GET");

	    Scanner s = new Scanner(inputStream).useDelimiter("\\A");
	    json = s.hasNext() ? s.next() : "";
	    s.close();

	} catch (OAuthRequestFailedException | IOException e) {
	    // TODO URL was malformed.
	    e.printStackTrace();
	}

	ObjectMapper mapper = new ObjectMapper();
	SimpleModule module = new SimpleModule("DiscogsReleaseSearchResultsDeserializer",
		new Version(1, 0, 0, null, null, null));
	module.addDeserializer(ArrayList.class, new DiscogsReleaseSearchResultsDeserializer());
	mapper.registerModule(module);

	ArrayList<Release> releases = new ArrayList<>();

	try {
	    releases = mapper.readValue(json, new TypeReference<ArrayList<Release>>() {
	    });
	} catch (JsonProcessingException e) {
	    // TODO Discogs JSON not mapped properly
	    e.printStackTrace();
	}

	return releases;
    }
}