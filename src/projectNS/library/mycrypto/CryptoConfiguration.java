package projectNS.library.mycrypto;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import projectNS.library.mycrypto.MyCrypto.CryptoAlgorithm;

public class CryptoConfiguration {
	private CryptoAlgorithm algorithm;
	private String mode = "NONE";
	private String padding = "NoPadding";
	
	private int keyLength = 64;
	
	public CryptoConfiguration(CryptoAlgorithm alg){
		this.algorithm = alg;
		this.setMode(alg.defaultMode);
		this.setPadding(alg.defaultPadding);
		this.setKeyLength(alg.defaultKeySize);
	}
	
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getPadding() {
		return padding;
	}
	public void setPadding(String padding) {
		this.padding = padding;
	}
	public CryptoAlgorithm getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(CryptoAlgorithm algorithm) {
		this.algorithm = algorithm;
	}
	
	public String getComplete() {
		String toReturn = "";
		 
		toReturn += this.getAlgorithm().getAlgorithmString();
		if(this.getMode() != "NONE") toReturn += "/"+this.getMode();
		if(this.getPadding() != "NoPadding") toReturn += "/"+this.getPadding();
		
		return toReturn;
	}

	public int getKeyLength() {
		return keyLength;
	}

	public void setKeyLength(int keyLength) {
		this.keyLength = keyLength;
	}
	
	public static CryptoConfiguration deserialize(String cryptoConfiguration) {
		Gson gson = new Gson();
		
		try {
			return gson.fromJson(cryptoConfiguration, CryptoConfiguration.class);
		} catch(JsonParseException e) {
			return null;
		}
	}
	
}
