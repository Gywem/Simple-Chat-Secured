package projectNS.library.security.persistance.model;

import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class AsymmetricKeyRequest extends NoSignedNoEncrypted {
	public enum AsymmetricKeyRequestType {
		SIGNATURE,
		CYPER
	};
	
	private AsymmetricKeyRequestType akrType;
	
	public AsymmetricKeyRequest(AsymmetricKeyRequestType akrType) {
		super(NoSignedNoEncryptedType.ASYMMETRIC_KEY_REQUEST);
		
		this.setAkrType(akrType);
	}
	
	public AsymmetricKeyRequestType getAkrType() {
		return akrType;
	}

	private void setAkrType(AsymmetricKeyRequestType akrType) {
		this.akrType = akrType;
	}
	
	public static AsymmetricKeyRequest deserialize(String asymmetricKeyRequest) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(AsymmetricKeyRequest.class, new AsymmetricKeyRequestCustom());
	    
		try {
			return gson.create().fromJson(asymmetricKeyRequest, AsymmetricKeyRequest.class);
		} catch(JsonParseException e) {
			return null;
		}
	}

	private static class AsymmetricKeyRequestCustom implements JsonDeserializer<AsymmetricKeyRequest> {
		@Override
		public AsymmetricKeyRequest deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			if(json == null){
				return null;
			} else {	        	
				AsymmetricKeyRequestType akrType = AsymmetricKeyRequestType.valueOf(json.getAsJsonObject().get("akrType").getAsString());
	        	AsymmetricKeyRequest aux;
	        	
	        	switch(akrType) {
					case CYPER:
						aux = CypherAsymmetricKeyRequest.deserialize(json.getAsJsonObject().toString());
						break;
					case SIGNATURE:
						aux = SignatureAsymmetricKeyRequest.deserialize(json.getAsJsonObject().toString());
						break;
					default:
						return null;       	
	        	}
	        	
	        	return aux;
			}
		}
	}

}
