import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;



public class Main {

	public static void main(String[] args) {
		QuerySuggestor qs = new QuerySuggestor();
		System.out.println("Parsing files...");
		qs.ParseFiles();
		System.out.println("Done parsing files.");
		qs.PrintSomeTrie();
		while (true) {
			String in = ValidInput("[A-Za-z ]+");
			HashMap<String, Double> outputs =qs.Suggest(in);
			for (int i = 0; i < 8; i++) {
				System.out.println(LargestKey(outputs));
			}
			System.out.println();
		}
	}
	
	private static String ValidInput(String regex) {
		Scanner s = new Scanner(System.in);
		boolean valid = false;
		String input = "";
		while (!valid) {
			System.out.println("Please enter a query:");
			input = s.nextLine();
			if (input.matches(regex)) {
				valid = true;
				System.out.println("Valid.");
			}
			else 
				System.out.println("Your query must match " + regex);
		}
		return input.toLowerCase();
	}
	
	private static String LargestKey(HashMap<String, Double> m) {
		Iterator<Entry<String, Double>> it = m.entrySet().iterator();
		double largestVal = 0;
		String bestSugg = "";
		while (it.hasNext()) {
			Map.Entry<String, Double> pair = (Map.Entry<String, Double>) it.next();
			Double temp = (Double)pair.getValue();
			if (temp >= largestVal) {
				largestVal = temp;
				bestSugg = (String)pair.getKey();
			}
		}
		if (bestSugg.equals("")) {
			return "---";
		}
		else {
			m.remove(bestSugg);
			return bestSugg + " (" + Double.toString(largestVal) + ")";
		}
	}

}
