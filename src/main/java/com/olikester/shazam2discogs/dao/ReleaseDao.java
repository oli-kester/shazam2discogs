package com.olikester.shazam2discogs.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.olikester.shazam2discogs.model.Release;

public interface ReleaseDao extends JpaRepository<Release, String> {

}
