package textops;

import java.io.*;
import java.util.*;

public class TextProcessor {

	public void main(String[] args) {
		
//		0. Init stuff that won't change doc by doc
		StopWords stopwords = new StopWords();
		PorterStemmer stemmer = new PorterStemmer();
		HashMap<String, Tuple<Integer, Integer>> index = new HashMap<String, Tuple<Integer, Integer>>();
//				 word		  doc_id   freq_of_occur
		
//		1. Tokenize - remove caps, punctuation, hyphens
		String[] words = null;
		try {
			words = Tokenize("wikidocs/Doc (3).txt");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
//		2. Remove stop-words
		for (int i = 0; i < words.length; i++) {
			if (!stopwords.contains(words[i])) {
				words[i] = null;
			}
		}
		
//		3. Stem remaining words
		for (int i = 0; i < words.length; i++) {
			if (words[i] != null) {
				words[i] = stemmer.stem(words[i]);
			}
		}
		
//		4. Add word to indexed structure 
		for (int i = 0; i < words.length; i++) {
			
		}
		
	}
	
	public String[] Tokenize(String file) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		StringBuffer str = new StringBuffer();
		String nextLine = "";
		while ((nextLine = in.readLine()) != null)
			str.append(nextLine+"\n");
		in.close();
		
		return str.toString().replaceAll("[^a-zA-Z0-9 \n]", "").toLowerCase().split("\\s+");
//		for (int i = 0; i < words.length; i++) System.out.println(words[i].toString());
	}
	
	public class Tuple<X, Y> {
		public final X x;
		public final Y y;
		public Tuple(X x, Y y) {
			this.x = x;
			this.y = y;
		}
	}
}
