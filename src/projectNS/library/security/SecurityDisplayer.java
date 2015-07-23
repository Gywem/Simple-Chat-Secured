package projectNS.library.security;

import projectNS.library.mycrypto.CryptoConfiguration;
import projectNS.library.mycrypto.MyCrypto.CryptoType;
import projectNS.library.mycrypto.MySignature.HashType;
import projectNS.library.mycrypto.MySignature.SignatureAlgorithm;
import projectNS.library.mycrypto.SignatureConfiguration;
import projectNS.library.mycrypto.MyCrypto.CryptoAlgorithm;
import projectNS.library.security.persistance.model.ApplicationMsg;
import projectNS.library.security.persistance.model.AsymmetricKeyRequest;
import projectNS.library.security.persistance.model.AsymmetricKeyResponse;
import projectNS.library.security.persistance.model.CertificatePK;
import projectNS.library.security.persistance.model.CertificateRequest;
import projectNS.library.security.persistance.model.CertificateResponse;
import projectNS.library.security.persistance.model.CypherCertificateRequest;
import projectNS.library.security.persistance.model.SignatureCertificateRequest;
import projectNS.library.security.persistance.model.SignedEncrypted;
import projectNS.library.security.persistance.model.SignedNoEncrypted;
import projectNS.library.security.persistance.model.SymmetricKeyRequest;
import projectNS.library.security.persistance.model.SymmetricKeyResponse;

public class SecurityDisplayer {
	private static SecurityDisplayer Singleton;

	public static SecurityDisplayer getInstance() {
		if(Singleton == null) return new SecurityDisplayer();
		else return Singleton;
	}
	
	private SecurityDisplayer() {
		
	}
	
	public void displayGetServerPortSection(){
		System.out.println();
		System.out.println("# Setting up secure server...");
	}
	
	public void displayServerOutputMessage(Boolean success, String serverID){
		if(success){
			System.out.println("## Secure server creation successfully at "+serverID);
		} else {
			System.out.println("## Something wrong happened with the secure server creation.");
		}
	}

	public void displayGetClientPortSection() {
		System.out.println();
		System.out.println("# Trying to conect to a secure client...");
	}
	
	public void displayCAServerConectionSection() {
		System.out.println();
		System.out.println("# Trying to conect to a CA secure server...");
	}
	
	public void displayCAServerConectionOutputMessage(Boolean success) {
		if(success){
			System.out.println("## CA conection successfully.");
		} else {
			System.out.println("## Something wrong happened with the CA conection.");
		}
		
	}

	public void displayClientOutputMessage(Boolean success, String clientID) {
		if(success){
			System.out.println("\n## Secure client conection to "+clientID+" successfully.");
		} else {
			System.out.println("\n## Something wrong happened with the secure client conection.");
		}
	}

	public void displaySendSignatureCertificateRequest(String caID) {
		System.out.println("\n# Requesting a signature certificate to the CA: "+caID);
	}
	
	public void displaySendCrypthoCertificateRequest(String caID) {
		System.out.println("\n# Requesting a crypher certificate to the CA: "+caID);
	}

	public void displayNewCertificateRequest(String clientID) {
		System.out.println("\n# A certification request has arrived from: "+clientID);		
	}

	public void displaySendCertificateResponse(String subject, String clientID) {
		System.out.println("\n# Sending a certification response to: "+subject+" ("+clientID+")");
	}
	
	public void displaySentCertificateResponse(String subject, String clientID) {
		System.out.println("## Sent certification response to: "+subject+" ("+clientID+")");
	}

	public void displayCertificateResponse(CertificateResponse cresp) {
		String toPrint = "", pre = "\t";
		
		toPrint += this.stringCertificateResponse(pre, cresp);
		
		System.out.println(toPrint);		
	}
	
	public String stringCertificateResponse(String pre, CertificateResponse cresp) {
		String toPrint = "", n= "\n";
		
		toPrint += pre+"## Certification Response ##"+n;
		toPrint += this.stringCertificateRequest(pre+"\t", cresp.getHeader())+n;
		toPrint += pre+"## ##";
		
		return toPrint;	
	}
	
	public String stringApplicationMsg(String pre, ApplicationMsg cresp) {
		String toPrint = "", n= "\n";
		
		toPrint += pre+"## Application Message ##"+n;
		toPrint += pre+"## Data:"+cresp.getData()+n; 
		toPrint += pre+"## ##";
		
		return toPrint;	
	}
	
	public void displaySignedNoEncryptedMsg(SignedNoEncrypted cresp) {
		String toPrint = "", pre = "\t";
		
		toPrint += this.stringSignedNoEncryptedMsg(pre, cresp);
		
		System.out.println(toPrint);		
	}
	
