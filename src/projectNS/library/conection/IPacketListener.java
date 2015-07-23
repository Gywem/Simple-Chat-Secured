/*
 * Project Network Security
 * author: Ignacio José Codoñer Gil (igcogi@gmail.com)
 *  - st number : 0416040
 * */

package projectNS.library.conection;

public interface IPacketListener {
	public void onNewPacket(String clientID, String m);
}
