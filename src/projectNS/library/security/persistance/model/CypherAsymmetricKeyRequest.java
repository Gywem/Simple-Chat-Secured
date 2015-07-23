package projectNS.library.security.persistance.model;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class CypherAsymmetricKeyRequest extends AsymmetricKeyRequest {
	
	public CypherAsymmetricKeyRequest() {
		super(AsymmetricKeyRequestType.CYPER);
	}
	
	public static CypherAsymmetricKeyRequest deserialize(String applicationMsg) {
		Gson gson = new Gson();
		try {
			return gson.fromJson(applicationMsg, CypherAsymmetricKeyRequest.class);
		} catch(JsonParseException e) {
			return null;
		}
	}
}
