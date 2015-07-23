package projectNS.library.conection.model;

import java.net.InetAddress;

/*
 * Information about a node (computer) for connecting purposes
 * */
public abstract class NodeP2P {
	private String id;
	private String idLocal;
	private InetAddress ip;
	private int port;
	
	NodeP2P(InetAddress inetAddress, int port){
		this.ip = inetAddress;
		this.port = port;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdLocal() {
		return idLocal;
	}

	public void setIdLocal(String idLocal) {
		this.idLocal = idLocal;
	}

}
