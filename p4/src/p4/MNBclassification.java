package p4;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class MNBclassification {
	// name of the collection
	private File DC;
	// names of each class. to get the directory name: DC+"/"+classes[i]
	private File[] classes;
	// names of each file assigned to training
	private File[] DC_training;
	// names of each file assigned to test
	private File[] DC_test;
	
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
		// get all classes as files and count the number of documents
		ArrayList<File> classFiles = new ArrayList<File>();
		int num_files = 0;
		for (int i = 0; i < classnames.length; i++) {
			File c = new File(DC+"/"+classes[i]);
			classFiles.add(c);
			num_files += c.listFiles().length;
		}
		classes = (File[]) classFiles.toArray();
		
		// partition into two subsets
		int training = (int) Math.floor(num_files*0.8);
		int test = num_files - training;
		ArrayList<String> DCtraining = new ArrayList<String>();
		ArrayList<String> DCtest = new ArrayList<String>();
		for (int i = 0; i < classes.length; i++) {
			File dir = new File(DC+"/"+classes[i]);
			File[] listings = dir.listFiles();
			for (File f : listings) {
				if (DCtraining.size() < training && Math.random() < 0.8) {
					
				}
			}
		}
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
