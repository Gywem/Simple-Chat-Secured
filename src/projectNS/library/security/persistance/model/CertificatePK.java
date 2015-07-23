package projectNS.library.security.persistance.model;

import java.lang.reflect.Type;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class CertificatePK extends NoSignedNoEncrypted {
	
	private CertificateResponse body;
	private String signature;
	
	public CertificatePK(CertificateResponse body, String signature){
		super(NoSignedNoEncryptedType.CERTIFICATE);
		
		this.setBody(body);
		this.setSignature(signature);
	}

	public CertificateResponse getBody() {
		return body;
	}

	public void setBody(CertificateResponse body) {
		this.body = body;
	}
	
	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public static CertificatePK deserialize(String certificate) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(CertificatePK.class, new CertificatePKCustom());
	    
		try {
			return gson.create().fromJson(certificate, CertificatePK.class);
		} catch(JsonParseException e) {
			return null;
		}
	}

	private static class CertificatePKCustom implements JsonDeserializer<CertificatePK> {
		@Override
		public CertificatePK deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			if(json == null){
				return null;
			} else {
				CertificateResponse body;
				String signature;
	        	
	        	body = CertificateResponse.deserialize(json.getAsJsonObject().get("body").getAsJsonObject().toString());
	        	signature = json.getAsJsonObject().get("signature").getAsString();
	        	
	        	return new CertificatePK(body, signature);
			}
		}
	}

}
