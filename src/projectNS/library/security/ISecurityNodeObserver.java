package projectNS.library.security;

public interface ISecurityNodeObserver {
	public void addListener(ISecurityNodeListener observer);
	public void certificateResponseNotify(String clientID);
}
