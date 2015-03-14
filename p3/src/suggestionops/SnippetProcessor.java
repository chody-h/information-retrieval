package suggestionops;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import textops.PorterStemmer;
import textops.StopWords;

public class SnippetProcessor {
	
	private String[] query_words;
	private String doc;
	private int num_sentences;
	private Double max_sentence_length_in_doc;
	private StopWords s;
	private PorterStemmer p;

	public SnippetProcessor() {
		s = new StopWords();
		p = new PorterStemmer();
	}

	// note that this must return TWO sentences as one string.
	// don't forget to bold query terms
	public String GetSnippet(String doc_name, String query) {
		StringBuilder ret = new StringBuilder();
		doc = GetContentsOfDoc(doc_name);
		query_words = query.split(" ");
		if (doc != null) {
//			System.out.println(doc_name + "\n");
			HashMap<String, Double> scores = ScoreDoc();
			// place the sentences in a formatted string
			Double highest_score = 0.0;
			String top_sentence = "";
			Double second_highest_score = 0.0;
			String second_top_sentence = "";
			for (Entry<String, Double> entry : scores.entrySet()) {
				String sentence = entry.getKey();
				Double score = entry.getValue();
				if (score > highest_score) {
					second_highest_score = highest_score;
					second_top_sentence = top_sentence;

					highest_score = score;
					top_sentence = sentence;
				}
				else if (score > second_highest_score) {
					second_highest_score = score;
					second_top_sentence = sentence;
				}
//				System.out.println(sentence + "\n" + score.toString());
			}
			
			ret.append(doc_name + "\n");

			String delimiter = "";
			String[] words = top_sentence.replaceAll("\\.", "").split(" ");
			for (int i = 0; i < words.length; i++) {
				ret.append(delimiter);
				delimiter = " ";
				for (int j = 0; j < query_words.length; j++) {
					if (i < words.length && !s.contains(words[i].toLowerCase()) && p.stem(words[i].toLowerCase()).equals(p.stem(query_words[j]))) {
						ret.append("<b>" + words[i] + "</b> ");
						i++;
						continue;
					}
				}
				if (i < words.length) ret.append(words[i]);
			}
			ret.append(". ");
			if (!second_top_sentence.equals("")) {
				words = second_top_sentence.replaceAll("\\.", "").split(" ");
				for (int i = 0; i < words.length; i++) {
					ret.append(delimiter);
					delimiter = " ";
					for (int j = 0; j < query_words.length; j++) {
						if (i < words.length && !s.contains(words[i]) && p.stem(words[i].toLowerCase()).equals(p.stem(query_words[j]))) {
							ret.append("<b>" + words[i] + "</b> ");
							i++;
							continue;
						}
					}
					if (i < words.length) ret.append(words[i]);
				}
				ret.append(". ");
			}
					
//			System.out.println("\n");
		}
		
		query_words = null;
		doc = null;
		num_sentences = 0;
		max_sentence_length_in_doc = null;

		return ret.toString();
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
			query_count = (query_count < 2) ? 0.05 : Math.log(query_count);
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
			unique_query_count = (unique_query_count < 2) ? 0.05 : Math.log(unique_query_count) ;
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
			score += Math.log(longest) / Math.log((double)words.length);

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

			// 3 of my own:
			// locality - adjacent query words within +-2 words
			// stem the words & compare
			Double stem_count = 0.0;
			String[] stemmed_query = new String[query_words.length];
			String[] stemmed_words = new String[words.length];
			for (j = 0; j < query_words.length; j++) {
				stemmed_query[j] = p.stem(query_words[j].toLowerCase());
				for (int k = 0; k < words.length; k++) {
					stemmed_words[k] = p.stem(words[k].toLowerCase());
					if (stemmed_words[k].equals(stemmed_query[j])) {
						stem_count++;
					}
				}
			}
			stem_count = (stem_count < 2) ? 0.1 : Math.log(stem_count) ;
			score += stem_count / words.length;
			
			// non-stopwords
			Double sw_count = 0.0;
			ArrayList<String> sw_query = new ArrayList<String>();
			for (j = 0; j < query_words.length; j++) {
				if (!s.contains(query_words[j])) {
					sw_query.add(stemmed_query[j]);
				}
			}
			for (j = 0; j < sw_query.size(); j++) {
				for (int k = 0; k < stemmed_words.length; k++) {
					if (!s.contains(words[k]) && sw_query.get(j).equals(stemmed_words[k])) {
						sw_count++;
					}
				}
			}
			sw_count = (sw_count < 2) ? 0.1 : Math.log(sw_count*2) ;
			score += sw_count;
			
			// existence of complete query. stemming allowed
			for (j = 0; j < stemmed_words.length; j++) {
				if (stemmed_words[j].equals(stemmed_query[0])) {
					boolean full_query = true;
					for (int a = j, b = 0; (a < stemmed_words.length) && (b < stemmed_query.length); a++, b++) {
						if (!stemmed_words[a].equals(stemmed_query[b])) {
							full_query = false;
							break;
						}
					}
					if (full_query) {
						score += 1.0;
						break;
					}
				}
			}

			return score;
	}
	
	private String GetContentsOfDoc(String doc_name) {
		try {
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
			    r.close();
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
