
public class Trie {
	
	public Node root = new Node();
	
	private class Node {
		public char[] children = new char[26];
		public boolean complete = false;
	}
}
