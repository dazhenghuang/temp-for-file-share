package external;

import java.rmi.RemoteException;


public interface ExternalStorage {
	
	/**
	 * Starts the metamorphosis code (used to change the random
	 * numbers stored in the feedback code).
	 * 
	 * @throws RemoteException
	 */
	void startMetamorphosis() throws RemoteException;
	
	/**
	 * Kills the running Metamorphosis code and its associated
	 * RMI server.
	 * 
	 * @throws RemoteException
	 */
	void endMetamorphosis() throws RemoteException;
	
	/**
	 * Starts the Feedback code (used to hash the first
	 * few characters and get the seeds to update the 
	 * mapping tables).
	 * 
	 * @throws RemoteException
	 */
	void startFeedback() throws RemoteException;
	
	/**
	 * Kills the running Feedback code and its associated
	 * RMI server.
	 * 
	 * @throws RemoteException
	 */
	void endFeedback() throws RemoteException;
	
	/**
	 * Uses a seed to generate a random number. The random
	 * number is intended to be inserted into the feedback code.
	 * 
	 * @param The seed to generate a random number
	 * @throws RemoteException
	 * @return the random number
	 */
	String getRandomNumber(byte[] seed) throws RemoteException;
	
	/**
	 * Reads the Feedback.java file and inserts to random numbers
	 * into the ArrayList that contains Feedback.java.
	 * 
	 * @param Random number for the current login
	 * @param Random number for the next login
	 * @throws RemoteException
	 */
	void readFile(String rand1, String rand2, String rand3, String rand4) throws RemoteException;
	
	/**
	 * Writes the Feedback.java code (stored in ArrayList) to the
	 * file, with modified random numbers.
	 * 
	 * @throws RemoteException
	 */
	void writeFile() throws RemoteException;
	
	/** 
	 * Clears the ArrayList that holds Feedback.java code. This
	 * is for inserting new random numbers in the metamorphosis 
	 * code. 
	 * 
	 * @throws RemoteException
	 */
	void clearList() throws RemoteException;
	
	/**
	 * Feedback hash for the current login.
	 * 
	 * @param Message to be hashed
	 * @throws RemoteException
	 */
	void hash1(String message) throws RemoteException;
	
	/**
	 * Feedback hash for the next login.
	 * 
	 * @param Message to be hashed
	 * @throws RemoteException
	 */
	void hash2(String message) throws RemoteException;
	
	/** 
	 * Gets the final feedback hash for the
	 * current login.
	 * 
	 * @throws RemoteException
	 * @return the hashed output 
	 */
	byte[] getHash1() throws RemoteException;
	
	/** 
	 * Gets the final feedback hash for the
	 * next login.
	 * 
	 * @throws RemoteException
	 * @return the hashed output 
	 */
	byte[] getHash2() throws RemoteException;
	
	
}
