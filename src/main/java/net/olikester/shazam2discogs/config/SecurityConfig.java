package net.olikester.shazam2discogs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(WebSecurity web) throws Exception {
	web.ignoring().antMatchers("/**"); // allow unauthenticated access everywhere (we base authentication on
					   // sessionID).
    }
}
