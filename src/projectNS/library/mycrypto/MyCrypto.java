package projectNS.library.mycrypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;

public abstract class MyCrypto {	
	public enum CryptoType {
		SYMMETRIC,
		ASYMMETRIC
	};
	
	public enum CryptoAlgorithm {
		DES3 (0, CryptoType.SYMMETRIC,  "DESede", "ECB", "PKCS5Padding", 64*3),
		RSA(1, CryptoType.ASYMMETRIC, "RSA", "ECB", "PKCS1Padding", 2048),
		AES(2, CryptoType.SYMMETRIC, "AES", "ECB", "PKCS5Padding", 128),
		ARC4(3, CryptoType.SYMMETRIC, "RC4", "NONE", "NoPadding", 128),
		BLOWFISH(4, CryptoType.SYMMETRIC, "Blowfish", "NONE", "NoPadding", 128),
		MyDES3(5, CryptoType.SYMMETRIC, "DESede", "ECB", "PKCS5Padding", 64*3),
		MyARC4(6, CryptoType.SYMMETRIC, "RC4", "NONE", "NoPadding", 128/*1684*/);

		public int number;
		public CryptoType type;
		public String text;
		public String defaultMode;
		public String defaultPadding;
		public int defaultKeySize;
		
		CryptoAlgorithm(int n, CryptoType type, String text, String defaultMode, String defaultPadding, int defaultKeySize){
			this.number = n;
			this.type = type;
			this.text = text;
			this.defaultMode = defaultMode;
			this.defaultPadding = defaultPadding;
			this.defaultKeySize = defaultKeySize;
		}

		public String getAlgorithmString() {
			return this.text;
		}
		
		public static CryptoAlgorithm getfromNumber(int n){
			switch(n){
				case 0:
					return CryptoAlgorithm.DES3;
				case 1:
					return CryptoAlgorithm.RSA;
				case 2:
					return CryptoAlgorithm.AES;
				case 3:
					return CryptoAlgorithm.ARC4;
				case 4:
					return CryptoAlgorithm.BLOWFISH;
				case 5:
					return CryptoAlgorithm.MyDES3;
				case 6:
					return CryptoAlgorithm.MyARC4;
				default:
					return null;
			}
		}		
	};
	
	protected CryptoConfiguration configuration;
	protected Cipher cipher;
	
	private IvParameterSpec ivspec;
	
	public static MyCrypto getInstance(CryptoConfiguration configuration, boolean generateKeys) {
		MyCrypto aux;
		if(configuration.getAlgorithm() == CryptoAlgorithm.MyDES3){
			aux = new MyDES();
		} if(configuration.getAlgorithm() == CryptoAlgorithm.MyARC4){
			aux = new MyARC4();
		} else {
			switch(configuration.getAlgorithm().type){
				case SYMMETRIC:
					aux = new DefaultSymmetricAlgorithm();
					break;
				case ASYMMETRIC:
					aux = new DefaultAsymmetricAlgorithm();
					break;
				default:
					return null;
			}
		}
		
		aux.setConfiguration(configuration);		
		if(generateKeys) aux.generateKey();
		
		return aux;
	}
	
	protected String defaultCipher(String rawData){
		try {
			Cipher cipher;
			cipher = Cipher.getInstance(this.getConfiguration().getComplete());
			
			cipher.init(Cipher.ENCRYPT_MODE, getKeyToCipher());
			
			byte[] encoded = rawData.getBytes("UTF8");
			return new Base64().encodeToString(cipher.doFinal(encoded));
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected String defaultDecipher(String rawData){
		try {
			Cipher cipher;
			cipher = Cipher.getInstance(this.getConfiguration().getComplete());
			
			cipher.init(Cipher.DECRYPT_MODE, getKeyToDecipher());
			
			byte[] decoded = Base64.decodeBase64(rawData);
			return new String(cipher.doFinal(decoded), "UTF8");
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void setConfiguration(CryptoConfiguration configuration) {
		this.configuration = configuration;
	}
	
	public CryptoConfiguration getConfiguration() {
		return this.configuration;
	}
	
	public abstract String cipher(String rawData);
	public abstract String decipher(String rawData);
	public abstract Key getKeyToCipher();
	public abstract Key getKeyToDecipher();
	public abstract String getKeyToShare();
	public abstract void setSharedKey(String k);
	public abstract void generateKey();

	public IvParameterSpec getIvspec() {
		return ivspec;
	}

	public void setIvspec(IvParameterSpec ivspec) {
		this.ivspec = ivspec;
	}

	@Override
	public String toString() {
		String result = "";
		
		result += "\t### Algorithm :"+this.getConfiguration().getAlgorithm().getAlgorithmString()+"\n";
		result += "\t### Mode :"+this.getConfiguration().getMode()+"\n";
		result += "\t### Padding :"+this.getConfiguration().getPadding()+"\n";
		
		return result;
	}
}
