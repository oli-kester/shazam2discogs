package net.olikester.shazam2discogs.service;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.OAuthConsumerToken;
import org.springframework.security.oauth.consumer.OAuthRequestFailedException;
import org.springframework.security.oauth.consumer.client.CoreOAuthConsumerSupport;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("deprecation")
@PropertySource("classpath:apiSecret.properties") // API keys are hidden in second properties file.
public class DiscogsServiceImpl implements DiscogsService {

    private final String USER_AGENT = "Shazam2Discogs/0.1 +http://oli-kester.net";
    @Value("${shazam2discogs.api-key}")
    private String API_KEY;
    @Value("${shazam2discogs.api-secret}")
    private String API_SECRET;

    private BaseProtectedResourceDetails resource;
    private HashMap<String, String> extraHeaderParams;
    private CoreOAuthConsumerSupport consumerSupport;

    @PostConstruct
    private void init() {
	resource = new BaseProtectedResourceDetails();
	extraHeaderParams = new HashMap<>();
	consumerSupport = new CoreOAuthConsumerSupport();

	resource.setId(APP_ID);
	resource.setConsumerKey(API_KEY);
	resource.setSharedSecret(new SharedConsumerSecretImpl(API_SECRET));
	resource.setRequestTokenURL(REQUEST_TOKEN_URL);
	resource.setUserAuthorizationURL(AUTHORIZATION_URL);
	resource.setAccessTokenURL(ACCESS_TOKEN_URL);

	extraHeaderParams.put("User-Agent", USER_AGENT);
	resource.setAdditionalParameters(extraHeaderParams);
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

}
