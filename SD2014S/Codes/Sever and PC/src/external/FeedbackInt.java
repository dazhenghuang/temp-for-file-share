package external;

import java.rmi.*;

public interface FeedbackInt extends Remote {
	public void hash1(String mess) throws RemoteException;
	public void hash2(String mess) throws RemoteException;
	public byte[] getHash1() throws RemoteException;
	public byte[] getHash2() throws RemoteException;
	public void endServer() throws RemoteException;
}