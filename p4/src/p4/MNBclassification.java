package p4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Scanner;

public class MNBclassification {
	// name of the collection
	private File DC;
	// names of each class. to get the directory name: DC+"/"+classes[i]
	private File[] classes;
	// names of each file assigned to training
	private File[] DC_training;
	// names of each file assigned to test
	private File[] DC_test;
	// total number of files in the DC
	private int DC_size;
	
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
		
		ParseFiles(DC_training);
	}

	// partition files into two subsets
	private void Partition() {
		int training = (int) Math.floor(DC_size*0.8);
		ArrayList<File> DCtraining = new ArrayList<File>();
		ArrayList<File> DCtest = new ArrayList<File>();
		for (File c : classes) {
			File[] listings = c.listFiles();
			int num_in_class = 0;
			int max_per_class = (int) Math.floor(listings.length*0.8);
			for (File f : listings) {
				if ((DCtraining.size() < training && num_in_class < max_per_class && Math.random() < 0.8) || DCtest.size() >= DC_size-training) {
					num_in_class++;
					DCtraining.add(f);
				}
				else {
					DCtest.add(f);
				}
			}
		}
		DC_training = DCtraining.toArray(new File[DCtraining.size()]);
		DC_test = DCtest.toArray(new File[DCtest.size()]);
		
		System.out.printf("Placed %d files in DC_training and %d files in DC_test.\n", DC_training.length, DC_test.length);
//		for (File f : DC_training) {
//			System.out.println(f.getName());
//		}
//		System.out.println("------");
//		for (File f : DC_test) {
//			System.out.println(f.getName());
//		}
	}
	
	// parse files to gather vocabulary
	private void ParseFiles(File[] set) {
		System.out.println("Parsing files.");
		Scanner s;
//		for (File f : set) {
			try {
				System.out.println(set[0].getPath());
				s = new Scanner(set[0]);
				String line;
				if (DC.getName().equals("20NG")) {
					s.useDelimiter("\n\n");
					while (s.hasNext()) {
						System.out.println(s.next()+"--------------------------------------");
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println("Error opening file " + set[0].getPath()+"/"+set[0].getName());
//				continue;
			}
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
}
