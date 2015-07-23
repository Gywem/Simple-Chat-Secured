package projectNS.library.mycrypto;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public abstract class SymmetricCrypto extends MyCrypto {
	
	private SecretKey symmetricKey;
	
	SymmetricCrypto() {
	}

	@Override
	public Key getKeyToCipher() {
		return this.symmetricKey;
	}

	public abstract void generateKey();
	
	public void defaultGenerateKey() {
		try {
			SecureRandom random = SecureRandom.getInstance("NativePRNG", "SUN");
		    
		    KeyGenerator keyGen;
			keyGen = KeyGenerator.getInstance(this.getConfiguration().getAlgorithm().getAlgorithmString());
			
			keyGen.init(this.getConfiguration().getKeyLength(), random);
		    
		    this.symmetricKey = keyGen.generateKey();
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
		}	    
	}

	@Override
	public Key getKeyToDecipher() {
		return this.symmetricKey;
	}

	public String getSymmetricKey() {
		byte[] bytes = this.symmetricKey.getEncoded();
		try {
			return new String(Base64.encodeBase64(bytes), "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	

	public String getKeyToShare() {
		return this.getSymmetricKey();
	}

	public void setSymmetricKey(SecretKeySpec symmetricKey) {
		this.symmetricKey = symmetricKey;
	}
	
	public abstract String cipher(String rawData);
	public abstract String decipher(String rawData);
	
	@Override
	public String toString() {
		String result = super.toString();
		
		result += "\t### Symmetric Key :"+this.getSymmetricKey()+"\n";
		
		return result;
	}
	
	public void setSharedKey(String k) {
		byte[] secret = Base64.decodeBase64(k);
		
		SecretKeySpec symmetricKey = new SecretKeySpec(secret, this.getConfiguration().getAlgorithm().getAlgorithmString());
		
		this.symmetricKey = symmetricKey;
	}
}
