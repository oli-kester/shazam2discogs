package com.olikester.shazam2discogs.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.olikester.shazam2discogs.dao.MatchesDao;
import com.olikester.shazam2discogs.model.JpaOAuthConsumerToken;
import com.olikester.shazam2discogs.model.MediaFormats;
import com.olikester.shazam2discogs.model.Release;
import com.olikester.shazam2discogs.model.Tag;
import com.olikester.shazam2discogs.model.TagReleaseMatch;

@Component
public class DiscogsAsync {

    @Autowired
    private MatchesDao matchesDao;
    @Autowired
    private DiscogsService discogsService;

    @Autowired
    private Set<String> cancelTaskSessionIds;
    @Autowired
    private Map<String, Integer> taskProgress;

    @Async
    public void asyncDiscogsSearch(MediaFormats preferredFormat, String sessionId,
	    Optional<JpaOAuthConsumerToken> userToken, Set<Tag> userTags) {
	final AtomicInteger progressCounter = new AtomicInteger();

	// for loop lets us break stream when the user cancels the search
	for (Tag currTag : userTags) {
	    if (cancelTaskSessionIds.contains(sessionId)) {
		break;
	    }

	    // search Discogs database for best match for each tag
	    ArrayList<Release> discogsSearchResults = discogsService.getReleaseList(currTag, userToken.get());

	    if (discogsSearchResults.isEmpty()) {
		// if we didn't find any results, try a less specific search.
		String searchTerm = currTag.getSimpleSearchTerm();
		discogsSearchResults = discogsService.getReleaseList(searchTerm, userToken.get());

		if (discogsSearchResults.isEmpty()) {
		    continue; // if there's still no results found, skip this one.
		}
	    }

	    // throw away master releases
	    List<Release> nonMasterReleases = discogsSearchResults.stream()
		    .filter(release -> release.getReleaseType().equals("release")).collect(Collectors.toList());

	    // add best match to release database
	    Release bestMatch = Release.selectPreferredReleaseByFormat(nonMasterReleases, preferredFormat);
	    TagReleaseMatch currMatchRecord = matchesDao.getByTagAndSessionId(sessionId, currTag.getId());
	    currMatchRecord.setRelease(bestMatch);
	    bestMatch.addMatch(currMatchRecord);
	    currTag.addMatch(currMatchRecord);
	    matchesDao.save(currMatchRecord);

	    // update search progress so this can be requested by the webpage (casting to
	    // avoid rounding errors)
	    double progressPercentage = (progressCounter.incrementAndGet() / (double) userTags.size()) * 100;
	    taskProgress.put(sessionId, (int) progressPercentage);
	}

	if (!cancelTaskSessionIds.contains(sessionId)) {
	    taskProgress.put(sessionId, 100);
	}
    }
}
