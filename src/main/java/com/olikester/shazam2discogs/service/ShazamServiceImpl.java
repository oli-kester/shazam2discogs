package com.olikester.shazam2discogs.service;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olikester.shazam2discogs.model.Tag;

@Service
public class ShazamServiceImpl implements ShazamService {

    @Override
    public Tag fetchExtraTagData(Tag tag) {
	final RestTemplate restTemplate = new RestTemplate();
	ResponseEntity<String> response;
	try {
	    response = restTemplate.getForEntity(new URI(shazamTagDataRequestUrl + tag.getId()), String.class);

	} catch (RestClientException e) {
	    e.printStackTrace();
	    return tag;
	} catch (URISyntaxException e) {
	    e.printStackTrace();
	    return tag;
	}

	ObjectMapper mapper = new ObjectMapper();
	try {
	    JsonNode root = mapper.readTree(response.getBody());

	    if (root.has("images") && root.get("images").has("coverart")) {
		tag.setImageUrl(root.get("images").get("coverart").asText());
	    } else {
		tag.setImageUrl("");
	    }

	    if (root.has("sections") && root.get("sections").get(0).has("metadata")) {
		for (JsonNode metadataItem : root.get("sections").get(0).get("metadata")) {
		    switch (metadataItem.get("title").asText()) {
		    case "Album":
			tag.setAlbum(metadataItem.get("text").asText());
			break;
		    case "Label":
			tag.setLabel(metadataItem.get("text").asText());
			break;
		    case "Released":
			tag.setReleaseYear(metadataItem.get("text").asInt());
			break;
		    }
		}
	    }

	} catch (JsonProcessingException e) {
	    e.printStackTrace();
	    return tag;
	}

	return tag;
    }

}
