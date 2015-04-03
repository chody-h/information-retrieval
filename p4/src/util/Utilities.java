package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import textops.PorterStemmer;
import textops.StopWords;

public class Utilities {
	
	private static StopWords sw = new StopWords();
	
	// parse files to gather vocabulary
	public static LinkedHashMap<String, Double> GetVocab(File[] set) {
		LinkedHashMap<String, Double> vocab = new LinkedHashMap<String, Double>();
		for (File f : set) {
//			System.out.println("File: " + f.getPath());
			LinkedHashMap<String, Integer> temp = ParseFile(f);
			for (Entry<String, Integer> entry : temp.entrySet()) {
				String word = entry.getKey();
				Double value = entry.getValue() + 0.0;
				if (vocab.containsKey(word)) value += vocab.get(word);
				vocab.put(word, value);
			}
//			break;
		}
		return vocab;
	}
	
	// parse files to create vectors
	public static LinkedHashMap<File, LinkedHashMap<String, Integer>> GetDocumentVectors(File[] set) {
		LinkedHashMap<File, LinkedHashMap<String, Integer>> dv = new LinkedHashMap<File, LinkedHashMap<String, Integer>>();
		for (File f : set) {
//			System.out.println("File: " + f.getPath());
			LinkedHashMap<String, Integer> temp = ParseFile(f);
			dv.put(f, temp);
//			break;
		}
		return dv;
	}
	
	private static LinkedHashMap<String, Integer> ParseFile(File f) {
		try {
			Scanner s = new Scanner(f);
			LinkedHashMap<String, Integer> vocab = new LinkedHashMap<String, Integer>();
			s.useDelimiter("\n\n");
			if (s.hasNext()) s.next();
			s.useDelimiter("\\s");
			String word;
			while (s.hasNext()) {
				word = s.next();
				word = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
				if (word.length() > 0 && !sw.contains(word)) {
					word = PorterStemmer.stem(word);
					Integer count = 1;
					if (vocab.containsKey(word)) {
						count += vocab.get(word);
					}
					vocab.put(word, count);
				}
			}
			s.close();
			return vocab;
		} catch (FileNotFoundException e) {
			System.out.println("Error opening file " + f.getPath()+"/"+f.getName());
			return new LinkedHashMap<String, Integer>();
		}
	}
}
