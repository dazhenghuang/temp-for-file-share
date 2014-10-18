package external;

import java.rmi.*; // For Naming, RemoteException, etc.
import java.net.*; // For MalformedURLException
import java.io.*;  // For Serializable interface
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class External implements ExternalStorage {
	FeedbackInt feedback;
	MetamorphosisInt metamorphosis;
	
	/**
	 * Generic Constructor for external
	 */
	External() {}
	
	/**
	 * Starts the metamorphosis code (used to change the random
	 * numbers stored in the feedback code).
	 * 
	 * @throws RemoteException
	 */
	public void startMetamorphosis() throws RemoteException {
		try {
			//Run the batch file on external storage device
			Runtime.getRuntime().exec("cmd /c start F:\\runMetamorphosis.bat");
			
			//Allow some time for Metamorphosis.java to compile
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//Use RMI to create remote metamorphosis object
	        Registry registry = LocateRegistry.getRegistry(2222);
	        metamorphosis = (MetamorphosisInt) registry.lookup("Metamorphosis");
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Kills the running Metamorphosis code and its associated
	 * RMI server.
	 * 
	 * @throws RemoteException
	 */
	public void endMetamorphosis() {
		try {
			//Registry registry = LocateRegistry.getRegistry(2222);
			//registry.unbind("Metamorphosis");
			
			//Kill the metamorphosis code and it's command window
			metamorphosis.endServer();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		//} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	/**
	 * Starts the Feedback code (used to hash the first
	 * few characters and get the seeds to update the 
	 * mapping tables).
	 * 
	 * @throws RemoteException
	 */
	public void startFeedback() throws RemoteException {
		try {
			//Run the batch file on external storage device
			Runtime.getRuntime().exec("cmd /c start F:\\runFeedback.bat");
			
			//Allow some time for Feedback.java to compile
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//Use RMI to create remote metamorphosis object
	        Registry registry = LocateRegistry.getRegistry(2227);
	        feedback = (FeedbackInt) registry.lookup("Feedback");
		//} catch (RemoteException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Kills the running Feedback code and its associated
	 * RMI server.
	 * 
	 * @throws RemoteException
	 */
	public void endFeedback() {
		try {
			//Registry registry = LocateRegistry.getRegistry(2222);
			//registry.unbind("Metamorphosis");
			
			//Kill the feedback code and it's command window
			feedback.endServer();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		//} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	/**
	 * Uses a seed to generate a random number. The random
	 * number is intended to be inserted into the feedback code.
	 * 
	 * @param The seed to generate a random number
	 * @throws RemoteException
	 * @return the random number
	 */
	public String getRandomNumber(byte[] seed) throws RemoteException {
		return metamorphosis.getRandomNumber(seed);
	}
	
	/**
	 * Reads the Feedback.java file and inserts to random numbers
	 * into the ArrayList that contains Feedback.java.
	 * 
	 * @param Random number for the current login
	 * @param Random number for the next login
	 * @throws RemoteException
	 */
	public void readFile(String rand1, String rand2, String rand3, String rand4) throws RemoteException {
		metamorphosis.readFile(rand1, rand2, rand3, rand4);
	}
	
	/**
	 * Writes the Feedback.java code (stored in ArrayList) to the
	 * file, with modified random numbers.
	 * 
	 * @throws RemoteException
	 */
	public void writeFile() throws RemoteException {
		metamorphosis.writeFile();
	}
	
	/** 
	 * Clears the ArrayList that holds Feedback.java code. This
	 * is for inserting new random numbers in the metamorphosis 
	 * code. 
	 * 
	 * @throws RemoteException
	 */
	public void clearList() throws RemoteException {
		metamorphosis.clearList();
	}
	
	/**
	 * Feedback hash for the current login.
	 * 
	 * @param Message to be hashed
	 * @throws RemoteException
	 */
	public void hash1(String mess) throws RemoteException {
		feedback.hash1(mess);
	}
	
	/**
	 * Feedback hash for the next login.
	 * 
	 * @param Message to be hashed
	 * @throws RemoteException
	 */
	public void hash2(String mess) throws RemoteException {
		feedback.hash2(mess);
	} 
	
	/** 
	 * Gets the final feedback hash for the
	 * current login.
	 * 
	 * @throws RemoteException
	 * @return the hashed output 
	 */
	public byte[] getHash1() throws RemoteException {
		return feedback.getHash1();
	} 
	
	/** 
	 * Gets the final feedback hash for the
	 * next login.
	 * 
	 * @throws RemoteException
	 * @return the hashed output 
	 */
	public byte[] getHash2() throws RemoteException {
		return feedback.getHash2();
	} 
}
