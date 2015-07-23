package projectNS.library.mycrypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

public abstract class MySignature {
	public enum HashType {
		MD5,
		SHA1,
		SHA256
	};
	
	public enum SignatureAlgorithm {
		DSA,
		RSA,
		ECDSA
	};
	
	public MySignature(SignatureConfiguration config, String subject){
		this.setConfig(config);
		this.setSubject(subject);
	}
	
	protected SignatureConfiguration config;
	public SignatureConfiguration getConfig() {
		return config;
	}

	public void setConfig(SignatureConfiguration config) {
		this.config = config;
	}
	
	protected PublicKey mPublicKey;
	
	public String getPublicKey() {
		byte[] bytes = mPublicKey.getEncoded();
		try {
			return new String(Base64.encodeBase64(bytes), "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void setPublicKey(String pk){
		try {
		    byte[] pkbytes = Base64.decodeBase64(pk);
			
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pkbytes);
			KeyFactory keyFact = KeyFactory.getInstance(config.getAlgorithm().name());
			this.mPublicKey = keyFact.generatePublic(x509KeySpec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}		
	}
	
	protected String subject;
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public boolean verifySig(String data, String signature){
	    try {
	    	String algorithm = config.getHash().name()+"with"+config.getAlgorithm().name();
	    	Signature signer = Signature.getInstance(algorithm);
			signer.initVerify(mPublicKey);
		    signer.update(data.getBytes("UTF8"));
		    byte[] sign = Base64.decodeBase64(signature);
		    return (signer.verify(sign));
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String toString() {
		String result = "";
		
		result += "\t### Algorithm :"+this.getConfig().getAlgorithm().name()+"\n";
		result += "\t### Hash :"+this.getConfig().getHash().name()+"\n";
		result += "\t### Public key :"+this.getPublicKey()+"\n";
		
		return result;
	}
	
	
}
