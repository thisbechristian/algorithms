import java.util.Random;

public class Substitute implements SymCipher
{
	byte[] key;

	public Substitute(){
		key = new byte[256];
		int i = 0;
		byte j = -128;
		while(i < 256){
			key[i] = j;
			i++;
			j++;
		}
		shuffleArray(key);
	}

	public Substitute(byte [] k){
		key = k;
	}

	public byte [] getKey(){
		return key;
	}	
	

	public byte [] encode(String S){
		byte[] bytes = S.getBytes();
		int a;
		byte b;
		int i = 0;
		
		while(i < bytes.length){
			a = bytes[i];
			if(a < 0){
				a = a + 256;
			}
			b = key[a];
			bytes[i] = b;
			i++;
		}
		
		return bytes;
	}
	
	public String decode(byte [] bytes){
		int[] inverseKey = new int[256];
		int a,b;
		int i = 0;
		while(i < 256){
			a = key[i];
			if(a < 0)
				a = a + 256;
			inverseKey[a] = i;
			i++;
		}
		
		i = 0;
		while(i < bytes.length){
			a = bytes[i];
			if(a < 0){
				a = a + 256;
			}
			b = inverseKey[a];
			if(b > (256/2) - 1)
				b = b - 256;
			bytes[i] = (byte)b;
			i++;
		}
		
		return new String(bytes);
	
	}
	
	// Fisher–Yates shuffle
  	private void shuffleArray(byte[] bytes){
  	 	Random r = new Random();
  	 	for (int i = (bytes.length - 1); i > 0; i--){
   	 		int index = r.nextInt(i + 1);
   	   		byte temp = bytes[index];
   	   		bytes[index] = bytes[i];
   	   		bytes[i] = temp;
    	}
  	}
	
}

