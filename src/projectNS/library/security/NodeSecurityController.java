package projectNS.library.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import projectNS.library.conection.IConectionListener;
import projectNS.library.mycrypto.AsymmetricCrypto;
import projectNS.library.mycrypto.CryptoConfiguration;
import projectNS.library.mycrypto.MyCrypto;
import projectNS.library.mycrypto.MyCrypto.CryptoAlgorithm;
import projectNS.library.mycrypto.MyCrypto.CryptoType;
import projectNS.library.mycrypto.SomeoneSignature;
import projectNS.library.security.persistance.model.ApplicationMsg;
import projectNS.library.security.persistance.model.AsymmetricKeyRequest;
import projectNS.library.security.persistance.model.AsymmetricKeyResponse;
import projectNS.library.security.persistance.model.CertificatePK;
import projectNS.library.security.persistance.model.CertificateRequest;
import projectNS.library.security.persistance.model.CertificateResponse;
import projectNS.library.security.persistance.model.CypherAsymmetricKeyRequest;
import projectNS.library.security.persistance.model.CypherCertificateRequest;
import projectNS.library.security.persistance.model.Encrypted;
import projectNS.library.security.persistance.model.NoEncrypted;
import projectNS.library.security.persistance.model.SignatureAsymmetricKeyRequest;
import projectNS.library.security.persistance.model.SignatureCertificateRequest;
import projectNS.library.security.persistance.model.SignedEncrypted;
import projectNS.library.security.persistance.model.SignedNoEncrypted;
import projectNS.library.security.persistance.model.SymmetricKeyRequest;
import projectNS.library.security.persistance.model.SymmetricKeyResponse;

public class NodeSecurityController extends SecurityController implements IAppRawMsgObserver, ISecurityNodeObserver, IConectionListener  {
	private List<IAppRawMsgListener> listenersAppRaw;
	private List<ISecurityNodeListener> listenersSecurityNode;
	
	private boolean recievedCypherForExchangingSymKey = false, recievedSignature = false;
	
	private MyCrypto symmetricApproach;	
	// For key exchanging
	private MyCrypto asymmetricApproach;
	
	private CertificatePK signatureCertificate;
	private CertificatePK asymmetricApproachCertificate;
	
	private Map<String, MyCrypto> clientSymCryptos;
	private Map<String, MyCrypto> clientAsymCryptos;
	
	private Map<String, SomeoneSignature> clientSignatureApproach;	
	
	public static NodeSecurityController getInstance() {
		if(Singleton == null) return new NodeSecurityController();
		else return (NodeSecurityController) Singleton;
	}
	
	protected NodeSecurityController() {
		super(SecurityControllerType.COMMUNICATION_NODE);
		
		this.listenersAppRaw = new ArrayList<IAppRawMsgListener>();
		this.listenersSecurityNode = new ArrayList<ISecurityNodeListener>();
		this.rangePortsAvailableNodes = new ArrayList<Integer>() {
			private static final long serialVersionUID = 1L;

		{
			   add(5000);
			   add(5010);
		}};
		this.clientSignatureApproach = new HashMap<String, SomeoneSignature>();
		this.clientSymCryptos = new HashMap<String, MyCrypto>();
		this.clientAsymCryptos = new HashMap<String, MyCrypto>();
		conectionManager.addListener((IConectionListener)this);
		
		this.loadNodeSecurityServices();
	}
	
	private String clientID;
	private CryptoConfiguration symmetricDefaultConfig;
	
	public void loadNodeSecurityServices() {
		displayer.displayGetSymmetricEncrypt();
		while((symmetricDefaultConfig = this.getCryptoConfiguration(CryptoType.SYMMETRIC)) == null);
		displayer.displayCryptoConfigurationOptionChosen(symmetricDefaultConfig);
		
		displayer.displayGetAsymmetricKeyExchange();
		CryptoConfiguration asymmetricConfig;
		while((asymmetricConfig = this.getCryptoConfiguration(CryptoType.ASYMMETRIC)) == null);
		displayer.displayCryptoConfigurationOptionChosen(asymmetricConfig);
		
		asymmetricApproach = (AsymmetricCrypto) MyCrypto.getInstance(asymmetricConfig, true);
	}
	
