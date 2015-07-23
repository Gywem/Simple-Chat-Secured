/*
 * Project Network Security
 * author: Ignacio José Codoñer Gil (igcogi@gmail.com)
 *  - st number : 0416040
 * */

package projectNS.controller;

import projectNS.library.conection.ConectionManager;
import projectNS.library.conection.IConectionListener;
import projectNS.library.security.CASecurityController;
import projectNS.view.Displayer;

public class CAController implements IConectionListener {
	private static CAController Singleton;

	public static boolean verbose = true;
	
	@SuppressWarnings("unused")
	private Displayer displayer;
	private ConectionManager conectionManager;
	private CASecurityController securityController;
	
	
	private CAController() {
		displayer = Displayer.getInstance();
		conectionManager = ConectionManager.getInstance();
		securityController = CASecurityController.getInstance();

		conectionManager.addListener(this);
	}
	
	public static CAController getInstance() {
		if(Singleton == null) {
			Singleton = new CAController();
		}
		return Singleton;
	}

	private String serverID;
	
	public String getServerID() {
		return serverID;
	}

	public void setServerID(String serverID) {
		this.serverID = serverID;
	}
	
	public void load() {
		conectionManager.addListener(CAController.getInstance());
		
		securityController.startSecureServerService();
	}

	@Override
	public void onNewConection(String clientID) {}

	@Override
	public void onDisconection(String clientID) {}
}
