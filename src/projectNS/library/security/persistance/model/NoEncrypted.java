package projectNS.library.security.persistance.model;

import java.lang.reflect.Type;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class NoEncrypted extends MsgSecureLayer {
	NoEncrypted(SignedType signedType) {
		super(MsgSecureLayerType.NOENCRYPTED, signedType);
	}
	
	public static NoEncrypted deserialize(String noEncrypted) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(NoEncrypted.class, new NoEncryptedCustomDeserializer());
	    
		try {
			return gson.create().fromJson(noEncrypted, NoEncrypted.class);
		} catch(JsonParseException e) {
			return null;
		}
	}
	
	public static class NoEncryptedCustomDeserializer implements JsonDeserializer<NoEncrypted> {
		@Override
		public NoEncrypted deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			if(json == null){
				return null;
			} else {
				String stStr = json.getAsJsonObject().get("signedType").getAsString();
				SignedType signedType = SignedType.valueOf(stStr);
				
				NoEncrypted aux;
				switch(signedType) {
					case NOSIGNED:
						aux = NoSignedNoEncrypted.deserialize(json.getAsJsonObject().toString());
						break;
					case SIGNED:
						aux = SignedNoEncrypted.deserialize(json.getAsJsonObject().toString());
						break;
					default:
						return null;
				}
				return aux;
			}
		}
	}

}
