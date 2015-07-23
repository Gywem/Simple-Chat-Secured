package projectNS;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.Cipher;

public class Prueba {

	public static void main (String[] args) throws Exception {

		// check args and get plaintext
		if (args.length !=1) {
		System.err.println("Usage: java PublicExample text");
		System.exit(1);
		}
		byte[] plainText = args[0].getBytes("UTF8");

		//
		// generate an RSA key
		System.out.println( "\nStart generating RSA key" );
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		KeyPair key = keyGen.generateKeyPair();
		System.out.println( "Finish generating RSA key" );

		// get an RSA cipher object and print the provider
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		System.out.println( "\n" + cipher.getProvider().getInfo() );

		// encryption
		System.out.println( "\nStart encryption" );
		cipher.init(Cipher.ENCRYPT_MODE, key.getPublic());
		byte[] cipherText = cipher.doFinal(plainText);
		System.out.println( "Finish encryption: " );
		System.out.println( new String(cipherText, "UTF8") );

		// decryption
		System.out.println( "\nStart decryption" );
		cipher.init(Cipher.DECRYPT_MODE, key.getPrivate());
		byte[] newPlainText = cipher.doFinal(cipherText);
		System.out.println( "Finish decryption: " );
		System.out.println( new String(newPlainText, "UTF8") );
	}

}
