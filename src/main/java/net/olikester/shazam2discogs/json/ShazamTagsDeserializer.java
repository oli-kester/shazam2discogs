package net.olikester.shazam2discogs.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import net.olikester.shazam2discogs.model.Tag;

import java.io.IOException;
import java.util.ArrayList;

public class ShazamTagsDeserializer extends StdDeserializer<ArrayList<Tag>> {

    private static final long serialVersionUID = -6632369719629804358L;

    public ShazamTagsDeserializer() {
	this(null);
    }

    public ShazamTagsDeserializer(Class<?> vc) {
	super(vc);
    }

    /**
     * Deserialize a full Shazam JSON export into a list of tags, discarding
     * irrelevant data.
     */
    @Override
    public ArrayList<Tag> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
	ArrayList<Tag> tagList = new ArrayList<Tag>();
	ObjectCodec codec = parser.getCodec();
	JsonNode rootNode = codec.readTree(parser);

	for (JsonNode currTag : rootNode.get("tags")) {
	    Tag newTag = new Tag();
	    JsonNode headers = currTag.get("track").get("heading");

	    newTag.setId(currTag.get("key").asText());
	    newTag.setTrackTitle(headers.get("title").asText());
	    newTag.setArtist(headers.get("subtitle").asText());
	    if (currTag.get("track").get("images").has("default")) {
		newTag.setImageUrl(currTag.get("track").get("images").get("default").asText());
	    } else {
		newTag.setImageUrl("");
	    }

	    for (JsonNode currFootnote : currTag.get("track").get("footnotes")) {
		switch (currFootnote.get("title").asText()) {
		case "Album":
		    newTag.setAlbum(currFootnote.get("value").asText());
		    break;
		case "Label":
		    newTag.setLabel(currFootnote.get("value").asText());
		    break;
		case "Released":
		    newTag.setReleaseYear(currFootnote.get("value").asInt());
		    break;
		}
	    }
	    tagList.add(newTag);
	}

	return tagList;
    }
}
