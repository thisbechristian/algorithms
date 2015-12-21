import java.util.*;
import java.io.*;
import java.awt.*;

public class MyBoggle
{
	static int lowerCaseA = 97;
	static char [][] theBoard;
	static boolean simpleDictionaryFlag = true;
	static String boardFilename = "board1.txt";
	static String dictionaryFilename = "dictionary.txt";
	static DictionaryInterface theDictionary;
	static DictionaryInterface theBoardDictionary;
	static DictionaryInterface guessedWords;
	static ArrayList<Point> usedLetters;

	public static void main(String [] args)
	{
		if(args.length > 0)
		{
		
			if(args[0].toLowerCase().equals("-d"))
			{
				if(args[1].toLowerCase().equals("dlb"))
					simpleDictionaryFlag = false;
			}
		
			else if(args[0].equals("-b"))
			{
				boardFilename = args[1];
			}
		
			if(args.length > 2)
			{
		
				if(args[2].toLowerCase().equals("-d"))
				{
					if(args[3].toLowerCase().equals("dlb"))
						simpleDictionaryFlag = false;
				}
		
				else if(args[2].equals("-b"))
				{
					boardFilename = args[3];
				}
		
			}
		
		}
		
		loadBoard(boardFilename);
		loadDictionary(simpleDictionaryFlag);
		
		if(!simpleDictionaryFlag)
			theBoardDictionary = new DLBDictionary();
		else
			theBoardDictionary = new SimpleDictionary();
		
		preloadBoardDictionary();
		printBoard();
		
		if(!simpleDictionaryFlag)
			guessedWords = new DLBDictionary();
		else
			guessedWords = new SimpleDictionary();
		
		Scanner reader = new Scanner(System.in);
		boolean guessing = true;
		String guessedWord;
		String resume;
		
		System.out.println("The objective in Boggle is to find as many words as you can on the board. Goodluck!");
		System.out.println("Enter: \"-done\" when you are finished finding words.\n");
		
		while(guessing)
		{
			System.out.println("Please enter a word that you have found on the board: ");
			guessedWord = reader.nextLine();
			StringBuilder s = new StringBuilder(guessedWord.toLowerCase());
			
			if(guessedWord.toLowerCase().equals("-done"))
				guessing = false;
			
			else if(guessedWords.search(s) == 2 || guessedWords.search(s) == 3 )
				System.out.println("Sorry, you have already entered this word. Try again!");
			
			else
			{
				int found = theBoardDictionary.search(s);
				
				if( found == 2 || found == 3)
				{
					System.out.println("Correct, the word you have entered is valid. Good Job!");
					guessedWords.add(guessedWord.toLowerCase());
				}
				else
					System.out.println("Sorry, the word you have entered is not valid. Try again!");
			}
			
		}
		
		System.out.println("Thanks for playing! Here are all of the possible words for the board:");
		System.out.println(theBoardDictionary.toString());
		System.out.println("Here are all of the valid words you have guessed correctly:");
		System.out.println(guessedWords.toString());
		System.out.println();
		System.out.println("Number of words you have guessed correctly: " + guessedWords.size());
		System.out.println("Number of possible words for the board: " + theBoardDictionary.size());
		
		double numUserCorrect = guessedWords.size();
		double numBoardCorrect = theBoardDictionary.size();
		
		System.out.printf("Percentage of words you correctly guessed: %.2f%%\n\n",(numUserCorrect/numBoardCorrect)*100);
		

	}
	
	public static void preloadBoardDictionary()
	{
		for(int i = 0; i < 4; i++)
		{
			for(int j = 0; j < 4; j++)
			{
				Point P = new Point(i,j);
				StringBuilder S = new StringBuilder();
				int C = 0;
				usedLetters = new ArrayList<Point>();
				loadBoardDictionary(P,S,C,lowerCaseA);
			}
		
		}
	}
	
