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
	}

}
