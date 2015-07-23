package projectNS.library.mycrypto;



public class SomeoneSignature extends MySignature {
	
	public SomeoneSignature(SignatureConfiguration config, String subject, String publicKey) {
		super(config, subject);
		this.setPublicKey(publicKey);
	}
	
	@Override
	public String toString() {
		String result = super.toString();		
		return result;
	}
	
}
