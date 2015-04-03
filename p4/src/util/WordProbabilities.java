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
		LinkedHashMap<String, Double> temp = p.get(w);
		if (temp != null) {
			Double val = temp.get(c);
			if (val != null) {
				return val;
			}
		}
		return 0.0;
	}
	
	public Double GetNotProbability(String w, String c) {
		LinkedHashMap<String, Double> temp = np.get(w);
		if (temp != null) {
			Double val = temp.get(c);
			if (val != null) {
				return val;
			}
		}
		return 0.0;
	}
}