	private CryptoConfiguration getCryptoConfiguration(CryptoType type) {
		displayer.displayCryptoAlgorithmSelection(type);
		
		int option = this.getNumber();
		CryptoAlgorithm algorithmChosen = CryptoAlgorithm.getfromNumber(option);
		if(algorithmChosen.type == type) {
			return new CryptoConfiguration(algorithmChosen);
		} else {
			return null;
		}
	}

	public boolean createSymmetricApproach(String subject) {		
		if(this.getClientSymCryptos().containsKey(subject) == false) {
			
			MyCrypto symmetricApproach = MyCrypto.getInstance(symmetricDefaultConfig, true);
			this.getClientSymCryptos().put(subject, symmetricApproach);

			displayer.displaySymmetricApproachCreated(subject);
			return true;
		}
		return false;
	}
	
	private String trySecureClientConection(){		
		displayer.displayGetClientPortSection();
		
		this.clientID = conectionManager.tryNewConection(rangePortsAvailableNodes, this.hostOfClients);
		
		Boolean success = true;
		if(this.clientID == null) {
			success = false;
		}

		displayer.displayClientOutputMessage(success, this.clientID);
		
		return this.clientID;
	}
	
	public String startSecureClientConection(){
		String client = trySecureClientConection();
		
		// we create the symmetric approach from the server to the client
		this.createSymmetricApproach(this.serverID+client);
		
		this.sendSignatureAsymmetricKeyRequest(client);		
		this.sendCypherAsymmetricKeyRequest(client);
		
		while (!recievedCypherForExchangingSymKey || !recievedSignature) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// We are sure that we received the certificate from the client to exchange the symmetric key
		MyCrypto symmetricApproachToShare = this.getClientSymCryptos().get(this.serverID+client);
		MyCrypto asymmetricApproachToUseForExchanging = this.getClientAsymCryptos().get(client);
		
		this.sendSymmetricKeyResponse(client, symmetricApproachToShare, asymmetricApproachToUseForExchanging);
		
		// Wait until it receives the symmetric approach from the client to the server
		while (!this.getClientSymCryptos().containsKey(client+this.serverID)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return client;
	}

	@SuppressWarnings("serial")
	private List<Integer> rangePortsAvailableCA = new ArrayList<Integer>() {{
		   add(5011);
		   add(5015);
	}};
	
	private String certificationAuthorityID;
	
	public String startCAServerConection() {
		String caID = this.tryCAServerConection();
		
		this.installManualCertificate(caID);
		
		this.sendSignatureCertificateRequest(caID);
		this.sendCrypthoCertificateRequest(caID);
		
		return caID;
	}
	
	private String tryCAServerConection(){
		displayer.displayCAServerConectionSection();
		
		this.certificationAuthorityID = conectionManager.tryNewConection(rangePortsAvailableCA, this.hostOfClients);
		
		Boolean success = true;
		if(this.certificationAuthorityID == null) {
			success = false;
		}
		displayer.displayCAServerConectionOutputMessage(success);
		
		return this.certificationAuthorityID;
	}
	
	@Override
	public String startSecureServerService() {
		String serverID = super.trySecureServerService();
		
		return serverID;
	}
	
	public void installManualCertificate(String subject){
		displayer.displayInstallingManualCertificate(subject);
		CertificatePK certificate = certificateManager.loadManualCertficateFromSubject(subject);
		SomeoneSignature signature = new SomeoneSignature(certificate.getBody().getConfig(), subject, certificate.getBody().getHeader().getPublicKey());
		this.addClientSignatureApproach(subject, signature);

		displayer.displayManualCertificate(certificate);		
		displayer.displayInstalledManualCertificate(subject);
	}
	
	protected void sendSignatureCertificateRequest(String caID) {
		displayer.displaySendSignatureCertificateRequest(caID);
		SignatureCertificateRequest cr = new SignatureCertificateRequest(signatureApproach.getConfig(), signatureApproach.getPublicKey(), signatureApproach.getSubject());
		displayer.displayCertificateRequest(cr);
		
		this.sendSecureLayerMsg(caID, cr);
	}
	
	protected void sendCrypthoCertificateRequest(String caID) {
		displayer.displaySendCrypthoCertificateRequest(caID);
		CypherCertificateRequest cr = new CypherCertificateRequest(asymmetricApproach.getConfiguration(), asymmetricApproach.getKeyToShare(), this.serverID);
		displayer.displayCertificateRequest(cr);
		
		this.sendSecureLayerMsg(caID, cr);
	}
	
	protected void sendCypherAsymmetricKeyRequest(String clientID) {
		displayer.displaySendCypherAsymmetricKeyRequest(clientID);
		CypherAsymmetricKeyRequest cakr = new CypherAsymmetricKeyRequest();
		displayer.displayAsymmetricKeyRequest(cakr);
		
		this.sendSecureLayerMsg(clientID, cakr);		
	}
	
	protected void sendSignatureAsymmetricKeyRequest(String clientID) {
		displayer.displaySendSignatureAsymmetricKeyRequest(clientID);
		SignatureAsymmetricKeyRequest sakr = new SignatureAsymmetricKeyRequest();
		displayer.displayAsymmetricKeyRequest(sakr);
		
		this.sendSecureLayerMsg(clientID, sakr);
	}
	
	protected void sendAsymmetricKeyResponse(String clientID, CertificatePK certificate) {
		displayer.displaySendAsymmetricKeyResponse(clientID);
		AsymmetricKeyResponse sakr = new AsymmetricKeyResponse(certificate);
		displayer.displayAsymmetricKeyResponse(sakr);
		
		this.sendSecureLayerMsg(clientID, sakr);
	}
	
	public void sendApplicationMsg(String clientID, String data) {
		ApplicationMsg appmsg = new ApplicationMsg(data, signatureApproach.getSubject(), signatureApproach.getConfig());
		
		this.sendSignedEncrypted(clientID, signatureApproach, appmsg, getClientSymCryptos().get(this.serverID+clientID));
	}
	
	private void sendSymmetricKeyResponse(String client, MyCrypto symmetric , MyCrypto asymmetricforExchange) {
		displayer.displaySendSymmetricKeyResponse(client);
		SymmetricKeyResponse skr = new SymmetricKeyResponse(symmetric.getKeyToShare(), symmetric.getConfiguration().getAlgorithm(), signatureApproach.getSubject(), signatureApproach.getConfig());
		
		this.sendSignedEncrypted(client, signatureApproach, skr, asymmetricforExchange);
	}
	
	protected boolean checkCertificateResponse(CertificateResponse ms){
		boolean checked = false;
		if(this.getClientSignatureApproach().containsKey(ms.getSubject())) {
			SomeoneSignature sm = this.getClientSignatureApproach().get(ms.getSubject());
			
			CertificateResponse aux = new CertificateResponse(ms.getHeader(),ms.getSubject(), ms.getConfig());
			
			if(sm.verifySig(aux.serialize(), ms.getSignature())) {
				checked = true;
				
			}
		}
		
		return checked;
	}
	
	protected boolean checkSignature(SignedNoEncrypted ms, String signature){
		boolean checked = false;
		while(!this.getClientSignatureApproach().containsKey(ms.getSubject())) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		SomeoneSignature sm = this.getClientSignatureApproach().get(ms.getSubject());
		if(sm.verifySig(ms.serialize(), signature)) {
			checked = true;
		}
		return checked;
	}

	@Override
	protected void onNewEncryptedMsg(String clientID, Encrypted ms) {
		MyCrypto crypto;
		switch(ms.getSignedType()) {
			case NOSIGNED:
				// No exists such case in this implementation yet. All encrypted msg are signed
				break;
			case SIGNED:
				SignedEncrypted se = (SignedEncrypted) ms;
				displayer.displayRecievedSignedEncrypted(se.getSubject());
				displayer.displaySignedEncrypted(se);
				switch(se.getAlgorithm().type) {
					case ASYMMETRIC:
						crypto = asymmetricApproach;
						break;
					case SYMMETRIC:
						crypto = this.getClientSymCryptos().get(se.getSubject()+this.serverID);
						break;
					default:
						return;
					
				}
				String noEncrData = crypto.decipher(ms.getDataEncrypted());
				NoEncrypted nms = NoEncrypted.deserialize(noEncrData);
				
				boolean checked = false;
				if(nms != null) {
					checked = true;
					displayer.displayEncryptedChecked(checked, ms.getAlgorithm().name());
					SignedNoEncrypted sne = ((SignedNoEncrypted) nms);				
					sne.setConfig(se.getConfig());
					sne.setSignature(se.getSignature());
					sne.setSubject(se.getSubject());
					
					this.onNewSignedNoEncryptedMsg(clientID, sne);
					
				} else {
					displayer.displayEncryptedChecked(checked, ms.getAlgorithm().name());
				}
				break;
			default:
				break;		
		}
	}
	
	protected void onNewSignedNoEncryptedMsg(String clientID, SignedNoEncrypted ms) {
		boolean checked;
		String signature = ms.getSignature();
		ms.setSignature(null);
		
		if((checked = this.checkSignature(ms, signature))) {
			displayer.displaySignatureChecked(checked);
			ms.setSignature(signature);
			switch(ms.getSnType()) {
				case APPLICATION_MSG:
					displayer.displaySignedNoEncryptedMsg(ms);
					this.onNewApplicationMsg(clientID, (ApplicationMsg) ms);
					break;
				case CERTIFICATE_RESPONSE:
					this.onNewCertificateResponse(clientID, (CertificateResponse) ms);
					break;
				case SYMMETRIC_KEY_RESPONSE:
					this.onNewSymmetricKeyResponse(clientID, (SymmetricKeyResponse) ms);
					break;
				default:
					break;		
			}
		} else {
			displayer.displaySignatureChecked(checked);
		}
	}

	@Override
	protected void onNewSymmetricKeyResponse(String clientID,
			SymmetricKeyResponse ms) {
		displayer.displayRecievedSymmetricResponse(ms.getSubject());
		MyCrypto symmetricApproach = MyCrypto.getInstance(new CryptoConfiguration(ms.getAlg()), false);
		symmetricApproach.setSharedKey(ms.getSymmetricKey());
		this.getClientSymCryptos().put(ms.getSubject()+this.serverID, symmetricApproach);
	}

	@Override
	protected void onNewCertificateResponse(String clientID,
			CertificateResponse ms) {
		switch(ms.getHeader().getCerReqType()) {
			case CYPER_CERTIFICATE_REQUEST:
				displayer.displayRecievedCipherCertificateResponse(ms.getSubject(), clientID);
				this.setAsymmetricApproachCertificate(new CertificatePK(ms, ms.getSignature()));
				break;
			case SIGNATURE_CERTIFICATE_REQUEST:
				displayer.displayRecievedSignatureCertificateResponse(ms.getSubject(), clientID);
				this.setSignatureCertificate(new CertificatePK(ms, ms.getSignature()));
				break;
			default:
				break;
			
		}
	}

	@Override
	protected void onNewAsymmetricKeyRequest(String clientID,
			AsymmetricKeyRequest ms) {
		switch(ms.getAkrType()) {
			case CYPER:
				while(this.asymmetricApproachCertificate == null) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				this.sendAsymmetricKeyResponse(clientID, this.asymmetricApproachCertificate);
				break;
			case SIGNATURE:
				while(this.signatureCertificate == null) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				this.sendAsymmetricKeyResponse(clientID, this.signatureCertificate);
				break;
			default:
				break;
		
		}
	}

	@Override
	protected void onNewAsymmetricKeyResponse(String clientID,
			AsymmetricKeyResponse ms) {
		boolean checked;
		if((checked = this.checkCertificateResponse(ms.getCertificate().getBody()))) {
			switch(ms.getCertificate().getBody().getHeader().getCerReqType()){
				case CYPER_CERTIFICATE_REQUEST:
					displayer.displayRecievedCypherAsymmetricKeyResponse(clientID, ms.getCertificate().getBody().getHeader().getSubject());
					displayer.displayCheckedCertificate(checked);
					CypherCertificateRequest creq = (CypherCertificateRequest)ms.getCertificate().getBody().getHeader();
					MyCrypto crypto = MyCrypto.getInstance(((CypherCertificateRequest)ms.getCertificate().getBody().getHeader()).getConfig(), false);
					crypto.setSharedKey(creq.getPublicKey());
					
					this.addClientAsymCryptos(creq.getSubject(), crypto);
					
					recievedCypherForExchangingSymKey = true;
					break;
				case SIGNATURE_CERTIFICATE_REQUEST:
					displayer.displayRecievedSignatureAsymmetricKeyResponse(clientID, ms.getCertificate().getBody().getHeader().getSubject());
					displayer.displayCheckedCertificate(checked);
					SignatureCertificateRequest sreq = ((SignatureCertificateRequest)ms.getCertificate().getBody().getHeader());
					SomeoneSignature some = new SomeoneSignature(sreq.getConfig(), sreq.getSubject(), sreq.getPublicKey());
					
					this.addClientSignatureApproach(sreq.getSubject(), some);
					
					recievedSignature = true;
					break;
				default:
					break;
			}
		}
	}

	@Override
	protected void onNewApplicationMsg(String clientID, ApplicationMsg ms) {
		displayer.displayRecievedAppMsg(ms.getSubject());
		
		this.newAppMsgNotify(ms.getSubject(), ms.getData());
	}

	@Override
	protected void onNewCertificateRequest(String clientID,
			CertificateRequest ms) {}
	
	@Override
	protected void onNewCertificate(String clientID, CertificatePK ms) {}

	@Override
	protected void onNewSymmetricKeyRequest(String clientID,
			SymmetricKeyRequest ms) {}
	
	@Override
	public void addListener(ISecurityNodeListener observer) {
		this.listenersSecurityNode.add(observer);		
	}

	@Override
	public void certificateResponseNotify(String clientID) {
		Iterator<ISecurityNodeListener> it = listenersSecurityNode.iterator();
		
		while(it.hasNext()){
			ISecurityNodeListener next = it.next();
			next.onCertificateResponse(clientID);
		}	
		
	}

	@Override
	public void addListener(IAppRawMsgListener observer) {
		this.listenersAppRaw.add(observer);		
	}

	@Override
	public void newAppMsgNotify(String clientID, String m) {
		Iterator<IAppRawMsgListener> it = listenersAppRaw.iterator();
				
		while(it.hasNext()){
			IAppRawMsgListener next = it.next();
			next.onNewAppMsg(clientID, m);
		}
	}

	public MyCrypto getSymmetricApproach() {
		return symmetricApproach;
	}

	public void setSymmetricApproach(MyCrypto symmetricApproach) {
		this.symmetricApproach = symmetricApproach;
	}

	public MyCrypto getAsymmetricApproach() {
		return asymmetricApproach;
	}

	public void setAsymmetricApproach(MyCrypto asymmetricApproach) {
		this.asymmetricApproach = asymmetricApproach;
	}

	public Map<String, MyCrypto> getClientSymCryptos() {
		return clientSymCryptos;
	}

	public void setClientSymCryptos(Map<String, MyCrypto> clientCryptos) {
		this.clientSymCryptos = clientCryptos;
	}
	
	public void addClientSymCryptos(String client, MyCrypto crypto) {
		this.getClientSymCryptos().put(client, crypto);
	}

	public Map<String, SomeoneSignature> getClientSignatureApproach() {
		return clientSignatureApproach;
	}

	public void setClientSignatureApproach(Map<String, SomeoneSignature> clientSignatureApproach) {
		this.clientSignatureApproach = clientSignatureApproach;
	}
	
	public void addClientSignatureApproach(String client, SomeoneSignature clientSignature) {
		this.getClientSignatureApproach().put(client, clientSignature);
	}

	@Override
	public void onNewConection(String clientID) {}

	@Override
	public void onDisconection(String clientID) {}

	public CertificatePK getSignatureCertificate() {
		return signatureCertificate;
	}

	public void setSignatureCertificate(CertificatePK signatureCertificate) {
		this.signatureCertificate = signatureCertificate;
	}

	public CertificatePK getAsymmetricApproachCertificate() {
		return asymmetricApproachCertificate;
	}

	public void setAsymmetricApproachCertificate(
			CertificatePK asymmetricApproachCertificate) {
		this.asymmetricApproachCertificate = asymmetricApproachCertificate;
	}

	public Map<String, MyCrypto> getClientAsymCryptos() {
		return clientAsymCryptos;
	}

	public void setClientAsymCryptos(Map<String, MyCrypto> clientAsymCryptos) {
		this.clientAsymCryptos = clientAsymCryptos;
	}
	
	public void addClientAsymCryptos(String client, MyCrypto crypto) {
		this.getClientAsymCryptos().put(client, crypto);
	}
}
