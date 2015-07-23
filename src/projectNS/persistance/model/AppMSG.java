package projectNS.persistance.model;

import com.google.gson.Gson;

public class AppMSG {
	private String clientId;
	private String body;
	
	public AppMSG(String clientId, String body){
		this.setBody(body);
		this.setClientId(clientId);
	}
	
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	public String serialize(){
		Gson gson = new Gson();
		
		return gson.toJson(this);
		
	}
	
	public static AppMSG deserialize(String appMsg) {
		Gson gson = new Gson();
		
		return gson.fromJson(appMsg, AppMSG.class);
	}
}
