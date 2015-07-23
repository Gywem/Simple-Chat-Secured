/*
 * Project Network Security
 * author: Ignacio José Codoñer Gil (igcogi@gmail.com)
 *  - st number : 0416040
 * */

package projectNS.controller;

import java.util.Scanner;

import projectNS.library.conection.ConectionManager;
import projectNS.library.conection.IConectionListener;
import projectNS.library.security.ISecurityNodeListener;
import projectNS.library.security.NodeSecurityController;
import projectNS.manager.AppMSGManager;
import projectNS.manager.IAppMsgListener;
import projectNS.persistance.model.AppMSG;
import projectNS.view.Displayer;


public class AppController implements IConectionListener, IAppMsgListener, ISecurityNodeListener {
	private static AppController Singleton = AppController.getInstance();

	public static boolean verbose = true;
	
	private Displayer displayer;
	private ConectionManager conectionManager;
	private AppMSGManager appMsgManager;
	private NodeSecurityController securityController;
	
	private String serverID;
	
	
	private AppController () {
		displayer = Displayer.getInstance();
		conectionManager = ConectionManager.getInstance();
		appMsgManager = AppMSGManager.getInstance();
		securityController = NodeSecurityController.getInstance();
		
		securityController.addListener((ISecurityNodeListener)this);
		securityController.addListener(appMsgManager);
	}
	
	public static AppController getInstance() {
		if(Singleton == null) return new AppController();
		else return Singleton;
	}

	private String certificationAuthorityID;
	
	public String getCertificationAuthorityID() {
		return certificationAuthorityID;
	}

	public void setCertificationAuthorityID(String certificationAuthorityID) {
		this.certificationAuthorityID = certificationAuthorityID;
	}
	
	public void load() {
		conectionManager.addListener(AppController.getInstance());
		appMsgManager.addListener(AppController.getInstance());
		securityController.addListener(AppController.getInstance());
		
		String client;
		
		this.serverID = securityController.startSecureServerService();
		
		securityController.startCAServerConection();		
		
		client = securityController.startSecureClientConection();
		
		this.startMsgExchange(this.serverID, client);
	}
	
	private void startMsgExchange(String server, String client) {
		while(true){
			displayer.displayMsgExchangeSection(appMsgManager.getAppMSGs(), client, server);
			
			String msgToSend;
			msgToSend = this.getString();

			AppMSG appmsg = appMsgManager.createAppMSG(server, msgToSend);
			
			securityController.sendApplicationMsg(client, appmsg.getBody());
		}				
	}
	
	public int getPort(){
		String portStr;
		portStr = this.getString();
		
		return Integer.parseInt(portStr);
	}
	
	public String getString(){
		@SuppressWarnings("resource")
		Scanner userInputScanner = new Scanner(System.in);		
		return userInputScanner.nextLine();
	}

	@Override
	public void onNewConection(String clientID) {
	}

	@Override
	public void onDisconection(String clientID) {		
	}

	@Override
	public void onAppMsg(AppMSG msg) {
		appMsgManager.addAppMSG(msg);		
		displayer.displayMsgExchangeSection(appMsgManager.getAppMSGs(), msg.getClientId(), this.serverID);
	}

	@Override
	public void onCertificateResponse(String clientID) {
		
	}
	
}
