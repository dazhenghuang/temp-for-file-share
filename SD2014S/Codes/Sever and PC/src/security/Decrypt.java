package security;

import java.io.*;

import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * Used to decrypt files.
 * @author smotes
 * @author Caleb Schwind
 *
 */
public class Decrypt {
    Cipher dcipher;
    String fileName = ""; //input file name
    String filePath = ""; //input file path
    String outFileName = ""; //output file name
    String outFile = ""; //output file path

    /**
     * Constructor (password) method. Used to decrypt files.
     * @param decryption key
     * @param IVin
     */
    Decrypt(SecretKey key, String IVin) {
    	try {
    		//gets the IV from the hex string
    		IVin = readLine(IVin);
    		byte[] iv = IVFromHex(IVin);
	        dcipher = Cipher.getInstance("AES/CBC/NoPadding");
	        IvParameterSpec params = new IvParameterSpec(iv);
	       
	        //initializes the decryption cipher
	        dcipher.init(Cipher.DECRYPT_MODE, key, params);
        } 
    	catch (javax.crypto.NoSuchPaddingException e) { 
    		System.out.println(e.getMessage());
        } 
    	catch (java.security.NoSuchAlgorithmException e) { 
    		System.out.println(e.getMessage());
        } 
    	catch (java.security.InvalidKeyException e) { 
    		System.out.println(e.getMessage());
        } 
    	catch (IllegalArgumentException e) { 
    		System.out.println(e.getMessage());
        } 
    	catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Constructor (file) method. Used to decrypt files.
     * @param key
     * @param IVin
     */
    Decrypt(SecretKey key, String filepath, String IVin, boolean isIV, String outfile) {
		filePath = filepath.substring(0, filepath.lastIndexOf("\\") + 1);
	  	outFileName = filepath.substring(filepath.lastIndexOf("\\") + 1, filepath.length());
		String[] inputParts = outFileName.split("\\.");
		fileName = outFileName.replace(inputParts[inputParts.length - 1], "encrypted");
	  	outFile = outfile;
    	
	  	
	  	try {
	  		if (!isIV) {
	  			//gets the IV from the hex string
	  			IVin = readLine(IVin);
	  			byte[] iv = IVFromHex(IVin);
	  			dcipher = Cipher.getInstance("AES/CBC/NoPadding");
	  			IvParameterSpec params = new IvParameterSpec(iv);
	       
	  			//initializes the decryption cipher
	  			dcipher.init(Cipher.DECRYPT_MODE, key, params);
	  		} else {
	  			byte[] iv = IVFromHex(IVin);
	  			dcipher = Cipher.getInstance("AES/CBC/NoPadding");
	  			IvParameterSpec params = new IvParameterSpec(iv);
 	       
	  			//initializes the decryption cipher
	  			dcipher.init(Cipher.DECRYPT_MODE, key, params);
	  		}
  		}
	  	catch (javax.crypto.NoSuchPaddingException e) { 
	  		System.out.println(e.getMessage());
	  	} 
	  	catch (java.security.NoSuchAlgorithmException e) { 
	  		System.out.println(e.getMessage());
	  	} 
	  	catch (java.security.InvalidKeyException e) { 
	  		System.out.println(e.getMessage());
	  	}	 
	  	catch (IllegalArgumentException e) { 
	  		System.out.println(e.getMessage());
	  	} 
	  	catch (Exception e){
	  		System.out.println(e.getMessage());
	  	}
    }
    
	/**
	 *  Decrypts the input byte array an saves it 
	 *  in a file.
	 * @param input ciphertext message (byte array)
	 * @return output plaintext message (byte array)
	 */
    public byte[] decrypt(byte[] in) {
            byte[] plainMessage = new byte[dcipher.getOutputSize(in.length)];
            byte[] authenticatedData = new byte[10];
            
            try {
            	//decrypt message
				plainMessage = dcipher.doFinal(in);
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			}
            System.out.println("Decryption Successful.");
            
            return plainMessage;
    }
	
	/**
	 *  Decrypts the file that was provided in
	 *  the constructor.
	 *  
	 * @param was file successfully decrypted
	 */
    public boolean decrypt() {
    		boolean decrypted = false;
    		
    		byte[] in = getBytesFromFile(new File(filePath + fileName));
            byte[] plainMessage = new byte[dcipher.getOutputSize(in.length)];
            byte[] authenticatedData = new byte[10];
            
            try {
            	//decrypt file
				plainMessage = dcipher.doFinal(in);
				decrypted = true;
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			}
            System.out.println("Decryption Successful. Data Written to: " + outFile + outFileName);
            
            //saves the plaintext to the original file
            FileOutputStream output = null;
			try {
				output = new FileOutputStream(outFile + outFileName);
				output.write(plainMessage);
	            output.close();
			} 
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			deleteFile(filePath + fileName);
			deleteFile(filePath + fileName.split("\\.")[0] + "_key.txt");
			
			return decrypted;
    }
    
	
    
	/**
	 * merges the .piece and .bulk index pieces together
	 * 
	 * @param index filename that is being merged
	 * @param Directory for piece files
	 * @param Directory for bulk files
	 * @param Directory for random numbers
	 */
	public static void mergeIndex(String file, String pieceDir, String bulkDir, String tempPath)
	{
		byte[] bulkContents = null;
		byte[] pieceContents = null;
		String fileName = file.split("\\.")[0];

		if(new File(bulkDir + fileName + "_ind.bulk").exists())
		{
			//gets the contents of the .bulk file
			bulkContents = getBytesFromFile(new File(bulkDir + fileName + "_ind.bulk"));
		}
		if(new File(pieceDir + fileName + "_ind.piece").exists())
		{
			//gets the contents of the .piece file
			pieceContents = getBytesFromFile(new File(pieceDir + fileName + "_ind.piece"));
		}
		if(bulkContents != null && pieceContents != null)
		{
			int[] randNums = new int[bulkContents.length/5];
			
			//gets the random numbers to replace the bytes that were removed
			randNums = getRandNums(fileName, tempPath);
			
			//puts the pieces back into the bulk content
			for(int j = 0; j < bulkContents.length/5; j++)
			{
				bulkContents[randNums[j]] = pieceContents[j];
			}
			
			//saves the merged file
			File original = new File(tempPath + fileName + "_ind.encrypted");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(original);
				fos.write(bulkContents);
				fos.close();
			} 
			catch (FileNotFoundException e) {	
				e.printStackTrace();
			} 
			catch (IOException e) { 
				e.printStackTrace();
			}
			
			//deletes the piece, bulk, and the random number file
			deleteFile(bulkDir + fileName + "_ind.bulk");
			deleteFile(pieceDir + fileName + "_ind.piece");
			deleteFile(tempPath + fileName + "_ind_RandNums.txt");
		}
	}
    
