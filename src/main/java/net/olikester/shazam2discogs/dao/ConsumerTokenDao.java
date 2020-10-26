package net.olikester.shazam2discogs.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import net.olikester.shazam2discogs.model.JpaOAuthConsumerToken;

public interface ConsumerTokenDao extends JpaRepository<JpaOAuthConsumerToken, String> {

}
