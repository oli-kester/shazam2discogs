#Shazam2Discogs

##Usage

Head to web site...

##Development

For development, add a file named `apiSecret.properties` in the directory `src/main/resources/`. 

This should not be checked in to version control, it should follow the following format - 

```
shazam2discogs.api-key=[key for your Discogs Developer App]
shazam2discogs.api-secret=[secret for your Discogs Developer App]

shazam2discogs.test-access-token=[an OAuth user access token for testing]
shazam2discogs.test-access-secret=[an OAuth user access secret for testing]
```

The last two are optional. 

To skip the OAuth flow and use your test keys, set `shazam2discogs.oauth-bypass=true` in `application.properties`.  

Also, add a file named `database.properties`. This will store the username / password used to access your database. 

The format is as follows - 

```

spring.datasource.username=[your desired username here]
spring.datasource.password=[your desired password here]

```