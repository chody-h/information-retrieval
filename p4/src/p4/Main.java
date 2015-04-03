package p4;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class Main {

	public static void main(String[] args) {
		
		String dc = args[0];

		
		// training
		MNBclassification c = new MNBclassification(dc);
		
		LinkedHashMap<File, LinkedHashMap<String, Integer>> DC_training = c.getDCTraining();
		LinkedHashMap<String, Double> vocab = c.getVocab();
				// output stuff
				int amount = vocab.size();
				if (amount > 100) amount = 100;
				System.out.printf("First %d of %d words in the collection's vocab and their frequencies: \n\t{", amount, vocab.size());
				String delim = "";
				int count = 0;
				for (Entry<String, Double> entry : vocab.entrySet()) {
					if (count++ == 100) break;
					System.out.printf("%s%s=%2.2f", delim, entry.getKey(), entry.getValue());
					delim = ", ";
				}
				System.out.println("}");
		
		MNBprobability p = new MNBprobability(DC_training, vocab);
		
		LinkedHashMap<String, Double> features = c.featureSelection(100, p);
				// output stuff
				amount = features.size();
				if (amount > 100) amount = 100;
				System.out.printf("Top %d of %d features ranked by Information Gain: \n\t{", amount, features.size());
				delim = "";
				count = 0;
				for (Entry<String, Double> f : features.entrySet()) {
					if (count++ == amount) break;
					System.out.printf("%s%s=%2.4f", delim, f.getKey(), f.getValue());
					delim = ", ";
				}
				System.out.println("}");
			
		
		
		// testing
		// document vectors
		LinkedHashMap<File, LinkedHashMap<String, Integer>> DC_test = c.getDCTest();
		// document classification
		LinkedHashMap<File, String> DC_test_assignments = new LinkedHashMap<File, String>();
		for (Entry<File, LinkedHashMap<String, Integer>> e : DC_test.entrySet()) {
			File f = e.getKey();
			LinkedHashMap<String, Integer> docVector = e.getValue();
			String assignedClass = c.label(f, docVector, p);
			DC_test_assignments.put(f, assignedClass);
		}
		double accuracy = MNBevaluation.accuracyMeasure(DC_test_assignments);
		accuracy = (accuracy >= 0 && accuracy <= 100) ? accuracy * 100 : 0 ;
		System.out.printf("My classifier is %2.2f%% accurate for (%d) docs in the (%s) test set.", accuracy, DC_test.size(), dc);
	}

}
