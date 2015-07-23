/*
 * Project Network Security
 * author: Ignacio José Codoñer Gil (igcogi@gmail.com)
 *  - st number : 0416040
 * */

package projectNS.library.conection;

import projectNS.library.conection.model.Client;

public interface IConectionObservable {
	public void addListener(IConectionListener observer);
	public void newConectionUpdate(Client client);
	public void newDisconectionUpdate(Client client);
}
