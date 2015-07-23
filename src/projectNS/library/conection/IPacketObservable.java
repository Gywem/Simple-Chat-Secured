/*
 * Project Network Security
 * author: Ignacio José Codoñer Gil (igcogi@gmail.com)
 *  - st number : 0416040
 * */

package projectNS.library.conection;

import projectNS.library.conection.model.Client;

public interface IPacketObservable {
	public void addListener(IPacketListener observer);
	public void newMessageUpdate(Client client, String m);
}
