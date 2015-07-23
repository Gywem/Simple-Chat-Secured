package projectNS.manager;

import projectNS.persistance.model.AppMSG;

public interface IAppMsgListener {
	public void onAppMsg(AppMSG msg);
}
