package projectNS.library.security.persistance.model;

import java.lang.reflect.Type;

import projectNS.library.mycrypto.SignatureConfiguration;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class SignatureCertificateRequest extends CertificateRequest {	
	private SignatureConfiguration config;
	
	public SignatureCertificateRequest(SignatureConfiguration config, String publicKey, String subject){
		super(CertificateRequestType.SIGNATURE_CERTIFICATE_REQUEST, publicKey, subject);
		
		this.setConfig(config);
	}

	public SignatureConfiguration getConfig() {
		return config;
	}

	public void setConfig(SignatureConfiguration config) {
		this.config = config;
	}
	
	public static SignatureCertificateRequest deserialize(String certificateRequest) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(SignatureCertificateRequest.class, new SignatureCertificateRequestCustom());
	    
		try {
			return gson.create().fromJson(certificateRequest, SignatureCertificateRequest.class);
		} catch(JsonParseException e) {
			return null;
		}
	}

	private static class SignatureCertificateRequestCustom implements JsonDeserializer<SignatureCertificateRequest> {
		@Override
		public SignatureCertificateRequest deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			if(json == null){
				return null;
			} else {
	        	SignatureConfiguration config;
	        	
	        	config = SignatureConfiguration.deserialize(json.getAsJsonObject().get("config").getAsJsonObject().toString());
	        	
	        	return (SignatureCertificateRequest) CertificateRequest.getParentProperties(json, new SignatureCertificateRequest(config, null, null));
			}
		}
	}

}
