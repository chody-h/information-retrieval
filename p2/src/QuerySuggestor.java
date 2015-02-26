
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import textops.PorterStemmer;
import textops.StopWords;

public class QuerySuggestor {
	
	private static DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	
	private static PorterStemmer ps = new PorterStemmer();
	private static StopWords sw = new StopWords();
	private Trie t = new Trie();
	
	public HashMap<String, Double> Suggest(String in) {
		System.out.println("Calculating suggestions...");
		HashMap<String, Double> ret = new HashMap<String, Double>();
		
		in = FormatQuery(in);
		if (in.equals("") || in.equals(" ")) {
			System.out.println("You entered only a stopword. Input a better query.");
			return ret;
		}
		
		Trie.Node n = GetSubtree(in + ' ');
		HashSet<String> expansions = n.GetExpansions();

		int maxFreqofSugg = 1;
		int maxModofSugg = 1;
		for (String sq : expansions) {
			Trie.Node node = t.GetNode(sq);
			
			int freq = node.freq;
			if (freq > maxFreqofSugg) maxFreqofSugg = freq;
			
			int mod = node.mod;
			if (mod > maxModofSugg) maxModofSugg = mod;
		}
//		System.out.println(maxFreqofSugg);
		
		for (String sq : expansions) {
			Trie.Node node = t.GetNode(sq);									// Node representing SQ in Trie
			
			double freq = (double) (node.freq / (double)maxFreqofSugg);
			
			String w1 = ps.stem(in.split(" ")[in.split(" ").length-1]);		// last word of query
			String w2 = ps.stem(sq.replace(in+' ', "").split(" ")[0]);		// first word of expansion
			double wcf = WCF_App.Score(w1, w2);
			
			double mod = (double) (node.mod / maxModofSugg);
			
			double rank = (freq + wcf + mod) / (1 - Math.min(freq, Math.min(wcf, mod)));
			
			ret.put(sq, rank);
		}
		
		return ret;
	}
	
	private Trie.Node GetSubtree(String s) {
		return t.GetNode(s);
	}
	
	public void ParseFiles() {
		String pre = "files/";
		String[] docs = {
				"Clean-Data-01.txt",
//				"Clean-Data-02.txt",
//				"Clean-Data-03.txt",
//				"Clean-Data-04.txt",
//				"Clean-Data-05.txt",
		};
//		int num = 1;
		for (String doc : docs) {
//			double startTime = System.nanoTime();
//			System.out.printf("parsing file %d ", num++);
			try {
				BufferedReader in = new BufferedReader(new FileReader(pre+doc));
				
				// first line is just a column title
				in.readLine();
				
				// initialize necessary variables
				String nextLine = in.readLine();
				String[] q1 = FormatLine(nextLine);
				String[] q2 = new String[3];
				boolean mod = false;
				t.AddQuery(q1[1], mod);
				
				while ((nextLine = in.readLine()) != null) {
					q2 = FormatLine(nextLine);
					
					mod = IsRelated(q1, q2);
					
					// construct trie structure
					t.AddQuery(q2[1], mod);
					
					// swap q2 to previous
					q1 = q2;
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

//			double endTime = System.nanoTime();
//			double duration = (endTime - startTime)/1000000000;  //divide by 1000000 to get milliseconds.
//			System.out.printf("(%2.2f seconds)\n", duration);
		}
	}
	
	private boolean IsRelated(String[] q1, String[] q2) {
		// different first word
		String w1 = q1[1].split(" ")[0];
		String w2 = q2[1].split(" ")[0];
		if (!w1.equals(w2)) return false;
		
		// different user id
		if (!q1[0].equals(q2[0])) return false;
		
		// within 10 minutes
		DateTime t1 = DateTime.parse(q1[2], f);
		DateTime t2 = DateTime.parse(q2[2], f);
		if (Math.abs(t2.getMillis() - t1.getMillis()) > 600000) return false;
		
		return true;
	}
	
	private String[] FormatLine(String line) {
		// split userID (0), query (1), and date (2)
		String[] q = line.split("\t");
		q[1] = FormatQuery(q[1]);		
		return q;
	}
	
	private String FormatQuery(String query) {
		// format query: ignore punctuation, check if first word is stopword
		query = query.replaceAll("[^a-zA-Z ]", "").toLowerCase();
		String[] q = query.split(" ");
		if (sw.contains(q[0])) {
			StringBuilder s = new StringBuilder();
			for (int i = 1; i < q.length; i++) {
				s.append(q[i]);
				if (i+1 < q.length) s.append(" ");
			}
			return s.toString();
		}
		else
			return query;
	}
	
	// DEBUGGING AND TESTING
	
	public void PrintSomeTrie() {
		System.out.printf("\nTRIE STATS\nMax Freq: %d\nMax Mod: %d\n", t.maxFreq, t.maxMod);
		System.out.println("\nHere are ten queries I saw:");
		for (int i = 0; i < 10; i++) {
			System.out.println(t.GetRandom());
		}
	}

}
