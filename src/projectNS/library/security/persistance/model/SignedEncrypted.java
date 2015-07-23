package projectNS.library.security.persistance.model;

import java.lang.reflect.Type;

import projectNS.library.mycrypto.SignatureConfiguration;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class SignedEncrypted extends Encrypted {

	private String subject;
	private String signature;
	private SignatureConfiguration config;
	
	public SignedEncrypted(SignatureConfiguration config, String subject, String signature) {
		super(SignedType.SIGNED);
		
		this.setConfig(config);
		this.setSubject(subject);
		this.setSignature(signature);
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public SignatureConfiguration getConfig() {
		return config;
	}

	public void setConfig(SignatureConfiguration config) {
		this.config = config;
	}
	
	public static SignedEncrypted deserialize(String signedEncrypted) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(SignedEncrypted.class, new SignedEncryptedCustomDeserializer());
	    
		try {
			return gson.create().fromJson(signedEncrypted, SignedEncrypted.class);
		} catch(JsonParseException e) {
			return null;
		}
	}

	public static class SignedEncryptedCustomDeserializer implements JsonDeserializer<SignedEncrypted> {
		@Override
		public SignedEncrypted deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			if(json == null){
				return null;
			} else {
				String signature = json.getAsJsonObject().get("signature").getAsString();
				String subject = json.getAsJsonObject().get("subject").getAsString();
				SignatureConfiguration config = SignatureConfiguration.deserialize(json.getAsJsonObject().get("config").getAsJsonObject().toString());
				
				return new SignedEncrypted(config, subject, signature);
			}
		}
	}

}
