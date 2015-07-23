package projectNS.library.security.persistance.model;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class SymmetricKeyRequest extends NoSignedNoEncrypted {

	public SymmetricKeyRequest() {
		super(NoSignedNoEncryptedType.SYMMETRIC_KEY_RESQUEST);
	}
	
	public static SymmetricKeyRequest deserialize(String symmetricKeyRequest) {
		Gson gson = new Gson();
		try {
			return gson.fromJson(symmetricKeyRequest, SymmetricKeyRequest.class);
		} catch(JsonParseException e) {
			return null;
		}
	}

}
