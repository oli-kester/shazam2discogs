package com.olikester.shazam2discogs.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.olikester.shazam2discogs.model.Tag;

public interface TagDao extends JpaRepository<Tag, String> {

}