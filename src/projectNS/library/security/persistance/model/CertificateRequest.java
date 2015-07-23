package projectNS.library.security.persistance.model;

import java.lang.reflect.Type;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class CertificateRequest extends NoSignedNoEncrypted {
	public enum CertificateRequestType {
		SIGNATURE_CERTIFICATE_REQUEST,
		CYPER_CERTIFICATE_REQUEST
	};
	
	private CertificateRequestType cerReqType;
	
	private String publicKey;
	private String subject;
	
	public CertificateRequest(CertificateRequestType cerReqType, String publicKey, String subject){
		super(NoSignedNoEncryptedType.CERTIFICATE_REQUEST);
		
		this.setCerReqType(cerReqType);
		this.setPublicKey(publicKey);
		this.setSubject(subject);
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public CertificateRequestType getCerReqType() {
		return cerReqType;
	}

	private void setCerReqType(CertificateRequestType cerReqType) {
		this.cerReqType = cerReqType;
	}
	
	public static CertificateRequest deserialize(String certificateRequest) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(CertificateRequest.class, new CertificateRequestCustom());
	    
		try {
			return gson.create().fromJson(certificateRequest, CertificateRequest.class);
		} catch(JsonParseException e) {
			return null;
		}
	}

	private static class CertificateRequestCustom implements JsonDeserializer<CertificateRequest> {
		@Override
		public CertificateRequest deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			if(json == null){
				return null;
			} else {	        	
	        	CertificateRequestType type = CertificateRequestType.valueOf(json.getAsJsonObject().get("cerReqType").getAsString());
	        	CertificateRequest aux;
	        	
	        	switch(type) {
					case CYPER_CERTIFICATE_REQUEST:
						aux = CypherCertificateRequest.deserialize(json.getAsJsonObject().toString());
						break;
					case SIGNATURE_CERTIFICATE_REQUEST:
						aux = SignatureCertificateRequest.deserialize(json.getAsJsonObject().toString());
						break;
					default:
						return null;	        	
	        	}
	        	
	        	return aux;
			}
		}
	}
	
	protected static CertificateRequest getParentProperties(JsonElement json, CertificateRequest crequest){
		String publicKey;
    	String subject;
    	
    	publicKey = json.getAsJsonObject().get("publicKey").getAsString();
    	subject =  json.getAsJsonObject().get("subject").getAsString();
    	
    	crequest.setPublicKey(publicKey);
    	crequest.setSubject(subject);
    	
    	return crequest;
	}
}
