package textops;

import java.util.HashMap;

public class QueryAnalyzer {

	private Dictionary d;
//					queryID	query
	private HashMap<String, String> queries;
//					word	count
	private HashMap<String, Integer> corrections;
//				misspelled			correct 	count
	private HashMap<String, HashMap<String, Integer>> misspelled;

	public QueryAnalyzer(Dictionary dict) {
		queries = new HashMap<String, String>();
		corrections = new HashMap<String, Integer>();
		misspelled = new HashMap<String, HashMap<String, Integer>>();
		d = dict;
	}
	
	public String Correct(String query) {
		return query;
	}

	public void AddCorrectQuery(String id, String q) {
		if (NoMisspells(q)) 
			queries.put(id, q);
	}

	public void AddIncorrectQuery(String id, String q) {
		if (!NoMisspells(q)) {
			String[] temp = q.split(" ");
			for (int i = 0; i < temp.length; i++) {
				// if this word is misspelled
				if (!d.ContainsWord(temp[i])) {

					String e = temp[i];

					// find if query log has a correction
					if (queries.containsKey(id)) {
						String w = "";
						if (queries.get(id).split(" ").length > i) 
							w = queries.get(id).split(" ")[i];
						else
							return;


						// update MISSPELLING table
						HashMap<String, Integer> misspelling;
						Integer count = 1;
						if (misspelled.containsKey(e)) {
							misspelling = misspelled.get(e);
							// e has been corrected to w, so increment count
							if (misspelling.containsKey(w)) 
								count = (misspelling.get(w)) + 1;
						}
						// e has not been seen before
						else 
							misspelling = new HashMap<String, Integer>();
						
						misspelling.put(w, count);
						misspelled.put(e, misspelling);


						// update CORRECTIONS table
						if (corrections.containsKey(w)) 
							count = (corrections.get(w)) + 1;
						else 
							count = 1;

						corrections.put(w, count);
					}
				}
			}
		}
	}

	private boolean NoMisspells(String q) {
		String[] words = q.split(" ");
		for (String w : words)
			if (!d.ContainsWord(w))
				return false;

		return true;
	}

	class Tuple<X, Y> { 
		public final X x; 
		public final Y y; 
		public Tuple(X x, Y y) { 
			this.x = x; 
			this.y = y; 
		} 
	} 
}
