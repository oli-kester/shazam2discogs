package net.olikester.shazam2discogs.service;

import org.springframework.security.oauth.consumer.OAuthConsumerToken;

@SuppressWarnings("deprecation")
public interface DiscogsService {
    
    public final String APP_ID = "discogs";
    public final String REQUEST_TOKEN_URL = "https://api.discogs.com/oauth/request_token";
    public final String AUTHORIZATION_URL = "https://www.discogs.com/oauth/authorize";
    public final String ACCESS_TOKEN_URL = "https://api.discogs.com/oauth/access_token";

    OAuthConsumerToken fetchRequestToken(String callbackURL);

}
