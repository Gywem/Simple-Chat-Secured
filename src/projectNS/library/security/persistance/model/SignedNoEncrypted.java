package projectNS.library.security.persistance.model;

import java.lang.reflect.Type;

import projectNS.library.mycrypto.SignatureConfiguration;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class SignedNoEncrypted extends NoEncrypted {
	public enum SignedNoEncryptedType {
		CERTIFICATE_RESPONSE,
		SYMMETRIC_KEY_RESPONSE,
		APPLICATION_MSG
	};
	
	private SignedNoEncryptedType snType;
	
	private String subject;
	private String signature;
	private SignatureConfiguration config;
	
	public SignedNoEncrypted(SignedNoEncryptedType snType, String subject, SignatureConfiguration config) {
		super(SignedType.SIGNED);
		
		this.setSnType(snType);
		this.setSubject(subject);
		this.setConfig(config);
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public SignedNoEncryptedType getSnType() {
		return snType;
	}

	private void setSnType(SignedNoEncryptedType snType) {
		this.snType = snType;
	}
	
	public SignatureConfiguration getConfig() {
		return config;
	}

	public void setConfig(SignatureConfiguration config) {
		this.config = config;
	}
	
	public static SignedNoEncrypted deserialize(String signedNoEncrypted) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(SignedNoEncrypted.class, new NoSignedNoEncryptedCustomDeserializer());
	    
		try {
			return gson.create().fromJson(signedNoEncrypted, SignedNoEncrypted.class);
		} catch(JsonParseException e) {
			return null;
		}
	}

	public static class NoSignedNoEncryptedCustomDeserializer implements JsonDeserializer<SignedNoEncrypted> {
		@Override
		public SignedNoEncrypted deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			if(json == null){
				return null;
			} else {
				String sntStr = json.getAsJsonObject().get("snType").getAsString();
				SignedNoEncryptedType snType = SignedNoEncryptedType.valueOf(sntStr);
				
				SignedNoEncrypted aux;
				switch(snType) {
				case APPLICATION_MSG:
					aux = ApplicationMsg.deserialize(json.getAsJsonObject().toString());
					break;
				case CERTIFICATE_RESPONSE:
					aux = CertificateResponse.deserialize(json.getAsJsonObject().toString());
					break;
				case SYMMETRIC_KEY_RESPONSE:
					aux = SymmetricKeyResponse.deserialize(json.getAsJsonObject().toString());
					break;
				default:
					return null;
				}
				
				return aux;
			}
		}
	}
	
	protected static SignedNoEncrypted getParentProperties(JsonElement json, SignedNoEncrypted crequest){
		String signature = null; 
		if(json.getAsJsonObject().get("signature") != null) signature = json.getAsJsonObject().get("signature").getAsString();
		String subject = json.getAsJsonObject().get("subject").getAsString();
		SignatureConfiguration config = SignatureConfiguration.deserialize(json.getAsJsonObject().get("config").getAsJsonObject().toString());
		
		crequest.setSignature(signature);
		crequest.setSubject(subject);
		crequest.setConfig(config);
    	
    	return crequest;
	}
}
