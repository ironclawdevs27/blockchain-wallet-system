package blockchain;

import java.util.ArrayList;
import java.util.Date;

public class Block {
	
	public String hash;
	public String previousHash; 
	public String merkleRoot;
	public ArrayList<Transaction> transactions = new ArrayList<>(); // Our data will be a simple message.
	public long timeStamp; // As number of milliseconds since 1/1/1970.
	public int nonce;
	
	// Block Constructor.  
	public Block(String previousHash) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash(); // Making sure we do this after we set the other values.
	}
	
	// Calculate new hash based on block's contents.
	public String calculateHash() {
		return StringUtil.applySha256(
				previousHash +
				timeStamp +
				nonce +
				merkleRoot
		);
	}
	
	// Increases nonce value until hash target is reached.
	public void mineBlock(int difficulty) {
		merkleRoot = StringUtil.getMerkleRoot(transactions);
		String target = StringUtil.getDifficultyString(difficulty); // Create a string with difficulty * "0".
		while (!hash.startsWith(target)) {
			nonce++;
			hash = calculateHash();
		}
		System.out.println("Block Mined!!! : " + hash);
	}
	
	// Add transactions to this block.
	public boolean addTransaction(Transaction transaction) {
		// Process transaction and check if valid, unless block is genesis block then ignore.
		if (transaction == null) return false;		
		if (!"0".equals(previousHash) && !transaction.processTransaction()) {
			System.out.println("Transaction failed to process. Discarded.");
			return false;
		}

		transactions.add(transaction);
		System.out.println("Transaction Successfully added to Block");
		return true;
	}
}
