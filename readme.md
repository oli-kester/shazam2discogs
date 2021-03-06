# Shazam2Discogs
<p align="center">
  <a href="https://shazam2discogs.olikester.com">
    <img
      alt="Shazam2Discogs"
      src="https://olikester.blob.core.windows.net/$web/assets/project-images/s2d.png"
      width="90%"
    />
  </a>
</p>

This is a Spring Boot application that takes a CSV file of Shazam Tags, matches them with your chosen media format, and adds them to your Discogs Wantlist. 

## Usage

The service is hosted at [shazam2discogs.olikester.com](https://shazam2discogs.olikester.com). 

## Development

For development, two files must be manually added - 

### 1. API Access Keys

First, add a file named `apiSecret.properties` in the directory `src/main/resources/`. 

This should not be checked in to version control, as it will contain sensitive information. 

It should follow the following format - 

```
shazam2discogs.api-key=[key for your Discogs Developer App]
shazam2discogs.api-secret=[secret for your Discogs Developer App]

shazam2discogs.test-access-token=[an OAuth user access token for testing]
shazam2discogs.test-access-secret=[an OAuth user access secret for testing]
```

The last two arguments are optional. 

To skip the OAuth and use your own Discogs OAuth keys, set `shazam2discogs.oauth-bypass=true` in `application.properties`. 

### 2. Database Username & Password

Also, add a file named `database.properties`. This will store the username / password used to access your database. 

The format is as follows - 

```

spring.datasource.username=[your desired username here]
spring.datasource.password=[your desired password here]

```

### Local Development URL
During local development, you should access the site through IP (e.g. `http://127.0.0.1:8080/`) rather than `localhost`. 

- Using `localhost` will prevent the OAuth callback from working. 


### Docker
Docker images can be created using the included Dockerfile. 

- Note that the Gradle command `bootJar` should be run before the `docker build` command to create a .jar file for Docker to bundle.  
