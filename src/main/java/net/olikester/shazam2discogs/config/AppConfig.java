package net.olikester.shazam2discogs.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean // Used to store a set of sessionIds that have sent 'cancel' requests. 
    public Set<String> cancelTaskSessionIds() {
        return Collections.synchronizedSet(new HashSet<>());
    }
    
    @Bean // Used to store a map of running task progress for each sessionID. 
    public Map<String, Integer> taskProgress() {
        return Collections.synchronizedMap(new HashMap<>());
    }
}