	private String stringSignedNoEncryptedMsg(String pre, SignedNoEncrypted sign) {
		String toPrint = "", n= "\n";
		
		toPrint += pre+"## Signature Information ##"+n;
		toPrint += pre+"## Subject: "+sign.getSubject()+n;
		toPrint += pre+"## Configuration: Algorithm - "+sign.getConfig().getAlgorithm().name()+"; Hash - "+sign.getConfig().getHash().name()+";"+n;
		switch(sign.getSnType()) {
			case APPLICATION_MSG:
				toPrint += this.stringApplicationMsg(pre, (ApplicationMsg) sign)+n;
				break;
			case CERTIFICATE_RESPONSE:
				toPrint += this.stringCertificateResponse(pre+"\t", (CertificateResponse) sign)+n;
				break;
			case SYMMETRIC_KEY_RESPONSE:
				toPrint += pre+"\t"+"## Symmetric Key Response ##"+n;
				toPrint += this.stringSymmetricKeyResponse(pre+"\t", (SymmetricKeyResponse) sign);
				toPrint += pre+"\t"+"## ##";
				break;
			default:
				break;
		}
		toPrint += pre+"## Signature: "+sign.getSignature()+n;
		toPrint += pre+"## ##";
		
		return toPrint;		
	}
	
	public void displayCertificateRequest(CertificateRequest creq) {
		String toPrint = "", pre = "\t";
		
		toPrint += this.stringCertificateRequest(pre, creq);
		
		System.out.println(toPrint);
	}
	
	public String stringCertificateRequest(String pre, CertificateRequest creq) {
		String toPrint = "", n= "\n";
		
		switch(creq.getCerReqType()){
			case CYPER_CERTIFICATE_REQUEST:
				toPrint += pre+"## Cypher Certification Request ##"+n;
				toPrint += this.stringCypherCertificateRequest(pre, (CypherCertificateRequest) creq)+n;
				break;
			case SIGNATURE_CERTIFICATE_REQUEST:
				toPrint += pre+"## Signature Certification Request ##"+n;
				toPrint += this.stringSignatureCertificateRequest(pre, (SignatureCertificateRequest) creq)+n;
				break;
		default:
			break;
		
		}
		toPrint += pre+"## Subject: "+creq.getSubject()+n;
		toPrint += pre+"## Public Key: "+creq.getPublicKey()+n;
		toPrint += pre+"## ##";
		
		return toPrint;		
	}

	private String stringCypherCertificateRequest(String pre,
			CypherCertificateRequest creq) {
		String toPrint = "";
		
		toPrint += pre+this.stringCryptoConfiguration(creq.getConfig());
		
		return toPrint;	
	}

	private String stringCryptoConfiguration(CryptoConfiguration config) {
		String toPrint = "";
		toPrint += "## Configuration: Algorithm - "+config.getAlgorithm().name()+"; Mode - "+config.getMode()+"; Padding - "+config.getPadding()+"; Key Size - "+config.getKeyLength()+";";
		return toPrint;
	}

	public void displaySignatureCertificateRequest(SignatureCertificateRequest creq) {
		String toPrint = "", pre = "\t";
		
		toPrint += this.stringSignatureCertificateRequest(pre, creq);
		
		System.out.println(toPrint);
	}
	
	public String stringSignatureCertificateRequest(String pre, SignatureCertificateRequest creq) {
		String toPrint = "";
		
		toPrint += pre+this.stringSignatureConfiguration(creq.getConfig());
		
		return toPrint;		
	}
	
	public String stringSignatureConfiguration(SignatureConfiguration config) {
		String toPrint = "";
		toPrint += "## Configuration: Algorithm - "+config.getAlgorithm().name()+"; Hash - "+config.getHash().name()+"; Key Size - "+config.getKeyLength()+";";
		
		return toPrint;		
	}

	public void displaySharingManualCertificate() {		
		System.out.println("\n# Saving public key certificate");
		
	}

	public void displaySharedManualCertificate() {		
		System.out.println("## Saved. For now on other nodes may install it in their servers.");
		
	}

	public void displayInstallingManualCertificate(String caID) {
		System.out.println("\n# Installing manual certificate from "+caID);
	}

	public void displayInstalledManualCertificate(String caID) {
		System.out.println("## Installed manual certificate from "+caID);
	}

	public void displayRecievedSignatureCertificateResponse(String subject, String clientID) {
		System.out.println("\n# Recieved a signature certificate from the CA "+subject+" ("+clientID+")");
	}
	
	public void displayRecievedCipherCertificateResponse(String subject, String clientID) {
		System.out.println("\n# Recieved a cipher certificate from the CA "+subject+" ("+clientID+")");
	}

