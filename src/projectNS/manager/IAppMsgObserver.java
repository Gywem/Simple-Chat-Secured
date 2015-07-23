package projectNS.manager;

import projectNS.persistance.model.AppMSG;

public interface IAppMsgObserver {
	public void addListener(IAppMsgListener observer);
	public void newAppMsg(AppMSG msg);
}
