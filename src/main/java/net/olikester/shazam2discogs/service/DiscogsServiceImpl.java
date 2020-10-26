package net.olikester.shazam2discogs.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.OAuthConsumerToken;
import org.springframework.security.oauth.consumer.OAuthRequestFailedException;
import org.springframework.security.oauth.consumer.client.CoreOAuthConsumerSupport;
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
    public OAuthConsumerToken fetchRequestToken(String callbackURL) {
	BaseProtectedResourceDetails resource = new BaseProtectedResourceDetails();
	HashMap<String, String> extraHeaderParams = new HashMap<String, String>();
	CoreOAuthConsumerSupport consumerSupport = new CoreOAuthConsumerSupport();

	resource.setId("discogs");
	resource.setConsumerKey(API_KEY);
	resource.setSharedSecret(new SharedConsumerSecretImpl(API_SECRET));
	resource.setRequestTokenURL("https://api.discogs.com/oauth/request_token");
	resource.setUserAuthorizationURL("https://www.discogs.com/oauth/authorize");
	resource.setAccessTokenURL("https://api.discogs.com/oauth/access_token");

	extraHeaderParams.put("User-Agent", USER_AGENT);
	resource.setAdditionalParameters(extraHeaderParams);

	OAuthConsumerToken token = consumerSupport.getUnauthorizedRequestToken(resource, callbackURL);

	System.out.println(token.getAdditionalParameters().get("oauth_callback_confirmed"));

	if (token.getAdditionalParameters().get("oauth_callback_confirmed").equals("true")) {
	    return token;
	} else {
	    throw new OAuthRequestFailedException("Callback URL not confirmed by OAuth provider. ");
	}
    }

}
