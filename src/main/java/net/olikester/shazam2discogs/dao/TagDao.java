package net.olikester.shazam2discogs.dao;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;

import net.olikester.shazam2discogs.model.Tag;

public interface TagDao extends JpaRepository<Tag, Integer> {

    public ArrayList<Tag> findBySessionId(String sessionId);

}