	public void displayCheckedCertificate(boolean checked) {
		if(checked){
			System.out.println("## Digital certificate valid");
		} else {
			System.out.println("## Digital certificate invalid. It may have been modified through the path.");
		}		
	}

	public void displayManualCertificate(CertificatePK certificate) {
		String toPrint = "", n= "\n", pre="\t";
		
		toPrint += pre+"## Manual Certificate ##"+n;
		toPrint += this.stringCertificatePK(pre, certificate);
		toPrint += pre+"## ##";
		
		System.out.println(toPrint);		
	}
	
	public void displayAsymmetricKeyRequest(AsymmetricKeyRequest akr) {
		String toPrint = "", pre = "\t";
		
		toPrint += this.stringAsymmetricKeyRequest(pre, akr);
		
		System.out.println(toPrint);
	}
	
	public String stringAsymmetricKeyRequest(String pre, AsymmetricKeyRequest akr) {
		String toPrint = "", n= "\n";
	
		switch(akr.getAkrType()){
			case CYPER:
				toPrint += pre+"## Cipher Asymmertic Key Request ##"+n;
				break;
			case SIGNATURE:
				toPrint += pre+"## Signature Asymmertic Key Request ##"+n;
				break;
			default:
				break;		
		}
		toPrint += pre+"## ##";
		
		return toPrint;		
	}
	
	public void displaySendCypherAsymmetricKeyRequest(String clientID) {
		System.out.println("\n# Requesting the cypher certificate to the client: "+clientID);
	}
	
	public void displaySendSignatureAsymmetricKeyRequest(String clientID) {
		System.out.println("\n# Requesting the signature certificate to the client: "+clientID);
	}

	public void displaySendAsymmetricKeyResponse(String clientID) {
		System.out.println("\n# Sending a asymmetric key certificate to a client "+"(socket: "+clientID+")");
	}

	public void displayAsymmetricKeyResponse(AsymmetricKeyResponse sakr) {
		String toPrint = "", pre = "\t";
		
		toPrint += this.stringAsymmetricKeyResponse(pre, sakr);
		
		System.out.println(toPrint);
	}
	
	public String stringAsymmetricKeyResponse(String pre, AsymmetricKeyResponse sakr) {
		String toPrint = "", n= "\n";
		
		toPrint += pre+"## Asymmetric Key Certificate ##"+n;
		toPrint += this.stringCertificatePK(pre, sakr.getCertificate());
		toPrint += pre+"## ##";
		
		return toPrint;		
	}

	private String stringCertificatePK(String pre, CertificatePK certificate) {
		String toPrint = "", n= "\n";
		
		toPrint += pre+"## Certificate"+n;
		toPrint += this.stringCertificateRequest(pre+"\t", certificate.getBody().getHeader())+n;
		toPrint += pre+"## Subject CA: "+certificate.getBody().getSubject()+n;
		toPrint += pre+this.stringSignatureConfiguration(certificate.getBody().getConfig())+n;
		toPrint += pre+"## Signature CA: "+certificate.getSignature()+n;
		
		return toPrint;
	}

	public void displayRecievedCypherAsymmetricKeyResponse(String clientID,
			String subject) {
		System.out.println("\n# Recieved a cipher certificate by "+subject+" ("+clientID+")");
	}
	
	public void displayRecievedSignatureAsymmetricKeyResponse(String clientID,
			String subject) {
		System.out.println("\n# Recieved a signature certificate by "+subject+" ("+clientID+")");
	}

	public void displaySendSymmetricKeyRequest(String clientID) {
		System.out.println("\n# Requesting a symmetric key to the client "+clientID);
	}

	public void displaySymmetricKeyRequest(SymmetricKeyRequest sreq) {
		String toPrint = "", pre = "\t", n= "\n";

		toPrint += pre+"## Symmetric Key Request ##"+n;
		toPrint += pre+"## ##";
		
		System.out.println(toPrint);
	}
	
	public void displayRecievedSymmetricRequest(String clientID) {
		System.out.println("\n# Recieved a symmetric key request by (socket : "+clientID+")");
	}

	public void displaySymmetricKeyResponse(SymmetricKeyResponse skr) {
		String toPrint = "", pre = "\t";
		
		toPrint += this.stringSymmetricKeyResponse(pre, skr);
		
		System.out.println(toPrint);
	}
	
	private String stringSymmetricKeyResponse(String pre, SymmetricKeyResponse skr) {
		String toPrint = "", n= "\n";
		
		toPrint += pre+"## Symmetric Key : "+skr.getSymmetricKey()+n;
		
		return toPrint;
	}

	public void displaySignatureChecked(boolean checked) {
		if(checked) System.out.println("## Signature checked as correct.");
		else System.out.println("## Signature checked as incorrect.");
	}

