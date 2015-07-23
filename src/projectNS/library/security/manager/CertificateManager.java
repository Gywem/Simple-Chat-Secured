package projectNS.library.security.manager;

import projectNS.library.security.persistance.dao.ManualCertificateDao;
import projectNS.library.security.persistance.model.CertificatePK;

public class CertificateManager {
	private static CertificateManager Singleton;
	
	private ManualCertificateDao manualCertificateDao;
	
	public static CertificateManager getInstance(){
		if(Singleton == null) return new CertificateManager();
		else return Singleton;
	}
	
	private CertificateManager(){
		this.manualCertificateDao = ManualCertificateDao.getInstance();		
	}
	
	public void saveManualCertificate(CertificatePK certificate){
		manualCertificateDao.save(certificate);
	}
	
	public CertificatePK loadManualCertficateFromSubject(String subject) {
		return manualCertificateDao.readBySubject(subject);
	}
}
