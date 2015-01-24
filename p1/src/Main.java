import textops.TextProcessor;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TextProcessor tp = new TextProcessor();
		int test = tp.FindFrequencyByDocument("hello", 0);
		System.out.println(test);
	}

}
