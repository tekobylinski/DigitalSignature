import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class RSAEncryption {
	//class variables used in RSA Encryption
	public BigInteger p;
	public BigInteger q;
	public BigInteger n;
	public BigInteger phiN;
	public BigInteger e;
	public BigInteger d;
	public final int maxPrimeNumOfBits = 515;
	
	//Constructor, calculates all needed elements for RSA, leaving all 
	//variables publicly visible to the user, (not ideally safe)
	public RSAEncryption() {
		p = fermatsTest(maxPrimeNumOfBits);
		q = fermatsTest(maxPrimeNumOfBits);
		n = p.multiply(q);
		phiN = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
		e = calculateE(phiN);
		d = e.modInverse(phiN);
		
		primeToFile(p,q);
		publicToFile(e,n);
		privateToFile(d,n);
	}
	
	/**
	 * primeToFile takes two large integers, converts them to strings,
	 * then writes them to the p_q.txt file.
	 * @param p1 The first prime number.
	 * @param p2 The second prime number.
	 */
	static void primeToFile(BigInteger p1, BigInteger p2) {
		//make the BigIntegers into strings
		String p1string = p1.toString();
		String p2string = p2.toString();
		
		FileOutputStream fop = null;
		File file;
		//try file opening and writing
		try {

			file = new File("p_q.txt");
			fop = new FileOutputStream(file);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] p1InBytes = p1string.getBytes();
			byte[] p2InBytes = p2string.getBytes();
			byte[] newline = "\n".getBytes();
			
			//write the numbers to the file with a newline in between
			fop.write(p1InBytes);
			fop.write(newline);
			fop.write(p2InBytes);
			fop.flush();
			fop.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * publicToFile takes two large integers, converts them to strings,
	 * then writes them to the e_n.txt file.
	 * @param p1 The first number in the key(e).
	 * @param p2 The second number in the key(n).
	 */
	static void publicToFile(BigInteger p1, BigInteger p2) {
		//make the BigIntegers into strings
		String p1string = p1.toString();
		String p2string = p2.toString();
		
		FileOutputStream fop = null;
		File file;
		//try file opening and writing
		try {

			file = new File("e_n.txt");
			fop = new FileOutputStream(file);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] p1InBytes = p1string.getBytes();
			byte[] p2InBytes = p2string.getBytes();
			byte[] newline = "\n".getBytes();
			
			//write the numbers to the file with a newline in between
			fop.write(p1InBytes);
			fop.write(newline);
			fop.write(p2InBytes);
			fop.flush();
			fop.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * privateToFile takes two large integers, converts them to strings,
	 * then writes them to the d_n.txt file.
	 * @param p1 The first number in the key(d).
	 * @param p2 The second number in the key(n).
	 */
	static void privateToFile(BigInteger p1, BigInteger p2) {
		//make the BigIntegers into strings
		String p1string = p1.toString();
		String p2string = p2.toString();
		
		FileOutputStream fop = null;
		File file;
		//try file opening and writing
		try {

			file = new File("d_n.txt");
			fop = new FileOutputStream(file);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] p1InBytes = p1string.getBytes();
			byte[] p2InBytes = p2string.getBytes();
			byte[] newline = "\n".getBytes();
			
			//write the numbers to the file with a newline in between
			fop.write(p1InBytes);
			fop.write(newline);
			fop.write(p2InBytes);
			fop.flush();
			fop.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	* Loops through possible e values starting at 3, and tries every
	* odd number until it finds one that is relatively prime to the parameter.
	* @param phiN is the number we are trying to find a relatively prime number for.
	* @return a number that is relatively prime to the parameter.
	 */	
	static BigInteger calculateE(BigInteger phiN) {
		BigInteger e = new BigInteger("3");
	    while (phiN.gcd(e).intValue() > 1) {
	      e = e.add(new BigInteger("2"));
	    }		
		return e;
	}
	
	/**
	 * Tests to see if a randomly generated number is prime
	 * p is the number we're testing, a is a random number smaller than p
	 * @param size is the size(# of bits) of the number
	 * @return a (very likely) prime number
	 */
	static BigInteger fermatsTest(int size) {
		
		BigInteger p = new BigInteger(size, new Random());
		BigInteger a;
		boolean prime = false;
		
		while(!prime) {
			//loop enough times to remove as much chance for fools
		
			for(int i = 0; i < 3; ++i) {
				//select an a value(smaller than p) to test p
				a = new BigInteger(p.bitCount()-1, new Random());
				
				//if n^(p-1) % p == 1, set prime to true, then test p with another a
				if(a.modPow(p.subtract(BigInteger.ONE), p).equals(BigInteger.ONE)) {
					prime = true;
				}
				//else set prime to false, make for loop exit
				else {
					prime = false;
					p = new BigInteger(size, new Random());
					i = 10;
				}
			}
		}		
		return p;
	}
}