/*
 * Project Network Security
 * author: Ignacio José Codoñer Gil (igcogi@gmail.com)
 *  - st number : 0416040
 * */

package projectNS.library.conection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import projectNS.library.conection.model.Client;
import projectNS.library.conection.model.Server;

public class ConectionManager implements IConectionObservable, IPacketObservable {
	private static ConectionManager Singleton = ConectionManager.getInstance();
	
	private String host = "127.0.0.1";	

	private Server serverInfo;
	
	private List<Client> clients;
	private Map<String, Client> clientsMap;
	private List<IConectionListener> listeners;
	private List<IPacketListener> packetListeners;
	
	public int nextPort = 0;
	
	public String newServerConection(int port) throws IOException{
		Server server;
		
		if(this.serverInfo == null) {
			server = new Server(new ServerSocket(port));
			this.serverInfo = server;
			
			return this.host+":"+port;
		}
		return this.host+":"+this.serverInfo.getPort();
	}
	
	public Client newClientConection(String host, int port) throws IOException {
		Client client;
		if(!this.clientsMap.containsKey(host+":"+port)) {
			InetAddress address;
			try {
				address = InetAddress.getByName(host);
				client = this.newConection(new Socket(address, port), true);
				return client;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		
		return this.clientsMap.get(host+":"+port);
	}
	
	public Server getServerInfo() {
		return serverInfo;
	}

	public void setServerInfo(Server serverInfo) {
		this.serverInfo = serverInfo;
	}

	private ConectionManager(){
		this.listeners = new ArrayList<IConectionListener>();
		this.packetListeners = new ArrayList<IPacketListener>();
		this.clients = new ArrayList<Client>();
		this.clientsMap = new HashMap<String,Client>();
		
	}
	
	@SuppressWarnings("static-access")
	public String tryNewConection(List<Integer> rangePortsAvailable, String host){
		int port = rangePortsAvailable.get(0) + nextPort;
		if(serverInfo != null && serverInfo.getPort() == port) {
			nextPort++;
			
			return tryNewConection(rangePortsAvailable, host);
		}
		if(port >= rangePortsAvailable.get(0) && port <= rangePortsAvailable.get(1)) {
			try {
				Client client = this.newClientConection(host, port);
				String id = host+":"+port;
				if(!this.clientsMap.containsKey(id)) {
					this.clientsMap.put(id, client);
					
					new listenClientMessages(client).start();
				}
				return id;
			} catch (IOException e) {
				try {
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				nextPort++;
				return tryNewConection(rangePortsAvailable, host);
			}
		} else {
			nextPort = 0;
			return this.tryNewConection(rangePortsAvailable, host);
		}		
	}
	
	public Client newConection(Socket socket, Boolean local) {
		Client client = null;
		client = new Client(socket);
		
		client.setPort(socket.getPort());
		
		String id = socket.getInetAddress().getHostAddress()+":";
		id += (local)? socket.getLocalPort(): socket.getPort();
		client.setId(id);
		
		if(!this.clientsMap.containsKey(id)) {
			this.clientsMap.put(id, client);
			
			new listenClientMessages(client).start();
		}
		
		
		return this.clientsMap.get(id);
	}
	
	public String tryServerConection(List<Integer> rangePortsAvailable){
		if(serverInfo != null) return this.host+":"+this.serverInfo.getPort();
		int port = rangePortsAvailable.get(0) + nextPort;
		if(port >= rangePortsAvailable.get(0) && port <= rangePortsAvailable.get(1)) {
			try {
				return this.newServerConection(port);
			} catch (IOException e) {
				nextPort++;
				return this.tryServerConection(rangePortsAvailable);
			}
		} else {
			nextPort = 0;
			return this.tryServerConection(rangePortsAvailable);
		}
	}
	
	public static ConectionManager getInstance(){
		if(Singleton == null) {
			return new ConectionManager();
		} else {
			return Singleton;
		}
	}

	@Override
	public void addListener(IConectionListener observer) {
		listeners.add(observer);
	}

	public void runServer() {
		new acceptConections(this.serverInfo).start();
	}
	
	public void broadcastPacket(String raw){
		synchronized(this.clientsMap) {
			Iterator<Client> it = this.clients.iterator();
			
			while(it.hasNext()) {
				Client c = it.next();
				
				this.sendPacket(c, raw);
			}
		}
	}
	
	public void sharePacket(Client from, String raw){
		Iterator<Client> it = this.clients.iterator();
		
		while(it.hasNext()) {
			Client c = it.next();
			
			if(!from.equals(c)) this.sendPacket(c, raw);
		}
	}
	
	public void sendPacket(Client client, String raw){
		if(client.getSocket() != null) {
			try {
				BufferedOutputStream os = new BufferedOutputStream(client.getSocket().getOutputStream());
			    OutputStreamWriter osw = new OutputStreamWriter(os, "US-ASCII");
				osw.write(raw+(char) 13);
				osw.flush();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				this.newDisconectionUpdate(client);
				try {
					client.getSocket().close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public void sendPacket(String hostport, String rawData){
		Client client = this.clientsMap.get(hostport);
		
		if(client.getSocket() != null) {
			try {
				BufferedOutputStream os = new BufferedOutputStream(client.getSocket().getOutputStream());
			    OutputStreamWriter osw = new OutputStreamWriter(os, "US-ASCII");
				osw.write(rawData+(char) 13);
				osw.flush();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				this.newDisconectionUpdate(client);
				try {
					client.getSocket().close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	class acceptConections extends Thread {	
		Server serverInfo;
		acceptConections(Server serverInfo) {
			this.serverInfo = serverInfo;
        }

        public void run() {
        	Client client;
    		try {
    			while(true) {
    				client = newConection(serverInfo.getSocket().accept(), false);
    				
    				newConectionUpdate(client);
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        	
        }
    }
	
	class listenClientMessages extends Thread {
		Client client;
		
		listenClientMessages(Client c) {
			this.client = c;
        }

        public void run() {
			BufferedInputStream is;
			try {
				is = new BufferedInputStream(client.getSocket().getInputStream());

				
				InputStreamReader isr = new InputStreamReader(is);
				String message = "";
				
				int character;
				
				while(true) {
					while((character = isr.read()) != 13) {
						message += (char)character;
					}
					new newMessageUpdate(client, message.toString()).start();
					//newMessageUpdate(client,message.toString());

					message = "";
				}				
			} catch (IOException e1) {
				try {
					this.client.getSocket().close();
				} catch (IOException ev) {
					ev.printStackTrace();
				}
				e1.printStackTrace();
			}
        	
        }
    }
	
	class newMessageUpdate extends Thread {
		String msg;
		Client client;
		
		newMessageUpdate(Client client, String message) {
			this.client = client;
			this.msg = message;
        }

        public void run() {        	
        	newMessageUpdate(client, msg);
        }
    }
	
	@Override
	public void newMessageUpdate(Client client, String m) {
		Iterator<IPacketListener> it = packetListeners.iterator();
		
		while(it.hasNext()){
			IPacketListener next = it.next();
			next.onNewPacket(client.getId(), m);
		}
	}

	@Override
	public void newConectionUpdate(Client client) {
		Iterator<IConectionListener> it = listeners.iterator();
		
		while(it.hasNext()){
			IConectionListener next = it.next();
			next.onNewConection(client.getId());
		}
	}
	
	@Override
	public void newDisconectionUpdate(Client client) {
		Iterator<IConectionListener> it = listeners.iterator();
		
		while(it.hasNext()){
			IConectionListener next = it.next();
			next.onDisconection(client.getId());
		}
	}

	public void sharePacketTo(String serialize, Client client) {
		this.sendPacket(client, serialize);
	}

	@Override
	public void addListener(IPacketListener observer) {
		this.packetListeners.add(observer);		
	}

		
	
}
