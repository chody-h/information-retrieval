package util;

import java.util.HashMap;

public class ClassProbabilities {
//	<class, probability>
	private HashMap<String, Double> p;
	
	public ClassProbabilities(HashMap<String, Double> m) {
		p = m;
	}
	
	public Double GetProbability(String c) {
		return p.get(c);
	}
}
