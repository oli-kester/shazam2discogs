package net.olikester.shazam2discogs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Shazam2discogsApplication {

    public static void main(String[] args) {
	SpringApplication.run(Shazam2discogsApplication.class, args);
    }
}
