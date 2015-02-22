import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import textops.PorterStemmer;
import textops.StopWords;

public class QuerySuggestor {
	
	private PorterStemmer ps = new PorterStemmer();
	private StopWords sw = new StopWords();
	private ArrayList<RelatedQueries> rq = new ArrayList<RelatedQueries>();
	
//	private static Freq_App freq = new Freq_App();
	private static WCF_App wcf = new WCF_App();
//	private static Mod_App mod = new Mod_App();
	
	public static void main(String[] args) {
		ParseFiles();
	}
	
	public static void ParseFiles() {
		String[] docs = {
				"Clean-Data-01.txt",
				"Clean-Data-02.txt",
				"Clean-Data-03.txt",
				"Clean-Data-04.txt",
				"Clean-Data-05.txt",
		};
		for (String doc : docs) {
			try {
				BufferedReader in = new BufferedReader(new FileReader("files/Clean-Data-0" + doc_num + ".txt"));
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
				
//					4. Add word to indexed structure 
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

}
