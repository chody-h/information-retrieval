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
		
//			queries[0] = "James Bond Actors";		// test query
			
		String[] stemmed = new String[10];
		Results[] results = new Results[10];
			
		for (int i = 0; i < queries.length; i++) {
			String[] temp = tp.Tokenize(queries[i]);
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < temp.length; j++) {
				if (temp[j] != null) sb.append(temp[j] + " ");
			}
			stemmed[i] = sb.toString();
			
			results[i] = qp.Search(queries[i]);
			System.out.format(
					"==================\n" +
					"You searched for:  %s\n" +
					"%s", 
					queries[i], results[i]);
			System.out.format("Debug:  %s\n", stemmed[i]);
			System.out.format("------------------\n");
		}
	}

}
