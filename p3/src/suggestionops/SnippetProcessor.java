package suggestionops;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

import textops.TextProcessor;

public class SnippetProcessor {
	
	private TextProcessor t;

	public SnippetProcessor(TextProcessor tp) {
		t = tp;
	}

	// note that this must return TWO sentences as one string.
	// don't forget to bold query terms
	public String GetSnippet(String doc_name, String query) {
		String ret = null;
		String doc = GetContentsOfDoc(doc_name);
		String[] query_words = query.split(" ");
		if (doc != null) {
			// 	sentence	score
			HashMap<String, Double> scores = new HashMap<String, Double>();
			String[] sentences = doc.replaceAll("[^\\w.\\s]|[\\r]", "").replaceAll("\\n{2}|(.\\n{2})", "\n").split("\n|\\.(?!\\d)|(?<!\\d)\\.");
			
			double max_sentence_length_in_doc = 0.0;
			for (int i = 0; i < sentences.length; i++) {
				double length = sentences[i].length() - sentences[i].replace(" ", "").length() + 1;
				if (length > max_sentence_length_in_doc)
					max_sentence_length_in_doc = length;
			}
			
			for (int i = 0; i < sentences.length; i++) {
				Double score = 0.0;
				String[] words = sentences[i].split(" ");

				// looks like this: [w, w, s, s, w, w, s, w, w]
				String[] markers = new String[words.length];
				for (int j = 0; j < markers.length; j++) {
					for (int k = 0; k < query_words.length; k++) {
						if (query_words[k].equals(words[j].toLowerCase())) {
							markers[j] = "s";
							k = (j == markers.length-1) ? query_words.length : -1;
							j++;
						}
					}
					markers[j] = "w";
				}

				// heading (ignore, no headings in doc collection)

				// 1st or 2nd line of doc - gives more weight to sentences in larger docs
				if (i == 0) score += (0.1 * sentences.length);
				else if (i == 1) score += (0.05 * sentences.length);

				// total # query terms occurring in sentence - normalized by dividing #query_words * #words_in_sentence (gives a percentage of max possible)
				double query_count = 0.0;
				for (int j = 0; j < markers.length; j++)
					if (markers[j].equals("s"))
						query_count++;
				query_count = (query_count < 2) ? 0.35 : Math.log(query_count);
				score += query_count / ((double)query_words.length) * max_sentence_length_in_doc / ((double)words.length);

				// # unique query terms in sentence
				double unique_query_count = 0.0;
				for (int j = 0; j < query_words.length; j++) 
					for (int k = 0; k < words.length; k++) 
						if (query_words[j].equals(words[k].toLowerCase())) {
							unique_query_count++;
							k = (j == query_words.length-1) ? words.length : -1; 
							j++;
						}
				unique_query_count = (unique_query_count < 2) ? 0.35 : Math.log(unique_query_count) ;
				score += unique_query_count / ((double)query_words.length);

				// longest contiguous run of query words in sentence
				double longest = 0.0;
				// for (int j = 1; j < words.length; j++) {
				// 	double current_longest = 1.0;
				// 	for (int k = 0; k < query_words.length; k++) 
				// 		if (words[j].toLowerCase().equals(query_words[k])) 
				// 			for (int l = 0; l < query_words.length; l++) 
				// 				if (words[j-1].toLowerCase().equals(query_words[l])) {
				// 					current_longest++;
				// 					if (current_longest > longest) {
				// 						longest = current_longest;
				// 					}
				// 					// found a match; just go straight to the next word in the sentence
				// 					j++; k = 0; l = 0;
				// 				}
				// }
				double current_longest = 0.0;
				for (int j = 0; j < markers.length; j++) {
					if (markers[j].equals("s")) 
						current_longest++;
						if (current_longest > longest) 
							longest = current_longest;
					else 
						current_longest = 0;
				}
				score += longest * (double)query_words.length / ((double)words.length);

				// density measure of query words (i.e., significance factor on query words in sentences)
				// 3 of my own

				scores.put(sentences[i], score);
			}
		}
		return ret;
	}
	
	private String GetContentsOfDoc(String doc_name) {
		try {
			return (new Scanner(new File(doc_name)).useDelimiter("\\A").next());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
