package p3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import textops.*;

public class Main {
	
	private static String f_dict = "dictionary.txt";
	private static String f_queries = "query_log.txt";
//	private static String f_dict = "test_dict.txt";
//	private static String f_queries = "test_querylog.txt";
	
	private static String[] queries = new String[] {
		"sentenced to prision",
		"open cuort case",
		"entretainment group",
		"tv axtor",
		"scheduled movie screning"
	};

	public static void main(String[] args) {
		
//		TestEditDistance();
		
//		process dictionary
		Dictionary d = new Dictionary();
		try {
			BufferedReader in = new BufferedReader(new FileReader(f_dict));
			String nextLine = "";
			while ((nextLine = in.readLine()) != null) {
				nextLine = nextLine.replace("[^a-zA-Z]", "");
				d.AddWord(nextLine);
			}
			in.close();
		} 
		catch (IOException e) {
			System.out.println("ERROR: problem reading dictionary file.");
			e.printStackTrace();
			return;
		}
//		TestDictionary(d);
		
//		process query file
		// add only the correct queries first. then go through the file again
		// and add the incorrect queries with the data from the correct ones
		TextProcessor t = new TextProcessor();
		t.InitFiles();
		QueryAnalyzer q = new QueryAnalyzer(d, t);
		try {
			BufferedReader in = new BufferedReader(new FileReader(f_queries));
			String nextLine = in.readLine();
			while ((nextLine = in.readLine()) != null) {
				String id = nextLine.split("\t")[0];
				String query = nextLine.split("\t")[1].replace("[^a-zA-Z]", "").toLowerCase();
				q.AddCorrectQuery(id, query);
			}

			in = new BufferedReader(new FileReader(f_queries));
			nextLine = in.readLine();
			while ((nextLine = in.readLine()) != null) {
				String id = nextLine.split("\t")[0];
				String query = nextLine.split("\t")[1].replace("[^a-zA-Z]", "").toLowerCase();
				q.AddIncorrectQuery(id, query);
			}
		}
		catch (IOException e) {
			System.out.println("ERROR: problem reading query file.");
			e.printStackTrace();
			return;
		}
		
//		spell check queries
		for (String query : queries) {
			System.out.print("Did you mean: ");
			System.out.print(q.Correct(query));
			System.out.print("? (Originally ");
			System.out.print(query);
			System.out.println(")");
		}
		
//		retrieve top 5 documents from collection with snippets
		
		

	}

	
//	test stuff
	
	public static void TestSoundex() {
		System.out.println(Util.Soundex("extenssions"));	// E235
		System.out.println(Util.Soundex("extensions"));		// E235
		System.out.println(Util.Soundex("marshmellow"));	// M625
		System.out.println(Util.Soundex("marshmallow"));	// M625
		System.out.println(Util.Soundex("brimingham"));		// B655
		System.out.println(Util.Soundex("birmingham"));		// B655
		System.out.println(Util.Soundex("poiner"));			// P560
		System.out.println(Util.Soundex("pointer"));		// P536
	}
	
	public static void TestDictionary(Dictionary d) {
		System.out.println(d.ContainsWord("accommodationism"));
		System.out.println(d.ContainsWord("wheezinesses"));
		System.out.println(d.ContainsWord("cryptococcal"));
		System.out.println(d.ContainsWord("negativism"));
		System.out.println(d.ContainsWord("ye"));

		System.out.println(d.GetWords("A253"));
		System.out.println(d.GetWords("W252"));
		System.out.println(d.GetWords("C613"));
		System.out.println(d.GetWords("N231"));
		System.out.println(d.GetWords("Y000"));
	}

	public static void TestEditDistance() {
		System.out.println(Util.EditDistance("kitten", "sitting"));
		System.out.println(Util.EditDistance("saturday", "sunday"));
		System.out.println(Util.EditDistance("prision", "prison"));
		System.out.println(Util.EditDistance("entretainment", "entertainment"));
		System.out.println(Util.EditDistance("axtor", "actor"));
		System.out.println(Util.EditDistance("screning", "screening"));
	}

}
