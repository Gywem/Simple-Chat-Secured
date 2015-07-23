package projectNS.library.security.persistance.model;

import java.lang.reflect.Type;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class NoSignedNoEncrypted extends NoEncrypted {
	public enum NoSignedNoEncryptedType {
		CERTIFICATE_REQUEST,
		ASYMMETRIC_KEY_REQUEST,
		ASYMMETRIC_KEY_RESPONSE,
		SYMMETRIC_KEY_RESQUEST,
		CERTIFICATE
	};
	
	private NoSignedNoEncryptedType nnType;
	
	NoSignedNoEncrypted(NoSignedNoEncryptedType nnType) {
		super(SignedType.NOSIGNED);
		this.setNnType(nnType);
	}
	
	public NoSignedNoEncryptedType getNnType() {
		return nnType;
	}

	private void setNnType(NoSignedNoEncryptedType nnType) {
		this.nnType = nnType;
	}
	
	public static NoSignedNoEncrypted deserialize(String noSignedNoEncrypted) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(NoSignedNoEncrypted.class, new NoSignedNoEncryptedCustomDeserializer());
	    
		try {
			return gson.create().fromJson(noSignedNoEncrypted, NoSignedNoEncrypted.class);
		} catch(JsonParseException e) {
			return null;
		}
	}
	
	public static class NoSignedNoEncryptedCustomDeserializer implements JsonDeserializer<NoSignedNoEncrypted> {
		@Override
		public NoSignedNoEncrypted deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			if(json == null){
				return null;
			} else {
				String nntStr = json.getAsJsonObject().get("nnType").getAsString();
				NoSignedNoEncryptedType nnType = NoSignedNoEncryptedType.valueOf(nntStr);
				
				NoSignedNoEncrypted aux;
				switch(nnType) {
					case ASYMMETRIC_KEY_REQUEST:
						aux = AsymmetricKeyRequest.deserialize(json.getAsJsonObject().toString());
						break;
					case ASYMMETRIC_KEY_RESPONSE:
						aux = AsymmetricKeyResponse.deserialize(json.getAsJsonObject().toString());
						break;
					case CERTIFICATE_REQUEST:
						aux = CertificateRequest.deserialize(json.getAsJsonObject().toString());
						break;
					case SYMMETRIC_KEY_RESQUEST:
						aux = SymmetricKeyRequest.deserialize(json.getAsJsonObject().toString());
						break;
					case CERTIFICATE:
						aux = CertificatePK.deserialize(json.getAsJsonObject().toString());
						break;
					default:
						return null;
				}
				return aux;
			}
		}
	}

}
