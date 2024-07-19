package blockchain;

import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import com.google.gson.GsonBuilder;

public class StringUtil {
	
	// Applies Sha256 to a string and returns the result.
	public static String applySha256(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes("UTF-8"));
			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	// Applies ECDSA Signature and returns the result (as bytes).
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		try {
			Signature dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			dsa.update(input.getBytes("UTF-8"));
			return dsa.sign();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	// Verifies a String signature.
	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes("UTF-8"));
			return ecdsaVerify.verify(signature);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	// Short hand helper to turn Object into a json string.
	public static String getJson(Object o) {
		return new GsonBuilder().setPrettyPrinting().create().toJson(o);
	}
	
	// Returns difficulty string target, to compare to hash. eg difficulty of 5 will return "00000".
	public static String getDifficultyString(int difficulty) {
		return "0".repeat(difficulty);
	}
	
	// Encodes a key to a string.
	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	// Returns the Merkle root from a list of transactions.
	public static String getMerkleRoot(ArrayList<Transaction> transactions) {
		List<String> treeLayer = new ArrayList<>();
		for (Transaction transaction : transactions) {
			treeLayer.add(transaction.transactionId);
		}
		
		while (treeLayer.size() > 1) {
			List<String> newLayer = new ArrayList<>();
			for (int i = 0; i < treeLayer.size(); i += 2) {
				String left = treeLayer.get(i);
				String right = (i + 1 < treeLayer.size()) ? treeLayer.get(i + 1) : left;
				newLayer.add(applySha256(left + right));
			}
			treeLayer = newLayer;
		}
		
		return treeLayer.size() == 1 ? treeLayer.get(0) : "";
	}
}
