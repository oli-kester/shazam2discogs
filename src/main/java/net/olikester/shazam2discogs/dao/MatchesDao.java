package net.olikester.shazam2discogs.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import net.olikester.shazam2discogs.model.Tag;
import net.olikester.shazam2discogs.model.TagReleaseMatch;

public interface MatchesDao extends JpaRepository<TagReleaseMatch, Integer> {
    
    @Query("SELECT tag FROM TagReleaseMatch WHERE SESSION_ID = ?1")
    public Set<Tag> getAllTagsForSession(String sessionId);

    @Query("SELECT tag FROM TagReleaseMatch WHERE SESSION_ID = ?1 and RELEASE_ID = NULL")
    public Set<Tag> getAllUnmatchedTagsForSession(String id);

    @Query("select T from TagReleaseMatch T where SESSION_ID = ?1 and TAG_ID = ?2")
    public TagReleaseMatch getByTagAndSessionId(String sessionId, String tagId);

    @Query("select T from TagReleaseMatch T where SESSION_ID = ?1")
    public Set<TagReleaseMatch> getAllMatchDataForSession(String sessionId);
}
