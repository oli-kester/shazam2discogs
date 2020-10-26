package net.olikester.shazam2discogs.service;

import java.text.ParseException;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.OAuthRequestFailedException;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("deprecation")
@PropertySource("classpath:apiSecret.properties") // my API keys are hidden in second properties file.
public class DiscogsServiceImpl implements DiscogsService {

    private final String USER_AGENT = "Shazam2Discogs/0.1 +http://oli-kester.net";
    private final MediaType REQUEST_MEDIA_TYPE = MediaType.APPLICATION_FORM_URLENCODED;
    @Value("${shazam2discogs.api-key}")
    private String API_KEY;
    @Value("${shazam2discogs.api-secret}")
    private String API_SECRET;

    @Override
    public OauthRequestToken fetchRequestToken() {
	BaseProtectedResourceDetails resource = new BaseProtectedResourceDetails();
	resource.setId("discogs");
	resource.setConsumerKey(API_KEY);
	resource.setSharedSecret(new SharedConsumerSecretImpl(API_SECRET));
	resource.setRequestTokenURL("https://api.discogs.com/oauth/request_token");
	resource.setUserAuthorizationURL("https://www.discogs.com/oauth/authorize");
	resource.setAccessTokenURL("https://api.discogs.com/oauth/access_token");

	HashMap<String, String> extraHeaderParams = new HashMap<String, String>();
	extraHeaderParams.put("User-Agent", USER_AGENT);
	resource.setAdditionalParameters(extraHeaderParams);

	OAuthRestTemplate discogsOauthTemplate = new OAuthRestTemplate(resource);

	ResponseEntity<String> response = discogsOauthTemplate.exchange("https://api.discogs.com/oauth/request_token",
		HttpMethod.GET, null, String.class);

	if (response.getStatusCode().is2xxSuccessful()) {
	    try {
		return new OauthRequestToken(response.getBody());
	    } catch (ParseException e) {
		throw new OAuthRequestFailedException("Format of Discogs Request Token does not match. ");
	    }
	}
	throw new OAuthRequestFailedException("Discogs API Request Token not received successfully. ");
    }

}
