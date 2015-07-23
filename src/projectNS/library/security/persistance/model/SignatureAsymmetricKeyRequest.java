package projectNS.library.security.persistance.model;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class SignatureAsymmetricKeyRequest extends AsymmetricKeyRequest {
	
	public SignatureAsymmetricKeyRequest() {
		super(AsymmetricKeyRequestType.SIGNATURE);
	}
	
	public static SignatureAsymmetricKeyRequest deserialize(String applicationMsg) {
		Gson gson = new Gson();
		try {
			return gson.fromJson(applicationMsg, SignatureAsymmetricKeyRequest.class);
		} catch(JsonParseException e) {
			return null;
		}
	}
}
