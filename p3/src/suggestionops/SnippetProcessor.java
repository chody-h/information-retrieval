package suggestionops;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import textops.TextProcessor;

public class SnippetProcessor {
	
	private TextProcessor t;
	private String[] query_words;
	private String doc;
	private int num_sentences;
	private Double max_sentence_length_in_doc;

	public SnippetProcessor(TextProcessor tp) {
		t = tp;
	}

	// note that this must return TWO sentences as one string.
	// don't forget to bold query terms
	public String GetSnippet(String doc_name, String query) {
		if (doc_name.equals("wikidocs/Doc (15).txt")) {
			int m = 0;
		}
		String ret = null;
		doc = GetContentsOfDoc(doc_name);
		query_words = query.split(" ");
		if (doc != null) {
			 System.out.println(doc_name + "\n");
			HashMap<String, Double> scores = ScoreDoc();
			// place the sentences in a formatted string
			for (Entry<String, Double> entry : scores.entrySet()) {
				String sentence = entry.getKey();
				Double score = entry.getValue();
				 System.out.println(sentence + "\n" + score.toString());
			}
			System.out.println("\n");
		}
		
		query_words = null;
		doc = null;
		num_sentences = 0;
		max_sentence_length_in_doc = null;

		return ret;
	}

	private HashMap<String, Double> ScoreDoc() {
				// 	sentence	score
		HashMap<String, Double> scores = new HashMap<String, Double>();
		String[] sentences = doc.replaceAll("[^\\w.\\s]|[\\r]|( \\.\\.\\.)", "").replaceAll("\\.? ?(\\n{4}|\\n{2})", "\n").split("\n|\\.(?!\\d)|(?<!\\d)\\.");
		num_sentences = sentences.length;
		String[] originals = doc.replaceAll("[\\r]|( \\.\\.\\.)", "").replaceAll("(\\.')", "'.").replaceAll("(\\.\")", "\".").replaceAll("\\.? ?(\\n{4}|\\n{2})", "\n").split("\n|\\.(?!\\d)|(?<!\\d)\\.");
		assert sentences.length == originals.length;
		for (int i = 0; i < originals.length; i++) {
			originals[i] += ".";
		}

		// determine the length of the largest sentence
		max_sentence_length_in_doc = 0.0;
		for (int i = 0; i < sentences.length; i++) {
			double length = sentences[i].length() - sentences[i].replace(" ", "").length() + 1;
			if (length > max_sentence_length_in_doc)
				max_sentence_length_in_doc = length;
		}
		
		for (int i = 0; i < sentences.length; i++) {
			Double score = ScoreSentence(sentences[i], i);
			scores.put(originals[i], score);
		}

		return scores;
	}

	private Double ScoreSentence(String sentence, int index) {
			Double score = 0.0;
			String[] words = sentence.split(" ");
			if (words.length < 2) return 0.0;

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
				if (j < markers.length) markers[j] = "w";
			}

			// heading (ignore, no headings in doc collection)

			// 1st or 2nd line of doc - gives more weight to sentences in larger docs
			if (index == 0) score += (0.1 * num_sentences);
			else if (index == 1) score += (0.05 * num_sentences);

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
			// preprocess markers: get rid of trailing w's, leading w's, and 5+ w's in a row
			int j = markers.length - 1;
			while (j >= 0 && markers[j].equals("w")) 
				markers[j--] = "*";

			j = 0;
			while (markers[j].equals("w")) 
				markers[j++] = "*";

			int consecutive_w = 0;
			for (  ; j < markers.length; j++) {
				if (markers[j].equals("s")) {
					if (consecutive_w > 4) {
						while (consecutive_w != 0) {
							markers[j-consecutive_w] = "*";
							consecutive_w--;
						}
					}
					else {
						consecutive_w = 0;
					}
				}
				else if (markers[j].equals("*")) {
					break;
				}
				else {
					consecutive_w++;
				}
			}
			double max_s = 0;
			double max_l = 0;
			int cur_s = 0;
			int cur_l = 0;
			for (j = 0; j < markers.length; j++) {
				if (markers[j].equals("s")) {
					if (++cur_s > max_s) max_s = cur_s;
					if (++cur_l > max_l) max_l = cur_l;
					
				}
				else if (markers[j].equals("w")) {
					if (++cur_l > max_l) max_l = cur_l;
				}
				else if (markers[j].equals("*")) {
					cur_s = 0;
					cur_l = 0;
				}
			}
			Double significance_factor = (max_s * max_s / max_l);
			significance_factor = (significance_factor.equals(Double.NaN)) ? 0 : significance_factor ;
			score += ((significance_factor > 1) ? Math.log(significance_factor) : significance_factor / 10);

			// 3 of my own

			return score;
	}
	
	private String GetContentsOfDoc(String doc_name) {
		try {
//			File f = new File(doc_name);
//			Scanner s = new Scanner(f);
//			s.useDelimiter("\\Z");
//			String contents = s.next();
//			
//			s.close();
//			return contents;
			return (new Scanner(new File(doc_name)).useDelimiter("\\A").next());
		} catch (Exception e) {
			try {
				BufferedReader r = new BufferedReader(new FileReader(doc_name));
			    StringBuilder sb = new StringBuilder();
			    String nextLine = "";

			    while ((nextLine = r.readLine()) != null) {
			    	nextLine = nextLine.replaceAll("[^\\p{L}\\p{Nd} \\.]+", "");
			        sb.append(nextLine);
			    }
			    return sb.toString();
			}
			catch (Exception ex) {
				System.out.println("Could not open document: " + doc_name + "\n\n");
				e.printStackTrace();
				return null;
			}
		}
	}

}