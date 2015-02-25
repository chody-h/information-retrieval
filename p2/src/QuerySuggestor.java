
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import textops.PorterStemmer;
import textops.RelatedQueries;
import textops.StopWords;

public class QuerySuggestor {
	
	private static DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	
	private static PorterStemmer ps = new PorterStemmer();
	private static StopWords sw = new StopWords();
	private static Trie t = new Trie();
	
	public static void main(String[] args) throws InterruptedException, IOException {
		// put console into raw mode
//		String[] cmd = {"/bin/sh", "-c", "stty raw </dev/tty"};
//	    Runtime.getRuntime().exec(cmd).waitFor();
	    
		System.out.println("Parsing files...");
		ParseFiles();
		System.out.println("Done parsing files.");
		PrintSomeQueries();
//		PrintSomeTrie();
		while (true) {
			String in = ValidInput("[A-Za-z ]+");
			HashMap<String, Double> outputs = Suggest(in);
			for (int i = 0; i < 8; i++) {
				System.out.println(LargestKey(outputs));
			}
			System.out.println();
		}
	}
	
//	private static String ValidInput(String regex) {
//		Scanner s = new Scanner(System.in);
//		boolean valid = false;
//		String input = "";
//		while (!valid) {
//			System.out.println("Please enter a query:");
//			input = s.nextLine();
//			if (input.matches(regex)) {
//				valid = true;
//				System.out.println("Valid.");
//			}
//			else {
//				System.out.println("Your query must match " + regex);
//			}
//		}
//		return input.toLowerCase();
//	}
	
//	private static HashMap<String, Double> Suggest(String in) {
//		System.out.println("Calculating suggestions...");
//		
//		HashMap<String, Double> ret = new HashMap<String, Double>();
//		ArrayList<RelatedQueries> r = FindRelatedQueries(in);
////		Trie.Node n = GetSubtree(in);
//		int maxFreqofSugg = 0;
//		for (RelatedQueries rel : r) {
//			String SQ = rel.GetNextQuery(in);
//			if (SQ == null) continue;
//			
//			int freq = t.GetNode(SQ).count;
//			if (freq > maxFreqofSugg) maxFreqofSugg = freq;
//		}
//		System.out.println(maxFreqofSugg);
//		for (RelatedQueries rel : r) {
////			String s = t.GetWord(ex);										// suggested expansion
////			String w = s.replace(in+" ", "");								// suggested expansion w/o original
//			String SQ = rel.GetNextQuery(in);								// Suggested Query
//			if (SQ == null || SQ.equals(in)) continue;
//			Trie.Node n = t.GetNode(SQ);									// Node representing SQ in Trie
//			
//			double freq = (double)(n.count / (double)maxFreqofSugg);
//			
//			String w1 = ps.stem(in.split(" ")[in.split(" ").length-1]);		// last word of query
//			String w2 = ps.stem(SQ.replace(in+" ", "").split(" ")[0]);		// first word of expansion
//			double wcf = WCF_App.Score(w1, w2);
//			
//			double mod = ModifiedTo(r, in, SQ);
//			
//			double rank = (freq + wcf + mod) / (1 - Math.min(freq, Math.min(wcf, mod)));
//			
//			ret.put(SQ.replace(in+" ", "").split(" ")[0], rank);
//		}
//		
//		return ret;
//	}
	
//	private static double ModifiedTo(ArrayList<RelatedQueries> r, String in, String expansion) {
//		double ret = 0;
//		
//		for (RelatedQueries rel : r) {
//			double temp = rel.ModifiedTo(in, expansion);
//			if (temp > ret) ret = temp;
//		}
//		
//		return Math.log(ret);
//	}
	
//	private static String LargestKey(HashMap<String, Double> m) {
//		Iterator<Entry<String, Double>> it = m.entrySet().iterator();
//		double largestVal = 0;
//		String bestSugg = "";
//		while (it.hasNext()) {
//			Map.Entry<String, Double> pair = (Map.Entry<String, Double>) it.next();
//			Double temp = (Double)pair.getValue();
//			if (temp >= largestVal) {
//				largestVal = temp;
//				bestSugg = (String)pair.getKey();
//			}
//		}
//		if (bestSugg.equals("")) {
//			return "---";
//		}
//		else {
//			m.remove(bestSugg);
//			return bestSugg + " (" + Double.toString(largestVal) + ")";
//		}
//	}
//	
//	private static Trie.Node GetSubtree(String s) {
//		return t.GetNode(s);
//	}
//	
//	private static ArrayList<RelatedQueries> FindRelatedQueries(String s) {
//		ArrayList<RelatedQueries> ret = new ArrayList<RelatedQueries>();
//		for (RelatedQueries q : rq) {
//			if (q.QueryInSet(s)) ret.add(q);
//		}
//		return ret;
//	}
	
	private static void ParseFiles() {
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
				
				// initialize necessary variables
				String[] q1 = nextLine.split("\t");
				q1[1] = q1[1].replaceAll("[^a-zA-Z ]", "").toLowerCase();
				q1[1] = ChopFirstStopword(q1[1]);
				String[] q2 = new String[3];
				boolean mod = false;
				
				while ((nextLine = in.readLine()) != null) {
					q2 = nextLine.split("\t");
					q2[1] = q2[1].replaceAll("[^a-zA-Z ]", "").toLowerCase();
					q2[1] = ChopFirstStopword(q2[1]);
					
					mod = IsRelated(q1, q2);
					
					// construct trie structure
					t.AddQuery(q2[1], mod);
					============================
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	private static boolean IsRelated(String[] q1, String[] q2) {
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
	
	private static String ChopFirstStopword(String q) {
		String[] spl = q.split(" ");
		if (sw.contains(spl[0])) {
			StringBuilder s = new StringBuilder();
			for (int i = 1; i < spl.length; i++) {
				s.append(spl[i]);
				if (i+1 < spl.length) s.append(" ");
			}
			return s.toString();
		}
		else 
			return q;
	}
	
	// DEBUGGING AND TESTING
	
//	public static void PrintSomeQueries() {
//		for (int i = 0; i < 10; i++) {
//			System.out.println(rq.get(i).toString());
//		}
//	}
//	
//	public static void PrintSomeTrie() {
//		for (int i = 0; i < 10; i++) {
//			System.out.println(t.GetRandom());
//		}
//	}

}
