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
		String[] words = query.split(" ");
		if (doc != null) {
			// 	sentence	score
			HashMap<String, Double> sentences = SeparateBySentence(doc);
			for ()
		}
		return ret;
	}
	
	// splits on . or newline, but not if the period is surrounded by decimals
	private HashMap<String, Double> SeparateBySentence(String entire_doc) {
		String[] sentences = entire_doc.split("\n|\\.(?!\\d)|(?<!\\d)\\.");
		HashMap<String, Double> ret = new HashMap<String, Double>();
		for (String s : sentences) {
			ret.put(s, 0.0);
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
