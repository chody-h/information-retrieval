
import java.io.BufferedReader;
import java.io.Console;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import textops.PorterStemmer;
import textops.RelatedQueries;
import textops.StopWords;

public class QuerySuggestor {
	
	private static PorterStemmer ps = new PorterStemmer();
	private static StopWords sw = new StopWords();
	private static ArrayList<RelatedQueries> rq = new ArrayList<RelatedQueries>();
	private static Trie t = new Trie();
	
//	private static Freq_App freq = new Freq_App();
	private static WCF_App WCF = new WCF_App();
//	private static Mod_App mod = new Mod_App();
	
	public static void main(String[] args) {
		System.out.println("Parsing files...");
		ParseFiles();
		System.out.println("Done parsing files.");
//		PrintSomeQueries();
//		PrintSomeTrie();
		Console c = System.console();
		while (true) {
			String input = c.readLine("Please enter a query:");
			HashMap<String, Double> outputs = Suggest(input);
			for (int i = 0; i < 8; i++) {
				System.out.println(LargestKey(outputs));
			}
		}
	}
	
	public static HashMap<String, Double> Suggest(String in) {
		HashMap<String, Double> ret = new HashMap<String, Double>();
		RelatedQueries r = FindRelatedQueries(in);
		Trie.Node n = GetSubtree(in);
		for (Trie.Node ex : n.GetExpansions()) {
			String s = t.GetWord(ex);										// suggested expansion
			
			double freq = ex.count / t.maxCount;
			
			String w1 = ps.stem(in.split(" ")[in.split(" ").length-1]);		// last word of query
			String w2 = ps.stem(s.split(" ")[0]);							// first word of expansion
			double wcf = WCF.Score(w1, w2);
			if (wcf == -1) return null;
			
			double mod = r.ModifiedTo(in, s);
			
			double rank = (freq + wcf + mod) / (1 - Math.min(freq, Math.min(wcf, mod)));
			
			ret.put(s, rank);
		}
		
		return ret;
	}
	
	public static String LargestKey(HashMap<String, Double> m) {
		Iterator it = m.entrySet().iterator();
		double largestVal = 0;
		String bestSugg = "";
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Double temp = (Double)pair.getValue();
			if (temp > largestVal) {
				largestVal = temp;
				bestSugg = (String)pair.getKey();
			}
		}
		m.remove(bestSugg);
		return bestSugg + " (" + Double.toString(largestVal) + ")";
	}
	
	public static Trie.Node GetSubtree(String s) {
		return t.GetNode(s);
	}
	
	public static RelatedQueries FindRelatedQueries(String s) {
		for (RelatedQueries q : rq) {
			if (q.QueryInSet(s)) return q;
		}
		return null;
	}
	
	public static void ParseFiles() {
		String pre = "files/";
		String[] docs = {
				"Clean-Data-01.txt",
//				"Clean-Data-02.txt",
//				"Clean-Data-03.txt",
//				"Clean-Data-04.txt",
//				"Clean-Data-05.txt",
		};
		int num = 1;
		for (String doc : docs) {
			System.out.println("parsing file " + num++);
			try {
				BufferedReader in = new BufferedReader(new FileReader(pre+doc));
				String nextLine = "";
				
				// first line is just a column title
				in.readLine();
				
				// initialize first piece of data
				RelatedQueries r = new RelatedQueries();
				String[] q = r.ParseLine(in.readLine());
				r.AddRelated(q);
				
				while ((nextLine = in.readLine()) != null) {
					q = r.ParseLine(nextLine);
					q[1] = q[1].replaceAll("[^a-zA-Z ]", "").toLowerCase();
					q[1] = ChopFirstStopword(q[1]);
					
					// construct related queries structure
					if (!r.IsRelated(q)) {
						rq.add(r);
						r = new RelatedQueries();
					}
					r.AddRelated(q);
					
					// construct trie structure
					String groomed = q[1].replaceAll("[^a-zA-Z ]", "").toLowerCase();
					t.AddQuery(groomed);
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	public static String ChopFirstStopword(String q) {
		String[] spl = q.split(" ");
		if (sw.contains(spl[0])) {
			StringBuilder s = new StringBuilder();
			for (int i = 1; i < spl.length; i++) 
				s.append(spl[i]);
			return s.toString();
		}
		else 
			return q;
	}
	
	// DEBUGGING AND TESTING
	
	public static void PrintSomeQueries() {
		for (int i = 0; i < 10; i++) {
			System.out.println(rq.get(i).toString());
		}
	}
	
	public static void PrintSomeTrie() {
		for (int i = 0; i < 10; i++) {
			System.out.println(t.GetRandom());
		}
	}

}
