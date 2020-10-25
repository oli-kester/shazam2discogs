package net.olikester.shazam2discogs.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DiscogsServiceTest {

    @Autowired
    DiscogsService discogsService;
    
    @DisplayName("Should get no exceptions from a simple login token request.")
    @Test
    public void simpleRestLoginTokenTest() {
	discogsService.login();
    }
    
}
