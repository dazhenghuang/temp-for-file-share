package security;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.Splitter;

import config.Config;

/**
 * 
 * @author Caleb Schwind
 *
 */
public class ClientSecurity implements Security {
		boolean isIndex = false; // index file flag
		ArrayList<File> files = new ArrayList<File>();
		ArrayList<Double> encTimes = new ArrayList<Double>();
		ArrayList<Double> splTimes = new ArrayList<Double>();
	    String bulksPath = "";//"C:\\Users\\Caleb\\SeniorDesign\\sys\\bulks\\";
	    String piecePath = "";//"C:\\Users\\Caleb\\SeniorDesign\\sys\\pieces\\";
	    String tempPath = "";//"C:\\Users\\Caleb\\SeniorDesign\\sys\\temp\\";
	    String IVs = "";//"C:\\Users\\Caleb\\SeniorDesign\\sys\\ivs\\";
	    String outputDir = "";//"C:\\Users\\Caleb\\SeniorDesign\\sys\\decrypted\\";
		String indexPath = "";//"C:\\Users\\Caleb\\SeniorDesign\\sys\\Index.index";		
	   
		JProgressBar progBar;
		JTextArea progText;
		
		public ClientSecurity() {
			new Config();
			bulksPath = Config.bulksPath;
			piecePath = Config.piecesPath;
			tempPath = Config.tempPath;
			IVs = Config.IVsPath;
			outputDir = Config.outputDir;
			indexPath = Config.indexPath;
			
			 progBar = new JProgressBar(0, 100);
			 progBar.setValue(0);
			 progText = new JTextArea(20, 40);
			 progText.setMargin(new Insets(5,5,5,5));
			 progText.setEditable(false);
		}
		
	    /**
		 * Protects the selected directory. All of the PDF files
		 * are split by page and each page is encrypted.
		 * 
		 * @param directory to protect
		 */
		public void protectDir(File directory) {
			 String path = "";
			 int totalPages = 0;
			 int tempPages = 0;
			 progText.setText("");
			
			 JFrame frame = new JFrame("Protection Progress");
			 frame.add(progBar, BorderLayout.NORTH);
			 frame.add(new JScrollPane(progText), BorderLayout.SOUTH);
			 frame.pack();
			 frame.setVisible(true);
			 
			 totalPages = getNumPages(directory);
			 tempPages = totalPages;
			 progText.append("Number of pages to encrypt: " + totalPages + "\n");
			

			double totStart = (double) System.nanoTime(); 
			for( int i = 0; i < files.size(); i++) {				
				File openFile = files.get(i);
				path = openFile.getAbsolutePath();
			    path = path.substring(0, path.lastIndexOf("\\") + 1);
			    
				byte[] key = null;
								
				//Splits the PDF file and gets the number of pages
				progText.append("Splitting \"" + openFile.getName() + "\" ");
				double splStart = (double) System.nanoTime();
				int numPages = splitPDF(openFile);
				double splEnd = (double) System.nanoTime();
				splTimes.add(((splEnd - splStart)/numPages)/1000000000);
				addToMasterIndex(openFile.getName(), path + openFile.getName(), numPages);
				        	  	

				for (int j = 0; j < numPages; j++) {
					//generates a new key to encrypt the file
					key = getRandomKey();					
									
					//protects the file
					double start = (double) System.nanoTime();
					protectFile(path + openFile.getName().split("\\.")[0] + "_" + (j + 1) + ".pdf", key);
					long end = System.nanoTime();
					encTimes.add(end - start);
					
					tempPages = tempPages - 1;
					int p = (int)(((double)(totalPages - tempPages)/totalPages)*100);
					progBar.setValue(p);
					progText.append("Protecting \"" + openFile.getName().split("\\.")[0] + "_" + (j + 1) + ".pdf\"" + "\n");		
					progText.setCaretPosition(progText.getDocument().getLength());
				}
			}	

			 double totEnd = System.nanoTime();
				
			 double avgTime = 0; 
			 double temp = 0;
			 for (int j = 0; j < encTimes.size(); j++) {
				 temp = temp + encTimes.get(j);
			 }
			 avgTime = (temp / (encTimes.size() + 1))/1000000000;
				 
			 double avgSplTime = 0; 
			 temp = 0;
			 for (int j = 0; j < splTimes.size(); j++) {
				 temp = temp + splTimes.get(j);
			 }
			 avgSplTime = (temp / (splTimes.size()));
				 
			 System.out.println ("Average Encryption Time: " + avgTime + " seconds");
			 System.out.println ("  Number of Files: " + encTimes.size());
			 System.out.println ("Average PDF Split Time: " + (avgSplTime * totalPages) + " seconds");
			 System.out.println ("Averge PDF Split Time (per page): " + avgSplTime + " seconds");
			 System.out.println ("  Number of PDFs: " + splTimes.size());
			 System.out.println ("  Number of Pages: " + totalPages);
			 System.out.println ("Total Time: " + ((totEnd - totStart)/1000000000) + " seconds");
				 
			 progText.append("\n\n//----------------------------------------------------------------//\n");
			 progText.append("// Performance Metrics: \n");
			 progText.append("//    Average Encryption Time: " + avgTime + " seconds\n");
			 progText.append("//       Number of Files: " + encTimes.size() + " \n");
			 progText.append("//    Average PDF Split Time: " + (avgSplTime * totalPages) + " seconds\n");
			 progText.append("//    Average PDF Split Time (per page): " + avgSplTime + " seconds\n");
			 progText.append("//       Number of PDFs: " + splTimes.size() + "\n");
			 progText.append("//       Number of Pages: " + totalPages + "\n");
			 progText.append("//    Total Time: " + ((totEnd - totStart)/1000000000) + " seconds\n");
			 progText.append("//----------------------------------------------------------------//\n");

			 progText.append("\n\n//----------------------------------------------------------------//\n");
			 progText.append("//        Directory/File Protection Successful!            //\n");
			 progText.append("//----------------------------------------------------------------//\n");
			 progText.setCaretPosition(progText.getDocument().getLength());
			 System.out.println("Directory Protection Successful!");
			 
			 files.clear();
			 encTimes.clear();
			 splTimes.clear();
		}
				
