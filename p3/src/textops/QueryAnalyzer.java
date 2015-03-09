package textops;

import java.util.HashMap;

public class QueryAnalyzer {
	
	private HashMap<Integer, String> correct;
	private HashMap<String, Tuple<Integer, String>> misspelled;
	private Dictionary d;

	public QueryAnalyzer(Dictionary dict) {
		correct = new HashMap<Integer, String>();
		misspelled = new HashMap<String, ArrayList<Tuple<Integer, String>>>();
		d = dict;
	}

	public void AddCorrectQuery(Integer id, String q) {
		if (NoMisspells(q)) 
			correct.put(id, q);
	}

	public void AddIncorrectQuery(Integer id, String q) {
		if (!NoMisspells(q)) {
			String[] temp = q.split(" ");
			for (int i = 0; i < temp.length; i++) {
				if (!d.ContainsWord(temp[i])) {
					String word = temp[i];
					String correction = correct.get(id);
					if (!correction.equals(null)) {
						
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
