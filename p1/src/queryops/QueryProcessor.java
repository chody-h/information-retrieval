package queryops;

import textops.*;

public class QueryProcessor {

	private PorterStemmer stemmer;
	private TextProcessor tp;
	
	public QueryProcessor(TextProcessor tp) {
		stemmer = new PorterStemmer();	
		this.tp = tp;
	}

	public String StemQuery(String query) {
		String[] ret = tp.Tokenize(query);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ret.length; i++) {
			if (ret[i] != null) sb.append(ret[i] + " ");
		}
		return sb.toString();
	}
}
