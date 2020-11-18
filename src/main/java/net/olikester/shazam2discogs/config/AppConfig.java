package net.olikester.shazam2discogs.config;

import java.util.HashSet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public HashSet<String> cancelSearchRequestSessionIds() {
        return new HashSet<String>();
    }
}