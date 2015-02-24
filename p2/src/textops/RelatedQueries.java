package textops;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class RelatedQueries {
	
	public int userID;
	public ArrayList<Query> queries;
	
	public RelatedQueries() {
		userID = 0;
		queries = new ArrayList<Query>();
	}
	
	public boolean IsRelated(String[] query) {
		// false: nothing in it
		// false: user id is different
		// false: more than 10min apart
		// true: otherwise
		try {
			int uID = Integer.parseInt(query[0]);
			Query q = new Query(query[1], query[2]);
			if (uID != userID) return false;
			if (q.time.getMillis() - queries.get(queries.size() - 1).time.getMillis() > 600000) return false;
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public void AddRelated(String[] query) {
		userID = Integer.parseInt(query[0]);
		Query q = new Query(query[1], query[2]);
		queries.add(q);
	}
	
	public String[] ParseLine(String line_from_log) {
		String[] tokens = line_from_log.split("\t");
		if (tokens.length > 3) System.out.println(tokens[3].toString());
//		try {
//			String[] ret = new String[3];
//			ret[0] = tokens[0];														// userID
//			ret[2] = tokens[tokens.length-2] + ' ' + tokens[tokens.length-1];		// datetime
//			ret[1] = line_from_log.replace(ret[0], "").replace(ret[2], "").replace("\t", "");	// query
//			return ret;
//		}
//		catch (Exception e) {
////			e.printStackTrace();
//			String[] ret = new String[3];
//			return ret;
//		}
		return tokens;
	}
	
	public String GetNextQuery(String s) {
		for (int i = 0; i < queries.size(); i++) {
			if (queries.get(i).text.equals(s)) {
				if (i != queries.size()-1) 
					return queries.get(i+1).text;
				else
					return null;
			}
		}
		return null;
	}
	
	public String GetLastQuery() {
		return queries.get(queries.size()-1).text;
	}
	
	public boolean QueryInSet(String s) {
		for(Query q : queries) {
			if (q.text.equals(s))
				return true;
		}
		
		return false;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(userID);
		sb.append("\n");
		for (Query q : queries) {
			sb.append(q.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private class Query {
		public String text;
		public DateTime time;
		
		Query(String q, String t) {
			text = q;
//			time = new DateTime(t);
			time = DateTime.parse(t, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
		}
		
		public String toString() {
			return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(time) + ": " + text;
		}
	}
}
