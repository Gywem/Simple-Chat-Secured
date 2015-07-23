package projectNS.library.security.persistance.model;

import java.lang.reflect.Type;

import projectNS.library.mycrypto.CryptoConfiguration;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class CypherCertificateRequest extends CertificateRequest {	
	private CryptoConfiguration config;
	
	public CypherCertificateRequest(CryptoConfiguration config, String publicKey, String subject){
		super(CertificateRequestType.CYPER_CERTIFICATE_REQUEST, publicKey, subject);
		
		this.setConfig(config);
	}

	public CryptoConfiguration getConfig() {
		return config;
	}

	public void setConfig(CryptoConfiguration config) {
		this.config = config;
	}
	
	public static CypherCertificateRequest deserialize(String certificateRequest) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(CypherCertificateRequest.class, new SignatureCertificateRequestCustom());
	    
		try {
			return gson.create().fromJson(certificateRequest, CypherCertificateRequest.class);
		} catch(JsonParseException e) {
			return null;
		}
	}

	private static class SignatureCertificateRequestCustom implements JsonDeserializer<CypherCertificateRequest> {
		@Override
		public CypherCertificateRequest deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			if(json == null){
				return null;
			} else {
				CryptoConfiguration config;
	        	
	        	config = CryptoConfiguration.deserialize(json.getAsJsonObject().get("config").getAsJsonObject().toString());
	        	
	        	return (CypherCertificateRequest) CertificateRequest.getParentProperties(json, new CypherCertificateRequest(config, null, null));
			}
		}
	}

}
