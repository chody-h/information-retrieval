package textops;

import java.io.*;
import java.util.*;

public class TextProcessor {
	
	private static int NUM_DOCS_TO_PROCESS = 322;
	private StopWords stopwords;
	private PorterStemmer stemmer;
	private Map<String, int[]> index;
//				word	index: doc_id
//						value: freq_of_occur
	private int[] wordcount;

	public TextProcessor() {
//		0. Init stuff that won't change doc by doc
		stopwords = new StopWords();
		stemmer = new PorterStemmer();
		index = new HashMap<String, int[]>();
		wordcount = new int[NUM_DOCS_TO_PROCESS];
	}

	// doc_num is the number as the user would see it; ie. it's 1-based index
	// if doc_num is 0, find the count in all docs
	public int WordCount(int doc_num) {
		if (doc_num > NUM_DOCS_TO_PROCESS || doc_num < 0) return -1;	// document out of range
		
		if (doc_num == 0) {
			int sum = 0;
			for (int i = 0; i < wordcount.length; i++) sum += wordcount[i];
			return sum;
		}
		else
			return wordcount[doc_num - 1];
	}
	
	// doc_num is the number as the user would see it; ie. it's 1-based index
	// if doc_num is 0, find the freq in all docs
	public int FindFrequencyByDocument(String word, int doc_num) {
		int[] freq = index.get(word);
		if (freq == null) return 0;						// word not found
		if (doc_num > NUM_DOCS_TO_PROCESS) return -1;	// document out of range
		
		if (doc_num > 0) {
			doc_num -= 1;
			return freq[doc_num];
		}
		else if (doc_num == 0) {
			int sum = 0;
			for (int i = 0; i < freq.length; i++) sum += freq[i];
			return sum;
		}
		else return 0;
	}
	
	public void TestIndex() {
		Iterator it = index.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			int[] value = (int[]) pairs.getValue();
			System.out.println(pairs.getKey() + " = " + value[0] + " " + value[1] + " "  + value[2] + " "  + value[3]);
		}
	}
	
	public void InitFiles() {
		for (int doc_index = 0; doc_index < NUM_DOCS_TO_PROCESS; doc_index++) {
			String[] words = null;
			try {
				int num = doc_index + 1;
				BufferedReader in = new BufferedReader(new FileReader("wikidocs/Doc (" + num + ").txt"));
				StringBuffer str = new StringBuffer();
				String nextLine = "";
				while ((nextLine = in.readLine()) != null) {
					str.append(nextLine+"\n");
				}
				in.close();

				String file_contents = str.toString();
				
				String[] c = file_contents.split("\\s+");
				wordcount[doc_index] = c.length;
				
				words = Tokenize(file_contents);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			for (int i = 0; i < words.length; i++) {
				
				if (words[i] == null) continue;
				
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
	
	public String[] Tokenize(String phrase) {
//		1. Tokenize - remove caps, punctuation, hyphens
		String[] words = phrase.replaceAll("[^a-zA-Z0-9 \n]", "").toLowerCase().split("\\s+");
		
		for (int i = 0; i < words.length; i++) {
//			2. Remove stop-words
			if (stopwords.contains(words[i])) {
				words[i] = null;
				continue;
			}
			
//			3. Stem remaining words
			String temp = stemmer.stem(words[i]);
			if (temp != "Invalid term") {
				words[i] = temp;
			}
		}
		
		return words;
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
