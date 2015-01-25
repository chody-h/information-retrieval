package queryops;

import textops.*;

public class QueryProcessor {

	private static int NUM_DOCS_TO_PROCESS = 322;
	private TextProcessor tp;
	
	public QueryProcessor(TextProcessor tp) {
		this.tp = tp;
	}
	
	public String Search(String query) {
		double[] result_scores = new double[NUM_DOCS_TO_PROCESS];
		
		String[] q = tp.Tokenize(query);
		int[][] n = tp.FindRelevantDocs(q);
		int[] max_f = tp.MaxFreqOfDoc(-1);
		int[] d_scores = new int[q.length];
		for (int j = 0; j < q.length; j++) {
			for (int i = 0; i < result_scores.length; i++) {
				if (n[i][j] != 0) d_scores[j] += 1;
			}
		}
		
		int C = NUM_DOCS_TO_PROCESS;
		for (int i = 0; i < result_scores.length; i++) {
			double score = 0;
			int B = max_f[i];
			for (int j = 0; j < q.length; j++) {
				double A = n[i][j];
				double D = d_scores[j];
				
				score += CalculateScore(A, B, C, D);
			}
			result_scores[i] = score;
		}
		
		Results r = new Results(result_scores);
		
		return r.toString();
	}
	
	public double CalculateScore(double A, double B, double C, double D) {
		double tf = A / B;
		double idf_one = (double)(Math.log(C)/Math.log(2));
		double idf_two = (double)(Math.log(D)/Math.log(2));
		
		return tf * (idf_one - idf_two);
	}
	
	public class Results {
		public int[] doc_ids;
		public String[] first_sentences;
		public double[] top_scores;
		
		public Results(double[] scores) {
			doc_ids = new int[10];
			first_sentences = new String[10];
			top_scores = new double[10];
			
			for (int i = 0; i < scores.length; i++) {
				if (scores[i] >= top_scores[9]) {
					BubbleUp(i, scores[i]);
				}
			}
		}
		
		private void BubbleUp(int doc_id, double score) {
			int i = 9;
			while (i >= 0 && score >= top_scores[i]) {
				if (i != 9) {
					doc_ids[i+1] = doc_ids[i];
					first_sentences[i+1] = first_sentences[i];
					top_scores[i+1] = top_scores[i];
				}
				i--;
			}
			i++;
			doc_ids[i] = doc_id;
			first_sentences[i] = tp.GetFirstSentence(doc_id);
			top_scores[i] = score;
		}
		
		public String toString() {
			StringBuilder ret = new StringBuilder();
			for (int i = 0; i < 10; i++) {
				ret.append(doc_ids[i] + "\n");
				ret.append(first_sentences[i] + "\n");
				ret.append(top_scores[i] + "\n");
			}
			return ret.toString();
		}
	}
}
