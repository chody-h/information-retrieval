package p4;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import textops.StopWords;
import util.Utilities;

public class MNBclassification {
	// name of the collection
	private File DC;
	// names of each class. to get the directory name: DC+"/"+classes[i]
	private File[] classes;
	// names of each file assigned to training
	private File[] DC_training_files;
	// names of each file assigned to test
	private File[] DC_test_files;
	// total number of files in the DC
	private int DC_size;
	// complete vocabulary of DC: <word, IG>
	private LinkedHashMap<String, Double> v;
	// storage of training document vectors sorted by class - <class, <word, count>>
	private LinkedHashMap<File, LinkedHashMap<String, Integer>> DC_training;
	// storage of test document vectors sorted by class - <class, <word, count>>
	private LinkedHashMap<File, LinkedHashMap<String, Integer>> DC_test;
	
	private StopWords sw = new StopWords();
	
//	remove stopwords
//	partition into two subsets, training & test
//	be able to apply feature selection
	MNBclassification(String dc) {
		DC = new File(dc);
		String[] classnames = DC.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		// open all classes as files and count the number of documents
		ArrayList<File> classFiles = new ArrayList<File>();
		DC_size = 0;
		for (String n : classnames) {
			File c = new File(DC+"/"+n);
			classFiles.add(c);
			DC_size += c.listFiles().length;
		}
		classes = classFiles.toArray(new File[classFiles.size()]);
		
		Partition();
		
		v = Utilities.GetVocab(DC_training_files);
		if (v.size() < 100) System.out.println("Vocab: " + v);
		else {
			System.out.printf("First 100 of %d features in the collection's vocab: {", v.size());
			String delim = "";
			int count = 0;
			for (Entry<String, Double> entry : v.entrySet()) {
				if (count++ == 100) break;
				System.out.printf("%s%s=%2.2f", delim, entry.getKey(), entry.getValue());
				delim = ", ";
			}
			System.out.println("}");
		}
		
		DC_training = Utilities.GetDocumentVectors(DC_training_files);
	}

	// partition files into two subsets
	private void Partition() {
		ArrayList<File> DCtraining = new ArrayList<File>();
		ArrayList<File> DCtest = new ArrayList<File>();
		if (DC.getName().equals("test")) {
			for (File c : classes) {
				File[] listings = c.listFiles();
				for (File f : listings) {
					int number = Integer.parseInt(f.getName().split("\\.txt")[0]);
					if (number <= 6) {
						DCtraining.add(f);
					}
					else if (number == 7) {
						DCtest.add(f);
					}
					else {
						// extra files
					}
				}
			}
		}
		else {
			int training = (int) Math.floor(DC_size*0.8);
			for (File c : classes) {
				File[] listings = c.listFiles();
				int num_in_training = 0;
				int max_train = (int) Math.floor(listings.length*0.8);
				int num_in_test = 0;
				int max_test = listings.length - max_train;
				for (File f : listings) {
					if ((DCtraining.size() < training && num_in_training < max_train && Math.random() < 0.8) || num_in_test >= max_test) {
						num_in_training++;
						DCtraining.add(f);
					}
					else {
						num_in_test++;
						DCtest.add(f);
					}
				}
				assert(num_in_training == max_train);
				assert(num_in_test == max_test);
			}
		}
		
		DC_training_files = DCtraining.toArray(new File[DCtraining.size()]);
		DC_test_files = DCtest.toArray(new File[DCtest.size()]);
		
		System.out.printf("Placed %d files in DC_training and %d files in DC_test.\n", DC_training_files.length, DC_test_files.length);
//		for (File f : DC_training) {
//			System.out.println(f.getName());
//		}
//		System.out.println("------");
//		for (File f : DC_test) {
//			System.out.println(f.getName());
//		}
	}
	
////	determine which words to represent documents in training&test set based on IG
////	if M >= size of vocab, apply no feature selection
////	return selectedFeatures
//	private void featureSelection(DCtraining, M>=1) {
//		
//	}
//	
////	assigns most probable class for a particular doc
////	must use getWordProbability and getClassProbability
////	returns the class that should be assigned to doc
//	private void label(Document in test_set) {
//		
//	}
	
	public LinkedHashMap<String, Double> getVocab() {
		return v;
	}

	public LinkedHashMap<File, LinkedHashMap<String, Integer>> getDCTraining() {
		return DC_training;
	}
	
	public LinkedHashMap<File, LinkedHashMap<String, Integer>> getDCTest() {
		return DC_test;
	}
}
