package utils;


import java.util.Base64;




public class Decrypt {
	public static String decodeBase64 (String encodedString) { // To decode an encoded string	
	byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
	String decoded = new String(decodedBytes);
	return decoded;
	}

}
