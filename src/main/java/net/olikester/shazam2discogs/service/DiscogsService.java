package net.olikester.shazam2discogs.service;

import org.springframework.security.oauth.consumer.OAuthConsumerToken;

@SuppressWarnings("deprecation")
public interface DiscogsService {

    OAuthConsumerToken fetchRequestToken(String callbackURL);

}
