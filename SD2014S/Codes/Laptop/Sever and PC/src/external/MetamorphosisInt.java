package external;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface MetamorphosisInt extends Remote {
	String getRandomNumber(byte[] seed) throws RemoteException;
	
	void readFile(String rand1, String rand2, String rand3, String rand4) throws RemoteException;
	
	void writeFile() throws RemoteException;
	
	void clearList() throws RemoteException;
	
	void startServer() throws RemoteException;
	
	void endServer() throws RemoteException;
}
