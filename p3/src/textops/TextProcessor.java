package textops;

import java.io.*;
import java.util.*;

public class TextProcessor {
	
	private static int NUM_DOCS_TO_PROCESS = 322;
	private StopWords stopwords;
//	private PorterStemmer stemmer;
	private Map<String, int[]> index;
//				word	index: doc_id
//						value: freq_of_occur
	private int[] wordcount;
	private int[] max_freq_of_doc;

	public TextProcessor() {
//		0. Init stuff that won't change doc by doc
		stopwords = new StopWords();
//		stemmer = new PorterStemmer();
		index = new HashMap<String, int[]>();
		wordcount = new int[NUM_DOCS_TO_PROCESS];
		max_freq_of_doc = null;
	}
	
	public String GetFirstSentence(int doc_num) {
		
		try {
			doc_num++;
			BufferedReader in = new BufferedReader(new FileReader("wikidocs/Doc (" + doc_num + ").txt"));
			StringBuffer str = new StringBuffer();
			String nextLine = "";
			if ((nextLine = in.readLine()) != null) {
				in.close();
				String firstSentence = nextLine.split("(?<=[a-z])\\.\\s+")[0];
				firstSentence += ".";
				return firstSentence;
			}
			in.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int[][] FindRelevantDocs(String[] q) {
//		1. for each word in query
//		2. if it matches any documents
//		3. add the documents that do match to the array
		
		int[][] relevant = new int[NUM_DOCS_TO_PROCESS][q.length];
		for (int i = 0; i < q.length; i++) {
			for (int j = 0; j < NUM_DOCS_TO_PROCESS; j++) {
				relevant[j][i] = FindFrequencyByDocument(q[i], j);
			}
		}
		return relevant;
	}
	
	public int[] MaxFreqOfDoc(int doc_num) {
		if (doc_num < -1 || doc_num >= NUM_DOCS_TO_PROCESS) return null;
		
		else if (max_freq_of_doc != null) {
			if (doc_num == -1) {
				return max_freq_of_doc;
			}
			else {
				int[] max = new int[1];
				max[0] = max_freq_of_doc[doc_num];
				return max;
			}
		}
		
		if (doc_num > -1 && doc_num < NUM_DOCS_TO_PROCESS) {
			int[] max = new int[1];
			
			Iterator it = index.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry)it.next();
				int val = ((int[])pairs.getValue())[doc_num];
				if (val > max[0]) max[0] = val;
			}
			
			return max;
		}
		else { // if (doc_num == -1)
			int[] max = new int[NUM_DOCS_TO_PROCESS];
			
			Iterator it = index.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry)it.next();
				for (int i = 0; i < NUM_DOCS_TO_PROCESS; i++) {
					int val = ((int[])pairs.getValue())[i];
					if (val > max[i]) max[i] = val;
				}
			}
			
			return max;
		}
	}
	
	public int NumDocsWordIsIn(String word) {
		int docs = 0;
		int[] freq = index.get(word);
		if (freq == null) return 0;
		
		for (int i = 0; i < freq.length; i++) {
			if (freq[i] > 0) docs++;
		}
		return docs;
	}

	public int WordCount(int doc_num) {
		if (doc_num >= NUM_DOCS_TO_PROCESS || doc_num < -1) return -1;	// document out of range
		
		if (doc_num == 0) {
			int sum = 0;
			for (int i = 0; i < wordcount.length; i++) sum += wordcount[i];
			return sum;
		}
		else
			return wordcount[doc_num - 1];
	}
	
	public int FindFrequencyByDocument(String word, int doc_num) {
		int[] freq = index.get(word);
		if (freq == null) return 0;										// word not found
		if (doc_num >= NUM_DOCS_TO_PROCESS || doc_num < -1) return -1;	// document out of range
		
		if (doc_num == -1) {
			int sum = 0;
			for (int i = 0; i < freq.length; i++) sum += freq[i];
			return sum;
		}
		else { 
			return freq[doc_num];
		}
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
		for (int doc_num = 0; doc_num < NUM_DOCS_TO_PROCESS; doc_num++) {
			String[] words = null;
			try {
				int num = doc_num + 1;
				BufferedReader in = new BufferedReader(new FileReader("wikidocs/Doc (" + num + ").txt"));
				StringBuffer str = new StringBuffer();
				String nextLine = "";
				while ((nextLine = in.readLine()) != null) {
					str.append(nextLine+"\n");
				}
				in.close();

				String file_contents = str.toString();
				
				String[] c = file_contents.split("\\s+");
				wordcount[doc_num] = c.length;
				
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
					freq[doc_num] += 1;
					index.put(words[i], freq);
				}
				else {
					int[] freq = new int[NUM_DOCS_TO_PROCESS];
					freq[doc_num] = 1;
					index.put(words[i], freq);
				}
			}
		}
		
		max_freq_of_doc = MaxFreqOfDoc(-1);
	}
	
	public String[] Tokenize(String phrase) {
//		1. Tokenize - remove caps, punctuation, hyphens
		String[] words = phrase.replaceAll("[^a-zA-Z0-9 \n]", " ").toLowerCase().split("\\s+");
		
		for (int i = 0; i < words.length; i++) {
//			2. Remove stop-words
			if (stopwords.contains(words[i])) {
				words[i] = null;
				continue;
			}
			
//			3. Stem remaining words
//			String temp = stemmer.stem(words[i]);
//			if (temp != "Invalid term") {
//				words[i] = temp;
//			}
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
