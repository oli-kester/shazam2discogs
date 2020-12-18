package com.olikester.shazam2discogs.csv;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import com.olikester.shazam2discogs.model.Tag;

public class ShazamTagsCsvParser {

    /**
     * Define custom Cell Processor array This dictates the rules of what's
     * acceptable in each CSV cell.
     */
    private static CellProcessor[] cellProcessors = new CellProcessor[] { null, // CSV index (not needed)
	    new NotNull(new ParseDate("yyyy-MM-dd")), // Tag Time
	    new StrNotNullOrEmpty(), // Title
	    new StrNotNullOrEmpty(), // Artist
	    new StrNotNullOrEmpty(), // URL
	    new StrNotNullOrEmpty() // Track Key
    };

    /**
     * An example of reading using CsvBeanReader.
     * 
     * @throws IOException
     */
    public static ArrayList<Tag> readWithCsvBeanReader(String input) throws IOException {

	ICsvBeanReader beanReader = null;
	ArrayList<Tag> tagList = new ArrayList<Tag>();
	
	String escapedInput = fixUnescapedQuotes(input);

	try {
	    beanReader = new CsvBeanReader(new StringReader(escapedInput), CsvPreference.STANDARD_PREFERENCE);

	    beanReader.getHeader(false);
	    beanReader.getHeader(false); // skip two rows to get to the data

	    // set header row to map values to the Tag object
	    final String[] header = new String[] { null, "tagTime", "trackTitle", "artist", "shazamInfoUrl", "id" };

	    Tag newTag;
	    while ((newTag = beanReader.read(Tag.class, header, cellProcessors)) != null) {
		tagList.add(newTag);
	    }

	} finally {
	    if (beanReader != null) {
		beanReader.close();
	    }
	}
	return tagList;
    }

    /**
     * Escape un-escaped quotes (Shazam can output invalid CSV)
     * @param input String
     * @return The more-valid CSV
     */
    protected static String fixUnescapedQuotes(String input) {
	// look for un-escaped quotes in the CSV and escape them.
	Pattern unescapedQuoteRegex = Pattern.compile(",\"(([^\",]+\"[^\",]+)+)\",");
	Matcher matcher = unescapedQuoteRegex.matcher(input);
	return matcher.replaceAll(match -> {
	    String escaped = match.group(1).replaceAll("\"", "\"\"");
	    return ",\"" + escaped + "\",";
	});
    }
}
