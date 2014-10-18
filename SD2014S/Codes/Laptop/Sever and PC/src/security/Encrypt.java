package security;

import java.io.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import javax.swing.JOptionPane;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

    
/**
 * Encrypter class used to encrypt files
 * @author smotes
 * @author Caleb Schwind
 */
class Encrypt {
    Cipher ecipher;
    String fileName = "";
    String filePath = "";
    String outFileName = "";
    String outFile = "";
    
    /**
     * Constructor (password) method for Encrypter class 
     * @param key used for encryption
     * @param the IV filename 
     */
    Encrypt (SecretKey key) {
    	byte[] iv = new byte[] { 
                // This is the authentication tag length (12).  This indicates the number of bytes in hash output.
               (byte) 0x0c,
               // This is the length of the authenticated data (10). The authenticated data will not be encrypted 
   			// but will be authenticated.
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0a,
             };			
               
   	      try { //creates a new IV and converts it to hex format
   	    	  byte[] biv = createIV();
   	    	  byte[] newIV = new byte[iv.length + biv.length];
   	    	  System.arraycopy(iv, 0, newIV, 0, iv.length);
   	    	  System.arraycopy(biv, 0, newIV, iv.length, biv.length);

   	    	  //sets the encryption mode to AES-GCM
   		      ecipher = Cipher.getInstance("AES/CBC/NoPadding");//PKCS5Padding
   		      IvParameterSpec params = new IvParameterSpec(newIV);
   		        
   		      System.out.println("IV length: " + newIV.length);
   		        
   		      //initializes the cipher object
   		      ecipher.init(Cipher.ENCRYPT_MODE, key, params);
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
     * Constructor (password) method for Encrypter class 
     * @param key used for encryption
     * @param the IV filename 
     */
    Encrypt (SecretKey key, String IVOut) {
    	byte[] iv = new byte[] { 
                // This is the authentication tag length (12).  This indicates the number of bytes in hash output.
               (byte) 0x0c,
               // This is the length of the authenticated data (10). The authenticated data will not be encrypted 
   			// but will be authenticated.
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0a,
             };			
               
   	      try { //creates a new IV and converts it to hex format
   	    	  byte[] biv = createIV();
   	    	  byte[] newIV = new byte[iv.length + biv.length];
   	    	  System.arraycopy(iv, 0, newIV, 0, iv.length);
   	    	  System.arraycopy(biv, 0, newIV, iv.length, biv.length);
   	    	  String IVString = ToHEX(newIV);

   			  //saves the generated IV to a text file
   			  File iv1 = new File(IVOut);
   			  BufferedWriter out = new BufferedWriter(new FileWriter(iv1));
   			  out.write(IVString);
   			  out.close();

   	    	  //sets the encryption mode to AES-GCM
   		      ecipher = Cipher.getInstance("AES/CBC/NoPadding");//PKCS5Padding
   		      IvParameterSpec params = new IvParameterSpec(newIV);
   		        
   		      System.out.println("IV length: " + newIV.length);
   		        
   		      //initializes the cipher object
   		      ecipher.init(Cipher.ENCRYPT_MODE, key, params);
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
     * Constructor (file) method for Encrypter class
     * @param key used for encryption
     * @param isIndex index file flag
     * @param keyNum
     * @param the filename
     * @param the IVs path
     * @param the path for the encrypted output
     */
    Encrypt(SecretKey key, boolean isIndex, int keyNum, String filepath, String IVpath, String outfile) {    	
    		//Split up the filepaths to get input input filename, input filepath, output filename, and output filepath
    		filePath = filepath.substring(0, filepath.lastIndexOf("\\") + 1);
    	  	fileName = filepath.substring(filepath.lastIndexOf("\\") + 1, filepath.length());
			String[] inputParts = fileName.split("\\.");
			outFileName = fileName.replace(inputParts[inputParts.length - 1], "encrypted");
    	  	outFile = outfile;

    		byte[] iv = new byte[] { 
             // This is the authentication tag length (12).  This indicates the number of bytes in hash output.
            (byte) 0x0c,
            // This is the length of the authenticated data (10). The authenticated data will not be encrypted 
			// but will be authenticated.
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0a,
          };			
            
	      try { //creates a new IV and converts it to hex format
	    	  byte[] biv = createIV();
	    	  byte[] newIV = new byte[iv.length + biv.length];
	    	  System.arraycopy(iv, 0, newIV, 0, iv.length);
	    	  System.arraycopy(biv, 0, newIV, iv.length, biv.length);
	    	  String IVString = ToHEX(newIV);
				
	    	  if(isIndex) {
	    		  if(keyNum == 1) {	    			  
	    			  //saves the generated IV to a text file
	    			  File iv1 = new File(IVpath + fileName.split("\\.")[0] + "_IV.txt");
	    			  BufferedWriter out = new BufferedWriter(new FileWriter(iv1));
	    			  out.write(IVString);
	    			  out.close();
	    		  }
	    	  }
	    	  else { 
	    		  // Index file line 1
    			  // =================
    			  // write the filename to the index file (line 1)
    			  //
    			  addToIndex(filepath, outFile);
	    		  
    			  // Index file line 2
    			  // =================
    			  //adds the generated index to the index file
	    		  addToIndex(IVString, outFile);
	    	  }

	    	  //sets the encryption mode to AES-GCM
		      ecipher = Cipher.getInstance("AES/CBC/NoPadding");
		      IvParameterSpec params = new IvParameterSpec(newIV);
		    
		      System.out.println("IV length: " + newIV.length);

		      //initializes the cipher object
		      ecipher.init(Cipher.ENCRYPT_MODE, key, params);
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
     * creates a 5 byte IV used for encryption/decryption.
     * @return 16 byte IV
     * @throws IOException
     */
    private static byte[] createIV() throws IOException {
    	SecureRandom random = null;
    	try {
    		random = SecureRandom.getInstance("SHA1PRNG");
		}
		catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException: " + e);
			System.exit(-1);
		}
			
		byte[] bIV = new byte[7];
		random.nextBytes(bIV);
			
		return bIV;
	}
    
    /**
	 * Encrypts the input byte array and returns the ciphertext
	 * byte array.
	 * 
	 * @param in byte array to be encrypted
	 * @return ciphertext byte array
	 */
	 public byte[] encrypt(byte[] in) {
	     byte[] encryptedMessage = new byte[in.length];	
		 
		 if (in.length % 16 != 0) { // get input as multiple in 16 bytes for encryption
	     	System.out.println("Length of input: " + in.length);
	     	
	     	System.out.println("Buffering input file...");
	     	byte[] bufferedIn = new byte[((in.length/16)*16)+16];
	     	
	     	for(int i=0; i < in.length; i++) {
	     		bufferedIn[i] = in[i];
	     	}
	     	in = bufferedIn;
	     	System.out.println("Length of buffered: " + bufferedIn.length);
	     }
	     	
	     try {	        	
	    	 //Encrypt the message
	 	    encryptedMessage = ecipher.doFinal(in);
	 	        
	 	    } catch (Exception e){
	 	      	System.out.println("Encryption Failed: " + e.getMessage());
	 	    }
	     
	     //return the cyphertext byte array
	     return encryptedMessage;
	 }
		
    /**
	 * Encrypts the file that was provided in the constructor.
	 * 
	 */
    public void encrypt() {
    	byte[] in = getBytesFromFile(new File(filePath + fileName));
    	
    	if (in.length % 16 != 0) { // get input as multiple in 16 bytes for encryption
    		System.out.println("Length of input: " + in.length);
    		
    		System.out.println("Buffering input file...");
    		byte[] bufferedIn = new byte[((in.length/16)*16)+16];
    		
    		for(int i=0; i < in.length; i++) {
    			bufferedIn[i] = in[i];
    		}
    		in = bufferedIn;
    		System.out.println("Length of buffered: " + bufferedIn.length);
    	}
    	
    	try {	        	
	            byte[] encryptedMessage = new byte[in.length];
	            //byte[] authenticatedData = new byte[10];
	            //int outputLenUpdate = ecipher.update(authenticatedData,0, authenticatedData.length, encryptedMessage, 0);
	            
	            //outputLenUpdate += ecipher.update(in, 0, in.length, encryptedMessage, outputLenUpdate);           
	            
	            //Encrypt the file
	            encryptedMessage = ecipher.doFinal(in);
	            
	            System.out.println("Encryption Successful. Data Written to: " + outFile + outFileName);
	            
	            //Write the cyphertext to the output file
	            FileOutputStream output = new FileOutputStream(outFile + outFileName);  
	            output.write(encryptedMessage);
	            output.close();
	        } 
	        catch (Exception e){
	        	System.out.println("Encryption Failed: " + e.getMessage());
	        }
	    }
	
	
	/**
	 * splits a file into a .bulk and .piece file
	 * 
	 * @param fileName of the file to split
	 * @param Directory to save piece files to
	 * @param Directory to save bulk files to
	 * @param Directory to save random numbers to 
	 * @param directory
	 */
	public void splitFile(String fileName, String pieceDir, String bulkDir, String tempPath) {
		Random r = null;
		
		try {
			r = SecureRandom.getInstance("SHA1PRNG");
		} 
		catch(NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
		}
		
		if (!fileName.contains(".index") && !fileName.contains(".bulk") 
				&& !fileName.contains(".piece")) 
		{
			File source = new File(fileName);
			
			try {
				String sourceFName = source.getName();
				sourceFName = sourceFName.substring(0, 
						sourceFName.lastIndexOf("."));
				
				byte[] contents = getBytesFromFile(new File(fileName));
				System.out.println("File contents: " + contents);
				
				byte[] pieces = new byte[contents.length%29];
				int[] randNumsA = new int[contents.length%29];
				
				//removes the random pieces from the contents
				for (int i = 0; i < randNumsA.length; i++) {
					int randNum = r.nextInt(contents.length);
					boolean done = false;
					
					while(!done) {
						boolean isThere = false;
						
						for(int j = 0; j <= i; j++) {
							//checks if the random location was already used
							if(randNum == randNumsA[j]) {
								isThere = true;
							}
						}
						if(isThere) {
							//gets the next random location
							randNum = r.nextInt(contents.length);
						}
						else {
							//saves the random location
							randNumsA[i] = randNum;
							done = true;
						}
					}
					
					// takes a byte from the contents
					// and put it into the piece file
					pieces[i] = contents[randNum];
					
					//replace the byte with a random byte
					byte[] temp = new byte[1];
					r.nextBytes(temp);
					contents[randNum] = temp[0];
				}
				
				//converts the random number array to a string
				String randNums = "";
				for (int i = 0; i < contents.length%29; i++) {
					//System.out.println(randNumsA[i]);
					randNums += randNumsA[i];
					
					if(i < (contents.length%29) - 1) {
						randNums += ",";
					}
				}

				// Index file line 4
			    // =================
			    // adds the random locations used for the pieces 
				// in the index file (line 4)
			    //
				addToIndex(randNums, outFile);
				/**File index = new File(tempPath + "Index.index");
				BufferedWriter indexNew = new BufferedWriter(new FileWriter(index, true));
				indexNew.write(randNums);
				indexNew.write("\n");
				indexNew.close();**/
				
				// saves the removed pieces
				//String pieceFile = (directory + "\\").replace("\\\\", "\\").replace("\\sd\\", "\\pf\\") + sourceFName  + ".piece";
				String pieceFile = pieceDir + sourceFName  + ".piece";
				System.out.println("Temp move piece file to " + pieceFile);
		        File piece = new File(pieceFile);
				FileOutputStream fos2 = null;
				fos2 = new FileOutputStream(piece);
				fos2.write(pieces);
				fos2.close();
				
				//saves the contents with the pieces removed
				//String bulkFile = (directory + "\\").replace("\\\\", "\\") + sourceFName  + ".bulk";
				String bulkFile = bulkDir + sourceFName  + ".bulk";
				File bulk = new File(bulkFile);
				FileOutputStream fos3 = null;
				fos3 = new FileOutputStream(bulk);
				fos3.write(contents);
				fos3.close();
				
				//deletes the ciphertext file
				deleteFile(fileName);
			}
	        catch (Exception e) 
	        {
	
	            System.out.println("Error in Splitting" + e);
	            JOptionPane.showMessageDialog(null, "Error in Splitting File \n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        }
		}
		else
		{
			JOptionPane.showMessageDialog(null,  "Cannot split .index, .bulk, or .piece files.");
		}
	}
	
	
	/**
	 * splits the index into two files: 
	 * 1) index.bulk
	 * 2) index.piece
	 * 
	 * @param filename of the index file to split
	 * @param Directory to save piece files to
	 * @param Directory to save bulk files to 
	 * @param Directory to save random numbers to
	 */
	public static void splitIndex(String fileName, String pieceDir, String bulkDir, String tempPath) {
		Random r = null;
		
			try {
				//sets the random number generator to SHA1PRNG
			    r = SecureRandom.getInstance("SHA1PRNG");
			} 
			catch(NoSuchAlgorithmException nsae) 
			{
			    // Process the exception in some way or the other
				nsae.printStackTrace();
			}

		File source = new File(tempPath + fileName);
		try
		{
			String sourceFName = source.getName();
			sourceFName = sourceFName.substring(0, sourceFName.lastIndexOf("."));
			if(source.exists())
			{
				//gets the contents of the index file
				byte[] contents = getBytesFromFile(new File(tempPath + fileName));
				byte[] pieces = new byte[contents.length/5];
				int[] randNumsA = new int[contents.length/5];
				
				//generates an array of random values within the length of the file
				//removes the bytes at the random value locations from the contents
				for (int i = 0; i < contents.length/5; i++)
				{
					int randNum = r.nextInt(contents.length);
					boolean done = false;
					while(!done)
					{
						boolean isThere = false;
						for(int j = 0; j <= i; j++)
						{
							//checks if the random number is in the array
							if(randNum == randNumsA[j])
							{
								isThere = true;
							}
						}
						if(isThere)
						{
							//gets the next random number
							randNum = r.nextInt(contents.length);
						}
						else
						{
							//saves the random number into the array
							randNumsA[i] = randNum;
							done = true;
						}
					}
					
					//removes the byte at the random location
					pieces[i] = contents[randNum];
					
					//replaces the byte with a random byte
					byte[] temp = new byte[1];
					r.nextBytes(temp);
					contents[randNum] = temp[0];
				}
				
				//converts the array of random numbers into a string
				String randNums = "";
				for (int i = 0; i < contents.length/5; i++)
				{
					randNums += randNumsA[i];
					if(i < (contents.length/5) - 1)
					{
						randNums += ",";
					}
				}

				//saves the random numbers to a file
				File randNum = new File(tempPath + fileName.split("\\.")[0] + "_RandNums.txt");
				BufferedWriter randNumNew = new BufferedWriter(new FileWriter(randNum));
				randNumNew.write(randNums);
				randNumNew.newLine();
				randNumNew.close();
					
				//saves the removed bytes into a .piece file
				String pieceFile = pieceDir + sourceFName  + ".piece";
			    File piece = new File(pieceFile);
				FileOutputStream fos2 = null;
				fos2 = new FileOutputStream(piece);
				fos2.write(pieces);
				fos2.close();
					
				//saves the remaining contents to a .bulk file
				String bulkFile = bulkDir + sourceFName  + ".bulk";
			    File bulk = new File(bulkFile);
				FileOutputStream fos3 = null;
				fos3 = new FileOutputStream(bulk);
				fos3.write(contents);
				fos3.close();
					
				//deletes the index file
				deleteFile(tempPath + fileName);
			}
			
		}
	    catch (Exception e) 
	    {
	
	    	System.out.println("Error in Splitting" + e);
	        JOptionPane.showMessageDialog(null, "Error in Splitting Index \n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}
	    
	/**
	 * converts a byte array to hex format
	 * @param text
	 * @return
	 * @throws IOException
	 */
    public static String ToHEX(byte[] text) throws IOException {
			
	 	   String hexString = "";
	 		for(int i = 0; i < text.length; i++)
	 		{
	 			String hex = Integer.toHexString(text[i]&0xFF );
	 			if (hex.length() == 1) {
	 			    hex = "0" + hex;
	 			}
	 			hexString = hexString + hex;
	 		}   
	 		return hexString;
	    }
	    
    /**
     * adds a line to the index file
     * 
     * @param string to append to index file
     * @param path of the index file
     */
    public void addToIndex(String input, String path)
		 {
    		//open index file
			 File index = new File(path + fileName.split("\\.")[0] + "_ind.index");
			 if(!index.exists())
			 {
			   	try 
			   	{
					index.createNewFile();
				} 
			   	catch (IOException e) 
			   	{
					e.printStackTrace();
				}
			 }
			 try 
			 {
				 //adds the input to the index file
					BufferedWriter out = new BufferedWriter(new FileWriter(index, true));
					out.write(input);
					out.newLine();
					out.close();
			 } 
			 catch (IOException e) 
			 {
				 e.printStackTrace();
			 }
		  }
    
	 /**
	  * reads the bytes from a file
	  * 
	  * @param file
	  * @return
	  */
	 private static byte[] getBytesFromFile(File file) {
		byte[] bytes = null;
		
		try {
			InputStream is = new FileInputStream(file);
   
			// Get the size of the file
			long length = file.length();
   
			if (length > Integer.MAX_VALUE) {
				// File is too large
			}
   
			// Create the byte array to hold the data
			bytes = new byte[(int)length];
   
    		// Read in the bytes
    		int offset = 0;
    		int numRead = 0;
    		while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
    			offset += numRead;
    		}
   
    	   // Ensure all the bytes have been read in
    	   if (offset < bytes.length) {
    		   extracted(file);
    	   }
   
    	   // Close the input stream and return bytes
    	   is.close();
    	} catch (IOException e) {
    		
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
