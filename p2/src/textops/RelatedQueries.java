package textops;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class RelatedQueries {
	
	public RelatedQueries() {
		userID = 0;
		queries = new ArrayList<Query>();
	}
	
	public double ModifiedTo(String q1, String q2) {
		double ret = 0;
		
		for (int i = 0; i < queries.size(); i++) {
			if (queries.get(i).text.equals(q1) && i < queries.size()-1 && queries.get(i+1).text.equals(q2)) {
				ret++;
				break;
			}
		}
		
		return ret;
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
	
	public String GetNextQuery(String s) {
		for (int i = 0; i < queries.size(); i++)
			if (queries.get(i).text.equals(s))
				if (i != queries.size()-1)
					if (queries.get(i+1).text.equals(s)) 
						continue;
					else return queries.get(i+1).text;
				else
					return null;
		return null;
	}
	
	public String GetLastQuery() {
		return queries.get(queries.size()-1).text;
	}
	
	public boolean QueryInSet(String s) {
		for(Query q : queries) {
			if (q.text.lastIndexOf(s, 0) == 0)
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
			time = DateTime.parse(t, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
		}
		
		public String toString() {
			return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(time) + ": " + text;
		}
	}
}
