package net.olikester.shazam2discogs.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import net.olikester.shazam2discogs.model.DiscogsSearchProgress;

public interface DiscogsSearchProgressDao extends JpaRepository<DiscogsSearchProgress, String> {

}
