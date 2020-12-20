package com.olikester.shazam2discogs.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.olikester.shazam2discogs.model.Release;
import com.olikester.shazam2discogs.model.Tag;
import com.olikester.shazam2discogs.model.TagReleaseMatch;

public interface MatchesDao extends JpaRepository<TagReleaseMatch, Integer> {

    @Query("SELECT tag FROM TagReleaseMatch WHERE SESSION_ID = ?1")
    public Set<Tag> getAllTagsForSession(String sessionId);

    @Query("SELECT tag FROM TagReleaseMatch WHERE SESSION_ID = ?1 and (RELEASE_ID = NULL or DISCOGS_ADDITION_STATUS != 'ADDED')")
    public Set<Tag> getAllUnmatchedTagsForSession(String id);

    @Query("select T from TagReleaseMatch T where SESSION_ID = ?1 and TAG_ID = ?2")
    public TagReleaseMatch getByTagAndSessionId(String sessionId, String tagId);

    @Query("select T from TagReleaseMatch T where SESSION_ID = ?1 and RELEASE_ID = ?2")
    public List<TagReleaseMatch> getByReleaseAndSessionId(String sessionId, String releaseId);

    @Query("select T from TagReleaseMatch T where SESSION_ID = ?1")
    public Set<TagReleaseMatch> getAllMatchDataForSession(String sessionId);

    @Query("select count(T) from TagReleaseMatch T where SESSION_ID = ?1 and DISCOGS_ADDITION_STATUS = 'ADDED'")
    public int getNumberOfReleasesAddedBySessionId(String sessionId);

    @Query("select release from TagReleaseMatch where SESSION_ID = ?1 and DISCOGS_ADDITION_STATUS = 'FAILED'")
    public Set<Release> getFailedReleasesBySessionId(String sessionId);
}
