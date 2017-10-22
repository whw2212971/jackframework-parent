package org.jackframework.jdbc.spring;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

public class Transaction {

    protected TransactionDefinition transactionDefinition;

    protected TransactionStatus transactionStatus;

    public Transaction(TransactionDefinition transactionDefinition, TransactionStatus transactionStatus) {
        this.transactionDefinition = transactionDefinition;
        this.transactionStatus = transactionStatus;
    }

    public TransactionDefinition getTransactionDefinition() {
        return transactionDefinition;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

}
