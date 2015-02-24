import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;


public class Trie {
	
	public Node root = new Node(null, (char)0, false);
	Random r = new Random();
	
	// q should consist ONLY of lower case letters and spaces. nothing else.
	public void AddQuery(String q) {
		Node current = root;
		for (int i = 0; i < q.length(); i++) {
			char c = q.charAt(i);
			c = (char) ConvertToIndex(c);
			boolean isLast = (i == q.length() - 1);
			if (current.children[c] == null) 
				current.children[c] = new Node(current, q.charAt(i), isLast);
			current = current.children[c];
			if (isLast) 
				current.IncrementCount();
		}
	}
	
	public Node GetNode(String s) {
		try {
			Node current = root;
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				c = ConvertToIndex(c);
				current = current.children[c];
			}
			return current;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	// find a random word in the trie
	public String GetRandom() {
		Node current = root;
		while (!current.complete) {
			int randomInt = r.nextInt(26);
			while (current.children[randomInt] == null)
				randomInt = (randomInt + 1) % 27;
			current = current.children[randomInt];
		}
		return GetWord(current);
	}
	
	// pass in a node, traverse up the tree, return the whole string
	public String GetWord(Node n) {
		StringBuilder ret = new StringBuilder();
		while (n.parent != null) {
			ret.insert(0, n.c);
			n = n.parent;
		}
		return ret.toString();
	}
	
	private char ConvertToIndex(char c) {
		if (c == ' ') return c -= 6;			// put space at the last spot in the array
		else return c -= 97;					// put everything else in order in the array
	}
	
	public class Node {
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
		
		public boolean HasNoChildren() {
			for (Node n : children) {
				if (n != null) return false;
			}
			return true;
		}
		
		public HashSet<Node> GetExpansions() {
			HashSet<Node> ret = new HashSet<Node>();
			for (Node n : children) 
				if (n != null) 
					ret.addAll(n.GetValidExpansions());

			return ret;
		}
		
		public HashSet<Node> GetValidExpansions() {
			HashSet<Node> ret = new HashSet<Node>();
			if (this.complete) 
				ret.add(this);
			else 
				for (Node n : children) 
					if (n != null) 
						ret.addAll(n.GetValidExpansions());

			return ret;
		}
	}
}