	/**
	 * merges the .piece and .bulk files back together
	 * @param fileName
	 * @param randNums
	 * @param Directory for piece files
	 * @param Directory for bulk files
	 * @param File to output merged file to
	 */
	public static void mergeFile(String filename, String[] randNums, String pieceDir, String bulkDir, String outfile) {
		//Create filenames and paths
		String filepath = filename.substring(0, filename.lastIndexOf("\\") + 1);
	  	filename = filename.substring(filename.lastIndexOf("\\") + 1, filename.length());
		String[] inputParts = filename.split("\\.");
		String outFileName = filename.replace(inputParts[inputParts.length - 1], "encrypted");
	  	String outFile = outfile;
	  	
		
		byte[] bulkContents = null;
		byte[] pieceContents = null;
		
		System.out.println("Attempting to merge " + filename + " " + outFileName);
		
		//gets the contents of the .bulk file and .piece file
		bulkContents = getBytesFromFile(new File(bulkDir + outFileName.replace(".encrypted", ".bulk")));
		pieceContents = getBytesFromFile(new File(pieceDir + outFileName.replace(".encrypted", ".piece")));
		
		System.out.println("Bulk Length: " + bulkContents.length);
		System.out.println("Piece Length: " + pieceContents.length);

		// puts the pieces back in the bulk content
		for(int j = 0; j < bulkContents.length%29; j++) {
			bulkContents[Integer.parseInt(randNums[j])] = pieceContents[j];
		}
		
		// saves the merged contents into the original ciphertext file
		File original = new File(outFile + outFileName);
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(original);
			fos.write(bulkContents);
			fos.close();
		} 
		catch (FileNotFoundException e) {	
			e.printStackTrace();
		} 
		catch (IOException e) { 
			e.printStackTrace();
		}
		
