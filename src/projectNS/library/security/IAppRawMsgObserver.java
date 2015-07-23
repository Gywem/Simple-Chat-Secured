package projectNS.library.security;

public interface IAppRawMsgObserver {
	public void addListener(IAppRawMsgListener observer);
	public void newAppMsgNotify(String clientID, String m);
}
