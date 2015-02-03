import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import textops.*;


public class Main {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		TextProcessor tp = new TextProcessor();
		System.out.println(tp.InitFiles());
		
//		PrintWriter writer = new PrintWriter("output_words.txt", "UTF-8");
//		writer.print(tp.PrintRankFreq("words"));
//		writer.close();
//		
//		writer = new PrintWriter("output_bigrams.txt", "UTF-8");
//		writer.print(tp.PrintRankFreq("bigrams"));
//		writer.close();
//		
//		writer = new PrintWriter("output_combination.txt", "UTF-8");
//		writer.print(tp.PrintRankFreq("combination"));
//		writer.close();
	}

}
