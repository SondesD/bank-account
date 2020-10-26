package model;

public enum TransactionType {
    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal");

    String type;
    private TransactionType( String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
