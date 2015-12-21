import java.util.Random;

public class Add128 implements SymCipher
{

	byte[] key;

	public Add128(){
		key = new byte[128];
		new Random().nextBytes(key);
	}

	public Add128(byte [] k){
		key = k;
	}

	public byte [] getKey(){
		return key;
	}	
	

	public byte [] encode(String S){
		byte[] bytes = S.getBytes();
		
		int i = 0;
		int j = 0;
		
		while(i < bytes.length){
		
		bytes[i] = (byte)(bytes[i] + key[j]);
		
		i++;
		j++;
		
		if(j == 127)
			j = 0;
		}
		
		return bytes;
	}
	
	public String decode(byte [] bytes){
		
		int i = 0;
		int j = 0;
		
		while(i < bytes.length){
		
		bytes[i] = (byte)(bytes[i] - key[j]);
		
		i++;
		j++;
		
		if(j == 127)
			j = 0;
		}
		
		return new String(bytes);
	}
	
}
