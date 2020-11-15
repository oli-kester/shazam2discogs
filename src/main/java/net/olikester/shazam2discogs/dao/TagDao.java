package net.olikester.shazam2discogs.dao;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import net.olikester.shazam2discogs.model.Tag;

public interface TagDao extends JpaRepository<Tag, String> {

    @Query("from Tag where SESSION_ID=?1 and DISCOGS_RELEASE_ID is null")
    ArrayList<Tag> getAllUnmatchedTagsForSession(String sessionId);

}