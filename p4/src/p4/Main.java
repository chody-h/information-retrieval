package p4;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class Main {

	public static void main(String[] args) {
		String dc = args[0];
		MNBclassification c = new MNBclassification(dc);
		
		LinkedHashMap<File, LinkedHashMap<String, Integer>> DC_training = c.getDCTraining();
		LinkedHashMap<String, Double> vocab = c.getVocab();
		MNBprobability p = new MNBprobability(DC_training, vocab);
		
		LinkedHashMap<String, Double> features = c.featureSelection(100, p);
		for (Entry<String, Double> f : features.entrySet()) {
			System.out.printf("IG(%s): %2.4f\n", f.getKey(), f.getValue());
		}
	}

}
