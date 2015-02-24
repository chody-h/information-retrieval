import java.util.Random;


public class Trie {
	
	public Node root = new Node(null, (char)0, false);
	Random r = new Random();
	
	// q should consist ONLY of lower case letters and spaces. nothing else.
	public void AddQuery(String q) {
		Node current = root;
		for (int i = 0; i < q.length(); i++) {
			char c = q.charAt(i);
			if (c == ' ') c -= 6;						// put space at the last spot in the array
			else c -= 97;								// put everything else in order in the array
			boolean isLast = (i == q.length() - 1);
			if (current.children[c] == null) 
				current.children[c] = new Node(current, q.charAt(i), isLast);
			current = current.children[c];
			if (isLast) 
				current.IncrementCount();
		}
	}
	
	// find a random word in the trie
	public String GetRandom() {
		Node current = root;
		while (!current.complete) {
			int combo_breaker = 0;
			int randomInt = r.nextInt(26);
			while (current.children[randomInt] == null)
				randomInt = (randomInt + 1) % 27;
			current = current.children[randomInt];
		}
		return GetWord(current);
	}
	
	// pass in a node, traverse up the tree, return the whole string
	private String GetWord(Node n) {
		StringBuilder ret = new StringBuilder();
		while (n.parent != null) {
			ret.insert(0, n.c);
			n = n.parent;
		}
		return ret.toString();
	}
	
	private class Node {
		public Node[] children;
		public Node parent;
		public char c;
		public boolean complete;
		public int count;
		
		public Node(Node parent, char c, boolean iscomplete) {
			children = new Node[27];	// hold all letters + space
			this.parent = parent;
			this.c = c;
			complete = iscomplete;
			count = 0;
		}
		
		public void IncrementCount() {
			count++;
		}
	}
}
