package projectNS.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import projectNS.library.security.IAppRawMsgListener;
import projectNS.persistance.model.AppMSG;

public class AppMSGManager implements IAppMsgObserver, IAppRawMsgListener {
	private static AppMSGManager Singleton = AppMSGManager.getInstance();
	
	private AppMSGManager(){		
		this.appMsgListeners = new ArrayList<IAppMsgListener>();
		this.appMSGs = new ArrayList<AppMSG>();
	}
	
	public static AppMSGManager getInstance() {
		if(Singleton == null) return new AppMSGManager();
		else return AppMSGManager.Singleton;
	}
	
	private List<IAppMsgListener> appMsgListeners;
	private List<AppMSG> appMSGs;
	
	public List<AppMSG> getAppMSGs() {
		return appMSGs;
	}

	public void setAppMSGs(List<AppMSG> appMSGs) {
		this.appMSGs = appMSGs;
	}

	@Override
	public void addListener(IAppMsgListener observer) {
		appMsgListeners.add(observer);
	}

	@Override
	public void newAppMsg(AppMSG msg) {
		Iterator<IAppMsgListener> it = this.appMsgListeners.iterator();
		
		while(it.hasNext()){
			IAppMsgListener next = it.next();
			next.onAppMsg(msg);
		}
	}
	
	public AppMSG createAppMSG(String clientID, String m) {
		AppMSG appmsg = new AppMSG(clientID, m);
		this.addAppMSG(appmsg);
		
		return appmsg;
	}
	
	public AppMSG addAppMSG(AppMSG appmsg) {
		appMSGs.add(appmsg);
		
		return appmsg;
	}

	@Override
	public void onNewAppMsg(String clientID, String m) {
		this.newAppMsg(new AppMSG(clientID, m));
	}

}
