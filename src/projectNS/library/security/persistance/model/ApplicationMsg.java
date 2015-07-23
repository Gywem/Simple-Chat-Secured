package projectNS.library.security.persistance.model;

import projectNS.library.mycrypto.SignatureConfiguration;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class ApplicationMsg extends SignedNoEncrypted {
	private String data;
	
	public ApplicationMsg(String data, String subject, SignatureConfiguration config) {
		super(SignedNoEncryptedType.APPLICATION_MSG, subject, config);
		
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public static ApplicationMsg deserialize(String applicationMsg) {
		Gson gson = new Gson();
		try {
			return gson.fromJson(applicationMsg, ApplicationMsg.class);
		} catch(JsonParseException e) {
			return null;
		}
	}
}
