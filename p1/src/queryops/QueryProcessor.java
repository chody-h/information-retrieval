package queryops;

import textops.*;

public class QueryProcessor {

	private static int NUM_DOCS_TO_PROCESS = 322;
	private TextProcessor tp;
	
	public QueryProcessor(TextProcessor tp) {
		this.tp = tp;
	}
	
	public Results Search(String query) {
		double[] result_scores = new double[NUM_DOCS_TO_PROCESS];
		
		// convert query from one string to multiple tokenized words
		String[] q = tp.Tokenize(query);
		// all documents enumerating how many times they contain the specified token
		// [doc][word]
		int[][] n = tp.FindRelevantDocs(q);
		// the maximum frequency of every document
		int[] max_f = tp.MaxFreqOfDoc(-1);
		// the number of documents in which the token appears
		int[] D_scores = new int[q.length];
		for (int j = 0; j < q.length; j++) {
			if (q[j] == null) continue;
			
			for (int i = 0; i < result_scores.length; i++) {
				if (n[i][j] != 0) D_scores[j] += 1;
			}
		}
		
		int C = NUM_DOCS_TO_PROCESS;
		for (int i = 0; i < result_scores.length; i++) {
			double score = 0;
			int B = max_f[i];											// if (i==199) System.out.println(B);
			for (int j = 0; j < q.length; j++) {
				if (q[j] == null) continue;
				
				double A = n[i][j];
				double D = D_scores[j];
				
				score += CalculateScore(A, B, C, D);
			}
			result_scores[i] = score;
		}
		
		return new Results(tp, result_scores);
	}
	
	public double CalculateScore(double A, double B, double C, double D) {
		double tf = A / B;
		double idf_one = (double)(Math.log(C)/Math.log(2));
		double idf_two = (double)(Math.log(D)/Math.log(2));
		
		double ret = tf * (idf_one - idf_two);
		return ret;
	}
}