	public void displayRecievedSymmetricResponse(String subject) {
		System.out.println("\n# Recieved a symmetric key from "+subject);
	}

	public void displaySendSymmetricKeyResponse(String subject) {
		System.out.println("\n# Sending a symmetric key to a client "+subject);
	}

	public void displaySymmetricApproachCreated(String subject) {
		System.out.println("\n# Symmetric key approach created to communicate securely with "+subject);
		
	}

	public void displayEncryptedChecked(boolean checked, String algorithm) {
		if(checked) System.out.println("## Encryption checked as correct. ("+algorithm+")");
		else System.out.println("## Something bad ocurred. Encryption checked as incorrect.");		
	}

	public void displayRecievedAppMsg(String subject) {
		System.out.println("\n# Recieved an application msg from "+subject);
	}

	public void displayRecievedSignedEncrypted(String subject) {
		System.out.println("\n# Recieved an encrypted msg signed by "+subject);
	}

	public void displaySignedEncrypted(SignedEncrypted se) {
		String toPrint = "", pre = "\t";
		
		toPrint += this.stringSignedEncrypted(pre, se);
		
		System.out.println(toPrint);
	}
	
	public String stringSignedEncrypted(String pre, SignedEncrypted sakr) {
		String toPrint = "", n= "\n";
		
		toPrint += pre+"## Encrypted and Signed msg ##"+n;
		toPrint += pre+"## Algorithm: "+sakr.getAlgorithm().name()+n;
		toPrint += pre+"## Data: "+sakr.getDataEncrypted()+n;
		toPrint += pre+"## Signature Information ##"+n;
		toPrint += pre+"## Subject: "+sakr.getSubject()+n;
		toPrint += pre+this.stringSignatureConfiguration(sakr.getConfig())+n;
		toPrint += pre+"## ##"+n;
		toPrint += pre+"## Signature: "+sakr.getSignature()+n;
		toPrint += pre+"## ##";
		
		return toPrint;		
	}

	public void displayCryptoAlgorithmSelection(CryptoType type) {
		String toPrint = "", pre = "\t", n="\n";
		
		toPrint += pre+"## Please, input the number of the algorithm to use ##"+n;
		CryptoAlgorithm[] all = CryptoAlgorithm.values();
		for(int i = 0; i < all.length; ++i) {
			if(all[i].type == type) toPrint += pre+"## "+all[i].number+" | "+all[i].name()+" "+" "+n;
		}
		System.out.println(toPrint);
		System.out.print("Selection: ");
	}

	public void displayGetAsymmetricKeyExchange() {
		System.out.println("\n# Selection of the Asymmetric Key approach for exchanging the symmetric key");
	}

	public void displayGetSymmetricEncrypt() {
		System.out.println("\n# Selection of the Symmetric Key approach for exchanging secure data");
	}

	public void displaySignatureSelection() {
		String toPrint = "", pre = "\t", n="\n";
		
		toPrint += pre+"## Please, input the number of the algorithm and hash to use for the signature ##"+n;
		toPrint += pre+"## "+0+" | "+SignatureAlgorithm.DSA.name()+" - "+HashType.SHA1.name()+" "+n;
		toPrint += pre+"## "+1+" | "+SignatureAlgorithm.RSA.name()+" - "+HashType.MD5.name()+" "+n;
		toPrint += pre+"## "+2+" | "+SignatureAlgorithm.RSA.name()+" - "+HashType.SHA1.name()+" "+n;
		toPrint += pre+"## "+3+" | "+SignatureAlgorithm.RSA.name()+" - "+HashType.SHA256.name()+" "+n;
		toPrint += pre+"## ##"+n;
		
		System.out.println(toPrint);
		System.out.print("Selection: ");
	}

	public void displayGetSignatureMethod() {
		System.out.println("\n# Selection of the Asymmetric Key Approach for personal signature");		
	}

	public void displaySignatureConfigurationOptionChosen(SignatureConfiguration config) {
		String toPrint = "", pre = "\t", n="\n";
		
		System.out.println("\n## Selected option");
		toPrint += pre+this.stringSignatureConfiguration(config)+n;		
		
		System.out.println(toPrint);
	}
	
	public void displayCryptoConfigurationOptionChosen(CryptoConfiguration config) {
		String toPrint = "", pre = "\t", n="\n";
		
		System.out.println("\n## Selected option");
		toPrint += pre+this.stringCryptoConfiguration(config)+n;		
		
		System.out.println(toPrint);
	}

	public void displaySendSignedNoEncrypted(String clientID) {
		System.out.println("\n# Sending a signed message to "+clientID);
	}

	public void displaySendSignedEncrypted(String clientID) {
		System.out.println("\n# Sending an encypted and signed message to "+clientID);
	}
	
	

}
