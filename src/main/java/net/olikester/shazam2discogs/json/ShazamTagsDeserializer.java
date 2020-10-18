package net.olikester.shazam2discogs.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import net.olikester.shazam2discogs.model.Tag;
import net.olikester.shazam2discogs.model.TagList;

import java.io.IOException;

public class ShazamTagsDeserializer extends StdDeserializer<TagList> {

    // recommended by Eclipse 
    private static final long serialVersionUID = 1L;

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
    public TagList deserialize(JsonParser parser, DeserializationContext context) throws IOException {
	TagList tagList = new TagList();
	ObjectCodec codec = parser.getCodec();
	JsonNode rootNode = codec.readTree(parser);

	for (JsonNode currTag : rootNode.get("tags")) {
	    Tag newTag = new Tag();
	    JsonNode headers = currTag.get("track").get("heading");

	    newTag.setKey(currTag.get("key").asInt());
	    newTag.setTrackTitle(headers.get("title").asText());
	    newTag.setArtist(headers.get("subtitle").asText());

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
