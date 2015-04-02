package util;

import java.util.HashMap;

public class WordProbabilities {
//	<class, <word, probability>>
	private HashMap<String, HashMap<String, Double>> p;
	
	public WordProbabilities(HashMap<String, HashMap<String, Double>> m) {
		p = m;
	}
	
//	public HashMap<String, Double> GetClass(String c) {
//		return p.get(c);
//	}
	
	public Double GetProbability(String w, String c) {
		return p.get(c).get(w);
	}
}
