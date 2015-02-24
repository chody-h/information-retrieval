
import java.io.BufferedReader;
import java.io.Console;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
			ArrayList<String> outputs = Suggest(input);
		}
	}
	
	public static HashMap Suggest(String in) {
		HashMap ret = new HashMap();
		RelatedQueries r = FindRelatedQueries(in);
		Trie.Node n = FindExpandedQueries(in);
		for (int i = 0; i < n.CountExpansions(); i++) {
			double freq = 0;
			double wcf = 0;
			double mod = 0;
		}
		
		return ret;
	}
	
	public static Trie.Node FindExpandedQueries(String s) {
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
