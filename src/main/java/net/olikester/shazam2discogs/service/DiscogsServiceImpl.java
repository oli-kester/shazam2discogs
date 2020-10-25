package net.olikester.shazam2discogs.service;

import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.common.signature.SignatureSecret;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("deprecation")
@Component
@PropertySource("classpath:apiSecret.properties") // my API keys are hidden in second properties file.
public class DiscogsServiceImpl implements DiscogsService {

    private final String USER_AGENT = "Shazam2Discogs/0.1 +http://oli-kester.net";
    private final MediaType REQUEST_MEDIA_TYPE = MediaType.APPLICATION_FORM_URLENCODED;
    @Value("${shazam2discogs.api-key}")
    private String API_KEY;
    @Value("${shazam2discogs.api-secret}")
    private String API_SECRET;

//    TODO delete this once we know OAuthRestTemplate works
//    public void login2() {
//	RestTemplate discogsOauthTemplate = new RestTemplate();
//	HttpHeaders headers = new HttpHeaders();
//	headers.setContentType(REQUEST_MEDIA_TYPE);
//	StringBuilder authHeaders = new StringBuilder();
//	authHeaders.append("OAuth oauth_consumer_key=\"" + API_KEY + "\",");
//	authHeaders.append("oauth_nonce=\"" + java.time.Clock.systemUTC().instant() + "\",");
//	authHeaders.append("oauth_signature=\"" + API_SECRET + "&\",");
//	authHeaders.append("oauth_signature_method=\"PLAINTEXT\",");
//	authHeaders.append("oauth_timestamp=\"" + java.time.Clock.systemUTC().instant() + "\",");
//	authHeaders.append("oauth_callback=\"null\"");
//	headers.set("Authorization", authHeaders.toString());
//	headers.set("User-Agent", USER_AGENT);
//	HttpEntity<String> entity = new HttpEntity<>(headers);
//
//	ResponseEntity<String> response = discogsOauthTemplate.exchange("https://api.discogs.com/oauth/request_token",
//		HttpMethod.GET, entity, String.class);
//	System.out.println("Response - " + response.toString());
//    }

    @Override
    public void login() {
	BaseProtectedResourceDetails resource = new BaseProtectedResourceDetails();
	resource.setId("discogs");
	resource.setConsumerKey(API_KEY);
	resource.setSharedSecret(new SharedConsumerSecretImpl(API_SECRET));
	resource.setRequestTokenURL("https://api.discogs.com/oauth/request_token");
	resource.setUserAuthorizationURL("https://www.discogs.com/oauth/authorize");
	resource.setAccessTokenURL("https://api.discogs.com/oauth/access_token");
	OAuthRestTemplate discogsOauthTemplate = new OAuthRestTemplate(resource);
	// TODO best practice to insert user agent here. 

	ResponseEntity<String> response = discogsOauthTemplate.exchange("https://api.discogs.com/oauth/request_token",
		HttpMethod.GET, null, String.class);
	System.out.println("Response - " + response.toString());

    }

}
