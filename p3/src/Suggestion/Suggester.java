package Suggestion;

public class Suggester {
	
	// input: single word, no space, no punctuation. caps OK
	// output: soundex code
	// examples:
	// 	extenssions —> E235; extensions —> E235
	// 	marshmellow —> M625; marshmallow —> M625
	// 	brimingham —> B655; birmingham —> B655
	// 	poiner —> P560; pointer —> P536
	public static String Soundex(String word) {
		char[] w = word.toLowerCase().toCharArray();

		// keep the first letter in uppercase
		w[0] = Character.toUpperCase(w[0]);

		for (int i = 1; i < w.length; i++) {
			String c = Character.toString(w[i]);

			// replace these letters with hyphens: a, e, i, o, u, y, h, w
			if (c.toString().matches("[aeiouyhw]"))
				w[i] = '-';

			// replace the other letters by numbers as follows:

			// 	1: b, f, p, v
			else if (c.matches("[bfpv]"))
				w[i] = '1';

			// 	2: c, g, j, k, q, s, x, z 
			else if (c.matches("[cgjkqsxz]"))
				w[i] = '2';

			// 	3: d, t
			else if (c.matches("[dt]"))
				w[i] = '3';

			// 	4: l 
			else if (c.matches("[l]"))
				w[i] = '4';

			// 	5: m, n 
			else if (c.matches("[mn]"))
				w[i] = '5';

			// 	6: r 
			else if (c.matches("[r]"))
				w[i] = '6';

			else
				return "ERROR";
		}

		// delete adjacent repeats of a number (deletions will be marked with *)
		char prev = w[0];
		for (int i = 1; i < w.length; i++) {
			if (w[i] == prev) 
				w[i] = '*';

			// delete hyphens 
			if (w[i-1] == '-')
				w[i-1] = '*';

			prev = w[i];
		}

		// keep first three numbers or pad out with zeroes
		StringBuilder soundex = new StringBuilder();
		for (int i = 0; i < w.length; i++) {
			if (soundex.length() == 4)
				break;

			if (w[i] != '*')
				soundex.append(w[i]);
		}
		while (soundex.length() != 4)
			soundex.append("0");

		return soundex.toString();
	}
	
}
