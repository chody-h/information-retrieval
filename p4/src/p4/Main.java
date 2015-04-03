package p4;

import java.io.File;
import java.util.LinkedHashMap;

public class Main {

	public static void main(String[] args) {
		String dc = args[0];
		MNBclassification c = new MNBclassification(dc);
		
		LinkedHashMap<File, LinkedHashMap<String, Integer>> DC_training = c.getDCTraining();
		LinkedHashMap<String, Double> vocab = c.getVocab();
		MNBprobability p = new MNBprobability(DC_training, vocab);
		
		c.featureSelection(3);
	}

}
