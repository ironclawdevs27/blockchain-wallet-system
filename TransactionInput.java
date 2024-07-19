package blockchain;

public class TransactionInput {
    public String transactionOutputId; // Reference to the TransactionOutput's transactionId
    public TransactionOutput UTXO; // Contains the Unspent Transaction Output (UTXO)
    
    // Constructor
    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