		/**
		 * AES-GCM encryption function that encrypts a file.
		 * Returns a new file that is the ciphertext of the
		 * original saved as .encrypted
		 * 
		 * @param input filename as string
		 * @param 32 byte array used to generate encryption key
		 */
		public void protectFile(String input, byte[] inputKey) {
			isIndex = false;
			SecretKey key = null;

			String filename = input.substring(input.lastIndexOf("\\") + 1, input.length());
			String[] inputParts = input.split("\\.");
			String _FileEnc = tempPath + filename.replace(inputParts[inputParts.length - 1], "encrypted");

		    try {     
		    	key = new SecretKeySpec(inputKey, "AES");	  
		    }
		    catch(Exception e) {
		    	System.out.println("Error in creating key");
		    }
		    
		    Encrypt encrypt = new Encrypt(key, isIndex, 0, input, IVs, tempPath);
		    
		    String keyString = null;
		    try {
				keyString = ToHEX(inputKey);
			} 
		    catch (UnsupportedEncodingException e1) { 
		    	e1.printStackTrace();
			} 
		    catch (IOException e) { 
		    	e.printStackTrace();
			}  
		    
		    // Index file line 3
		    // =================
		    // write the encryption key to the index file (line 2)
		    //
		    encrypt.addToIndex(keyString, tempPath);
		    	encrypt.encrypt();	
				
		    // deletes the plaintext version of the input file
		    	deleteFile(input); 
		  
		    // splits the input file into a .bulk and .piece file
		    encrypt.splitFile(_FileEnc, piecePath, bulksPath, tempPath);
		    
		    //deletes the .encrypted version of the input file
		    deleteFile(_FileEnc);
		    
			byte[] iKey = getRandomKey();
			indexEncrypt(iKey, filename);
		}
		
