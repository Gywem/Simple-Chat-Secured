package projectNS.library.security;

import java.util.ArrayList;

import projectNS.library.security.persistance.model.ApplicationMsg;
import projectNS.library.security.persistance.model.AsymmetricKeyRequest;
import projectNS.library.security.persistance.model.AsymmetricKeyResponse;
import projectNS.library.security.persistance.model.CertificatePK;
import projectNS.library.security.persistance.model.CertificateRequest;
import projectNS.library.security.persistance.model.CertificateResponse;
import projectNS.library.security.persistance.model.Encrypted;
import projectNS.library.security.persistance.model.SignatureCertificateRequest;
import projectNS.library.security.persistance.model.SignedNoEncrypted;
import projectNS.library.security.persistance.model.SymmetricKeyRequest;
import projectNS.library.security.persistance.model.SymmetricKeyResponse;

public class CASecurityController extends SecurityController {
	
	public static CASecurityController getInstance() {
		if(Singleton == null) return new CASecurityController();
		else return (CASecurityController) Singleton;
	}
	
	protected CASecurityController(){
		super(SecurityControllerType.CERTIFICATION_AUTHORITY);
		this.rangePortsAvailableNodes = new ArrayList<Integer>() {
			private static final long serialVersionUID = 1L;

		{
			   add(5011);
			   add(5015);
		}};
	}
	
	@Override
	public String startSecureServerService() {
		String serverID = trySecureServerService();
		
		this.shareManualCertificate(serverID);
		return serverID;
	}
	
	public void shareManualCertificate(String serverID){
		CertificateRequest cr = new SignatureCertificateRequest(signatureApproach.getConfig(), signatureApproach.getPublicKey(), serverID);
		CertificateResponse cresp =new CertificateResponse(cr, serverID, signatureApproach.getConfig());
		String signature = signatureApproach.signData(cresp.serialize());
		cresp.setSignature(signature);
		CertificatePK certificate = new CertificatePK(cresp, signature);
		
		displayer.displaySharingManualCertificate();
		displayer.displayManualCertificate(certificate);
		certificateManager.saveManualCertificate(certificate);
		displayer.displaySharedManualCertificate();
	}
	
	@Override
	protected void onNewCertificateRequest(String clientID,
			CertificateRequest ms) {
		displayer.displayNewCertificateRequest(ms.getSubject());
		
		this.sendCertificationResponse(clientID, ms);
	}
	
	private void sendCertificationResponse(String clientID, CertificateRequest ms) {
		displayer.displaySendCertificateResponse(ms.getSubject(),clientID);
		
		CertificateResponse cresp = new CertificateResponse(ms, signatureApproach.getSubject(), signatureApproach.getConfig());
		this.sendSignedNoEncrypted(clientID, signatureApproach, (SignedNoEncrypted) cresp);
		displayer.displaySentCertificateResponse(ms.getSubject(), clientID);
	}

	@Override
	protected void onNewSymmetricKeyResponse(String clientID,
			SymmetricKeyResponse ms) {}

	@Override
	protected void onNewCertificateResponse(String clientID,
			CertificateResponse ms) {}

	@Override
	protected void onNewAsymmetricKeyResponse(String clientID,
			AsymmetricKeyResponse ms) {}

	@Override
	protected void onNewApplicationMsg(String clientID, ApplicationMsg ms) {}

	@Override
	protected void onNewSymmetricKeyRequest(String clientID,
			SymmetricKeyRequest ms) {}

	@Override
	protected void onNewAsymmetricKeyRequest(String clientID,
			AsymmetricKeyRequest ms) {}
	
	@Override
	protected void onNewEncryptedMsg(String clientID, Encrypted ms) {}

	@Override
	protected void onNewCertificate(String clientID, CertificatePK ms) {}

	@Override
	protected void onNewSignedNoEncryptedMsg(String clientID,
			SignedNoEncrypted ms) {}

}
