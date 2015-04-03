package p4;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import util.ClassProbabilities;
import util.Utilities;
import util.WordProbabilities;

public class MNBprobability {	
	
	private WordProbabilities wp;
	private ClassProbabilities cp;
	
	MNBprobability(LinkedHashMap<File, LinkedHashMap<String, Integer>> training_set, LinkedHashMap<String, Double> vocab) {
		wp = ComputeWordProbability(training_set, vocab);
		
		HashMap<String, Double> classCounts = new HashMap<String, Double>();
		for (Entry<File, LinkedHashMap<String, Integer>> entry : training_set.entrySet()) {
			File doc = entry.getKey();
			String className = Utilities.GetClassFromFile(doc);
//			LinkedHashMap<String, Integer> documentVector = entry.getValue();
//			Double classCount = documentVector.size() + 0.0;
			Double classCount = 1.0;
			if (classCounts.containsKey(className)) classCount += classCounts.get(className);
			classCounts.put(className, classCount);
		}
		cp = ComputeClassProbability(classCounts);
	}
	
//	compute probability of each word in each class using training set
//	use Laplacian Smoothed Estimate (slide 16)
//	return WordProbabilities: each word and its probability (hashmap?)
	private WordProbabilities ComputeWordProbability(LinkedHashMap<File, LinkedHashMap<String, Integer>> training_set, LinkedHashMap<String, Double> vocab) {
		// stores each word's count by class
		HashMap<String, HashMap<String, Double>> classes = new HashMap<String, HashMap<String, Double>>();
		// iterate over every file
		for (Entry<File, LinkedHashMap<String, Integer>> entry : training_set.entrySet()) {
			// get the file
			File doc = entry.getKey();
			// get the name of the class the file is classified in
			String className = Utilities.GetClassFromFile(doc);
			// each word and its count
			HashMap<String, Double> wordCountsByClass;
			if (classes.containsKey(className))
				wordCountsByClass = classes.get(className);
			else
				 wordCountsByClass = new HashMap<String, Double>();
			// each word in the document and its count
			LinkedHashMap<String, Integer> documentvector = entry.getValue();
			for (Entry<String, Integer> wordcounts : documentvector.entrySet()) {
				String word = wordcounts.getKey();
				Double count = wordcounts.getValue().doubleValue();
				// add the current count to the total class count
				if (wordCountsByClass.containsKey(word))
					count += wordCountsByClass.get(word);
				wordCountsByClass.put(word, count);
			}
			// update the class information
			classes.put(className, wordCountsByClass);
		}
		
		
		for (Entry<String, HashMap<String, Double>> entry : classes.entrySet()) {
			String className = entry.getKey();
			int totalCount = 0;
			HashMap<String, Double> wordCounts = entry.getValue();
			for (Entry<String, Double> words : wordCounts.entrySet()) {
//				String word = words.getKey();
				Double count = words.getValue();
				totalCount += count;
			}
			for (Entry<String, Double> words : vocab.entrySet()) {
				String word = words.getKey();
				Double count = 0.0;
				if (wordCounts.containsKey(word)) count = wordCounts.get(word);
				Double probability = (count + 1) / (totalCount + vocab.size());
				wordCounts.put(word, probability);
			}
			classes.put(className, wordCounts);
		}
		
		WordProbabilities ret = new WordProbabilities(classes);
		return ret;
	}
	
//	compute probability of each class in C
//	return ClassProbabilities: each class and its probability (hashmap?)
	private ClassProbabilities ComputeClassProbability(HashMap<String, Double> classCounts) {
		int numDocs = 0;
		for (Entry<String, Double> entry : classCounts.entrySet()) {
//			String className = entry.getKey();
			Double docCount = entry.getValue();
			numDocs += docCount;
		}
		for (Entry<String, Double> entry : classCounts.entrySet()) {
			String className = entry.getKey();
			Double docCount = entry.getValue();
			Double probability = docCount / numDocs;
			classCounts.put(className, probability);
		}
		
		ClassProbabilities ret = new ClassProbabilities(classCounts);
		return ret;
	}
	
//	retrieves probability of word in class
//	includes probability of words not seen while training
//	returns probability of w in c stored in WordProbabilities
	public Double GetWordProbability(String w, String c) {
		return wp.GetProbability(w, c);
	}
	
//	returns probability of c
	public Double GetClassProbability(String c) {
		return cp.GetProbability(c);
	}
}
