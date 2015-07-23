package projectNS.library.security.persistance.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import projectNS.library.security.persistance.model.CertificatePK;

public class ManualCertificateDao {
	private static ManualCertificateDao Singleton;
	
	public static ManualCertificateDao getInstance(){
		if(Singleton == null) return new ManualCertificateDao();
		else return Singleton;
	}
	
	private ManualCertificateDao(){
		
	}
	
	public CertificatePK readBySubject(String subject){
		File file = new File(subject+".txt");
		
		FileReader fr;
		try {
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			String linea;
			CertificatePK c = null;
			while((linea=br.readLine())!=null) {
				c = CertificatePK.deserialize(linea);
			}
			fr.close();
			
			return c;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void save(CertificatePK certificate){
		File file = new File(certificate.getBody().getSubject()+".txt");
		try {
			FileWriter w;
			w = new FileWriter(file);
			PrintWriter wr = new PrintWriter(w);
			
			String toPrint = certificate.serialize();
			wr.println(toPrint);
			
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
