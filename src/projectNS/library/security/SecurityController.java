package projectNS.library.security;

import java.util.List;
import java.util.Scanner;

import projectNS.library.conection.ConectionManager;
import projectNS.library.conection.IPacketListener;
import projectNS.library.mycrypto.MyCrypto;
import projectNS.library.mycrypto.MySignature.HashType;
import projectNS.library.mycrypto.PersonalSignature;
import projectNS.library.mycrypto.SignatureConfiguration;
import projectNS.library.mycrypto.MySignature.SignatureAlgorithm;
import projectNS.library.security.manager.CertificateManager;
import projectNS.library.security.persistance.model.ApplicationMsg;
import projectNS.library.security.persistance.model.AsymmetricKeyRequest;
import projectNS.library.security.persistance.model.AsymmetricKeyResponse;
import projectNS.library.security.persistance.model.CertificatePK;
import projectNS.library.security.persistance.model.CertificateRequest;
import projectNS.library.security.persistance.model.CertificateResponse;
import projectNS.library.security.persistance.model.Encrypted;
import projectNS.library.security.persistance.model.MsgSecureLayer;
import projectNS.library.security.persistance.model.NoEncrypted;
import projectNS.library.security.persistance.model.NoSignedNoEncrypted;
import projectNS.library.security.persistance.model.SignedEncrypted;
import projectNS.library.security.persistance.model.SignedNoEncrypted;
import projectNS.library.security.persistance.model.SymmetricKeyRequest;
import projectNS.library.security.persistance.model.SymmetricKeyResponse;

public abstract class SecurityController implements IPacketListener {
	public enum SecurityControllerType {
		CERTIFICATION_AUTHORITY,
		COMMUNICATION_NODE
	};
	
	private SecurityControllerType type;
	
	protected static SecurityController Singleton;
	protected ConectionManager conectionManager;
	protected CertificateManager certificateManager;
	protected SecurityDisplayer displayer;
	
	protected PersonalSignature signatureApproach;
	
	protected String hostOfClients = "127.0.0.1";
	
	protected String serverID;
	public String getServerID() {
		return serverID;
	}

	public void setServerID(String serverID) {
		this.serverID = serverID;
	}

	protected List<Integer> rangePortsAvailableNodes;
	
	protected SecurityController (SecurityControllerType type) {		
		conectionManager = ConectionManager.getInstance();
		certificateManager = CertificateManager.getInstance();
		displayer = SecurityDisplayer.getInstance();
		
		conectionManager.addListener(this);
		
		this.loadSecurityServices();
	}

	public SecurityControllerType getType() {
		return type;
	}

	public void setType(SecurityControllerType type) {
		this.type = type;
	}
	
	protected void loadSecurityServices() {
		displayer.displayGetSignatureMethod();
		SignatureConfiguration config;
		while((config = this.getSignatureConfiguration())== null);
		signatureApproach = new PersonalSignature(config, null);
		displayer.displaySignatureConfigurationOptionChosen(config);
	}
	
	protected SignatureConfiguration getSignatureConfiguration() {
		displayer.displaySignatureSelection();
		int option = this.getNumber();
		
		switch(option) {
			case 0:
				return new SignatureConfiguration(SignatureAlgorithm.DSA, HashType.SHA1);
			case 1:
				return new SignatureConfiguration(SignatureAlgorithm.RSA, HashType.MD5);
			case 2:
				return new SignatureConfiguration(SignatureAlgorithm.RSA, HashType.SHA1);
			case 3:
				return new SignatureConfiguration(SignatureAlgorithm.RSA, HashType.SHA256);
			default:
				return null;
		}
	}
	
	protected String getString(){
		@SuppressWarnings("resource")
		Scanner userInputScanner = new Scanner(System.in);		
		return userInputScanner.nextLine();
	}
	
	protected int getNumber(){
		String portStr;
		portStr = this.getString();
		
		try {
			return Integer.parseInt(portStr);
		} catch (NumberFormatException e) {
			return -1;
		}
		
	}
	
	protected String trySecureServerService() {
		displayer.displayGetServerPortSection();
		
		this.serverID = conectionManager.tryServerConection(this.rangePortsAvailableNodes);
		
		Boolean success = true;
		if(this.serverID == null) {
			success = false;
		} else {
			conectionManager.runServer();
			
			signatureApproach.setSubject(this.serverID);
		}

		displayer.displayServerOutputMessage(success, serverID);
		
		return this.serverID;
	}
	public abstract String startSecureServerService();
	
