package util;

import java.util.LinkedHashMap;

public class WordProbabilities {
//	<word, <class, probability>>
	private LinkedHashMap<String, LinkedHashMap<String, Double>> p;
	private LinkedHashMap<String, LinkedHashMap<String, Double>> np;
	private LinkedHashMap<String, LinkedHashMap<String, Double>> l;
	
	public WordProbabilities(LinkedHashMap<String, LinkedHashMap<String, Double>> m, LinkedHashMap<String, LinkedHashMap<String, Double>> n, LinkedHashMap<String, LinkedHashMap<String, Double>> la) {
		p = m;
		np = n;
		l = la;
	}
	
//	public HashMap<String, Double> GetClass(String c) {
//		return p.get(c);
//	}
	
	// P(c|w)
	public Double GetWordProbability(String c, String w) {
		LinkedHashMap<String, Double> temp = p.get(w);
		if (temp != null) {
			Double val = temp.get(c);
			if (val != null) {
				return val;
			}
		}
		return 0.0;
	}

	// P(c|!w)
	public Double GetNotWordProbability(String c, String w) {
		LinkedHashMap<String, Double> temp = np.get(w);
		if (temp != null) {
			Double val = temp.get(c);
			if (val != null) {
				return val;
			}
		}
		return 0.0;
	}
	
	// P(w|c)
	public Double GetLaplacianProbability(String w, String c) {
		LinkedHashMap<String, Double> temp = l.get(c);
		if (temp != null) {
			Double val = temp.get(w);
			if (val != null) {
				return val;
			}
		}
		
		return 0.0;
	}
}
