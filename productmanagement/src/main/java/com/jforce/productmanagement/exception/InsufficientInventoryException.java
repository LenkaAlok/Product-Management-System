package com.jforce.productmanagement.exception;

public class InsufficientInventoryException extends RuntimeException {

    public InsufficientInventoryException(String message) {
        super(message);
    }
}
