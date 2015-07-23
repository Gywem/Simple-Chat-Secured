package projectNS.library.mycrypto;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

public abstract class AsymmetricCrypto extends MyCrypto {
	
	private PrivateKey privateKey;
	private PublicKey publicKey;
	
	AsymmetricCrypto() {
	}
	
	public void defaultGenerateKey() {
		KeyPairGenerator keyPairGenerator;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance(this.getConfiguration().getAlgorithm().getAlgorithmString());
			SecureRandom rng = SecureRandom.getInstance("NativePRNG", "SUN");
			keyPairGenerator.initialize(this.getConfiguration().getKeyLength(), rng);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			
			this.setPublicKey(keyPair.getPublic());
			this.setPrivateKey(keyPair.getPrivate());
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
		}
		
	}
	
	public abstract void generateKey();

	@Override
	public Key getKeyToDecipher() {
		return this.privateKey;
	}
	
	@Override
	public Key getKeyToCipher() {
		return this.publicKey;
	}

	public String getPrivateKey() {
		byte[] bytes = this.privateKey.getEncoded();
		try {
			return new String(Base64.encodeBase64(bytes), "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public String getPublicKey() {
		byte[] bytes = this.publicKey.getEncoded();
		try {
			return new String(Base64.encodeBase64(bytes), "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getKeyToShare() {
		return this.getPublicKey();
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}
	
	public abstract String cipher(String rawData);
	public abstract String decipher(String rawData);
	
	@Override
	public String toString() {
		String result = super.toString();
		
		result += "\t### Public Key :"+this.getPublicKey()+"\n";
		result += "\t### Private Key :"+this.getPrivateKey()+"\n";
		
		return result;
	}

	public void setSharedKey(String k) {
		try {
		    byte[] pkbytes = Base64.decodeBase64(k);
			
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pkbytes);
			KeyFactory keyFact = KeyFactory.getInstance(this.getConfiguration().getAlgorithm().getAlgorithmString());
			this.publicKey = keyFact.generatePublic(x509KeySpec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}	
}