		/**
		 * Un-protects the file given by input using the decryption
		 * key. 
		 * 
		 * @param input filename as string
		 * @param 32 byte array used to generate decryption key
		 * @return was unprotection successful
		 */
		public boolean unProtectFile(String input, byte[] keyIn) {
		    SecretKey key = null;
		    boolean decrypted = false;
		    String indexContents[] = null;
		    String fileName = "";
		    String indKey = "";
		    String IV = "";
		    String randNums = "";
		    
		    //Split up the input path
		    String file = input.substring(input.lastIndexOf("\\") + 1, input.length());
			String[] inputParts = input.split("\\.");
			String filename = file.split("\\.")[0];
			String _FileEnc = tempPath + file.replace(inputParts[inputParts.length - 1], "encrypted");
		      
		    try {
		    	//creates the key from string
		        key = new SecretKeySpec(keyIn, "AES");
		    }
		    catch(Exception e) {
		    	System.out.println("Error in creating key");
		    }
		    
			Decrypt decrypt = new Decrypt(key, IVs + filename + "_ind_IV.txt");
			
			//merge the index.piece and index.bulk files
		    decrypt.mergeIndex(filename, piecePath, bulksPath, tempPath);
		    
		    //decrypt the index file
		    indexDecrypt(keyIn, filename);
		    
		    try {
				//checks if the index exists
				if(new File(tempPath + filename.split("\\.")[0] + "_ind.index").exists())
				{
					//gets the contents of the index file
					indexContents = readFile(tempPath + filename + "_ind.index").split("\\r?\\n");
				}
			} 
			catch (IOException e1) { 
				e1.printStackTrace();
			}
	        
	        if(indexContents != null) { 	
		        if(indexContents.length > 3) {
		        	//adds the filename to unprotect
		    	    fileName = indexContents[0];
		    	        
		    	    //adds the IV to the array to decrypt
		    	    IV = indexContents[1];
		    	    
		    	    //adds the key to the array to decrypt
		    	    indKey = indexContents[2];
		    	        
		    	    //adds the random numbers to merge file
		    	    randNums = indexContents[3]; 
		        }
	   		}
	        System.out.println("File name1:"+indexContents[0]);
	        System.out.println("File name2:"+fileName);

	        //merge the file.bulk and file.piece files
	        decrypt.mergeFile(fileName, randNums.split(","), piecePath, bulksPath, tempPath);
			
	        //Generate the index decryption key
	        key = new SecretKeySpec(keyFromHex(indKey), "AES");
		    
	        //decrypt the main file
	        decrypt = new Decrypt(key, tempPath + filename + ".pdf", IV, true, outputDir);	    
		    decrypted = decrypt.decrypt();
	        
		    //delete the plaintext index file
		    deleteFile(tempPath + filename + "_ind.index");
		    return decrypted;
		}
		
		/**Encrypts the message or obfuscated password
		 * 
		 * @param obfuscated message or password
		 * @param encryption key
		 * @param IV output filename
		 * @return encrypted message or obfuscated password
		 */
		public byte[] encryptMessage(byte[] message, SecretKey key, String ivOut) {
			Encrypt encrypt = new Encrypt(key, ivOut);
			
			//Encrypt the message
			byte[] encryptedMessage = encrypt.encrypt(message);
			
			return encryptedMessage;
		}
		
		/**Decrypts messages or the obfuscated password
		 * 
		 * @param encrypted message or obfuscated password
		 * @param decryption key
		 * @param filename of the IV
		 * @return decrypted message or obfuscated password
		 */
		public byte[] decryptMessage(byte[] encrMessage, SecretKey key, String ivIn) {
			Decrypt decrypt = new Decrypt(key, ivIn);
			
			//decrypt the message
			byte[] decryptedMessage = decrypt.decrypt(encrMessage);
			
			return decryptedMessage;
		}

		
		public SecretKey getSecureChannelKey(byte[] seed1, byte[] seed2, String pressedKey) {
			byte[] seed = new byte[seed1.length];			
			
			// XORs the two generated seeds
			for(int i = 0; i < seed1.length; i++) {
				seed[i] = (byte) (seed1[i] ^ seed2[i]);
			}
			
			byte[] pressedKeyBytes = pressedKey.getBytes();
			byte[] tempKey = new byte[seed.length + pressedKeyBytes.length];
 	    	System.arraycopy(seed, 0, tempKey, 0, seed.length);
 	    	System.arraycopy(pressedKeyBytes, 0, tempKey, seed.length, pressedKeyBytes.length);

			byte[] newKey = new byte[16];
			
			//get 16 random bytes
			int getByte = 0;
			for (int i = 0; i < 16; i++) {
				getByte = (int)(Math.random() * (seed.length + pressedKeyBytes.length));
				newKey[i] = tempKey[getByte];
			}
 	    	
			//create key from xored seeds
			SecretKey seedKey = new SecretKeySpec(newKey, "AES");
			
			//Create secure channel encryption key
			Encrypt encrypt = new Encrypt(seedKey); //encrypts a string of 0s
			byte[] tempEncr1 = encrypt.encrypt("0000000000000000".getBytes());
			byte[] tempEncr2 = encrypt.encrypt(tempEncr1);
			
			byte[] connectionKeyBytes = new byte[16];
			
			//get 16 random bytes
			for (int i = 0; i < 16; i++) {
				getByte = (int)(Math.random() * (tempEncr2.length + 1));
				connectionKeyBytes[i] = tempKey[getByte];
			}
			
			SecretKey connectionKey = new SecretKeySpec(connectionKeyBytes, "AES");
			
			return connectionKey;
		}
		
