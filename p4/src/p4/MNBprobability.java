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
		// stores number of files each word is in by class
		// <word, <class, count>>
		LinkedHashMap<String, LinkedHashMap<String, Double>> wordcountbyclass = new LinkedHashMap<String, LinkedHashMap<String, Double>>();
		// stores number of files word is not in by class
		LinkedHashMap<String, LinkedHashMap<String, Double>> antiwordcountbyclass = new LinkedHashMap<String, LinkedHashMap<String, Double>>();
		// stores number of files word is in
		HashMap<String, Integer> wordcount = new HashMap<String, Integer>();
		// stores number of files word is not in
		HashMap<String, Integer> antiwordcount = new HashMap<String, Integer>();
		
		// iterate over every file
		for (Entry<File, LinkedHashMap<String, Integer>> document : training_set.entrySet()) {
			// get the file
			File doc = document.getKey();
			// get the name of the class the file is classified in
			String className = Utilities.GetClassFromFile(doc);
			// each word in the document and its count
			LinkedHashMap<String, Integer> documentvector = document.getValue();
			// iterate through every word in the vocab
			for (Entry<String, Double> component : vocab.entrySet()) {
				String word = component.getKey();
				LinkedHashMap<String, Double> temp;
				Double num_files = 1.0;
				// word is in document
				if (documentvector.containsKey(word)) {
					if (wordcountbyclass.containsKey(word)) {
						temp = wordcountbyclass.get(word);
						if (temp.containsKey(className)) {
							num_files = temp.get(className) + 1.0;
						}
						else {
							num_files = 1.0;
						}
					}
					else {
						temp = new LinkedHashMap<String, Double>();
					}
					temp.put(className, num_files);
					wordcountbyclass.put(word, temp);
					
					if (wordcount.containsKey(word)) {
						wordcount.put(word, wordcount.get(word)+1);
					}
					else {
						wordcount.put(word, 1);
					}
				}
				// word is not in document
				else {
					if (antiwordcountbyclass.containsKey(word)) {
						temp = antiwordcountbyclass.get(word);
						if (temp.containsKey(className)) {
							num_files = temp.get(className) + 1.0;
						}
						else {
							num_files = 1.0;
						}
					}
					else {
						temp = new LinkedHashMap<String, Double>();
					}
					temp.put(className, num_files);
					antiwordcountbyclass.put(word, temp);
					
					if (antiwordcount.containsKey(word)) {
						antiwordcount.put(word, antiwordcount.get(word)+1);
					}
					else {
						antiwordcount.put(word, 1);
					}
				}
			}
		}
		
		// iterate over every word to convert counts to probabilities
		for (Entry<String, LinkedHashMap<String, Double>> e : wordcountbyclass.entrySet()) {
			String word = e.getKey();
			LinkedHashMap<String, Double> byclass = e.getValue();
			for (Entry<String, Double> c : byclass.entrySet()) {
				String className = c.getKey();
				Double prob = c.getValue() / wordcount.get(word);
				byclass.put(className, prob);
			}
		}
		for (Entry<String, LinkedHashMap<String, Double>> e : antiwordcountbyclass.entrySet()) {
			String word = e.getKey();
			LinkedHashMap<String, Double> byclass = e.getValue();
			for (Entry<String, Double> c : byclass.entrySet()) {
				String className = c.getKey();
				Double prob = c.getValue() / antiwordcount.get(word);
				byclass.put(className, prob);
			}
		}
		
		WordProbabilities ret = new WordProbabilities(wordcountbyclass, antiwordcountbyclass);
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
		Double ret = wp.GetProbability(w, c);
		if (ret == null) return 0.0;
		else return ret;
	}
	
	public Double GetNotWordProbability(String w, String c) {
		Double ret = wp.GetNotProbability(w, c);
		if (ret == null) return 0.0;
		else return ret;
	}
	
//	returns probability of c
	public Double GetClassProbability(String c) {
		return cp.GetProbability(c);
	}
}
