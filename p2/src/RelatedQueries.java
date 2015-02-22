import java.util.ArrayList;
import org.joda.time.DateTime;

public class RelatedQueries {
	
	public int userID;
	public ArrayList<Query> queries;
	
	private class Query {
		public String text;
		public DateTime time;
		
		Query(String q, String t) {
			text = q;
			time = new DateTime(t);
		}
	}
}
