import java.util.ArrayList;
import org.joda.time.DateTime;

public class RelatedQueries {
	
	public int userID;
	public ArrayList<Query> queries;
	
	public RelatedQueries(String line_from_log) {
		
	}
	
	public boolean IsRelated(String line_from_log) {
		return false;
	}
	
	private String[] ParseLine(String line_from_log) {
		String[] tokens = line_from_log.split(" ");
		String[] ret = new String[3];
		ret[0] = tokens[0];														// userID
		ret[2] = tokens[tokens.length-2] + ' ' + tokens[tokens.length-1];		// datetime
		ret[1] = line_from_log.replace(ret[0], "");								// query
		ret[1] = ret[1].replace(ret[2], "");
		return ret;
	}
	
	private class Query {
		public String text;
		public DateTime time;
		
		Query(String q, String t) {
			text = q;
			time = new DateTime(t);
		}
	}
}
