package com.udemy.exception;

public class UnenrolledStudentException extends RuntimeException {
    public UnenrolledStudentException(String message) {
        super(message);
    }
}