		/**
		 * Generates a random key as byte array
		 * @return random key as byte array
		 */
		public byte[] getRandomKey() {
			long salt = 0;
			Random random = null;
			byte [] testrngrn = null;
			//sets the Random object to use SHA1PRNG
			try {
				random = SecureRandom.getInstance("SHA1PRNG");
			
				//gets the time of day in nanoseconds
				long nanoGMT2 = System.nanoTime();
			
				//loops through several times to get a random salt
				for (int i=0; i<2; i++) {
					//sets the random generator seed to the current time
					random.setSeed(nanoGMT2);
					nanoGMT2 = System.nanoTime();
					
					//sets the salt value
					salt = random.nextLong();	
				}
				
				//loops through several times to get a random key
				for (int i=0; i<2; i++) {
					// sets the random generator seed to the current time plus
					// the salt/password combination
					random.setSeed(nanoGMT2+salt);
					nanoGMT2 = System.nanoTime();
					
					// stores the random 16 byte array
					testrngrn = new byte [16];
					random.nextBytes(testrngrn);
				}
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// returns the random array produced
			return testrngrn;
		}
		
		/**
		 * Writes the seeds to USB/SD
		 * 
		 * @param current login seed
		 * @param next login seed
		 * @param filename + path to save seeds
		 */
		public void writeSeeds(byte[] seed1, byte[] seed2, String filename) {
			try {
				String seeds = new String(seed1, "UTF-8") + "," + new String(seed2, "UTF-8");
				File seedFile = new File("F:\\seeds.txt");
				File cloudSeedFile = new File("C:\\Users\\Caleb\\SkyDrive\\seeds.txt");
				BufferedWriter out = new BufferedWriter(new FileWriter(seedFile));
				BufferedWriter cloudOut = new BufferedWriter(new FileWriter(seedFile));
				out.write(seeds);
				cloudOut.write(seeds);
				out.close();
				cloudOut.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
		 * Gets the seeds from the USB/SD.
		 * 
		 * @param filename + path to read seeds
		 * @return seeds seperated by a comma
		 */
		public String getSeeds(String filename) {
			String seeds = "";
			seeds = readLine(filename);
			
			return seeds;
		}
		
		/**
		 * Converts long to byte[] for dealing with seeds of long type.
		 * 
		 * @param seed of type long
		 * @return seed of type byte[]
		 */
		public byte[] long2byte(long l) 
	    {
			byte[] bytes = null;
			ByteBuffer buffer = ByteBuffer.allocate(8);
			buffer.putLong(l);
			bytes = buffer.array();
		    
			return bytes;    
	    }
		
		/**
		 * Counts the number of pages of all of the
		 * PDF files contained in directory. Also
		 * adds each of the PDF files to the files
		 * arraylist. 
		 * 
		 * @param directory
		 * @return the total number of pages in this directory
		 */
		private int getNumPages(File directory) {
			int pages = 0;
			 String path = directory.getAbsolutePath() + "\\";
			 
			 if (directory.isDirectory()) {			
				//gets the list of all files in the directory
				File[] dirFiles = directory.listFiles();
				
				for (int i = 0; i < dirFiles.length; i++) {
					if (dirFiles[i].isFile()) {
						File openFile = new File(path + dirFiles[i].getName());
						files.add(openFile);
						
						if(openFile.exists()) {
							try {
								PDDocument doc = new PDDocument();
								doc = PDDocument.load(openFile);
								pages = pages + doc.getNumberOfPages();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	
						}
					} else {
						pages = pages + getNumPages(dirFiles[i]);
					}
				}
			 } else {
				 files.add(directory);
					try {
						PDDocument doc = new PDDocument();
						doc = PDDocument.load(directory);
						pages = pages + doc.getNumberOfPages();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
			 }
			 
			return pages;
		}

		
		/**
		 * Splits PDF files by page; for example, if a single 10 page pdf file
		 * is input then the directory will result in 10 single page pdf files.
		 * 
		 * @param input PDF file to be split by page
		 * @return number of pages
		 */
		private int splitPDF(File file) {
			int numPages = 0; //number of pages
			Splitter splitter = new Splitter(); //PDF splitter
	
			try {
				//load the PDF file
				PDDocument doc = new PDDocument(); //PDF document
				doc = doc.load(file); 

				//split the PDF file by page
				List<PDDocument> docs = splitter.split(doc);
				numPages = docs.size(); //number of pages
				
				//Get file path information
				String path = file.getAbsolutePath();
				path = path.substring(0,path.lastIndexOf(File.separator)) + "\\";
				path = path.replace("\\", "\\\\");
				
				//Save each of the pages as its own PDF file
				for (int i = 0; i < docs.size(); i++) {
					PDDocument tempDoc = docs.get(i); 
					tempDoc.save(path + file.getName().split("\\.")[0] + "_" + (i + 1) + ".pdf");
					tempDoc.close();
					
					if (i%10 == 0) {
						progText.append("#");
					}
				}
				doc.close();
				progText.append("\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (COSVisitorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			deleteFile(file.getAbsolutePath()); //delete the original PDF file
			
			return numPages;
		}
		
		/**
		 * used to encrypt the index file after all other files 
		 * have been encrypted
		 * @param keyin byte array generated by the android device.
		 * @param index filename 
		 */
		private void indexEncrypt(byte[] keyin, String filename) {
			byte[] inputKey1 = null;
			byte[] inputKey2 = null;
			SecretKey key_1 = null;
			boolean isIndex = true;
			
			filename = filename.split("\\.")[0];
			
			try {
				//Create key 1 for encrypting the Index file
				inputKey1 = getRandomKey();
				inputKey2 = keyin; //Key from android device
				System.out.println("master key: "+ new String(inputKey1));
				//System.out.println("slave key: "+ new String(inputKey2));
				
				//creates a new byte array for master key
				byte[] inputKey = new byte[inputKey1.length];
				
				// XORs the two generated keys to create a master key
				for(int i = 0; i < inputKey1.length; i++) {
					inputKey[i] = (byte) (inputKey1[i]);
				}
				
				// creates the master key
				key_1 = new SecretKeySpec(inputKey, "AES");
				
				// encrypts the byte array for the first key needed for the master key
				// inputKey1 = TPMProtect.encryptData(inputKey1);
				
				// saves the byte array for the first key needed for the master key
				File key1 = new File(tempPath + filename + "_key.txt");
				BufferedWriter out = new BufferedWriter(new FileWriter(key1));
				out.write(ToHEX(inputKey1));
				out.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Encrypt encrypt = new Encrypt(key_1, isIndex, 1, tempPath + filename + "_ind.index", IVs, tempPath);

			if(new File(tempPath + filename + "_ind.index").exists()) {
				//encrypts the index file using the master key

				//encrypt the index file
				encrypt.encrypt();
			}
			
			//Post-encryption filename
			String _FileEnc = filename + "_ind.encrypted";

			//split the index file into bulk and piece files
			encrypt.splitIndex(_FileEnc, piecePath, bulksPath, tempPath);
			
			//deletes the plaintext and encrypted index files
			deleteFile(tempPath + filename + "_ind.index");
		    deleteFile(tempPath + filename + "_ind.encrypted");
		}
		
		/**
		 * Decrypts the index file using the key generated during encryption.
		 * 
		 * @param Index decryption key
		 * @param Index filename
		 */
		private void indexDecrypt(byte[] keyin, String filename) {
			SecretKey key_1 = null;
			byte[] key1 = null;
			byte[] key2 = null;
			byte[] key = null;
			String IV1 = "";
			
			if(new File(tempPath + filename + "_key.txt").exists()) {
				//gets the encrypted key generated by the encryption process
				key1 = keyFromHex(readLine(tempPath + filename + "_key.txt"));
				//decrypts the first key for generating master key
				//key1 = TPMUnprotect.decryptData(key1);			
				
			}
			System.out.println("keyin"+new String(keyin));
			System.out.println("key1"+new String(key1));

			key2 = keyin;
			
			//makes sure both keys have a value
			if(key1 != null && key2 != null) {
				//XORs the two keys to create the master key
				key = new byte[key1.length];
				for(int i = 0; i < key1.length; i++) {
					key[i] = (byte) (key1[i]);//^key2[i]);
				}
			}
			
			//IV path
			IV1 = IVs + filename + "_ind_IV.txt";
		      
		    try {
		    	//creates the key using the master key
		    	key_1 = new SecretKeySpec(key, "AES");
		    }
		    catch(Exception e) {
		    	System.out.println("Error in creating key");
		    }	
		    
		    Decrypt decrypter = new Decrypt(key_1, tempPath + filename + "_ind.index", IV1, false, tempPath);
		    
		    if(new File(tempPath + filename + "_ind.encrypted").exists()) {
				//decrypts the index
				decrypter.decrypt();
			}
		    if(new File(tempPath + filename + "_ind.index").exists()) {
		    	//deletes the ciphertext index file and key file
		    	deleteFile(tempPath + filename + "_ind.encrypted");
		    	deleteFile(tempPath + filename + "_key.txt");
		    }
		    if(new File(IVs + filename + "_ind_IV.txt").exists()) {
		    	//deletes the plaintext index file
		    	deleteFile(IVs + filename + "_ind_IV.txt");		    	
		    }
		}
		
		/**
		 * reads a line from the input file
		 * @param fileName to read from
		 * @return line read from the file
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
	     * adds a line to the index file
	     * 
	     * @param string to append to index file
	     * @param path of the index file
	     */
	    public void addToMasterIndex(String filename, String filepath, int numFiles)
			 {
	    		String fileInfo = filename + ";" + numFiles + ";" + filepath;
	    	
	    		//open index file
				 File index = new File(indexPath);
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
						out.write(fileInfo);
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
		  * @param file to get bytes from
		  * @return bytes from the file
		  * @throws IOException
		  */
		 private static byte[] getBytesFromFile(File file) throws IOException {
	      InputStream is = new FileInputStream(file);
	  
	      // Get the size of the file
	      long length = file.length();
	  
	      if (length > Integer.MAX_VALUE) {
	          // File is too large
	      }
	  
	      // Create the byte array to hold the data
	      byte[] bytes = new byte[(int)length];
	  
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
	      return bytes;
	  }
		 
		   /**
		    * reads the contents of a file
		    * @param fileName to read
		    * @return content read from the file
		    * @throws IOException
		    */
			private static String readFile(String fileName) throws IOException 
		    {
		    	String output = "";
		    	try{
		    		//opens file to be read
		    		  FileInputStream fstream = new FileInputStream(fileName);
		    		  // Get the object of DataInputStream
		    		  DataInputStream in = new DataInputStream(fstream);
		    		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    		  String strLine;
		    		  
		    		  //Read File Line By Line
		    		  while ((strLine = br.readLine()) != null)   {
			    		  output += strLine;
			    		  output += "\n";
		    		  }
		    		  
		    		  //Close the input stream
		    		  in.close();
		    	}
		    	//Catch exception if any
		    	catch (Exception e)
		    	{
		    		  System.err.println("Error: " + e.getMessage());
		    	}
		    	
		    	//returns contents of file
		    	return output;
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
		 * converts the input hex string into a byte array
		 * @param input
		 * @return byte version of the key
		 */
		private static byte[] keyFromHex(String input) {
		    
			int len = input.length();
		    byte[] bKey = new byte[len / 2];
		    for (int i = 0; i < len; i += 2) {
		        bKey[i / 2] = (byte) ((Character.digit(input.charAt(i), 16) << 4)
		                             + Character.digit(input.charAt(i+1), 16));
		    }
		    return bKey;
		}

	 
		 /**
		 * deletes a file
		 * @param file
		 */
		 private static void deleteFile(String file) {
			  File f1 = new File(file);
			  f1.delete();
		 }
		 
		 /**
		  * converts a byte array to hex format
		  * @param text in bytes format
		  * @return hex format of the input bytes
		  * @throws IOException
		  */
		 private static String ToHEX(byte[] text) throws IOException{
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
}
