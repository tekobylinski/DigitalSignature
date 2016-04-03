import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigitalSignature {

	public static void main(String[] args) {
		String signOrVerify = args[1];
		String secondFile = args[2];
		
		//if we are running the program to sign a file:
		if(signOrVerify.equals("s")) {		
			//rsa variable is never used because the keys are saved to a file
			//rsa is only ran when the file needs signed.
			RSAEncryption rsa = new RSAEncryption();
			
			//try opening the file and reading into string builder
			try {
				FileInputStream fin = new FileInputStream(secondFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(fin));
				byte[] fileInBytes = "".getBytes();
				StringBuilder sb = new StringBuilder();				
				String line = null;
				//read each line in with a \n appended to the end of each
				while((line = br.readLine())!= null) {
					sb.append(line);
					sb.append('\n');
				}
				
				//remove the trailing '\n' on the end of the file by reducing the index
				sb.setLength(sb.length()-1);
				
				//place the bytes from the message into a byte array
				fileInBytes = sb.toString().getBytes(); 
				
	            //try hashing the contents and writing the signature to the new file,
	            //followed by the original message
	            try {
					MessageDigest md = MessageDigest.getInstance("SHA-256");
					md.update(fileInBytes);
					byte[] digest = md.digest();
					//convert byte array to string hexadecimal form
					String ss= String.format("%064x", new java.math.BigInteger(1, digest));
					//create base 10 big integer
					BigInteger message = new BigInteger(ss,16);		
					
					//perform signature
					BigInteger signature = createSignature(message);

					//now place the signature at the start of the .signed file
					//append .signed to user specified file
					secondFile += ".signed";
					File file = new File(secondFile);
					FileOutputStream fop = new FileOutputStream(file);

					// if file doesnt exists, then create it
					if (!file.exists()) {
						file.createNewFile();
					}

					// get the content in bytes
					byte[] newline = "\n".getBytes();
					
					//write the numbers to the file(in bytes) with a newline in between
					//signature always goes exactly one line at the top
					fop.write(signature.toString().getBytes());
					fop.write(newline);
					fop.write(fileInBytes);
					fop.flush();
					fop.close();
	            } catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
	            br.close();
	        }
	        catch(FileNotFoundException e) {
	            e.printStackTrace();               
	        }
	        catch(IOException e) {
	            e.printStackTrace();
	        }
		}
		
		//if we are running the program to verify a signed file
		else if(signOrVerify.equals("v")) {
			String line = null;
			//try reading in contents from signed file
			try {
				FileInputStream fin = new FileInputStream(secondFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(fin));
				int lineNum = 0;
				byte[] messageInBytes = "".getBytes();
				String signatureString = "";
				StringBuilder sb = new StringBuilder();
				
				//first line is signature, everything else is the original message
				while((line = br.readLine())!= null) {
					if(lineNum == 0) {
						signatureString = line;
					}
					else {
						sb.append(line);
						sb.append('\n');
					}
					lineNum++;
				}
				
				//remove trailing \n character
				sb.setLength(sb.length()-1);
				messageInBytes = sb.toString().getBytes();
				
				//try hashing the message from the signed file and finding the reverseSignature
				try {
					MessageDigest md = MessageDigest.getInstance("SHA-256");
					md.update(messageInBytes);
					byte[] digest = md.digest();
					
					String ss= String.format("%064x", new java.math.BigInteger(1, digest));
					BigInteger message = new BigInteger(ss,16);
					
					BigInteger signature = new BigInteger(signatureString);
					
					//the next line is what we must compare with the original message hash
					BigInteger reverseSignature = unsign(signature);
					
					System.out.println("The original message value : " + message);
					System.out.println("The reverse signature value: " + reverseSignature);
					//perform .equals check
					if(message.equals(reverseSignature)) {
						System.out.println("Congrats! The message has not been altered.");
					}
					else {
						System.out.println("Unfortunately someone has modified the message.");
					}
					
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * Takes the signature, performs modpow operation to obtain original message
	 * @param signature the signature contained at the top of the .signed file
	 * @return the "unsigned" signature (probably the original message)
	 */
	private static BigInteger unsign(BigInteger signature) {
		//open file containing e and n
		BufferedReader br;
		BigInteger unSignature = signature;
		try {
			//properly reads d_n file in
			br = new BufferedReader(new FileReader(new File("e_n.txt")));
			String line = null;
			String e = "";
			String n = "";
			int i = 0;
			while ((line = br.readLine()) != null) {
				if(i == 0) {
					//place first line--d-- into string
					e = line;
					i++;
				}
				else if(i == 1) {
					//place second line--n-- into string
					n = line;
					i++;
				}
			}
			br.close();
			BigInteger bigE = new BigInteger(e);
			BigInteger bigN = new BigInteger(n);
			
			//perform modpow operation			
			unSignature = signature.modPow(bigE, bigN);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return unSignature;
}
	
	/**
	 * Accepts a large integer form of a message, then loads the 
	 * appropriate key from a file(d_n.txt), then performs modpow
	 * operation to obtain a signature.
	 * @param m the original message in decimal form
	 * @return the calculated signature
	 */
	private static BigInteger createSignature(BigInteger m) {
		//open file containing d and n
		//initialize, only so we can return in the case of failed file reading
		BigInteger signature = m;
		BufferedReader br;
		try {
			//properly reads d_n file in
			br = new BufferedReader(new FileReader(new File("d_n.txt")));
			String line = null;
			String d = "";
			String n = "";
			int i = 0;
			while ((line = br.readLine()) != null) {
				if(i == 0) {
					//place first line--d-- into string
					d = line;
					i++;
				}
				else if(i == 1) {
					//place second line--n-- into string
					n = line;
					i++;
				}
			}
			br.close();
			BigInteger bigD = new BigInteger(d);
			BigInteger bigN = new BigInteger(n);
			
			//perform mod pow operations			
			signature = m.modPow(bigD, bigN);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return signature;
	}	
}