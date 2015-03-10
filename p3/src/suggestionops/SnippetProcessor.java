package suggestionops;

import java.io.File;
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
			String[] sentences = SeparateBySentence(doc);
			
			
		}
		return ret;
	}
	
	private String[] SeparateBySentence(String entire_doc) {
		
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
