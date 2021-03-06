package com.olikester.shazam2discogs.json;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.olikester.shazam2discogs.model.Release;

public class DiscogsReleaseSearchResultsDeserializer extends StdDeserializer<ArrayList<Release>> {

    private static final long serialVersionUID = -8457991728396345784L;

    public DiscogsReleaseSearchResultsDeserializer() {
	this(null);
    }

    public DiscogsReleaseSearchResultsDeserializer(Class<?> vc) {
	super(vc);
    }

    /**
     * Deserialize a full Shazam JSON export into a list of tags, discarding
     * irrelevant data.
     */
    @Override
    public ArrayList<Release> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
	ArrayList<Release> releaseList = new ArrayList<Release>();
	ObjectCodec codec = parser.getCodec();
	JsonNode rootNode = codec.readTree(parser);

	for (JsonNode currResult : rootNode.get("results")) {
	    Release newRelease = new Release();

	    newRelease.setId(currResult.get("id").asText());
	    newRelease.setTitle(currResult.get("title").asText());

	    if (currResult.has("country")) {
		newRelease.setCountry(currResult.get("country").asText());
	    }
	    if (currResult.has("year")) {
		newRelease.setReleaseYear(currResult.get("year").asInt());
	    }
	    if (currResult.has("thumb")) {
		newRelease.setThumbnailPath(currResult.get("thumb").asText());
	    }
	    if (currResult.has("type")) {
		newRelease.setReleaseType(currResult.get("type").asText());
	    }

	    if (currResult.has("format")) {
		JsonNode formatDetails = currResult.get("format");
		newRelease.setFormatType(formatDetails.get(0).asText());
		if (formatDetails.has(1)) {
		    newRelease.setFormatDesc(formatDetails.get(1).asText());
		}
	    } else {
		continue;
	    }

	    if (currResult.has("label")) {
		newRelease.setLabel(currResult.get("label").get(0).asText());
	    }

	    if (currResult.has("community") && currResult.get("community").has("have")) {
		newRelease.setPopularity(currResult.get("community").get("have").asInt());
	    } else {
		System.out.println("Root node - " + currResult.toPrettyString());
		continue;
	    }
	    releaseList.add(newRelease);
	}

	return releaseList;
    }

}
