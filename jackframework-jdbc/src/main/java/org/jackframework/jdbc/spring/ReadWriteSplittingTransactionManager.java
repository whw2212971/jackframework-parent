package org.jackframework.jdbc.spring;

import org.jackframework.common.CaptainTools;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

public class ReadWriteSplittingTransactionManager implements PlatformTransactionManager, InitializingBean {

    protected static final ThreadLocal<TransactionStack> TRANSACTION_STACK_LOCAL = new ThreadLocal<TransactionStack>();

    protected PlatformTransactionManager transactionManager;

    @Override
    public TransactionStatus getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        pushTransaction(transactionDefinition, transactionStatus);
        return transactionStatus;
    }

    @Override
    public void commit(TransactionStatus transactionStatus) throws TransactionException {
        transactionManager.commit(transactionStatus);
        removeTransaction(transactionStatus);
    }

    @Override
    public void rollback(TransactionStatus transactionStatus) throws TransactionException {
        transactionManager.rollback(transactionStatus);
        removeTransaction(transactionStatus);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CaptainTools.assertNotNull(transactionManager, "The property 'transactionManager' is required.");
    }

    protected void pushTransaction(TransactionDefinition transactionDefinition,
                                   TransactionStatus transactionStatus) {
        TransactionStack      stack       = getTransactionStack();
        Transaction           transaction = new Transaction(transactionDefinition, transactionStatus);
        TransactionStack.Node node        = new TransactionStack.Node();
        TransactionStack.Node last        = stack.getLast();

        node.setTransaction(transaction);
        if (last == null) {
            stack.setFirst(node);
            stack.setLast(node);
        } else {
            last.setNext(node);
            stack.setLast(node);
        }
    }

    protected void removeTransaction(TransactionStatus transactionStatus) {
        TransactionStack      stack = getTransactionStack();
        TransactionStack.Node last  = stack.getLast();
        TransactionStack.Node node  = last;
        while (node != null) {
            Transaction transaction = node.getTransaction();
            if (transaction.getTransactionStatus().equals(transactionStatus)) {
                TransactionStack.Node first = stack.getFirst();
                TransactionStack.Node prev  = node.getPrev();
                if (node == last) {
                    stack.setLast(prev);
                    if (prev == null) {
                        stack.setFirst(null);
                    } else {
                        prev.setNext(null);
                    }
                    return;
                }

                TransactionStack.Node next = node.getNext();
                if (node == first) {
                    stack.setFirst(next);
                    if (next == null) {
                        stack.setLast(null);
                    } else {
                        next.setPrev(null);
                    }
                    return;
                }

                prev.setNext(next);
                next.setPrev(prev);
                return;
            }
            node = node.getPrev();
        }
    }

    protected TransactionStack getTransactionStack() {
        TransactionStack stack = TRANSACTION_STACK_LOCAL.get();
        if (stack == null) {
            TRANSACTION_STACK_LOCAL.set(stack = new TransactionStack());
        }
        return stack;
    }

    protected boolean hasWritableTransaction() {
        TransactionStack stack = TRANSACTION_STACK_LOCAL.get();
        if (stack == null) {
            return false;
        }
        TransactionStack.Node node = stack.getFirst();
        while (node != null) {
            if (!node.getTransaction().getTransactionDefinition().isReadOnly()) {
                return true;
            }
            node = node.getNext();
        }
        return false;
    }

    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

}
