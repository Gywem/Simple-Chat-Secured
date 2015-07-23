package projectNS.library.security.persistance.model;

import projectNS.library.mycrypto.MyCrypto.CryptoAlgorithm;
import projectNS.library.mycrypto.SignatureConfiguration;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class SymmetricKeyResponse extends SignedNoEncrypted {
	private CryptoAlgorithm alg;
	private String symmetricKey;

	public SymmetricKeyResponse(String symmetricKey, CryptoAlgorithm alg, String subject, SignatureConfiguration config) {
		super(SignedNoEncryptedType.SYMMETRIC_KEY_RESPONSE, subject, config);
		
		this.setSymmetricKey(symmetricKey);
		this.setAlg(alg);
	}

	public String getSymmetricKey() {
		return symmetricKey;
	}


	public void setSymmetricKey(String symmetricKey) {
		this.symmetricKey = symmetricKey;
	}	
	
	public static SymmetricKeyResponse deserialize(String symmetricKeyResponse) {
		Gson gson = new Gson();
		try {
			return gson.fromJson(symmetricKeyResponse, SymmetricKeyResponse.class);
		} catch(JsonParseException e) {
			return null;
		}
	}

	public CryptoAlgorithm getAlg() {
		return alg;
	}

	public void setAlg(CryptoAlgorithm alg) {
		this.alg = alg;
	}
}
