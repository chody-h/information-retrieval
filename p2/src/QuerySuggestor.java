import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import textops.PorterStemmer;
import textops.StopWords;

public class QuerySuggestor {
	
	private PorterStemmer ps = new PorterStemmer();
	private StopWords sw = new StopWords();
	private static ArrayList<RelatedQueries> rq = new ArrayList<RelatedQueries>();
	
//	private static Freq_App freq = new Freq_App();
	private static WCF_App wcf = new WCF_App();
//	private static Mod_App mod = new Mod_App();
	
	public static void main(String[] args) {
		System.out.println("Parsing files...");
		ParseFiles();
		System.out.println("Done parsing files.");
		PrintSomeQueries();
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
				r.AddRelated(in.readLine());
				
				while ((nextLine = in.readLine()) != null) {
					if (!r.IsRelated(nextLine)) {
						rq.add(r);
						r = new RelatedQueries();
					}
					r.AddRelated(nextLine);
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	public static void PrintSomeQueries() {
		for (int i = 0; i < 10; i++) {
			System.out.println(rq.get(i).toString());
		}
	}

}
