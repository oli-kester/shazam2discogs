package net.olikester.shazam2discogs.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import net.olikester.shazam2discogs.model.SessionData;

public interface SessionDataDao extends JpaRepository<SessionData, String> {

}
