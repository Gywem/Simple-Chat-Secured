package projectNS.library.mycrypto;

public class DefaultSymmetricAlgorithm extends SymmetricCrypto {

	@Override
	public String cipher(String rawData) {
		return this.defaultCipher(rawData);
	}

	@Override
	public String decipher(String rawData) {
		return this.defaultDecipher(rawData);
	}

	@Override
	public void generateKey() {
		this.defaultGenerateKey();		
	}

}
