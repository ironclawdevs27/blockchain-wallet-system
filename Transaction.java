package blockchain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {

    public String transactionId; // Contains a hash of the transaction
    public PublicKey sender; // Sender's address/public key
    public PublicKey recipient; // Recipient's address/public key
    public float value; // Amount to send to the recipient
    public byte[] signature; // Signature to prevent others from spending funds

    public ArrayList<TransactionInput> inputs = new ArrayList<>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0; // A rough count of how many transactions have been generated

    // Constructor
    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    // Process transaction
    public boolean processTransaction() {
        if (!verifySignature()) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        // Gathers transaction inputs (Making sure they are unspent):
        for (TransactionInput i : inputs) {
            i.UTXO = BlockChain.UTXOs.get(i.transactionOutputId);
        }

        // Checks if transaction is valid:
        if (getInputsValue() < BlockChain.minimumTransaction) {
            System.out.println("Transaction Inputs too small: " + getInputsValue());
            return false;
        }

        // Generate transaction outputs:
        float leftOver = getInputsValue() - value;
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.recipient, value, transactionId));
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

        // Add outputs to Unspent list
        for (TransactionOutput o : outputs) {
            BlockChain.UTXOs.put(o.id, o);
        }

        // Remove transaction inputs from UTXO lists as spent:
        for (TransactionInput i : inputs) {
            BlockChain.UTXOs.remove(i.transactionOutputId);
        }

        return true;
    }

    // Get the total value of transaction inputs
    public float getInputsValue() {
        float total = 0;
        for (TransactionInput input : inputs) {
            if (input.UTXO != null) {
                total += input.UTXO.value;
            }
        }
        return total;
    }

    // Generate transaction signature
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient)
                + Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    // Verify transaction signature
    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient)
                + Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    // Get the total value of transaction outputs
    public float getOutputsValue() {
        float total = 0;
        for (TransactionOutput output : outputs) {
            total += output.value;
        }
        return total;
    }

    // Calculate transaction hash
    private String calculateHash() {
        sequence++; // Increase the sequence to avoid two identical transactions having the same
                    // hash
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) +
                        Float.toString(value) +
                        sequence);
    }
}
