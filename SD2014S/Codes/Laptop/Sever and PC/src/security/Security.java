package security;

import java.io.File;

import javax.crypto.SecretKey;

/**
 * @author Caleb Schwind
 */
public interface Security {
		
	/**
	 * Protects the selected directory. All of the PDF files
	 * are split by page and each page is encrypted.
	 * 
	 * @param directory to protect (including path)
	 */
	void protectDir(File directory);
	
	/**
	 * AES-GCM encryption function that encrypts a file.
	 * Returns a new file that is the ciphertext of the
	 * original saved as .encrypted
	 * 
	 * @param input filename+path as string
	 * @param 32 byte array used to generate encryption key
	 */
	void protectFile(String path, byte[] key);
	
	/**
	 * Un-protects the file given by input using the decryption
	 * key. 
	 * 
	 * @param input filename+path as string
	 * @param 32 byte array used to generate decryption key
	 * @return was unprotection successful
	 */
	boolean unProtectFile(String path, byte[] key);
	
	/**Encrypts messages or obfuscated password
	 * 
	 * @param obfuscated password
	 * @param encryption key
	 * @param IV output filename+path
	 * @return encrypted obfuscated password
	 */
	byte[] encryptMessage(byte[] message, SecretKey key, String IVOut);
	
	/**Decrypts messages or obfuscated password
	 * 
	 * @param encrypted obfuscated password
	 * @param decryption key
	 * @param filename of the IV
	 * @return decrypted obfuscated password
	 */
	byte[] decryptMessage(byte[] encrMessage, SecretKey key, String IVin);
	
	/**
	 * Generates a random key as byte array
	 * @return random key as byte array
	 */
	byte[] getRandomKey();
	
	/**
	 * Writes the seeds to USB/SD
	 * 
	 * @param current login seed
	 * @param next login seed
	 * @param filename + path to save seeds
	 */
	void writeSeeds(byte[] seed1, byte[] seed2, String filename);
	
	/**
	 * Gets the seeds from the USB/SD.
	 * 
	 * @param filename + path to read seeds
	 * @return seeds seperated by a comma
	 */
	String getSeeds(String filename);
	
	/**
	 * Converts long to byte[] for dealing with seeds of long type.
	 * 
	 * @param seed of type long
	 * @return seed of type byte[]
	 */
	byte[] long2byte(long l);
}
