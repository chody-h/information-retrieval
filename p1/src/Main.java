import textops.*;
import queryops.*;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TextProcessor tp = new TextProcessor();
		QueryProcessor qp = new QueryProcessor(tp);
		tp.InitFiles();
		
//		int test = tp.FindFrequencyByDocument("hello", 0);
//		int test2 = tp.WordCount(0);
//		System.out.println(test + " " + test2);
//		System.out.println(qp.StemQuery("to be or not to be"));
		
		String[] queries = new String[10];
			queries[0] = "killing incident";
			queries[1] = "suspect charged with murder";
			queries[2] = "court";
			queries[3] = "jury sentenced murderer to prison";
			queries[4] = "movie";
			queries[5] = "entertainment films";
			queries[6] = "court appeal won by accused";
			queries[7] = "action film producer";
			queries[8] = "drunk driving accusations";
			queries[9] = "actor appeared in movie premiere";
		String[] stemmed = new String[10];
			
		for (int i = 0; i < queries.length; i++) {
			stemmed[i] = qp.StemQuery(queries[i]);
			System.out.println(queries[i] + "\n  " + stemmed[i]);
		}
	}

}