	public void sendSignedEncrypted(String clientID, PersonalSignature signatureApproach, SignedNoEncrypted msg, MyCrypto crypto) {
		SignedNoEncrypted aux = this.signSignedNoEncrypted(signatureApproach, msg);
		
		displayer.displaySendSignedNoEncrypted(clientID);
		displayer.displaySignedNoEncryptedMsg(aux);
		
		SignatureConfiguration config = aux.getConfig(); 
		aux.setConfig(null);
		String subject = aux.getSubject();
		aux.setSubject(null);
		String signStr = aux.getSignature();
		aux.setSignature(null);
		
		String encryption = crypto.cipher(msg.serialize());
		
		SignedEncrypted enmsg = new SignedEncrypted(config, subject, signStr);
		enmsg.setDataEncrypted(encryption);
		enmsg.setAlgorithm(crypto.getConfiguration().getAlgorithm());
		
		displayer.displaySendSignedEncrypted(clientID);
		displayer.displaySignedEncrypted(enmsg);
		
		this.sendSecureLayerMsg(clientID, enmsg);
	}
	
	public void sendSignedNoEncrypted(String clientID, PersonalSignature signature, SignedNoEncrypted msg) {		
		SignedNoEncrypted aux = this.signSignedNoEncrypted(signatureApproach, msg);
		
		this.sendSecureLayerMsg(clientID, aux);
		
		displayer.displaySignedNoEncryptedMsg(aux);
	}
	
	public SignedNoEncrypted signSignedNoEncrypted(PersonalSignature signature, SignedNoEncrypted msg) {		
		String signatureStr = signatureApproach.signData(msg.serialize());
		
		msg.setSignature(signatureStr);
		
		return msg;
	}
	
	protected void sendSecureLayerMsg(String clientID, MsgSecureLayer ms) {
		conectionManager.sendPacket(clientID, ms.serialize());
	}
	
	protected String signMsg(MsgSecureLayer ms) {
		return signatureApproach.signData(ms.serialize());
	}
	
	@Override
	public void onNewPacket(String clientID, String m) {
		MsgSecureLayer ms;
		if((ms = MsgSecureLayer.deserialize(m)) != null){
			//sucessfull deserialize
			switch(ms.getType()){
				case ENCRYPTED:
					this.onNewEncryptedMsg(clientID, (Encrypted) ms);
					break;
				case NOENCRYPTED:
					this.onNewNoEncryptedMsg(clientID, (NoEncrypted) ms);
					break;
			default:
				break;
			}
			
		}
	}
	
	protected abstract void onNewEncryptedMsg(String clientID, Encrypted ms);
	
	protected void onNewNoEncryptedMsg(String clientID, NoEncrypted ms) {
		switch(ms.getSignedType()){
			case NOSIGNED:
				this.onNewNoSignedNoEncryptedMsg(clientID, (NoSignedNoEncrypted) ms);
				break;
			case SIGNED:
				this.onNewSignedNoEncryptedMsg(clientID, (SignedNoEncrypted) ms);
				break;
			default:
				break;
		}
		
	}
	
	protected abstract void onNewSignedNoEncryptedMsg(String clientID, SignedNoEncrypted ms);

	protected abstract void onNewSymmetricKeyResponse(String clientID, SymmetricKeyResponse ms);
	protected abstract void onNewCertificateResponse(String clientID, CertificateResponse ms);
	protected abstract void onNewAsymmetricKeyResponse(String clientID, AsymmetricKeyResponse ms);
	protected abstract void onNewApplicationMsg(String clientID, ApplicationMsg ms);

	protected void onNewNoSignedNoEncryptedMsg(String clientID,
			NoSignedNoEncrypted ms) {
		switch(ms.getNnType()) {
			case ASYMMETRIC_KEY_REQUEST:
				this.onNewAsymmetricKeyRequest(clientID, (AsymmetricKeyRequest) ms);
				break;
			case ASYMMETRIC_KEY_RESPONSE:
				this.onNewAsymmetricKeyResponse(clientID, (AsymmetricKeyResponse) ms);
				break;
			case CERTIFICATE_REQUEST:
				this.onNewCertificateRequest(clientID, (CertificateRequest) ms);
				break;
			case SYMMETRIC_KEY_RESQUEST:
				this.onNewSymmetricKeyRequest(clientID, (SymmetricKeyRequest) ms);
				break;
			case CERTIFICATE:
				this.onNewCertificate(clientID, (CertificatePK) ms);
				break;
			default:
			break;
		
		}
		
	}

	protected abstract void onNewSymmetricKeyRequest(String clientID, SymmetricKeyRequest ms);
	protected abstract void onNewCertificateRequest(String clientID, CertificateRequest ms);
	protected abstract void onNewAsymmetricKeyRequest(String clientID, AsymmetricKeyRequest ms);
	protected abstract void onNewCertificate(String clientID, CertificatePK ms);

	public PersonalSignature getSignatureApproach() {
		return signatureApproach;
	}

	public void setSignatureApproach(PersonalSignature signatureApproach) {
		this.signatureApproach = signatureApproach;
	}
	
}
