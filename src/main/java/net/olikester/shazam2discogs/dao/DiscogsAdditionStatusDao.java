package net.olikester.shazam2discogs.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import net.olikester.shazam2discogs.model.DiscogsAdditionStatus;

public interface DiscogsAdditionStatusDao extends JpaRepository<DiscogsAdditionStatus, String> {

}