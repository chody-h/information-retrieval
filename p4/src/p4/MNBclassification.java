package p4;

import java.io.File;
import java.io.FilenameFilter;

public class MNBclassification {
	private String[] classes;
	
//	remove stopwords
//	partition into two subsets, training & test
//	be able to apply feature selection
	MNBclassification(String dc) {
		File collection = new File(dc);
		classes = collection.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		for (int i = 0; i < classes.length; i++) {
			int num_files = new File(classes[i]).list().length;
		}
			
	}
	
//	determine which words to represent documents in training&test set based on IG
//	if M >= size of vocab, apply no feature selection
//	return selectedFeatures
	private void featureSelection(DCtraining, M>=1) {
		
	}
	
//	assigns most probable class for a particular doc
//	must use getWordProbability and getClassProbability
//	returns the class that should be assigned to doc
	private void label(Document in test_set) {
		
	}
}
