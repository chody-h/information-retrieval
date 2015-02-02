import textops.*;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TextProcessor tp = new TextProcessor();
		
		tp.InitFiles();
		System.out.println(tp.PrintRankFreq_Words());
		
//		tp.InitBigrams();
//		tp.PrintRankFreq_Bigrams();
	}

}
