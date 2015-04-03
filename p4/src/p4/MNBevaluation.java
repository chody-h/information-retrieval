package p4;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import util.Utilities;

public class MNBevaluation {
	MNBevaluation(){}
	
	public Double accuracyMeasure(LinkedHashMap<File, String> classifications) {
		double numDocs = classifications.size();
		double numCorrect = 0;
		for (Entry<File, String> e : classifications.entrySet()) {
			File f = e.getKey();
			String className = Utilities.GetClassFromFile(f);
			String assignedClass = e.getValue();
			if (className.equals(assignedClass)) {
				numCorrect++;
			}
		}
		
		return numCorrect/numDocs;
	}
}
