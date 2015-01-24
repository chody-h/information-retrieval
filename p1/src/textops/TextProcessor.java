package textops;

import java.io.*;
import java.util.*;

public class TextProcessor {
	
	private static int NUM_DOCS_TO_PROCESS = 322;
	private StopWords stopwords;
	private PorterStemmer stemmer;
	private Map<String, int[]> index;
//		word	index: doc_id
//				value: freq_of_occur
	
	// doc_num is the number as the user would see it; ie. it's 1-based index
	public int FindFrequencyByDocument(String word, int doc_num) {
		doc_num -= 1;
		int[] freq = index.get(word);
		return freq[doc_num];
	}
	
	public void TestIndex() {
		Iterator it = index.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			int[] value = (int[]) pairs.getValue();
			System.out.println(pairs.getKey() + " = " + value[0] + " " + value[1] + " "  + value[2] + " "  + value[3]);
		}
	}

	public TextProcessor() {
//		0. Init stuff that won't change doc by doc
		stopwords = new StopWords();
		stemmer = new PorterStemmer();
		index = new HashMap<String, int[]>();
		
		for (int doc_index = 0; doc_index < NUM_DOCS_TO_PROCESS; doc_index++) {
			
//			1. Tokenize - remove caps, punctuation, hyphens
			String[] words = null;
			try {
				int num = doc_index + 1;
				words = Tokenize("wikidocs/Doc (" + num + ").txt");
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			for (int i = 0; i < words.length; i++) {
//				2. Remove stop-words
				if (stopwords.contains(words[i])) {
					words[i] = null;
					continue;
				}
				
//				3. Stem remaining words
				String temp = stemmer.stem(words[i]);
				if (temp != "Invalid term") {
					words[i] = temp;
				}
				
//				4. Add word to indexed structure 
				if (index.containsKey(words[i])) {
					int[] freq = index.get(words[i]);
					freq[doc_index] += 1;
					index.put(words[i], freq);
				}
				else {
					int[] freq = new int[NUM_DOCS_TO_PROCESS];
					freq[doc_index] = 1;
					index.put(words[i], freq);
				}
			}
		}
	}
	
	private String[] Tokenize(String file) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		StringBuffer str = new StringBuffer();
		String nextLine = "";
		while ((nextLine = in.readLine()) != null)
			str.append(nextLine+"\n");
		in.close();
		
		return str.toString().replaceAll("[^a-zA-Z0-9 \n]", "").toLowerCase().split("\\s+");
//		for (int i = 0; i < words.length; i++) System.out.println(words[i].toString());
	}
	
	private class Tuple<X, Y> {
		public final X x;
		public final Y y;
		public Tuple(X x, Y y) {
			this.x = x;
			this.y = y;
		}
	}
}
