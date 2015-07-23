package projectNS.library.mycrypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

import org.apache.commons.codec.binary.Base64;

public class PersonalSignature extends MySignature {
	public PersonalSignature(SignatureConfiguration config, String subject) {
		super(config, subject);
		this.generateKeyPair();
	}

	private PrivateKey privateKey;
	
	public String getPrivateKey() {
		byte[] bytes = privateKey.getEncoded();
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
	
	public String signData(String data){
	    try {
	    	String algorithm = config.getHash().name()+"with"+config.getAlgorithm().name();
	    	Signature signer = Signature.getInstance(algorithm);
		    signer.initSign(privateKey);
			signer.update(data.getBytes("UTF8"));
			byte[] sign = signer.sign();
			return new String(Base64.encodeBase64(sign), "UTF8");
		} catch (SignatureException | UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	private void generateKeyPair() {
	    KeyPairGenerator keyGenerator;
		try {
			keyGenerator = KeyPairGenerator.getInstance(config.getAlgorithm().name());
			SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN");
		    keyGenerator.initialize(config.getKeyLength(), rng);
		    
		    KeyPair kpair = keyGenerator.generateKeyPair();
		    this.setPrivateKey(kpair.getPrivate());
		    this.mPublicKey = kpair.getPublic();
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
		}
	 }
	
	@Override
	public String toString() {
		String result = super.toString();
		
		result += "\t### Private key :"+this.getPrivateKey()+"\n";
		
		return result;
	}
}
