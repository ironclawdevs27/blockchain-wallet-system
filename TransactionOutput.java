package blockchain;

import java.security.PublicKey;

public class TransactionOutput {
    public final String id; // Unique identifier for this output
    public final PublicKey recipient; // New owner of these coins
    public final float value; // Amount of coins
    public final String parentTransactionId; // ID of the transaction that created this output

    // Constructor
    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = calculateId(); // Set the unique ID based on recipient, value, and parent transaction ID
    }

    // Calculate a unique ID for this output
    private String calculateId() {
        return StringUtil.applySha256(
            StringUtil.getStringFromKey(recipient) +
            Float.toString(value) +
            parentTransactionId
        );
    }

    // Check if this output belongs to the given public key
    public boolean isMine(PublicKey publicKey) {
        return publicKey.equals(recipient);
    }
}
