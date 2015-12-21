
import java.util.*;

public class DLBDictionary implements DictionaryInterface
{

	private ArrayList<String> list;
	private DLBNode rootNode;
	
	public DLBDictionary()
	{
		list = new ArrayList<String>();
		rootNode = new DLBNode();
	}
	
	//Pre-Recursive function to add new string to dictionary
	public boolean add(String s)
	{		
		list.add(s);
		return R_Add(rootNode, s, 0);
	}
	
	//Recursive function that adds new string to the dictionary by character
	public boolean R_Add(DLBNode currNode, String s, int p)
	{
		//Recurse until last character of the word is reached
		if(p < s.length())
		{
		
		char c = s.charAt(p);
		
		//When currNode is empty: Set its value to current character and create its child.
		//Continue recursion from currNode's child with the next character in the string.
		if(currNode.isEmpty())
		{	
			currNode.setValue(c);
			DLBNode childNode = new DLBNode();
			currNode.setChild(childNode);
			return R_Add(childNode, s, p+1);
		}
		
		//When currNode value == current character: Current character already exists.
		//Continue recursion from currNode's child with the next character in the string.
		else if(currNode.getValue() == c) 
			return R_Add(currNode.getChild(), s, p+1);
		
		//When currNode's next != null: Current character could exist.
		//Continue recursion from currNode's next to resume searching for the current character.	
		else if(currNode.hasNext())
			return R_Add(currNode.getNext(), s, p);
			
		//When currNode's next == null: Current character does not exist. Create currNode's next, set its value to current character, and also create its child.
		//Continue recursion from nextNode's child with the next character in the string.	
		else if(currNode.getValue() != c && !currNode.hasNext())
		{
			DLBNode nextNode = new DLBNode(c);
			DLBNode childNode = new DLBNode();
			nextNode.setChild(childNode);
			currNode.setNext(nextNode);
			return R_Add(nextNode.getChild(), s, p+1);
		}

		}
		
		//When last character of the word is added to dictionary create its child node with null terminator '$' and return true to end recursion
		else if(p >= s.length())
		{	
			currNode.setValue('$');
			return true;
		}
		
		return false;
	}

	//Recursive function that searches for a word in the dictionary by each character in the word
	public int R_Search(DLBNode currNode, String s, int p)
	{
		//Recurse until last character of the word is reached.
		if(p < s.length())
		{
			char c = s.charAt(p);
		
			//When currNode's value == current character, and currNode has a child: 
			//Continue recursion from currNode's child with the next character in the string.
			if(currNode.getValue() == c && currNode.hasChild())
				return R_Search(currNode.getChild(), s, p+1);
			
			//When currNode's value != current character, and currNode has a sibling: 
			//Continue recursion from currNode's next to resume searching for the current character.
			else if(currNode.getValue() != c && currNode.hasNext())
				return R_Search(currNode.getNext(), s, p);
				
			//When currNode's next == null: Current character does not exist.
			//Return 0. String is not a prefix or a word
			else if(!currNode.hasNext())
				return 0;
		}
		
		//When the string has been found determine whether it is a prefix, word or both.
		else if(p >= s.length())
		{
			//When currNode's value == '$': String is a word.
			if(currNode.getValue() == '$')
			{
				//When currNode has a right sibling or a previously passed up left sibling (p > s.length()):
				//Return 3. String it is a word and prefix. If not, Return 2. String is a word.
				if(currNode.hasNext() || p > s.length())
					return 3;
				else
					return 2;
			}
			
			//When currNode's value != '$', and currNode has a sibling: 
			//Continue recursion from currNode's next to resume searching for the '$'.
			//Also increment p, to allow for previously passed up left siblings
			else if(currNode.getValue() != '$' && currNode.hasNext())
			{
				return R_Search(currNode.getNext(), s, p+1);
			}
			
			//When currNode's next == null: '$' does not exist.
			//Return 1. String is a prefix.
			else
				return 1;
				
			
		}
		
		return 0;
	}
	
	//Pre-Recursive function to search for a word in the dictionary
	public int search(StringBuilder s)
	{
		return R_Search(rootNode, s.toString(), 0);
	}

	//Returns the entire sorted dictionary as a string, each word on its own line
	public String toString()
	{
		Collections.sort(list);
		StringBuilder returnString = new StringBuilder();
		for(String s : list)
		{
			returnString.append(s + "\n");
		}
		
		return returnString.toString();
	}
	
	//Returns the size of the dictionary
	public int size()
	{
		return list.size();
	}

	//Inner Node Class for DLB Trie
	private class DLBNode
	{
		//Node instance variables: Value of char, Node's sibling, and Node's Child
		private char value;
		private DLBNode next;
		private DLBNode child;
		
		//Constructors
		public DLBNode()
		{
			value = ' ';
		}
		
		public DLBNode(char c)
		{
			value = c;
		}
		
		//Returns true if the Node has a sibling its right
		public boolean hasNext()
		{
			return next != null;
		}
		
		//Gets the Node's right sibling (if it has one) or null
		public DLBNode getNext()
		{
			return next;
		}
		
		//Sets the Node's right sibling
		public void setNext(DLBNode node)
		{
			next = node;
		}
		
		//Returns true if the Node has a child
		public boolean hasChild()
		{
			return child != null;
		}
		
		//Gets the Node's child (if it has one) or null
		public DLBNode getChild()
		{
			return child;
		}
		
		//Sets the Node's child
		public void setChild(DLBNode node)
		{
			child = node;
		}
		
		//Gets the Node's character value or ' ' (if it doesnt currently have a value)
		public char getValue()
		{
			return value;
		}
		
		//Sets the Node's character value
		public void setValue(char c)
		{
			value = c;
		}
		
		//Returns whether the Node has a character value or not
		public boolean isEmpty()
		{
			return (value == ' ');
		}
	
	}

}