package p3;

import Suggestion.Suggester;

public class Main {
	
	private static String f_dict = "dictionary.txt";
	private static String f_queries = "query_log.txt";
	
	private static String[] queries = new String[] {
		"sentenced to prision",
		"open cuort case",
		"entretainment group",
		"tv axtor",
		"scheduled movie screning"
	};

	public static void main(String[] args) {
//		test stuff
		System.out.println(Suggester.Soundex("extenssions"));	// E235
		System.out.println(Suggester.Soundex("extensions"));	// E235
		System.out.println(Suggester.Soundex("marshmellow"));	// M625
		System.out.println(Suggester.Soundex("marshmallow"));	// M625
		System.out.println(Suggester.Soundex("brimingham"));	// B655
		System.out.println(Suggester.Soundex("birmingham"));	// B655
		System.out.println(Suggester.Soundex("poiner"));		// P560
		System.out.println(Suggester.Soundex("pointer"));		// P536
		
//		process dictionary
		
		
//		process query file
		
		
//		spell check queries
				
		
//		retrieve top 5 documents from collection with snippets
		
		
	}

}