	public static void loadBoardDictionary(Point P, StringBuilder currentWord, int count, int wildcard)
	{
		//Special Case when the current character is a wildcard
		if(theBoard[P.x][P.y] == '*')
		{
			if(wildcard < 123)
			{
				currentWord.append(Character.toChars(wildcard));
				wildcard++;
			}
			else
				return;	
		}
		
		//Append current letter to the string, add the current letter to the usedLetters list and increment the count (number of characters in the current string).
		else
			currentWord.append(Character.toLowerCase(theBoard[P.x][P.y]));
		
		usedLetters.add(P);
		count++;
		
		//If length of current string is greater then 1, check to see if it is a word or prefix.
		if(count > 1)
		{
			int state = theDictionary.search(currentWord);
			
			//State = 0: Current string is NOT a prefix OR a word. Remove last letter and remove letter from usedLetters list.
			if(state == 0) 
			{
				currentWord.deleteCharAt(count-1);
				usedLetters.remove(count-1);
				//Special Case when the current character is a wildcard
				if(theBoard[P.x][P.y] == '*')
					loadBoardDictionary(P, currentWord, count-1, wildcard);
				return;
			}
			
			//State = 2: Current string is a word but not a prefix. Add word to theBoardDictionary (if it isnt a duplicate), Remove last letter and remove letter from usedLetters list.
			else if(state == 2) 
			{
				int dupeCheck = theBoardDictionary.search(currentWord);
				if(dupeCheck != 2 && dupeCheck != 3)
					theBoardDictionary.add(currentWord.toString());
					
				currentWord.deleteCharAt(count-1);
				usedLetters.remove(count-1);
				//Special Case when the current character is a wildcard
				if(theBoard[P.x][P.y] == '*')
					loadBoardDictionary(P, currentWord, count-1, wildcard);
				return;
			}
			
			//State = 3: Current string is a word AND a prefix. Add word to theBoardDictionary (if it isnt a duplicate) and continue.
			else if(state == 3) 
			{
				int dupeCheck = theBoardDictionary.search(currentWord);
				if(dupeCheck != 2 && dupeCheck != 3)
					theBoardDictionary.add(currentWord.toString());
			}
		}
		
		//Check if West move is possible
		if((P.y - 1) >= 0)
		{
			Point X = new Point(P.x,(P.y-1));
			if(usedLetterCheck(X))
				loadBoardDictionary(X, currentWord, count, wildcard);
		}
		
		//Check if East move is possible
		if((P.y + 1) < 4)
		{
			Point X = new Point(P.x,(P.y+1));
			if(usedLetterCheck(X))
				loadBoardDictionary(X, currentWord, count, wildcard);
		}
		
		//Check if North move is possible
		if((P.x + 1) < 4)
		{
			Point X = new Point((P.x+1),P.y);
			if(usedLetterCheck(X))
				loadBoardDictionary(X, currentWord, count, wildcard);
		}
		
		//Check if South move is possible
		if((P.x - 1) >= 0)
		{
			Point X = new Point((P.x-1),P.y);
			if(usedLetterCheck(X))
				loadBoardDictionary(X, currentWord, count, wildcard);	
		}
		
		//Check if South-West move is possible
		if(((P.x - 1) >= 0) && ((P.y - 1) >= 0))
		{
			Point X = new Point((P.x-1),(P.y-1));
			if(usedLetterCheck(X))
				loadBoardDictionary(X, currentWord, count, wildcard);
		}
		
		//Check if North-West move is possible
		if(((P.x + 1) < 4) && ((P.y - 1) >= 0))
		{
			Point X = new Point((P.x+1),(P.y-1));
			if(usedLetterCheck(X))
				loadBoardDictionary(X, currentWord, count, wildcard);
		}
		//Check if South-East move is possible
		if(((P.x - 1) >= 0) && ((P.y + 1) < 4))
		{
			Point X = new Point((P.x-1),(P.y+1));
			if(usedLetterCheck(X))
				loadBoardDictionary(X, currentWord, count, wildcard);
		}
		
		//Check if North-East move is possible
		if(((P.x + 1) < 4) && ((P.y + 1) < 4))
		{
			Point X = new Point((P.x+1),(P.y+1));
			if(usedLetterCheck(X))
				loadBoardDictionary(X, currentWord, count, wildcard);
		}
		
		//When all possible moves are checked. Remove last letter and remove the letter from usedLetters list.
		currentWord.deleteCharAt(count-1);
		usedLetters.remove(count-1);
		
		//Special Case when the current character is a wildcard
		if(theBoard[P.x][P.y] == '*')
		{
			loadBoardDictionary(P, currentWord, count-1, wildcard);
		}	

		return;
		
	}

	public static void loadBoard(String fn)
	{
	
		try
		{
		
		File boardFile = new File(fn);
		Scanner scanner = new Scanner(boardFile);
		theBoard = new char[4][4];
		String board = scanner.nextLine();
		int x = 0;
		for(int i = 0; i < 4; i++)
		{
			for(int j = 0; j < 4; j++)
			{
				theBoard[i][j] = board.charAt(x);
				x++;
			}
		}
		scanner.close();
		
		}
		
		catch(FileNotFoundException e){}
		
		
	}
	
	public static void loadDictionary(Boolean simpleDictionary)
	{
		try
		{
		
		if(!simpleDictionary) 
			theDictionary = new DLBDictionary();
		else
			theDictionary = new SimpleDictionary();
			
		File dictionaryFile = new File(dictionaryFilename);
		Scanner scanner = new Scanner(dictionaryFile);
			
		while(scanner.hasNextLine())
		{
			theDictionary.add(scanner.nextLine());
		}
			
		scanner.close();
		}
		
		catch(FileNotFoundException e){}
	}

	public static void printBoard()
	{
		System.out.println("Welcome to Boggle!");
		System.out.println("Here is your Game Board:");
		
		for(int i = 0; i < 4; i++)
		{
			System.out.print("\t");
			for(int j = 0; j < 4; j++)
			{
				System.out.print("\t" + theBoard[i][j]);
			}
			System.out.print("\n");
		}
	}
	
	public static boolean usedLetterCheck(Point X)
	{
	
		if(usedLetters.size() > 1)
		{
		
			for(Point P : usedLetters)
			{
				if(P.equals(X))
				{
					return false;
				}
			}

		}
		
		return true;
	}

}