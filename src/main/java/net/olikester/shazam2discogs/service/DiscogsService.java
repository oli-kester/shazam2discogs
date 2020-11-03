package net.olikester.shazam2discogs.service;

import org.springframework.security.oauth.consumer.OAuthConsumerToken;

import net.olikester.shazam2discogs.model.JpaOAuthConsumerToken;
import net.olikester.shazam2discogs.model.MediaFormats;
import net.olikester.shazam2discogs.model.Release;
import net.olikester.shazam2discogs.model.Tag;


@SuppressWarnings("deprecation")
public interface DiscogsService {
    
    public final String APP_ID = "discogs";
    public final String REQUEST_TOKEN_URL = "https://api.discogs.com/oauth/request_token";
    public final String AUTHORIZATION_URL = "https://www.discogs.com/oauth/authorize";
    public final String ACCESS_TOKEN_URL = "https://api.discogs.com/oauth/access_token";
    public final String USER_AGENT = "Shazam2Discogs/0.1 +http://oli-kester.net";
    public final String DISCOGS_SEARCH_URL = "https://api.discogs.com/database/search";

    OAuthConsumerToken fetchRequestToken(String callbackURL);

    OAuthConsumerToken fetchAccessToken(OAuthConsumerToken oAuthConsumerToken, String oAuthVerifier);

    /**
     * Return the first release matching the given search parameters. 
     * @param currTag
     * @param accessToken
     * @return
     */
    Release getRelease(Tag currTag, JpaOAuthConsumerToken accessToken, MediaFormats preferredFormat);

}
