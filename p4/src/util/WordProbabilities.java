package util;

import java.util.LinkedHashMap;

public class WordProbabilities {
//	<word, <class, probability>>
	private LinkedHashMap<String, LinkedHashMap<String, Double>> p;
	private LinkedHashMap<String, LinkedHashMap<String, Double>> np;
	
	public WordProbabilities(LinkedHashMap<String, LinkedHashMap<String, Double>> m, LinkedHashMap<String, LinkedHashMap<String, Double>> n) {
		p = m;
		np = n;
	}
	
//	public HashMap<String, Double> GetClass(String c) {
//		return p.get(c);
//	}
	
	public Double GetProbability(String w, String c) {
		return p.get(w).get(c);
	}
	
	public Double GetNotProbability(String w, String c) {
		return np.get(w).get(c);
	}
}
