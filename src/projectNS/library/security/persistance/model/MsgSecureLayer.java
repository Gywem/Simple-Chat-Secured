package projectNS.library.security.persistance.model;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class MsgSecureLayer {
	public enum MsgSecureLayerType {
		ENCRYPTED,
		NOENCRYPTED
	};
	public enum SignedType {
		SIGNED,
		NOSIGNED
	};
	private MsgSecureLayerType type;
	private SignedType signedType;
	
	MsgSecureLayer(MsgSecureLayerType type, SignedType signedType){
		this.setType(type);
		this.setSignedType(signedType);
	}
	
	public boolean isSigned() {
		if(this.getSignedType() == SignedType.SIGNED) return true;
		else return false;
	}

	public MsgSecureLayerType getType() {
		return type;
	}

	private void setType(MsgSecureLayerType type) {
		this.type = type;
	}
	
	public String serialize(){
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public SignedType getSignedType() {
		return signedType;
	}

	private void setSignedType(SignedType signedType) {
		this.signedType = signedType;
	}
	
	public static MsgSecureLayer deserialize(String msgSecureLayer) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(MsgSecureLayer.class, new MsgSecureLayerCustomDeserializer());
	    
		try {
			return gson.create().fromJson(msgSecureLayer, MsgSecureLayer.class);
		} catch(JsonParseException e) {
			return null;
		}
	}

	public static class MsgSecureLayerCustomDeserializer implements JsonDeserializer<MsgSecureLayer> {
		@Override
		public MsgSecureLayer deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			if(json == null){
				return null;
			} else {
				String msgType = json.getAsJsonObject().get("type").getAsString();
				
				MsgSecureLayerType type = MsgSecureLayerType.valueOf(msgType);
				switch(type){
					case ENCRYPTED:
						return Encrypted.deserialize(json.getAsJsonObject().toString());
					case NOENCRYPTED:
						return NoEncrypted.deserialize(json.getAsJsonObject().toString());
					default:
						return null;
				}
			}
		}
	}
	
	
	
}
