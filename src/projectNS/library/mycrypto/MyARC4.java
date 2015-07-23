package projectNS.library.mycrypto;

import org.apache.commons.codec.binary.Base64;

public class MyARC4 extends SymmetricCrypto {

	@Override
	public void generateKey() {
		this.defaultGenerateKey();
	}

	@Override
	public String cipher(String rawData) {
		return new Base64().encodeToString(this.arc4(rawData).getBytes());
	}

	@Override
	public String decipher(String rawData) {
		return this.arc4(new String(Base64.decodeBase64(rawData)));
	}
	
	public String arc4(String rawData) {
		String key = this.getSymmetricKey();
		
		// KSA
		int[] s = new int[256];
		int i = 0, j = 0, aux;
		String output = "";
		
		for (i = 0; i < 256; i++) {
			s[i] = i;
		}
		for (i = 0; i < 256; i++) {
			j = (j + s[i] + ((int)key.charAt(i % key.length()))) % 256;
			aux = s[i]; s[i] = s[j]; s[j] = aux;
		}
		
		// Pseudo-random generation algorithm (PRGA)
		i = 0; j = 0;
		for (int y = 0; y < rawData.length(); y++) {
			i = (i + 1) % 256; 
			j = (j + s[i]) % 256;
			// Swap values s[i] s[j]
			aux = s[i];
			s[i] = s[j];
			s[j] = aux;
			output += Character.toString((char)(((int)rawData.charAt(y)) ^ s[(s[i] + s[j]) % 256]));
		}
		return output;
	}

}
