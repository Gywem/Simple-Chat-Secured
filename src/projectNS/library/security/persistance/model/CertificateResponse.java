package projectNS.library.security.persistance.model;

import java.lang.reflect.Type;

import projectNS.library.mycrypto.SignatureConfiguration;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class CertificateResponse extends SignedNoEncrypted {

	private CertificateRequest header;
	
	public CertificateResponse(CertificateRequest header, String subject, SignatureConfiguration config) {
		super(SignedNoEncryptedType.CERTIFICATE_RESPONSE, subject, config);
		
		this.setHeader(header);
	}

	public CertificateRequest getHeader() {
		return header;
	}

	public void setHeader(CertificateRequest header) {
		this.header = header;
	}
	
	public static CertificateResponse deserialize(String certificateResponseRawMsg) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(CertificateResponse.class, new CertificateResponseCustom());
	    
		try {
			return gson.create().fromJson(certificateResponseRawMsg, CertificateResponse.class);
		} catch(JsonParseException e) {
			return null;
		}
	}

	private static class CertificateResponseCustom implements JsonDeserializer<CertificateResponse> {
		@Override
		public CertificateResponse deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			if(json == null){
				return null;
			} else {
				
	        	CertificateRequest header;
	        	
	        	header = CertificateRequest.deserialize(json.getAsJsonObject().get("header").getAsJsonObject().toString());

	        	return (CertificateResponse) SignedNoEncrypted.getParentProperties(json, new CertificateResponse(header, null, null));
			}
		}
	}

}
