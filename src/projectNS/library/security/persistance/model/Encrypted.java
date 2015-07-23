package projectNS.library.security.persistance.model;

import java.lang.reflect.Type;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import projectNS.library.mycrypto.MyCrypto.CryptoAlgorithm;

public class Encrypted extends MsgSecureLayer {	
	private CryptoAlgorithm algorithm;
	private String dataEncrypted;
	
	Encrypted(SignedType signedType) {
		super(MsgSecureLayerType.ENCRYPTED, signedType);
	}

	public CryptoAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(CryptoAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	public String getDataEncrypted() {
		return dataEncrypted;
	}

	public void setDataEncrypted(String dataEncrypted) {
		this.dataEncrypted = dataEncrypted;
	}
	
	public static Encrypted deserialize(String encrypted) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(Encrypted.class, new EncryptedCustomDeserializer());
	    
		try {
			return gson.create().fromJson(encrypted, Encrypted.class);
		} catch(JsonParseException e) {
			return null;
		}
	}
	
	public static class EncryptedCustomDeserializer implements JsonDeserializer<Encrypted> {
		@Override
		public Encrypted deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			if(json == null){
				return null;
			} else {
				String stStr = json.getAsJsonObject().get("signedType").getAsString();
				SignedType signedType = SignedType.valueOf(stStr);
				
				Encrypted aux;
				switch(signedType) {
					case NOSIGNED:
						//aux = NoSignedEncrypted.deserialize(json.getAsJsonObject().toString());
						return null;
					case SIGNED:
						aux = SignedEncrypted.deserialize(json.getAsJsonObject().toString());
						break;
					default:
						return null;
				}
				
				String algorithm = json.getAsJsonObject().get("algorithm").getAsString();
				String dataEncrypted = json.getAsJsonObject().get("dataEncrypted").getAsString();
				
				aux.setAlgorithm(CryptoAlgorithm.valueOf(algorithm));
				aux.setDataEncrypted(dataEncrypted);
				
				return aux;
			}
		}
	}
	

}
