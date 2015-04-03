package p4;

public class Main {

	public static void main(String[] args) {
		String dc = args[0];
		MNBclassification c = new MNBclassification(dc);
		MNBprobability p = new MNBprobability(c.getDCTraining(), c.getVocabSize());
	}

}
