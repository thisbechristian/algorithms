/*************************************************************************

Christian Boni
Assignment 2
10/29/14

 *************************************************************************/

import java.lang.Math.*;

public class MyLZW {
    private static final int R = 256;     	// number of input chars
    private static final int MaxBitWidth = 16;    // number of input chars
    private static String mode;				// mode of the compression: n,r,m


    public static void compress() {
        int W = 9;         							// codeword width
        int L = (int)Math.pow(2,W);       			// number of codewords = 2^W
        double originalData;						// size of original data
        double compressData = 0;					// size of compressed data
        double compressRatio = 0;					// originalData/compressData
        
        String input = BinaryStdIn.readString();
        originalData = input.length();				// original size of the file
    
        TST<Integer> st = new TST<Integer>();    	// initialize the codebook for ASCII characters
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  							// R is codeword for EOF
        
        BinaryStdOut.write(st.get(mode), W);  		// Write out mode so it can be retrieved during inflation
        if(mode.equals("m"))
        	 BinaryStdOut.write(originalData);		// Write out original file size if mode is monitor

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  	// Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);      	// Print s's encoding.
            int t = s.length();
            compressData += (W/8);				// Increment the size of the compressed data by the current codeword's (bit width) / (8 bits) to get number of bytes
            
            if (t < input.length())
            {
            	if(code < L)    									// Add s to symbol table.
                	st.put(input.substring(0, t + 1), code++);
                	
            	else if(W < MaxBitWidth){				// If W < 16 and Codebook is full, increase the width
            		W++;
            		L = (int)Math.pow(2,W);
            		st.put(input.substring(0, t + 1), code++);
            	}
            
            	else if(mode.equals("r")){				// If W = 16, Codebook is full, and mode = "r". Reset the codebook
            		st = new TST<Integer>();	
        			for (int i = 0; i < R; i++)			// Re-initialize the codebook for ASCII characters
            			st.put("" + (char) i, i);
        			code = R+1;							// R is codeword for EOF
        			
        			W = 9;								// Reset codeword width to 9 bits
        			L = (int)Math.pow(2,W); 			// Reset codebook size to 2^W
        			
        			st.put(input.substring(0, t + 1), code++);
            	}
            	
            	else if(mode.equals("m")){			// If W = 16, Codebook is full, and mode = "r". Monitor the algorithm
            	
            	if(compressRatio == 0)				// If starting compression ratio not yet set, do so
            		compressRatio = originalData/compressData;	
            		
            	else{
            		double curr_CompressRatio = originalData/compressData;
            		if((compressRatio/curr_CompressRatio) > 1.100)
            		{
            			compressRatio = originalData/compressData;
            		    st = new TST<Integer>();		//Reset the codebook
        				for (int i = 0; i < R; i++)
            				st.put("" + (char) i, i);
        				code = R+1; 
        			
        				W = 9;
        				L = (int)Math.pow(2,W); 
        			
        				st.put(input.substring(0, t + 1), code++);
            		}
            	}
            		
            	}
            
            }
            
            input = input.substring(t);            // Scan past s in input.
        }
        
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 


    public static void expand() {
    
        int W = 9;         									// codeword width
        int L = (int)Math.pow(2,W);       					// number of codewords = 2^W
        String[] st = new String[(int)Math.pow(2,MaxBitWidth)];	// declare codebook to maximum size 2^16
        double originalData = 0;							// size of original data
        double compressData = 0;							// size of compressed data
        double compressRatio = 0;							// compressData/originalData
        int i; 												// next available codeword value
        													
        for (i = 0; i < R; i++)								// initialize codebook for all ASCII characters
            st[i] = "" + (char) i;
        st[i++] = "";                        				// (unused) lookahead for EOF

		mode = st[BinaryStdIn.readInt(W)];					//retrieve the mode the file was compressed with
		if(mode.equals("m"))
        	originalData = BinaryStdIn.readDouble();		//retrieve the original file size if in monitor mode
		
        int codeword = BinaryStdIn.readInt(W);
        compressData += (W/8);							
        if (codeword == R) return;           				// expanded message is empty string
        String val = st[codeword];
		
        while (true) {
        	if(i == L){
            	if(W < MaxBitWidth){
            		W++;
            		L = (int)Math.pow(2,W);
            	}
            	
            	else if((mode.equals("r"))){
            		W = 9;         						
         			L = (int)Math.pow(2,W);       	
        			st = new String[(int)Math.pow(2,MaxBitWidth)];
        			
        			for (i = 0; i < R; i++)
            			st[i] = "" + (char) i;
       				st[i++] = "";
            	}
            	
            	else if(mode.equals("m")){			// If W = 16, Codebook is full, and mode = "r". Monitor the algorithm
            	
            		if(compressRatio == 0)			// If starting compression ratio not yet set, do so
            			compressRatio = originalData/compressData;	
            		
            		else{
            			double curr_CompressRatio = originalData/compressData;
            			
            			if((compressRatio/curr_CompressRatio) > 1.100)
            			{
            				compressRatio = originalData/compressData;
        					W = 9;         						
         					L = (int)Math.pow(2,W);       	
        					st = new String[(int)Math.pow(2,MaxBitWidth)];
        			
        					for (i = 0; i < R; i++)		//Reset the codebook
            					st[i] = "" + (char) i;
       						st[i++] = "";
            			}
            		}
            		
            	}
            }
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
            compressData += (W/8);				// Increment the size of the compressed data by the current codeword's (bit width) / (8 bits) to get number of bytes
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
        }
        BinaryStdOut.close();
    }



    public static void main(String[] args) {
        if (args[0].equals("-")){
			mode = args[1];
         	compress();
         }
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}
