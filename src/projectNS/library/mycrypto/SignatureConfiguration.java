package projectNS.library.mycrypto;

import projectNS.library.mycrypto.MySignature.HashType;
import projectNS.library.mycrypto.MySignature.SignatureAlgorithm;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class SignatureConfiguration {
	private HashType hash;
	private SignatureAlgorithm algorithm;
	
	private int keyLength = 1024;
	
	public SignatureConfiguration(SignatureAlgorithm algorithm, HashType hash){
		this.setHash(hash);
		this.setAlgorithm(algorithm);
	}

	public int getKeyLength() {
		return keyLength;
	}

	public void setKeyLength(int keyLength) {
		this.keyLength = keyLength;
	}

	public SignatureAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(SignatureAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	public HashType getHash() {
		return hash;
	}

	public void setHash(HashType hash) {
		this.hash = hash;
	}
	
	public static SignatureConfiguration deserialize(String signatureConfiguration) {
		Gson gson = new Gson();
		
		try {
			return gson.fromJson(signatureConfiguration, SignatureConfiguration.class);
		} catch(JsonParseException e) {
			return null;
		}
	}
	
	@Override
	public String toString() {
		String result = "";
		
		result += "\t### Algorithm :"+this.getAlgorithm()+"\n";
		result += "\t### Hash :"+this.getHash()+"\n";
		
		return result;
	}
	
}
