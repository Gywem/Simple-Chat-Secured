package projectNS.library.security.persistance.model;

import java.lang.reflect.Type;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class AsymmetricKeyResponse extends NoSignedNoEncrypted {
	private CertificatePK certificate;
	
	public AsymmetricKeyResponse(CertificatePK certificate) {
		super(NoSignedNoEncryptedType.ASYMMETRIC_KEY_RESPONSE);
		
		this.setCertificate(certificate);
	}

	public CertificatePK getCertificate() {
		return certificate;
	}

	public void setCertificate(CertificatePK certificate) {
		this.certificate = certificate;
	}
	
	public static AsymmetricKeyResponse deserialize(String certificateRequest) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(AsymmetricKeyResponse.class, new AsymmetricKeyResponseCustom());
	    
		try {
			return gson.create().fromJson(certificateRequest, AsymmetricKeyResponse.class);
		} catch(JsonParseException e) {
			return null;
		}
	}

	private static class AsymmetricKeyResponseCustom implements JsonDeserializer<AsymmetricKeyResponse> {
		@Override
		public AsymmetricKeyResponse deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			if(json == null){
				return null;
			} else {
				CertificatePK certificate;
	        	
	        	certificate = CertificatePK.deserialize(json.getAsJsonObject().get("certificate").getAsJsonObject().toString());
	        	
	        	return new AsymmetricKeyResponse(certificate);
			}
		}
	}
	
	
}