		//deletes the piece and bulk files
		deleteFile(bulkDir + outFileName.replace(".encrypted", ".bulk"));
		deleteFile(pieceDir + outFileName.replace(".encrypted", ".piece"));
	}
	
	/**
	 * gets the random numbers for the index file
	 * 
	 * @param Filename for the file that holds the random numbers
	 * @param Path to the random numbers file
	 * @return Array of random numbers
	 */
	public static int[] getRandNums(String fileName, String tempPath)
	{
		String randNumsS = null;
		
		try
		{
			//reads the random numbers from the file
  		  FileInputStream fstream = new FileInputStream(tempPath + fileName + "_ind_RandNums.txt");
  		  // Get the object of DataInputStream
  		  DataInputStream in = new DataInputStream(fstream);
  		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
  		  randNumsS = br.readLine();
		  
  		  //Close the input stream
  		  in.close();
		}catch (Exception e){ System.err.println("Error: " + e.getMessage());
	  	}
		
		//creates an array of the random numbers
		String[] randNumsSA = randNumsS.split(",");
		int[] randNumsIA = new int[randNumsSA.length];
		for(int i = 0; i < randNumsSA.length; i++)
		{
			randNumsIA[i] = Integer.parseInt(randNumsSA[i]);
		}
		return randNumsIA;
	}
	
	/**
	 * reads a line from the input file
	 * @param fileName to read line from
	 * @return Line read from file
	 */
	private static String readLine(String fileName) 
    {
		String strLine = "";
    	try{
    		//reads the line from the file
    		  FileInputStream fstream = new FileInputStream(fileName);
    		  // Get the object of DataInputStream
    		  DataInputStream in = new DataInputStream(fstream);
    		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		  strLine = br.readLine();
    		  
    		  //Close the input stream
    		  in.close();
    	}
    	//Catch exception if any
    	catch (Exception e)
    	{
    		  System.err.println("Error: " + e.getMessage());
    	}
    	
    	return strLine;
    }

    /**
     * converts the hex string input to a byte array
     * @param IV in hex format
     * @return IV in byte array
     * @throws IOException
     */
	private static byte[] IVFromHex(String input) throws IOException {
	    int len = input.length();
	    byte[] bIV = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        bIV[i / 2] = (byte) ((Character.digit(input.charAt(i), 16) << 4)
	                             + Character.digit(input.charAt(i+1), 16));
	    }
	    return bIV;
	}
	
	/**
	 * converts the input hex string into a byte array
	 * @param Key in hex format
	 * @return Key in byte array
	 * @throws IOException
	 */
	private static byte[] keyFromHex(String input) throws IOException {
	    int len = input.length();
	    byte[] bKey = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        bKey[i / 2] = (byte) ((Character.digit(input.charAt(i), 16) << 4)
	                             + Character.digit(input.charAt(i+1), 16));
	    }
	    return bKey;
	}
	
	 /**
	  * reads the bytes from a file
	  * @param file to get bytes from
	  * @return File contents in byte array
	  * @throws IOException
	  */
	 private static byte[] getBytesFromFile(File file) {
		// Get the size of the file
		 long length = file.length();
		 
		// Create the byte array to hold the data
	      byte[] bytes = new byte[(int)length];
	      
		 try {
			 InputStream is = new FileInputStream(file);
    
		      if (length > Integer.MAX_VALUE) {
		          // File is too large
		      }
		  
		      // Read in the bytes
		      int offset = 0;
		      int numRead = 0;
		      while (offset < bytes.length
		             && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
		          offset += numRead;
		      }
		  
		      // Ensure all the bytes have been read in
		      if (offset < bytes.length) {
		          extracted(file);
		      }
		  
		      // Close the input stream and return bytes
		      is.close();
		 } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		 } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
      return bytes;
  }
	 
		/**
		 * checks that the entire file was read
		 * @param file
		 * @throws IOException
		 */
		 private static void extracted(File file) throws IOException {
		throw new IOException("Could not completely read file "+file.getName());
	}
	
	/**
	 * deletes a file
	 * @param file
	 */
	 private static void deleteFile(String file) {
		  File f1 = new File(file);
		  f1.delete();
	 }
}
